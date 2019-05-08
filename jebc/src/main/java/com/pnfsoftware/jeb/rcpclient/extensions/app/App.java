package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.util.base.Throwables;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class App {
    private static final ILogger logger = GlobalLog.getLogger(App.class);
    Display display;
    String appname;
    Shell shell;
    boolean hasToolbar;
    MenuManager menuManager;
    Composite toolbarContainer;
    ToolBarManager toolbarManager;
    Dock dock;
    StatusLineManager statusManager;

    public App(String name, int style) {
        onPreDisplayCreation(name, "");
        this.display = Display.getDefault();
        this.appname = name;
        this.hasToolbar = ((style & 0x4) != 0);
    }

    protected void onPreDisplayCreation(String name, String version) {
        Display.setAppName(name);
        Display.setAppVersion(version);
    }

    public void build() {
        this.shell = new Shell(this.display);
        this.shell.setText(Strings.safe(this.appname));
        this.shell.setLayout(new FormLayout());
        customizeShell(this.shell);
        this.menuManager = new MenuManager();
        Menu menu = this.menuManager.createMenuBar(this.shell);
        this.shell.setMenuBar(menu);
        this.toolbarContainer = new Composite(this.shell, 0);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 0);
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        this.toolbarContainer.setLayoutData(formData);
        if (this.hasToolbar) {
            GridLayout toolbarContainerLayout = new GridLayout();
            toolbarContainerLayout.marginTop = 2;
            toolbarContainerLayout.marginBottom = 3;
            this.toolbarContainer.setLayout(toolbarContainerLayout);
            this.toolbarManager = new ToolBarManager(8388928);
            ToolBar toolbar = this.toolbarManager.createControl(this.toolbarContainer);
            ToolItem item = new ToolItem(toolbar, 8);
            item.setText(" ");
            toolbar.pack();
        }
        this.statusManager = new StatusLineManager();
        Control statusLine = this.statusManager.createControl(this.shell);
        this.statusManager.setMessage("");
        formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.bottom = new FormAttachment(100, 0);
        statusLine.pack();
        statusLine.setLayoutData(formData);
        this.dock = new Dock(this.shell, true);
        formData = new FormData();
        formData.top = new FormAttachment(this.toolbarContainer);
        formData.bottom = new FormAttachment(statusLine);
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        this.dock.setLayoutData(formData);
        this.shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                App.logger.i("Primary shell is about to close", new Object[0]);
                if (!App.this.onApplicationCloseAttempt()) {
                    e.doit = false;
                }
            }
        });
        this.shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                App.logger.i("Primary shell is being disposed", new Object[0]);
            }
        });
        buildShell(this.shell);
        buildDock(this.dock);
        buildMenu(this.menuManager);
        if (this.toolbarManager != null) {
            buildToolbar(this.toolbarManager);
        }
        onApplicationBuilt();
    }

    protected void customizeShell(Shell shell) {
    }

    public String[] getArguments() {
        return new String[0];
    }

    public void run() {
        this.shell.open();
        try {
            onApplicationReady();
        } catch (Exception e) {
            logger.catching(e);
            System.exit(-1);
        }
        while (!this.shell.isDisposed()) {
            boolean canSleep = false;
            try {
                canSleep = !this.display.readAndDispatch();
            } catch (Exception e) {
                if (!onApplicationException(e)) {
                    try {
                        this.shell.dispose();
                    } catch (Exception localException1) {
                    }
                    break;
                }
            }
            if (canSleep) {
                this.display.sleep();
            }
        }
        onApplicationClose();
        try {
            this.display.dispose();
        } catch (Exception localException2) {
        }
        System.exit(0);
    }

    public void close() {
        if (this.shell == null) {
            throw new IllegalStateException();
        }
        this.shell.close();
    }

    protected void buildShell(Shell shell) {
    }

    protected void buildMenu(MenuManager menuManager) {
    }

    protected void buildToolbar(ToolBarManager toolbarManager) {
    }

    protected void buildDock(Dock dock) {
    }

    public Display getDisplay() {
        return this.display;
    }

    public Shell getPrimaryShell() {
        return this.shell;
    }

    public MenuManager getMenuManager() {
        return this.menuManager;
    }

    public ToolBarManager getToolbarManager() {
        return this.toolbarManager;
    }

    public Dock getDock() {
        return this.dock;
    }

    public StatusLineManager getStatusManager() {
        return this.statusManager;
    }

    public boolean onApplicationException(Exception e) {
        String msg = String.format("The following error was reported on the UI thread:\n\n%s", new Object[]{Throwables.formatStacktraceShort(e)});
        MessageDialog.openError(null, "Error", msg);
        return true;
    }

    public void onApplicationBuilt() {
    }

    public void onApplicationReady() {
    }

    public boolean onApplicationCloseAttempt() {
        return true;
    }

    public void onApplicationClose() {
    }

    public boolean isToolbarVisible() {
        return this.toolbarContainer.isVisible();
    }

    public void setToolbarVisibility(boolean visible) {
        setBarVisibility(this.toolbarContainer, visible);
    }

    public boolean isStatusVisible() {
        return this.statusManager.getControl().isVisible();
    }

    public void setStatusVisibility(boolean visible) {
        setBarVisibility(this.statusManager.getControl(), visible);
    }

    private void setBarVisibility(Control bar, boolean visible) {
        if (bar.isVisible() == visible) {
            return;
        }
        bar.setVisible(visible);
        if (!visible) {
            ((FormData) bar.getLayoutData()).height = 0;
        } else {
            int height = bar.computeSize(-1, -1, true).y;
            ((FormData) bar.getLayoutData()).height = height;
        }
        this.shell.layout();
    }
}