package com.pnfsoftware.jeb.rcpclient;

import java.util.concurrent.Callable;

public interface IGraphicalTaskExecutor {
    public abstract <T> T executeTask(String paramString, Callable<T> paramCallable);

    public abstract <T> T executeTask(String paramString, boolean paramBoolean, Callable<T> paramCallable);

    public abstract boolean executeTask(String paramString, Runnable paramRunnable);

    public abstract boolean executeTask(String paramString, boolean paramBoolean, Runnable paramRunnable);

    public abstract <T> T executeTaskWithPopupDelay(int paramInt, String paramString, boolean paramBoolean, Callable<T> paramCallable);

    public abstract void executeTaskWithPopupDelay(int paramInt, String paramString, boolean paramBoolean, Runnable paramRunnable);
}


