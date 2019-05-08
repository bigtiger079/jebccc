
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.exceptions.JebRuntimeException;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.RcpErrorHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportWriter;
import com.pnfsoftware.jeb.util.concurrent.DaemonExecutors;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FileExportDecompileAllJob<T extends ICodeItem>
        implements IRunnableWithProgress {
    private static final ILogger logger = GlobalLog.getLogger(FileExportDecompileAllJob.class);
    private static final int TIME_BEFORE_CHECK_NEW_THREAD = 100;
    private static final int TIMEOUT = 3000;
    private RcpClientContext context;
    private IFileExport<T> fileExport;
    private Pattern pattern;
    private IDecompilerUnit decompiler;
    private FileExportWriter fileWriter;

    public FileExportDecompileAllJob(RcpClientContext context, IFileExport<T> fileExport, Pattern pattern, IDecompilerUnit decompiler, FileExportWriter fileWriter) {
        this.context = context;
        this.fileExport = fileExport;
        this.pattern = pattern;
        this.decompiler = decompiler;
        this.fileWriter = fileWriter;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        int totalItems = 0;
        String fullName;
        for (T item : this.fileExport.getItems()) {
            if (this.fileExport.canProcess(item)) {
                if (this.pattern == null) {
                    totalItems++;
                } else {
                    fullName = this.fileExport.getFullName(item);
                    if ((fullName != null) && (this.pattern.matcher(fullName).matches()))
                        totalItems++;
                }
            }
        }
        logger.info("%d decompiled Code Item(s) will be saved", new Object[]{Integer.valueOf(totalItems)});
        monitor.beginTask("Exporting decompiled code", totalItems);
        ExecutorService pool = createExecutor();
        Map<String, Future<Boolean>> futures = new HashMap();
        for (T item : this.fileExport.getItems()) {
            if (this.fileExport.canProcess(item)) {
                String fullNameWithPackage = this.fileExport.getFullName(item);
                if ((this.pattern == null) || ((fullNameWithPackage != null) && (this.pattern.matcher(fullNameWithPackage).matches()))) {
                    FileExportDecompileAllJob<T>.ExportJob job = new ExportJob(monitor, item, fullNameWithPackage);
                    if (this.decompiler.getDecompiledUnit(item.getAddress()) != null) {
                        job.call();
                    } else {
                        submit(pool, futures, fullNameWithPackage, job);
                        if (monitor.isCanceled()) {
                            cancel(pool);
                        }
                        int i = 0;
                        while (!canLaunchNewJob(pool, futures)) {
                            pool.awaitTermination(100L, TimeUnit.MILLISECONDS);
                            if (monitor.isCanceled()) {
                                cancel(pool);
                            }
                            if (i > 3000) {
                                pool = rebuildPool(pool, futures);
                                break;
                            }
                            i++;
                        }
                    }
                }
            }
        }
        pool.shutdown();
        int i = 0;
        while ((!pool.isTerminated()) &&
                (!pool.awaitTermination(500L, TimeUnit.MILLISECONDS))) {
            if (monitor.isCanceled()) {
                cancel(pool);
            }
            if (i > 3000) {
                pool = rebuildPool(pool, futures);
                break;
            }
            i++;
        }
    }

    private ExecutorService rebuildPool(ExecutorService pool, Map<String, Future<Boolean>> futures)
            throws InvocationTargetException {
        for (Map.Entry<String, Future<Boolean>> f : futures.entrySet()) {
            if (!((Future) f.getValue()).isDone()) {
                logger.error("Method %s can not be decompiled", new Object[]{f.getKey()});
            }
        }
        pool.shutdownNow();
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException localInterruptedException1) {
        }
        for (Map.Entry<String, Future<Boolean>> f : futures.entrySet()) {
            if (!((Future) f.getValue()).isDone()) {
                JebRuntimeException e = new JebRuntimeException(Strings.f("Export decompiled has been stopped because method %s takes too long.\nDecompilation is still pending.", new Object[]{f
                        .getKey()}));
                this.context.getErrorHandler().processThrowableSilent(e);
                throw new InvocationTargetException(e, e.getMessage());
            }
        }
        futures.clear();
        pool = createExecutor();
        return pool;
    }

    private ExecutorService createExecutor() {
        return DaemonExecutors.newSingleThreadExecutor();
    }

    private int getActiveCount(ExecutorService pool) {
        if ((pool instanceof ThreadPoolExecutor)) {
            return ((ThreadPoolExecutor) pool).getActiveCount();
        }
        return 1;
    }

    public boolean canLaunchNewJob(ExecutorService pool, Map<String, Future<Boolean>> futures) {
        int i = 0;
        for (Map.Entry<String, Future<Boolean>> f : futures.entrySet()) {
            if (!((Future) f.getValue()).isDone()) {
                i++;
            }
        }
        if (i == 0) {
            return true;
        }
        if ((pool instanceof ThreadPoolExecutor)) {
            int maxPoolSize = ((ThreadPoolExecutor) pool).getMaximumPoolSize();
            int limit = Runtime.getRuntime().availableProcessors();
            return i >= Math.min(maxPoolSize, limit);
        }
        return pool.isTerminated();
    }

    public void submit(ExecutorService pool, Map<String, Future<Boolean>> futures, String key, Callable<Boolean> task) {
        Future<Boolean> value = pool.submit(task);
        futures.put(key, value);
    }

    private void cancel(ExecutorService pool)
            throws InterruptedException {
        pool.shutdown();
        int activeOld = getActiveCount(pool);
        int activeNew = 0;
        while (!pool.isTerminated()) {
            if (!pool.awaitTermination(5L, TimeUnit.SECONDS)) {
                activeNew = getActiveCount(pool);
                if (activeNew == activeOld) {
                    pool.shutdownNow();
                    break;
                }
                activeOld = activeNew;
            }
        }
        throw new InterruptedException("Export decompiled code cancelled");
    }

    private class ExportJob implements Callable<Boolean> {
        String fullNameWithPackage;
        IProgressMonitor monitor;
        T item;

        public ExportJob(IProgressMonitor monitor, T item, String fullNameWithPackage) {
            this.fullNameWithPackage = fullNameWithPackage;
            this.monitor = monitor;
            this.item = item;
        }

        public Boolean call() {
            long t0 = System.currentTimeMillis();
            FileExportDecompileAllJob.logger.debug("Decompile %s", new Object[]{this.fullNameWithPackage});
            this.monitor.subTask("Decompiling " + this.fullNameWithPackage);
            ISourceUnit fileSourceUnit = null;
            try {
                fileSourceUnit = FileExportDecompileAllJob.this.decompiler.decompile(this.item.getAddress());
                if (fileSourceUnit == null) {
                    FileExportDecompileAllJob.logger.warn("Unable to decompile %s", new Object[]{this.fullNameWithPackage});
                    return Boolean.valueOf(false);
                }
            } catch (Exception e) {
                FileExportDecompileAllJob.logger.error("An error occurred: cannot decompile %s", new Object[]{this.fullNameWithPackage});
                FileExportDecompileAllJob.logger.catchingSilent(e);
                return Boolean.valueOf(false);
            }
            long t1 = System.currentTimeMillis();
            FileExportDecompileAllJob.logger.debug("Decompiled %s in %d ms", new Object[]{this.fullNameWithPackage, Long.valueOf(t1 - t0)});
            this.monitor.subTask("Saving " + this.fullNameWithPackage);
            List<String> packages = FileExportDecompileAllJob.this.fileExport.getPath(this.item);
            try {
                FileExportDecompileAllJob.this.fileWriter.writeFile(fileSourceUnit, packages, this.item.getName(true));
            } catch (IOException e) {
                FileExportDecompileAllJob.logger.error("An IO error occurred: cannot write %s", new Object[]{this.fullNameWithPackage});
                FileExportDecompileAllJob.logger.catchingSilent(e);
                return Boolean.valueOf(false);
            }
            long t2 = System.currentTimeMillis();
            FileExportDecompileAllJob.logger.debug("Wrote %s in %d ms", new Object[]{this.fullNameWithPackage, Long.valueOf(t2 - t1)});
            synchronized (FileExportDecompileAllJob.this) {
                this.monitor.worked(1);
            }
            return Boolean.valueOf(true);
        }
    }
}


