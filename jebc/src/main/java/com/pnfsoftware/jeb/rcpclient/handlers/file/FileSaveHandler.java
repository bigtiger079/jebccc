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
/*    */ public class FileSaveHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileSaveHandler()
    /*    */ {
        /* 25 */
        super(null, S.s(556), "Save the current project to a JDB2 database", "eclipse/save_edit.png");
        /* 26 */
        setAccelerator(SWT.MOD1 | 0x53);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 31 */
        return this.context.getOpenedProject() != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 36 */
        if (Licensing.isDemoBuild()) {
            /* 37 */
            MessageDialog.openWarning(this.shell, S.s(249), S.s(267));
            /* 38 */
            return;
            /*    */
        }
        /*    */
        /* 41 */
        this.context.saveOpenedProject(this.shell, true, null);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileSaveHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */