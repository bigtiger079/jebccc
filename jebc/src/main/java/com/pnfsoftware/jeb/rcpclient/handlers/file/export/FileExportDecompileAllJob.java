/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;
/*     */
/*     */

import com.pnfsoftware.jeb.core.exceptions.JebRuntimeException;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpErrorHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportWriter;
/*     */ import com.pnfsoftware.jeb.util.concurrent.DaemonExecutors;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.core.runtime.IProgressMonitor;
/*     */ import org.eclipse.jface.operation.IRunnableWithProgress;

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
/*     */ public class FileExportDecompileAllJob<T extends ICodeItem>
        /*     */ implements IRunnableWithProgress
        /*     */ {
    /*  44 */   private static final ILogger logger = GlobalLog.getLogger(FileExportDecompileAllJob.class);
    /*     */
    /*     */   private static final int TIME_BEFORE_CHECK_NEW_THREAD = 100;
    /*     */
    /*     */   private static final int TIMEOUT = 3000;
    /*     */
    /*     */   private RcpClientContext context;
    /*     */   private IFileExport<T> fileExport;
    /*     */   private Pattern pattern;
    /*     */   private IDecompilerUnit decompiler;
    /*     */   private FileExportWriter fileWriter;

    /*     */
    /*     */
    public FileExportDecompileAllJob(RcpClientContext context, IFileExport<T> fileExport, Pattern pattern, IDecompilerUnit decompiler, FileExportWriter fileWriter)
    /*     */ {
        /*  58 */
        this.context = context;
        /*  59 */
        this.fileExport = fileExport;
        /*  60 */
        this.pattern = pattern;
        /*  61 */
        this.decompiler = decompiler;
        /*  62 */
        this.fileWriter = fileWriter;
        /*     */
    }

    /*     */
    /*     */
    public void run(IProgressMonitor monitor)
    /*     */     throws InvocationTargetException, InterruptedException
    /*     */ {
        /*  68 */
        int totalItems = 0;
        /*  69 */
        for (T item : this.fileExport.getItems()) {
            /*  70 */
            if (this.fileExport.canProcess(item)) {
                /*  71 */
                if (this.pattern == null) {
                    /*  72 */
                    totalItems++;
                    /*     */
                }
                /*     */
                else {
                    /*  75 */
                    fullName = this.fileExport.getFullName(item);
                    /*  76 */
                    if ((fullName != null) && (this.pattern.matcher(fullName).matches()))
                        /*  77 */ totalItems++;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        String fullName;
        /*  82 */
        logger.info("%d decompiled Code Item(s) will be saved", new Object[]{Integer.valueOf(totalItems)});
        /*  83 */
        monitor.beginTask("Exporting decompiled code", totalItems);
        /*     */
        /*  85 */
        ExecutorService pool = createExecutor();
        /*  86 */
        Map<String, Future<Boolean>> futures = new HashMap();
        /*     */
        /*  88 */
        for (T item : this.fileExport.getItems()) {
            /*  89 */
            if (this.fileExport.canProcess(item)) {
                /*  90 */
                String fullNameWithPackage = this.fileExport.getFullName(item);
                /*  91 */
                if ((this.pattern == null) || ((fullNameWithPackage != null) && (this.pattern.matcher(fullNameWithPackage).matches()))) {
                    /*  92 */
                    FileExportDecompileAllJob<T>.ExportJob job = new ExportJob(monitor, item, fullNameWithPackage);
                    /*     */
                    /*  94 */
                    if (this.decompiler.getDecompiledUnit(item.getAddress()) != null)
                        /*     */ {
                        /*  96 */
                        job.call();
                        /*     */
                    }
                    /*     */
                    else {
                        /*  99 */
                        submit(pool, futures, fullNameWithPackage, job);
                        /* 100 */
                        if (monitor.isCanceled()) {
                            /* 101 */
                            cancel(pool);
                            /*     */
                        }
                        /* 103 */
                        int i = 0;
                        /* 104 */
                        while (!canLaunchNewJob(pool, futures)) {
                            /* 105 */
                            pool.awaitTermination(100L, TimeUnit.MILLISECONDS);
                            /* 106 */
                            if (monitor.isCanceled()) {
                                /* 107 */
                                cancel(pool);
                                /*     */
                            }
                            /* 109 */
                            if (i > 3000)
                                /*     */ {
                                /* 111 */
                                pool = rebuildPool(pool, futures);
                                /* 112 */
                                break;
                                /*     */
                            }
                            /* 114 */
                            i++;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 121 */
        pool.shutdown();
        /*     */
        /* 123 */
        int i = 0;
        /* 124 */
        while ((!pool.isTerminated()) &&
                /* 125 */       (!pool.awaitTermination(500L, TimeUnit.MILLISECONDS)))
            /*     */ {
            /*     */
            /*     */
            /* 129 */
            if (monitor.isCanceled()) {
                /* 130 */
                cancel(pool);
                /*     */
            }
            /*     */
            /* 133 */
            if (i > 3000)
                /*     */ {
                /* 135 */
                pool = rebuildPool(pool, futures);
                /* 136 */
                break;
                /*     */
            }
            /* 138 */
            i++;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private ExecutorService rebuildPool(ExecutorService pool, Map<String, Future<Boolean>> futures)
    /*     */     throws InvocationTargetException
    /*     */ {
        /* 149 */
        for (Map.Entry<String, Future<Boolean>> f : futures.entrySet()) {
            /* 150 */
            if (!((Future) f.getValue()).isDone()) {
                /* 151 */
                logger.error("Method %s can not be decompiled", new Object[]{f.getKey()});
                /*     */
            }
            /*     */
        }
        /* 154 */
        pool.shutdownNow();
        /*     */
        try {
            /* 156 */
            Thread.sleep(2000L);
            /*     */
        }
        /*     */ catch (InterruptedException localInterruptedException1) {
        }
        /*     */
        /* 160 */
        for (Map.Entry<String, Future<Boolean>> f : futures.entrySet()) {
            /* 161 */
            if (!((Future) f.getValue()).isDone())
                /*     */ {
                /*     */
                /*     */
                /* 165 */
                JebRuntimeException e = new JebRuntimeException(Strings.f("Export decompiled has been stopped because method %s takes too long.\nDecompilation is still pending.", new Object[]{f
                        /*     */
                        /* 167 */.getKey()}));
                /* 168 */
                this.context.getErrorHandler().processThrowableSilent(e);
                /* 169 */
                throw new InvocationTargetException(e, e.getMessage());
                /*     */
            }
            /*     */
        }
        /* 172 */
        futures.clear();
        /* 173 */
        pool = createExecutor();
        /* 174 */
        return pool;
        /*     */
    }

    /*     */
    /*     */
    private ExecutorService createExecutor() {
        /* 178 */
        return DaemonExecutors.newSingleThreadExecutor();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private int getActiveCount(ExecutorService pool)
    /*     */ {
        /* 188 */
        if ((pool instanceof ThreadPoolExecutor)) {
            /* 189 */
            return ((ThreadPoolExecutor) pool).getActiveCount();
            /*     */
        }
        /* 191 */
        return 1;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public boolean canLaunchNewJob(ExecutorService pool, Map<String, Future<Boolean>> futures)
    /*     */ {
        /* 203 */
        int i = 0;
        /* 204 */
        for (Map.Entry<String, Future<Boolean>> f : futures.entrySet()) {
            /* 205 */
            if (!((Future) f.getValue()).isDone()) {
                /* 206 */
                i++;
                /*     */
            }
            /*     */
        }
        /* 209 */
        if (i == 0) {
            /* 210 */
            return true;
            /*     */
        }
        /* 212 */
        if ((pool instanceof ThreadPoolExecutor)) {
            /* 213 */
            int maxPoolSize = ((ThreadPoolExecutor) pool).getMaximumPoolSize();
            /* 214 */
            int limit = Runtime.getRuntime().availableProcessors();
            /* 215 */
            return i >= Math.min(maxPoolSize, limit);
            /*     */
        }
        /* 217 */
        return pool.isTerminated();
        /*     */
    }

    /*     */
    /*     */
    public void submit(ExecutorService pool, Map<String, Future<Boolean>> futures, String key, Callable<Boolean> task) {
        /* 221 */
        Future<Boolean> value = pool.submit(task);
        /* 222 */
        futures.put(key, value);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private void cancel(ExecutorService pool)
    /*     */     throws InterruptedException
    /*     */ {
        /* 232 */
        pool.shutdown();
        /* 233 */
        int activeOld = getActiveCount(pool);
        /* 234 */
        int activeNew = 0;
        /* 235 */
        while (!pool.isTerminated()) {
            /* 236 */
            if (!pool.awaitTermination(5L, TimeUnit.SECONDS)) {
                /* 237 */
                activeNew = getActiveCount(pool);
                /* 238 */
                if (activeNew == activeOld)
                    /*     */ {
                    /* 240 */
                    pool.shutdownNow();
                    /* 241 */
                    break;
                    /*     */
                }
                /* 243 */
                activeOld = activeNew;
                /*     */
            }
            /*     */
        }
        /* 246 */
        throw new InterruptedException("Export decompiled code cancelled");
        /*     */
    }

    /*     */
    /*     */   private class ExportJob implements Callable<Boolean> {
        /*     */ String fullNameWithPackage;
        /*     */ IProgressMonitor monitor;
        /*     */ T item;

        /*     */
        /*     */
        public ExportJob(T monitor, String item) {
            /* 255 */
            this.fullNameWithPackage = fullNameWithPackage;
            /* 256 */
            this.monitor = monitor;
            /* 257 */
            this.item = item;
            /*     */
        }

        /*     */
        /*     */
        public Boolean call()
        /*     */ {
            /* 262 */
            long t0 = System.currentTimeMillis();
            /* 263 */
            FileExportDecompileAllJob.logger.debug("Decompile %s", new Object[]{this.fullNameWithPackage});
            /* 264 */
            this.monitor.subTask("Decompiling " + this.fullNameWithPackage);
            /* 265 */
            ISourceUnit fileSourceUnit = null;
            /*     */
            try {
                /* 267 */
                fileSourceUnit = FileExportDecompileAllJob.this.decompiler.decompile(this.item.getAddress());
                /* 268 */
                if (fileSourceUnit == null) {
                    /* 269 */
                    FileExportDecompileAllJob.logger.warn("Unable to decompile %s", new Object[]{this.fullNameWithPackage});
                    /* 270 */
                    return Boolean.valueOf(false);
                    /*     */
                }
                /*     */
            }
            /*     */ catch (Exception e) {
                /* 274 */
                FileExportDecompileAllJob.logger.error("An error occurred: cannot decompile %s", new Object[]{this.fullNameWithPackage});
                /* 275 */
                FileExportDecompileAllJob.logger.catchingSilent(e);
                /* 276 */
                return Boolean.valueOf(false);
                /*     */
            }
            /* 278 */
            long t1 = System.currentTimeMillis();
            /* 279 */
            FileExportDecompileAllJob.logger.debug("Decompiled %s in %d ms", new Object[]{this.fullNameWithPackage, Long.valueOf(t1 - t0)});
            /* 280 */
            this.monitor.subTask("Saving " + this.fullNameWithPackage);
            /* 281 */
            List<String> packages = FileExportDecompileAllJob.this.fileExport.getPath(this.item);
            /*     */
            try {
                /* 283 */
                FileExportDecompileAllJob.this.fileWriter.writeFile(fileSourceUnit, packages, this.item.getName(true));
                /*     */
            }
            /*     */ catch (IOException e) {
                /* 286 */
                FileExportDecompileAllJob.logger.error("An IO error occurred: cannot write %s", new Object[]{this.fullNameWithPackage});
                /* 287 */
                FileExportDecompileAllJob.logger.catchingSilent(e);
                /* 288 */
                return Boolean.valueOf(false);
                /*     */
            }
            /* 290 */
            long t2 = System.currentTimeMillis();
            /* 291 */
            FileExportDecompileAllJob.logger.debug("Wrote %s in %d ms", new Object[]{this.fullNameWithPackage, Long.valueOf(t2 - t1)});
            /*     */
            /* 293 */
            synchronized (FileExportDecompileAllJob.this) {
                /* 294 */
                this.monitor.worked(1);
                /*     */
            }
            /* 296 */
            return Boolean.valueOf(true);
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\export\FileExportDecompileAllJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */