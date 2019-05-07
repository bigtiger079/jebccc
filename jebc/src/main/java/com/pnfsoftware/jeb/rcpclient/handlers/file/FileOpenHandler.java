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
/*    */ import org.eclipse.swt.SWT;
/*    */ import org.eclipse.swt.widgets.FileDialog;

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
/*    */ public class FileOpenHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 27 */   private static final ILogger logger = GlobalLog.getLogger(FileOpenHandler.class);

    /*    */
    /*    */
    public FileOpenHandler() {
        /* 30 */
        super(null, S.s(528), "Open a new file or an existing project", "eclipse/fldr_obj.png");
        /* 31 */
        setAccelerator(SWT.MOD1 | 0x4F);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 36 */
        return this.shell != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 41 */
        FileDialog dlg = new FileDialog(this.shell, 4096);
        /* 42 */
        dlg.setText(S.s(612));
        /* 43 */
        dlg.setFilterPath(this.context.getDefaultPathForDialog("dlgJebFileOpen"));
        /*    */
        /* 45 */
        String path = dlg.open();
        /* 46 */
        if (path != null) {
            /* 47 */
            String newDefaultDir = new File(path).getParent();
            /* 48 */
            this.context.setDefaultPathForDialog("dlgJebFileOpen", newDefaultDir);
            /*    */
            /* 50 */
            this.context.loadInputAsProject(this.shell, path);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileOpenHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */