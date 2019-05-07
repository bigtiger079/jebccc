
package com.pnfsoftware.jeb.rcpclient.extensions.ui;


import com.pnfsoftware.jeb.util.base.CallableWithProgressCallback;
import com.pnfsoftware.jeb.util.base.IProgressCallback;
import com.pnfsoftware.jeb.util.base.RunnableWithProgressCallback;
import com.pnfsoftware.jeb.util.concurrent.ThreadEx;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;


public class UITask<T> {
    private static final ILogger logger = GlobalLog.getLogger(UITask.class);

    private String taskName;
    private long checkTimeoutMs = 200L;

    private Runnable runnable;

    private Callable<T> callable;
    private T result;


    public UITask(ExecutorService execsvc, String taskName, Runnable runnable) {

        this.taskName = taskName;

        this.runnable = runnable;

    }


    public UITask(ExecutorService execsvc, String taskName, Callable<T> callable) {

        this.taskName = taskName;

        this.callable = callable;

    }


    public void setCheckTimeout(long millis) {

        this.checkTimeoutMs = millis;

    }


    public long setCheckTimeout() {

        return this.checkTimeoutMs;

    }


    public T getResult() {

        return (T) this.result;

    }


    public void run(final ITaskProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        if (this.taskName != null) {

            monitor.setTaskName(this.taskName);

        }


        IProgressCallback callback = new IProgressCallback() {

            public void progress(long current, long total) {

                monitor.progress(current, total);

            }

        };

        if ((this.callable instanceof CallableWithProgressCallback)) {

            ((CallableWithProgressCallback) this.callable).setCallback(callback);

        }

        if ((this.runnable instanceof RunnableWithProgressCallback)) {

            ((RunnableWithProgressCallback) this.runnable).setCallback(callback);

        }

        ThreadEx<T> t;

        if (this.runnable != null) {

            t = new ThreadEx(this.runnable);

        } else {

            t = new ThreadEx(this.callable);

        }

        t.start();


        for (; ; ) {

            try {

                if (this.runnable != null) {

                    t.get(this.checkTimeoutMs);

                } else {

                    this.result = t.get(this.checkTimeoutMs);

                }

            } catch (InterruptedException e) {

                throw e;

            } catch (ExecutionException e) {

                throw new InvocationTargetException(e.getCause());

            } catch (TimeoutException localTimeoutException) {
            }


            if (!t.isAlive()) {

                break;

            }


            if ((monitor.isCanceled()) && (!t.isInterrupted())) {

                t.interrupt();

            }

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extension\\ui\UITask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */