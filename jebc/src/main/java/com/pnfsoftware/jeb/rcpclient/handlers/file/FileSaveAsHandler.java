/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.client.Licensing;
/*    */ import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import org.eclipse.jface.dialogs.MessageDialog;
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
/*    */ public class FileSaveAsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileSaveAsHandler()
    /*    */ {
        /* 26 */
        super(null, S.s(557), "Save the current project to a JDB2 database", null);
        /* 27 */
        setAccelerator(SWT.MOD1 | SWT.MOD2 | 0x53);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 32 */
        return this.context.getOpenedProject() != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 37 */
        if (Licensing.isDemoBuild()) {
            /* 38 */
            MessageDialog.openWarning(this.shell, S.s(249), S.s(267));
            /* 39 */
            return;
            /*    */
        }
        /*    */
        /* 42 */
        FileDialog dlg = new FileDialog(this.shell, 8192);
        /* 43 */
        dlg.setFilterExtensions(new String[]{"*.jdb2"});
        /* 44 */
        String path = dlg.open();
        /* 45 */
        this.context.saveOpenedProject(this.shell, true, path);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileSaveAsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */