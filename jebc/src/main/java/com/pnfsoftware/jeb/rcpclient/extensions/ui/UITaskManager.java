package com.pnfsoftware.jeb.rcpclient.extensions.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class UITaskManager {
    Display display;
    ExecutorService execsvc;

    public UITaskManager(Display display, ExecutorService execsvc) {
        this.display = display;
        this.execsvc = execsvc;
    }

    public void create(Shell shell, String caption, Runnable runnable, long popupDelayMs) throws InvocationTargetException, InterruptedException {
        TaskMonitorDialog dlg = new TaskMonitorDialog(shell, popupDelayMs);
        UITask<?> job = new UITask(this.execsvc, caption, runnable);
        dlg.run(job);
    }

    public <T> T create(Shell shell, String caption, Callable<T> callable, long popupDelayMs) throws InvocationTargetException, InterruptedException {
        TaskMonitorDialog dlg = new TaskMonitorDialog(shell, popupDelayMs);
        UITask<T> job = new UITask<>(this.execsvc, caption, callable);
        dlg.run(job);
        return job.getResult();
    }
}


