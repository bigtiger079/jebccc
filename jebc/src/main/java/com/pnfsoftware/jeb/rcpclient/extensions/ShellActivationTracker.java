package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ShellActivationTracker implements Listener {
    private static final ILogger logger = GlobalLog.getLogger(ShellActivationTracker.class);
    private Display display;
    private Shell shell;
    private Shell mainShell;

    public ShellActivationTracker(Display display) {
        this.display = display;
        set(display.getActiveShell());
        display.addFilter(26, this);
    }

    public void dispose() {
        this.display.removeFilter(26, this);
    }

    public void handleEvent(Event event) {
        if ((event.widget instanceof Shell)) {
            set((Shell) event.widget);
        }
    }

    public void setMainShell(Shell mainShell) {
        this.mainShell = mainShell;
    }

    public Shell getMainShell() {
        return this.mainShell;
    }

    private void set(Shell shell) {
        if (shell == null) {
            logger.i("Shell is null");
        } else if (this.shell == null) {
            logger.i("Shell was null, now: %s", shell);
        }
        this.shell = shell;
    }

    public Shell get() {
        if (!UIUtil.isUIThread(this.display)) {
            return null;
        }
        Shell r = this.display.getActiveShell();
        if (r == null) {
            r = this.shell;
        }
        if ((r == null) || (r.isDisposed())) {
            r = this.mainShell;
        }
        return r;
    }
}


