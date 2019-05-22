package com.pnfsoftware.jeb.rcpclient;

import java.util.concurrent.Callable;

public interface IGraphicalTaskExecutor {
    <T> T executeTask(String paramString, Callable<T> paramCallable);

    <T> T executeTask(String paramString, boolean paramBoolean, Callable<T> paramCallable);

    boolean executeTask(String paramString, Runnable paramRunnable);

    boolean executeTask(String paramString, boolean paramBoolean, Runnable paramRunnable);

    <T> T executeTaskWithPopupDelay(int paramInt, String paramString, boolean paramBoolean, Callable<T> paramCallable);

    void executeTaskWithPopupDelay(int paramInt, String paramString, boolean paramBoolean, Runnable paramRunnable);
}


