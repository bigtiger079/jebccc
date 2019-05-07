
package com.pnfsoftware.jeb.rcpclient;


import com.pnfsoftware.jeb.client.ErrorLogGenerator;
import com.pnfsoftware.jeb.client.JebNet;
import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.exceptions.UnitLockedException;
import com.pnfsoftware.jeb.core.input.FileInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.UnitUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.base.Throwables;
import com.pnfsoftware.jeb.util.collect.Lists;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.encoding.HashCalculator;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.net.Net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.swt.SWTException;


public class RcpErrorHandler {
    private static final ILogger logger = GlobalLog.getLogger(RcpErrorHandler.class);

    public static final long STD_FAILSAFE__SLIDING_WINDOW_DURATION_MS = 60000L;

    public static final int STD_FAILSAFE__MAX_REPORT_COUNT_PER_SLIDING_WINDOW = 5;
    private long failsafeSlidingWindowDurationMs = 60000L;
    private int failsafeMaxReportCountPerSlidingWindow = 5;
    private List<Long> errorTimestamps = new ArrayList();
    private RcpClientContext ctx;


    public RcpErrorHandler(RcpClientContext ctx) {

        this.ctx = ctx;

    }


    public void disableFailsafe() {

        this.failsafeSlidingWindowDurationMs = 0L;

        this.failsafeMaxReportCountPerSlidingWindow = 0;

    }


    public void enableFailsafe(long failsafeSlidingWindowDurationMs, int failsafeMaxReportCountPerSlidingWindow) {

        this.failsafeSlidingWindowDurationMs = failsafeSlidingWindowDurationMs;

        this.failsafeMaxReportCountPerSlidingWindow = failsafeMaxReportCountPerSlidingWindow;

    }


    public void handle(Throwable t) {

        logger.catching(t);

        try {

            processThrowableVerbose(t);

        } catch (Exception e) {

            logger.catchingSilent(e);

        }

    }


    private static void displayUnitLockedException(UnitLockedException e) {

        UI.error("Please wait, analysis is executing in the background...");

    }


    public void processThrowableVerbose(Throwable t) {

        processThrowable(t, true, false, false, null, null, null);

    }


    public void processThrowableSilent(Throwable t) {

        processThrowable(t, false, false, false, null, null, null);

    }


    public void processThrowable(Throwable t, boolean verbose, boolean forceUpload, boolean doNotUploadSample, String details, Map<String, Object> extramap, IUnit faultyUnit) {
        if ((t instanceof UnitLockedException)) {
            displayUnitLockedException((UnitLockedException) t);
            return;
        }
        long currentTs = System.currentTimeMillis();
        this.errorTimestamps.add(Long.valueOf(currentTs));
        if ((this.failsafeMaxReportCountPerSlidingWindow > 0) && (this.failsafeSlidingWindowDurationMs > 0L) && (this.errorTimestamps.size() > this.failsafeMaxReportCountPerSlidingWindow)) {
            long ts = ((Long) this.errorTimestamps.get(this.errorTimestamps.size() - this.failsafeMaxReportCountPerSlidingWindow - 1)).longValue();
            long delta = currentTs - ts;
            if (delta <= this.failsafeSlidingWindowDurationMs) {
                logger.debug("Error report not generated, too many exceptions are being reported", new Object[0]);
                return;
            }
        }

        Throwable t0 = Throwables.getRootCause(t);
        boolean done = false;

        if (((t0 instanceof ClassNotFoundException)) || ((t0 instanceof NoSuchFieldException)) || ((t0 instanceof NoSuchMethodException)) || ((t0 instanceof NoClassDefFoundError)) || ((t0 instanceof IncompatibleClassChangeError))) {
            StringBuilder msg = new StringBuilder();
            msg.append(String.format("%s.\n%s.", new Object[]{S.s(310), S.s(193)}));
            msg.append(String.format("\n\n%s: %s", new Object[]{S.s(304), t0}));
            display(msg, verbose);
            logger.catching(t);
            done = true;
        } else if ((t0 instanceof Error)) {
            StringBuilder msg = new StringBuilder();
            msg.append(String.format("%s. %s.", new Object[]{S.s(795), S.s(323)}));
            msg.append(String.format("\n\n%s: %s", new Object[]{S.s(304), t0}));
            display(msg, verbose);
            logger.catching(t);
            done = true;
        } else if ((t0 instanceof SWTException)) {
            if (Licensing.isReleaseBuild()) {
                verbose = false;
            }
        }

        if (done) {
            return;
        }
        final ErrorLogGenerator gen = new ErrorLogGenerator(t);
        if (this.ctx.getEnginesContext() != null) {
            gen.recordEnginesInformation(this.ctx.getEnginesContext());
        }
        try {
            String supportPackageHash = Lists.getFirst(IO.readLines(new File(this.ctx.getProgramDirectory(), "INSTALLED_SP.TXT")));
            gen.addRecord("sp-hash", supportPackageHash);
        } catch (IOException localIOException) {
        }

        String prjReloaded = "unknown";
        String prjSha256 = "";
        Long prjFilesize = Long.valueOf(-1L);
        List<String> artSha256List = new ArrayList();
        List<Long> artFilesizeList = new ArrayList();

        IRuntimeProject prj = this.ctx.getOpenedProject();
        IInput input;
        if (prj != null) {
            prjReloaded = Boolean.toString(prj.isReloaded());
            for (ILiveArtifact a : prj.getLiveArtifacts()) {
                input = a.getArtifact().getInput();
                try {
                    InputStream in = input.getStream();
                    Throwable localThrowable7 = null;
                    try {
                        HashCalculator h = new HashCalculator(in, 16);
                        if (h.compute()) {
                            artSha256List.add(Formatter.byteArrayToHexString(h.getSha256()));
                            artFilesizeList.add(Long.valueOf(input.getCurrentSize()));
                        }
                    } catch (Throwable localThrowable2) {
                        localThrowable7 = localThrowable2;
                        throw localThrowable2;
                    } finally {
                        if (in != null) if (localThrowable7 != null) try {
                            in.close();
                        } catch (Throwable localThrowable3) {
                            localThrowable7.addSuppressed(localThrowable3);
                        }
                        else in.close();
                    }
                } catch (IOException localIOException1) {
                }
            }
        }
        if (this.ctx.getLastReloadedProjectPath() != null) {
            File prjFile = new File(this.ctx.getLastReloadedProjectPath());
            if ((prjFile.exists()) && (prjFile.canRead())) {
                prjFilesize = Long.valueOf(prjFile.length());
                try {
                    InputStream in = new FileInputStream(prjFile);
                    input = null;
                    try {
                        HashCalculator h = new HashCalculator(in, 16);
                        if (h.compute()) {
                            prjSha256 = Formatter.byteArrayToHexString(h.getSha256());

                        }
                    } catch (Throwable localThrowable5) {
                        throw localThrowable5;

                    } finally {
                        if (in != null) {
                            if (t0 != null) {
                                try {
                                    in.close();
                                } catch (Throwable localThrowable6) {
                                    t0.addSuppressed(localThrowable6);
                                }
                            } else {
                                in.close();
                            }
                        }
                    }

                } catch (IOException localIOException2) {
                }
            }
        }

        gen.addRecord("project-reloaded", prjReloaded);
        gen.addRecord("project-sha256", prjSha256);
        gen.addRecord("project-filesize", prjFilesize);
        gen.addRecord("artifacts-sha256-list", Strings.join(",", artSha256List));
        gen.addRecord("artifacts-filesize-list", Strings.join(",", artFilesizeList));
        IPropertyManager pm = this.ctx.getPropertyManager();
        boolean cfgUploadErrorLogs = BooleanUtils.toBoolean(Boolean.valueOf(pm.getBoolean(".UploadErrorLogs")));
        boolean cfgUploadErrorFiles = BooleanUtils.toBoolean(Boolean.valueOf(pm.getBoolean(".UploadErrorFiles")));
        gen.addRecord("up-set", String.format("%b;%b", new Object[]{Boolean.valueOf(cfgUploadErrorLogs), Boolean.valueOf(cfgUploadErrorFiles)}));

        if (details != null) {
            gen.addRecord("details", details);
        }

        if (extramap != null) {
            for (String key : extramap.keySet()) {
                if (key != null) {
                    Object value = extramap.get(key);
                    gen.addRecord(key, value);
                }
            }
        }
        if (faultyUnit != null) {
            try {
                gen.addRecord("unit_path", UnitUtil.buildFullyQualifiedUnitPath(faultyUnit, true, ">"));
                gen.addRecord("unit_type", faultyUnit.getFormatType());
            } catch (Exception e) {
                gen.addRecord("unit_info_Gen_error", "Cannot generate unit information");
            }
        }

        final boolean uploadErrorLogs = (!Licensing.isDebugBuild()) && ((forceUpload) || (cfgUploadErrorLogs) || (Licensing.isDemoBuild()));
        final boolean uploadErrorFiles = (uploadErrorLogs) && (!doNotUploadSample) && ((forceUpload) || (cfgUploadErrorFiles) || (Licensing.isDemoBuild()));
        final boolean verbose1 = verbose;
        ThreadUtil.start(Licensing.isDebugBuild() ? "UploadError" : null, new Runnable() {

            public void run() {

                boolean errorLogUploaded = false;

                if ((uploadErrorLogs) || (uploadErrorFiles)) {

                    Net net = new Net(RcpErrorHandler.this.ctx.getNetworkUtility());


                    if (uploadErrorLogs) {

                        String r = JebNet.post(net, "https://www.pnfsoftware.com/upload_errorlog", gen.getLog());

                        errorLogUploaded = r != null;

                    }


                    if (uploadErrorFiles) {

                        try {

                            File f = RcpErrorHandler.this.getPrimaryArtifact();

                            if (f == null) {

                                f = RcpErrorHandler.this.getReloadedProjectFile();

                            }

                            if (f != null) {

                                if ((!f.isFile()) || (!f.canRead())) {

                                    throw new IOException("No file or cannot read artifact file: " + f);

                                }


                                if (f.length() <= 134217728L) {

                                    JebNet.uploadFile(net, f, true);

                                }


                            }


                        } catch (Exception e) {

                            ErrorLogGenerator gen2 = new ErrorLogGenerator(e);

                            JebNet.post(net, "https://www.pnfsoftware.com/upload_errorlog", gen2.getLog());

                        }

                    }

                }


                boolean devMode = (RcpErrorHandler.this.ctx.isDevelopmentMode()) || (Licensing.isDebugBuild());

                if ((!errorLogUploaded) || (devMode)) {

                    File errorlogsFolder = new File(RcpErrorHandler.this.ctx.getBaseDirectory(), "errorlogs");

                    errorlogsFolder.mkdir();

                    String path = gen.dumpTo(errorlogsFolder.getAbsolutePath());


                    StringBuilder msg = new StringBuilder();


                    msg.append(String.format("%s.", new Object[]{S.s(305)}));

                    msg.append(String.format("\n\n%s: %s.", new Object[]{S.s(307), path}));


                    RcpErrorHandler.this.display(msg, verbose1);

                }

            }

        });

    }


    private void display(CharSequence msg, boolean verbose) {

        if (verbose) {

            UI.error(msg.toString());

        }

    }


    private File getPrimaryArtifact() throws IOException {

        IRuntimeProject prj = this.ctx.getOpenedProject();

        if ((prj != null) && (!prj.getLiveArtifacts().isEmpty())) {

            IInput input = ((ILiveArtifact) prj.getLiveArtifacts().get(0)).getArtifact().getInput();

            if ((input instanceof FileInput)) {

                return ((FileInput) input).getFile();

            }

            InputStream in = input.getStream();
            Throwable localThrowable3 = null;

            try {
                File f = IO.createTempFile();

                f.deleteOnExit();

                IO.writeFile(f, IO.readInputStream(in));

                return f;

            } catch (Throwable localThrowable1) {

                localThrowable3 = localThrowable1;
                throw localThrowable1;


            } finally {

                if (in != null) if (localThrowable3 != null) try {
                    in.close();
                } catch (Throwable localThrowable2) {
                    localThrowable3.addSuppressed(localThrowable2);
                }
                else in.close();

            }
        }

        return null;

    }


    private File getReloadedProjectFile() {

        if (this.ctx.getLastReloadedProjectPath() != null) {

            File prjFile = new File(this.ctx.getLastReloadedProjectPath());

            if ((prjFile.isFile()) && (prjFile.canRead())) {

                return prjFile;

            }

        }

        return null;

    }

}


