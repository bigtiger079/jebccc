/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.io.File;
/*    */ import org.eclipse.swt.widgets.FileDialog;
/*    */ import org.eclipse.swt.widgets.Shell;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class FileScriptsExecuteHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 29 */   private static final ILogger logger = GlobalLog.getLogger(FileScriptsExecuteHandler.class);

    /*    */
    /*    */
    public FileScriptsExecuteHandler() {
        /* 32 */
        super("runScript", S.s(554), null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 37 */
        return this.context.getEnginesContext() != null;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 44 */
        String path = askForScriptPath(this.shell, this.context);
        /* 45 */
        if (path != null) {
            /* 46 */
            this.context.executeScript(this.shell, path);
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    static String askForScriptPath(Shell shell, RcpClientContext context) {
        /* 51 */
        FileDialog dlg = new FileDialog(shell, 4096);
        /* 52 */
        dlg.setText(S.s(318));
        /* 53 */
        dlg.setFilterExtensions(new String[]{"*.py", "*.*"});
        /* 54 */
        dlg.setFilterNames(new String[]{S.s(674) + " (*.py)", S.s(55) + " (*.*)"});
        /*    */
        try {
            /* 56 */
            String defaultDir = context.getDefaultPathForDialog("dlgJebFileExecuteScript");
            /* 57 */
            if ((defaultDir == null) || (!new File(defaultDir).exists())) {
                /* 58 */
                defaultDir = new File(context.getScriptsDirectory()).getCanonicalPath();
                /*    */
            }
            /* 60 */
            dlg.setFilterPath(defaultDir);
            /*    */
        }
        /*    */ catch (Exception localException) {
        }
        /*    */
        /*    */
        /* 65 */
        String path = dlg.open();
        /* 66 */
        if (path != null) {
            /* 67 */
            String newDefaultDir = new File(path).getParent();
            /* 68 */
            context.setDefaultPathForDialog("dlgJebFileExecuteScript", newDefaultDir);
            /*    */
        }
        /* 70 */
        return path;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileScriptsExecuteHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */