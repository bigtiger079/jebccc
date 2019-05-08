
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileScriptsExecuteHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileScriptsExecuteHandler.class);

    public FileScriptsExecuteHandler() {
        super("runScript", S.s(554), null, null);
    }

    public boolean canExecute() {
        return this.context.getEnginesContext() != null;
    }

    public void execute() {
        String path = askForScriptPath(this.shell, this.context);
        if (path != null) {
            this.context.executeScript(this.shell, path);
        }
    }

    static String askForScriptPath(Shell shell, RcpClientContext context) {
        FileDialog dlg = new FileDialog(shell, 4096);
        dlg.setText(S.s(318));
        dlg.setFilterExtensions(new String[]{"*.py", "*.*"});
        dlg.setFilterNames(new String[]{S.s(674) + " (*.py)", S.s(55) + " (*.*)"});
        try {
            String defaultDir = context.getDefaultPathForDialog("dlgJebFileExecuteScript");
            if ((defaultDir == null) || (!new File(defaultDir).exists())) {
                defaultDir = new File(context.getScriptsDirectory()).getCanonicalPath();
            }
            dlg.setFilterPath(defaultDir);
        } catch (Exception localException) {
        }
        String path = dlg.open();
        if (path != null) {
            String newDefaultDir = new File(path).getParent();
            context.setDefaultPathForDialog("dlgJebFileExecuteScript", newDefaultDir);
        }
        return path;
    }
}


