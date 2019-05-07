package com.pnfsoftware.jeb.rcpclient;

import java.util.concurrent.Callable;

public abstract interface IGraphicalTaskExecutor {
    public abstract <T> T executeTask(String paramString, Callable<T> paramCallable);

    public abstract <T> T executeTask(String paramString, boolean paramBoolean, Callable<T> paramCallable);

    public abstract boolean executeTask(String paramString, Runnable paramRunnable);

    public abstract boolean executeTask(String paramString, boolean paramBoolean, Runnable paramRunnable);

    public abstract <T> T executeTaskWithPopupDelay(int paramInt, String paramString, boolean paramBoolean, Callable<T> paramCallable);

    public abstract void executeTaskWithPopupDelay(int paramInt, String paramString, boolean paramBoolean, Runnable paramRunnable);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\IGraphicalTaskExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */