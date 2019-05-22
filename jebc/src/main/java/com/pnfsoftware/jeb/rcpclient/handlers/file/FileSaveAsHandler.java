package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class FileSaveAsHandler extends JebBaseHandler {
    public FileSaveAsHandler() {
        super(null, S.s(557), "Save the current project to a JDB2 database", null);
        setAccelerator(SWT.MOD1 | SWT.MOD2 | 0x53);
    }

    public boolean canExecute() {
        return this.context.getOpenedProject() != null;
    }

    public void execute() {
        if (Licensing.isDemoBuild()) {
            MessageDialog.openWarning(this.shell, S.s(249), S.s(267));
            return;
        }
        FileDialog dlg = new FileDialog(this.shell, 8192);
        dlg.setFilterExtensions(new String[]{"*.jdb2"});
        String path = dlg.open();
        this.context.saveOpenedProject(this.shell, true, path);
    }
}


