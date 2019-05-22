package com.pnfsoftware.jeb.rcpclient.extensions.ui;

import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.base.Throwables;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TaskMonitorDialog extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(TaskMonitorDialog.class);
    private String[] labels = {"Please wait...", ""};
    private Label[] wLabels = new Label[this.labels.length];
    private Thread t;
    private Exception ex;
    private long popupTimeout;
    private volatile boolean canceled;

    public TaskMonitorDialog(Shell parent) {
        this(parent, 0L);
    }

    public TaskMonitorDialog(Shell parent, long popupTimeout) {
        super(parent, 67616, "Task", null);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.NONE;
        this.doNotOpenShell = true;
        this.doNotDispatchEvents = true;
        this.popupTimeout = popupTimeout;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 1);
        Composite c = new Composite(parent, 0);
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.minimumWidth = 680;
        data.grabExcessVerticalSpace = true;
        data.minimumHeight = 140;
        c.setLayoutData(data);
        UIUtil.setStandardLayout(c, 1);
        for (int i = 0; i < this.wLabels.length; i++) {
            this.wLabels[i] = new Label(c, 0);
            this.wLabels[i].setText(Strings.safe(this.labels[i]));
            this.wLabels[i].setLayoutData(UIUtil.createGridDataFillHorizontally());
        }
        createButtons(parent, 256, 256);
    }

    protected void onButtonClick(int style) {
        if (style == 256) {
            this.monitor.setCanceled(true);
            getButtonByStyle(256).setEnabled(false);
        } else {
            super.onButtonClick(style);
        }
    }

    ITaskProgressMonitor monitor = new ITaskProgressMonitor() {
        public void setTaskName(String name) {
            updateLabel(0, name);
        }

        public void setSubtaskName(String name) {
            updateLabel(1, name);
        }

        public void progress(long current, long total) {
            if ((total > 0L) && (current >= 0L) && (current <= total)) {
                String text = String.format("[Progress: %.1f%%]", current * 100.0D / total);
                updateLabel(1, text);
            }
        }

        public void setCanceled(boolean value) {
            TaskMonitorDialog.this.canceled = true;
            updateLabel(0, "Cancelling... please wait");
        }

        public boolean isCanceled() {
            return TaskMonitorDialog.this.canceled;
        }

        public void done() {
        }

        private void updateLabel(final int index, final String text) {
            UIExecutor.sync(TaskMonitorDialog.this.getParent().getDisplay(), new UIRunnable() {
                public void runi() {
                    TaskMonitorDialog.this.labels[index] = text;
                    Label wLabel = TaskMonitorDialog.this.wLabels[index];
                    if ((wLabel != null) && (!wLabel.isDisposed())) {
                        wLabel.setText(Strings.safe(text));
                    }
                }
            });
        }
    };

    public void run(final UITask<?> task) throws InvocationTargetException, InterruptedException {
        if (Display.getCurrent() == null) {
            throw new RuntimeException("TaskMonitorDialog.run() must be called from the UI thread");
        }
        this.t = ThreadUtil.start(new Runnable() {
            public void run() {
                try {
                    task.run(TaskMonitorDialog.this.monitor);
                } catch (Exception e) {
                    TaskMonitorDialog.this.ex = e;
                }
            }
        });
        super.open();
        long t0 = System.currentTimeMillis();
        boolean hiddenShell = false;
        Rectangle bounds0 = null;
        bounds0 = this.shell.getBounds();
        this.shell.setBounds(0, -50, 1, 1);
        hiddenShell = true;
        this.shell.open();
        Display display = this.shell.getDisplay();
        while (!this.shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                this.t.join(20L);
                if (!this.t.isAlive()) {
                    this.shell.close();
                    break;
                }
                if ((hiddenShell) && (System.currentTimeMillis() - t0 >= this.popupTimeout)) {
                    Rectangle displaybounds = this.shell.getDisplay().getClientArea();
                    int offx = 0;
                    int offy = 0;
                    Composite parent = this.shell.getParent();
                    if (parent != null) {
                        Rectangle r = parent.getBounds();
                        offx = r.x + r.width / 2 - (displaybounds.x + displaybounds.width / 2);
                        offy = r.y + r.height / 2 - (displaybounds.y + displaybounds.height / 2);
                    }
                    this.shell.setBounds(displaybounds.x + (displaybounds.width - bounds0.width) / 2 + offx, displaybounds.y + (displaybounds.height - bounds0.height) / 2 + offy, bounds0.width, bounds0.height);
                    hiddenShell = false;
                }
            }
        }
        if ((this.ex instanceof InvocationTargetException)) {
            throw ((InvocationTargetException) this.ex);
        }
        if ((this.ex instanceof InterruptedException)) {
            throw ((InterruptedException) this.ex);
        }
        if (this.ex != null) {
            Throwables.rethrowUnchecked(this.ex);
        }
    }
}


