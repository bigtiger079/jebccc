package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class FileOpenHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileOpenHandler.class);

    public FileOpenHandler() {
        super(null, S.s(528), "Open a new file or an existing project", "eclipse/fldr_obj.png");
        setAccelerator(SWT.MOD1 | 0x4F);
    }

    public boolean canExecute() {
        return this.shell != null;
    }

    public void execute() {
        FileDialog dlg = new FileDialog(this.shell, 4096);
        dlg.setText(S.s(612));
        dlg.setFilterPath(this.context.getDefaultPathForDialog("dlgJebFileOpen"));
        String path = dlg.open();
        if (path != null) {
            String newDefaultDir = new File(path).getParent();
            this.context.setDefaultPathForDialog("dlgJebFileOpen", newDefaultDir);
            this.context.loadInputAsProject(this.shell, path);
        }
    }
}


