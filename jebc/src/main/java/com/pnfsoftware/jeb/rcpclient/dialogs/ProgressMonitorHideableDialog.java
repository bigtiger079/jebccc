package com.pnfsoftware.jeb.rcpclient.dialogs;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ProgressMonitorHideableDialog
        extends ProgressMonitorDialog {
    public ProgressMonitorHideableDialog(Shell parent) {
        super(parent);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createCancelButton(parent);
    }

    protected void okPressed() {
        setVisible(false);
    }

    public void setVisible(boolean visible) {
        getShell().setVisible(visible);
    }

    public boolean isAlive() {
        return (getShell() != null) && (!getShell().isDisposed());
    }
}
