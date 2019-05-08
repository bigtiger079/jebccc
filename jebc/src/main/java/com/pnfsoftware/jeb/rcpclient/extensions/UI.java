package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.IWidgetManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ui.UITaskManager;
import com.pnfsoftware.jeb.util.base.OSType;
import com.pnfsoftware.jeb.util.concurrent.DaemonExecutors;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class UI {
    private static final ILogger logger = GlobalLog.getLogger(UI.class);
    private static Display display;
    private static ExecutorService execsvc;
    private static ShellActivationTracker shellTracker;
    private static UITaskManager taskManager;
    private static boolean[] kbmodstates = new boolean[4];
    public static final String MOD1;
    public static final String MOD2;

    static {
        if (OSType.determine().isMac()) {
            MOD1 = "⌘";
            MOD2 = "⇧";
            MOD3 = "⌥";
            MOD4 = "⌃";
        } else {
            MOD1 = "⌃";
            MOD2 = "⇧";
            MOD3 = "⌥";
            MOD4 = "";
        }
    }

    public static void initialize() {
        if ((display != null) && (Display.getCurrent() != display)) {
            throw new IllegalStateException("The UI class was already initialized on another UI thread");
        }
        display = Display.getCurrent();
        if (display == null) {
            throw new RuntimeException("The UI class must be loaded by the UI thread");
        }
        execsvc = DaemonExecutors.newFixedThreadPool(10);
        shellTracker = new ShellActivationTracker(display);
        taskManager = new UITaskManager(display, execsvc);
        Listener kbfilter = new Listener() {
            public void handleEvent(Event event) {
                int i;
                if (event.keyCode == SWT.MOD1) {
                    i = 0;
                } else {
                    if (event.keyCode == SWT.MOD2) {
                        i = 1;
                    } else {
                        if (event.keyCode == SWT.MOD3) {
                            i = 2;
                        } else {
                            if (event.keyCode == SWT.MOD4) i = 3;
                            else {
                                return;
                            }
                        }
                    }
                }
                if (event.type == 1) {
                    UI.kbmodstates[i] = true;
                } else if (event.type == 2) {
                    UI.kbmodstates[i] = false;
                }
            }
        };
        display.addFilter(1, kbfilter);
        display.addFilter(2, kbfilter);
    }

    private static void safeInit() {
        if (display == null) {
            initialize();
        }
    }

    public static Display getDisplay() {
        safeInit();
        return display;
    }

    public static ShellActivationTracker getShellTracker() {
        safeInit();
        return shellTracker;
    }

    public static UITaskManager getTaskManager() {
        safeInit();
        return taskManager;
    }

    public static int getKeyboardModifiersState() {
        int v = 0;
        if (kbmodstates[0]) {
            v |= SWT.MOD1;
        }
        if (kbmodstates[1]) {
            v |= SWT.MOD2;
        }
        if (kbmodstates[2]) {
            v |= SWT.MOD3;
        }
        if (kbmodstates[3]) {
            v |= SWT.MOD4;
        }
        return v;
    }

    public static void log(final int level, Shell shell, final String caption, final String message) {
        safeInit();
        final AtomicBoolean displayed = new AtomicBoolean();
        UIRunnable r = new UIRunnable() {
            public void runi() {
                Shell shell0 = shell != null ? shell : UI.shellTracker.get();
                if (shell0 != null) {
                    displayed.set(true);
                    if (level == 50) {
                        MessageDialog.openError(shell0, caption, message);
                    } else if (level == 40) {
                        MessageDialog.openWarning(shell0, caption, message);
                    } else {
                        MessageDialog.openInformation(shell0, caption, message);
                    }
                }
            }
        };
        Display display = Display.getDefault();
        if (Thread.currentThread() == display.getThread()) {
            r.run();
        } else {
            UIExecutor.sync(display, r);
        }
        if (!displayed.get()) {
            logger.log(level, false, "%s: %s", new Object[]{caption, message});
        }
    }

    public static boolean confirm(Shell shell, final String caption, final String message) {
        safeInit();
        final AtomicBoolean success = new AtomicBoolean();
        UIRunnable r = new UIRunnable() {
            public void runi() {
                Shell shell0 = shell != null ? shell : UI.shellTracker.get();
                if (shell0 != null) {
                    success.set(MessageDialog.openConfirm(shell0, caption, message));
                }
            }
        };
        if (Thread.currentThread() == display.getThread()) {
            r.run();
        } else {
            UIExecutor.sync(display, r);
        }
        return success.get();
    }

    public static void warn(Shell shell, String caption, String message) {
        log(40, shell, caption, message);
    }

    public static void info(Shell shell, String caption, String message) {
        log(30, shell, caption, message);
    }

    public static void info(String message) {
        log(30, null, S.s(384), message);
    }

    public static void warn(String message) {
        log(40, null, S.s(821), message);
    }

    public static void error(Shell shell, String caption, String message) {
        log(50, shell, caption, message);
    }

    public static void error(String message) {
        log(50, null, S.s(304), message);
    }

    public static boolean question(Shell shell, final String caption, final String message) {
        safeInit();
        final AtomicBoolean success = new AtomicBoolean();
        UIRunnable r = new UIRunnable() {
            public void runi() {
                Shell shell0 = shell != null ? shell : UI.shellTracker.get();
                if (shell0 != null) {
                    success.set(MessageDialog.openQuestion(shell0, caption, message));
                }
            }
        };
        if (Thread.currentThread() == display.getThread()) {
            r.run();
        } else {
            UIExecutor.sync(display, r);
        }
        return success.get();
    }

    public static boolean infoOptional(Shell shell, String caption, String message, String widgetName) {
        return popupOptional(shell, 2, caption, message, widgetName);
    }

    public static boolean warnOptional(Shell shell, String caption, String message, String widgetName) {
        return popupOptional(shell, 8, caption, message, widgetName);
    }

    public static final String MOD3;
    public static final String MOD4;

    public static boolean popupOptional(Shell shell, int style, String caption, String message, String widgetName) {
        if (widgetName == null) {
            throw new IllegalArgumentException();
        }
        if (message == null) {
            return false;
        }
        if ((style & 0x2) != 0) {
            logger.info(message, new Object[0]);
        } else if ((style & 0x8) != 0) {
            logger.warn(message, new Object[0]);
        }
        IWidgetManager widgetManager = JebDialog.getStandardWidgetManager();
        if ((widgetManager == null) || (!widgetManager.getShouldShowDialog(widgetName))) {
            return false;
        }
        String title;
        if (caption != null) {
            title = caption;
        } else {
            title = style == 2 ? "Warning" : style == 1 ? "Info" : "";
        }
        new AdaptivePopupDialog(shell, 1, title, message, widgetName).open();
        return true;
    }
}


