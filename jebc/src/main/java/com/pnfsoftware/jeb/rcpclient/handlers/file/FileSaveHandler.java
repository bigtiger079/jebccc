package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

public class FileSaveHandler extends JebBaseHandler {
    public FileSaveHandler() {
        super(null, S.s(556), "Save the current project to a JDB2 database", "eclipse/save_edit.png");
        setAccelerator(SWT.MOD1 | 0x53);
    }

    public boolean canExecute() {
        return this.context.getOpenedProject() != null;
    }

    public void execute() {
        if (Licensing.isDemoBuild()) {
            MessageDialog.openWarning(this.shell, S.s(249), S.s(267));
            return;
        }
        this.context.saveOpenedProject(this.shell, true, null);
    }
}


