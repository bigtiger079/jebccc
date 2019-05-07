/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.client.ErrorLogGenerator;
/*     */ import com.pnfsoftware.jeb.client.JebNet;
/*     */ import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*     */ import com.pnfsoftware.jeb.core.exceptions.UnitLockedException;
/*     */ import com.pnfsoftware.jeb.core.input.FileInput;
/*     */ import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.UnitUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.util.base.Throwables;
/*     */ import com.pnfsoftware.jeb.util.collect.Lists;
/*     */ import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
/*     */ import com.pnfsoftware.jeb.util.encoding.HashCalculator;
/*     */ import com.pnfsoftware.jeb.util.format.Formatter;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import com.pnfsoftware.jeb.util.net.Net;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang3.BooleanUtils;
/*     */ import org.eclipse.swt.SWTException;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class RcpErrorHandler
        /*     */ {
    /*  52 */   private static final ILogger logger = GlobalLog.getLogger(RcpErrorHandler.class);
    /*     */
    /*     */   public static final long STD_FAILSAFE__SLIDING_WINDOW_DURATION_MS = 60000L;
    /*     */
    /*     */   public static final int STD_FAILSAFE__MAX_REPORT_COUNT_PER_SLIDING_WINDOW = 5;
    /*  57 */   private long failsafeSlidingWindowDurationMs = 60000L;
    /*  58 */   private int failsafeMaxReportCountPerSlidingWindow = 5;
    /*  59 */   private List<Long> errorTimestamps = new ArrayList();
    /*     */   private RcpClientContext ctx;

    /*     */
    /*     */
    public RcpErrorHandler(RcpClientContext ctx)
    /*     */ {
        /*  64 */
        this.ctx = ctx;
        /*     */
    }

    /*     */
    /*     */
    public void disableFailsafe() {
        /*  68 */
        this.failsafeSlidingWindowDurationMs = 0L;
        /*  69 */
        this.failsafeMaxReportCountPerSlidingWindow = 0;
        /*     */
    }

    /*     */
    /*     */
    public void enableFailsafe(long failsafeSlidingWindowDurationMs, int failsafeMaxReportCountPerSlidingWindow) {
        /*  73 */
        this.failsafeSlidingWindowDurationMs = failsafeSlidingWindowDurationMs;
        /*  74 */
        this.failsafeMaxReportCountPerSlidingWindow = failsafeMaxReportCountPerSlidingWindow;
        /*     */
    }

    /*     */
    /*     */
    public void handle(Throwable t) {
        /*  78 */
        logger.catching(t);
        /*     */
        try
            /*     */ {
            /*  81 */
            processThrowableVerbose(t);
            /*     */
        }
        /*     */ catch (Exception e)
            /*     */ {
            /*  85 */
            logger.catchingSilent(e);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private static void displayUnitLockedException(UnitLockedException e) {
        /*  90 */
        UI.error("Please wait, analysis is executing in the background...");
        /*     */
    }

    /*     */
    /*     */
    public void processThrowableVerbose(Throwable t) {
        /*  94 */
        processThrowable(t, true, false, false, null, null, null);
        /*     */
    }

    /*     */
    /*     */
    public void processThrowableSilent(Throwable t) {
        /*  98 */
        processThrowable(t, false, false, false, null, null, null);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public void processThrowable(Throwable t, boolean verbose, boolean forceUpload, boolean doNotUploadSample, String details, Map<String, Object> extramap, IUnit faultyUnit)
    /*     */ {
        /* 105 */
        if ((t instanceof UnitLockedException)) {
            /* 106 */
            displayUnitLockedException((UnitLockedException) t);
            /* 107 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /* 111 */
        long currentTs = System.currentTimeMillis();
        /* 112 */
        this.errorTimestamps.add(Long.valueOf(currentTs));
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 120 */
        if ((this.failsafeMaxReportCountPerSlidingWindow > 0) && (this.failsafeSlidingWindowDurationMs > 0L) &&
                /* 121 */       (this.errorTimestamps.size() > this.failsafeMaxReportCountPerSlidingWindow)) {
            /* 122 */
            long ts = ((Long) this.errorTimestamps.get(this.errorTimestamps.size() - this.failsafeMaxReportCountPerSlidingWindow - 1)).longValue();
            /* 123 */
            long delta = currentTs - ts;
            /* 124 */
            if (delta <= this.failsafeSlidingWindowDurationMs)
                /*     */ {
                /* 126 */
                logger.debug("Error report not generated, too many exceptions are being reported", new Object[0]);
                /* 127 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 132 */
        Throwable t0 = Throwables.getRootCause(t);
        /* 133 */
        boolean done = false;
        /*     */
        /*     */
        /* 136 */
        if (((t0 instanceof ClassNotFoundException)) || ((t0 instanceof NoSuchFieldException)) || ((t0 instanceof NoSuchMethodException)) || ((t0 instanceof NoClassDefFoundError)) || ((t0 instanceof IncompatibleClassChangeError)))
            /*     */ {
            /*     */
            /* 139 */
            StringBuilder msg = new StringBuilder();
            /* 140 */
            msg.append(String.format("%s.\n%s.", new Object[]{S.s(310), S.s(193)}));
            /* 141 */
            msg.append(String.format("\n\n%s: %s", new Object[]{S.s(304), t0}));
            /* 142 */
            display(msg, verbose);
            /* 143 */
            logger.catching(t);
            /* 144 */
            done = true;
            /*     */
            /*     */
        }
        /* 147 */
        else if ((t0 instanceof Error)) {
            /* 148 */
            StringBuilder msg = new StringBuilder();
            /* 149 */
            msg.append(String.format("%s. %s.", new Object[]{S.s(795), S.s(323)}));
            /* 150 */
            msg.append(String.format("\n\n%s: %s", new Object[]{S.s(304), t0}));
            /* 151 */
            display(msg, verbose);
            /* 152 */
            logger.catching(t);
            /* 153 */
            done = true;
            /*     */
        }
        /* 155 */
        else if ((t0 instanceof SWTException))
            /*     */ {
            /* 157 */
            if (Licensing.isReleaseBuild()) {
                /* 158 */
                verbose = false;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 162 */
        if (done) {
            /* 163 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /* 167 */
        final ErrorLogGenerator gen = new ErrorLogGenerator(t);
        /*     */
        /*     */
        /* 170 */
        if (this.ctx.getEnginesContext() != null) {
            /* 171 */
            gen.recordEnginesInformation(this.ctx.getEnginesContext());
            /*     */
        }
        /*     */
        /*     */
        /*     */
        try
            /*     */ {
            /* 177 */
            String supportPackageHash = (String) Lists.getFirst(IO.readLines(new File(this.ctx.getProgramDirectory(), "INSTALLED_SP.TXT")));
            /* 178 */
            gen.addRecord("sp-hash", supportPackageHash);
            /*     */
        }
        /*     */ catch (IOException localIOException) {
        }
        /*     */
        /*     */
        /* 183 */
        String prjReloaded = "unknown";
        /* 184 */
        String prjSha256 = "";
        /* 185 */
        Long prjFilesize = Long.valueOf(-1L);
        /* 186 */
        List<String> artSha256List = new ArrayList();
        /* 187 */
        List<Long> artFilesizeList = new ArrayList();
        /*     */
        /* 189 */
        IRuntimeProject prj = this.ctx.getOpenedProject();
        /* 190 */
        if (prj != null) {
            /* 191 */
            prjReloaded = Boolean.toString(prj.isReloaded());
            /*     */
            /* 193 */
            for (ILiveArtifact a : prj.getLiveArtifacts()) {
                /* 194 */
                input = a.getArtifact().getInput();
                /* 195 */
                try {
                    InputStream in = input.getStream();
                    Throwable localThrowable7 = null;
                    /*     */
                    try {
                        /* 197 */
                        HashCalculator h = new HashCalculator(in, 16);
                        /* 198 */
                        if (h.compute()) {
                            /* 199 */
                            artSha256List.add(Formatter.byteArrayToHexString(h.getSha256()));
                            /* 200 */
                            artFilesizeList.add(Long.valueOf(input.getCurrentSize()));
                            /*     */
                        }
                        /*     */
                    }
                    /*     */ catch (Throwable localThrowable2)
                        /*     */ {
                        /* 195 */
                        localThrowable7 = localThrowable2;
                        throw localThrowable2;
                        /*     */
                        /*     */
                        /*     */
                    }
                    /*     */ finally
                        /*     */ {
                        /*     */
                        /* 202 */
                        if (in != null) if (localThrowable7 != null) try {
                            in.close();
                        } catch (Throwable localThrowable3) {
                            localThrowable7.addSuppressed(localThrowable3);
                        }
                        else in.close();
                        /*     */
                    }
                    /*     */
                } catch (IOException localIOException1) {
                }
                /*     */
            }
            /*     */
        }
        /*     */
        IInput input;
        /* 208 */
        if (this.ctx.getLastReloadedProjectPath() != null) {
            /* 209 */
            File prjFile = new File(this.ctx.getLastReloadedProjectPath());
            /* 210 */
            if ((prjFile.exists()) && (prjFile.canRead())) {
                /* 211 */
                prjFilesize = Long.valueOf(prjFile.length());
                /* 212 */
                try {
                    InputStream in = new FileInputStream(prjFile);
                    input = null;
                    /* 213 */
                    try {
                        HashCalculator h = new HashCalculator(in, 16);
                        /* 214 */
                        if (h.compute()) {
                            /* 215 */
                            prjSha256 = Formatter.byteArrayToHexString(h.getSha256());
                            /*     */
                        }
                        /*     */
                    }
                    /*     */ catch (Throwable localThrowable5)
                        /*     */ {
                        /* 212 */
                        input = localThrowable5;
                        throw localThrowable5;
                        /*     */
                        /*     */
                    }
                    /*     */ finally
                        /*     */ {
                        /* 217 */
                        if (in != null) if (input != null) try {
                            in.close();
                        } catch (Throwable localThrowable6) {
                            input.addSuppressed(localThrowable6);
                        }
                        else in.close();
                        /*     */
                    }
                    /*     */
                }
                /*     */ catch (IOException localIOException2) {
                }
                /*     */
            }
            /*     */
        }
        /* 223 */
        gen.addRecord("project-reloaded", prjReloaded);
        /* 224 */
        gen.addRecord("project-sha256", prjSha256);
        /* 225 */
        gen.addRecord("project-filesize", prjFilesize);
        /* 226 */
        gen.addRecord("artifacts-sha256-list", Strings.join(",", artSha256List));
        /* 227 */
        gen.addRecord("artifacts-filesize-list", Strings.join(",", artFilesizeList));
        /*     */
        /* 229 */
        IPropertyManager pm = this.ctx.getPropertyManager();
        /* 230 */
        boolean cfgUploadErrorLogs = BooleanUtils.toBoolean(Boolean.valueOf(pm.getBoolean(".UploadErrorLogs")));
        /* 231 */
        boolean cfgUploadErrorFiles = BooleanUtils.toBoolean(Boolean.valueOf(pm.getBoolean(".UploadErrorFiles")));
        /* 232 */
        gen.addRecord("up-set", String.format("%b;%b", new Object[]{Boolean.valueOf(cfgUploadErrorLogs), Boolean.valueOf(cfgUploadErrorFiles)}));
        /*     */
        /*     */
        /* 235 */
        if (details != null) {
            /* 236 */
            gen.addRecord("details", details);
            /*     */
        }
        /*     */
        /*     */
        /* 240 */
        if (extramap != null) {
            /* 241 */
            for (String key : extramap.keySet()) {
                /* 242 */
                if (key != null) {
                    /* 243 */
                    Object value = extramap.get(key);
                    /* 244 */
                    gen.addRecord(key, value);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 248 */
        if (faultyUnit != null) {
            /*     */
            try {
                /* 250 */
                gen.addRecord("unit_path", UnitUtil.buildFullyQualifiedUnitPath(faultyUnit, true, ">"));
                /* 251 */
                gen.addRecord("unit_type", faultyUnit.getFormatType());
                /*     */
            }
            /*     */ catch (Exception e) {
                /* 254 */
                gen.addRecord("unit_info_Gen_error", "Cannot generate unit information");
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 260 */
        final boolean uploadErrorLogs = (!Licensing.isDebugBuild()) && ((forceUpload) || (cfgUploadErrorLogs) || (Licensing.isDemoBuild()));
        /*     */
        /* 262 */
        final boolean uploadErrorFiles = (uploadErrorLogs) && (!doNotUploadSample) && ((forceUpload) || (cfgUploadErrorFiles) || (Licensing.isDemoBuild()));
        /*     */
        /*     */
        /*     */
        /* 266 */
        final boolean verbose1 = verbose;
        /* 267 */
        ThreadUtil.start(Licensing.isDebugBuild() ? "UploadError" : null, new Runnable()
                /*     */ {
            /*     */
            public void run() {
                /* 270 */
                boolean errorLogUploaded = false;
                /* 271 */
                if ((uploadErrorLogs) || (uploadErrorFiles))
                    /*     */ {
                    /* 273 */
                    Net net = new Net(RcpErrorHandler.this.ctx.getNetworkUtility());
                    /*     */
                    /*     */
                    /* 276 */
                    if (uploadErrorLogs) {
                        /* 277 */
                        String r = JebNet.post(net, "https://www.pnfsoftware.com/upload_errorlog", gen.getLog());
                        /* 278 */
                        errorLogUploaded = r != null;
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 282 */
                    if (uploadErrorFiles) {
                        /*     */
                        try {
                            /* 284 */
                            File f = RcpErrorHandler.this.getPrimaryArtifact();
                            /* 285 */
                            if (f == null) {
                                /* 286 */
                                f = RcpErrorHandler.this.getReloadedProjectFile();
                                /*     */
                            }
                            /* 288 */
                            if (f != null) {
                                /* 289 */
                                if ((!f.isFile()) || (!f.canRead())) {
                                    /* 290 */
                                    throw new IOException("No file or cannot read artifact file: " + f);
                                    /*     */
                                }
                                /*     */
                                /*     */
                                /* 294 */
                                if (f.length() <= 134217728L) {
                                    /* 295 */
                                    JebNet.uploadFile(net, f, true);
                                    /*     */
                                }
                                /*     */
                                /*     */
                            }
                            /*     */
                            /*     */
                        }
                        /*     */ catch (Exception e)
                            /*     */ {
                            /* 303 */
                            ErrorLogGenerator gen2 = new ErrorLogGenerator(e);
                            /* 304 */
                            JebNet.post(net, "https://www.pnfsoftware.com/upload_errorlog", gen2.getLog());
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 309 */
                boolean devMode = (RcpErrorHandler.this.ctx.isDevelopmentMode()) || (Licensing.isDebugBuild());
                /* 310 */
                if ((!errorLogUploaded) || (devMode)) {
                    /* 311 */
                    File errorlogsFolder = new File(RcpErrorHandler.this.ctx.getBaseDirectory(), "errorlogs");
                    /* 312 */
                    errorlogsFolder.mkdir();
                    /* 313 */
                    String path = gen.dumpTo(errorlogsFolder.getAbsolutePath());
                    /*     */
                    /* 315 */
                    StringBuilder msg = new StringBuilder();
                    /*     */
                    /* 317 */
                    msg.append(String.format("%s.", new Object[]{S.s(305)}));
                    /* 318 */
                    msg.append(String.format("\n\n%s: %s.", new Object[]{S.s(307), path}));
                    /*     */
                    /* 320 */
                    RcpErrorHandler.this.display(msg, verbose1);
                    /*     */
                }
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    private void display(CharSequence msg, boolean verbose) {
        /* 327 */
        if (verbose) {
            /* 328 */
            UI.error(msg.toString());
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private File getPrimaryArtifact() throws IOException {
        /* 333 */
        IRuntimeProject prj = this.ctx.getOpenedProject();
        /* 334 */
        if ((prj != null) && (!prj.getLiveArtifacts().isEmpty())) {
            /* 335 */
            IInput input = ((ILiveArtifact) prj.getLiveArtifacts().get(0)).getArtifact().getInput();
            /* 336 */
            if ((input instanceof FileInput)) {
                /* 337 */
                return ((FileInput) input).getFile();
                /*     */
            }
            /* 339 */
            InputStream in = input.getStream();
            Throwable localThrowable3 = null;
            /* 340 */
            try {
                File f = IO.createTempFile();
                /* 341 */
                f.deleteOnExit();
                /* 342 */
                IO.writeFile(f, IO.readInputStream(in));
                /* 343 */
                return f;
                /*     */
            }
            /*     */ catch (Throwable localThrowable1)
                /*     */ {
                /* 339 */
                localThrowable3 = localThrowable1;
                throw localThrowable1;
                /*     */
                /*     */
            }
            /*     */ finally
                /*     */ {
                /* 344 */
                if (in != null) if (localThrowable3 != null) try {
                    in.close();
                } catch (Throwable localThrowable2) {
                    localThrowable3.addSuppressed(localThrowable2);
                }
                else in.close();
                /*     */
            }
        }
        /* 346 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private File getReloadedProjectFile() {
        /* 350 */
        if (this.ctx.getLastReloadedProjectPath() != null) {
            /* 351 */
            File prjFile = new File(this.ctx.getLastReloadedProjectPath());
            /* 352 */
            if ((prjFile.isFile()) && (prjFile.canRead())) {
                /* 353 */
                return prjFile;
                /*     */
            }
            /*     */
        }
        /* 356 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\RcpErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */