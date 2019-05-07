/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.core.IEnginesContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.ListTypelibsDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

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
/*    */ public class FileEnginesTypelibsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileEnginesTypelibsHandler()
    /*    */ {
        /* 22 */
        super(null, "Type Libraries...", null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 27 */
        return this.context.getEnginesContext() != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 32 */
        IEnginesContext engctx = this.context.getEnginesContext();
        /* 33 */
        if (engctx == null) {
            /* 34 */
            return;
            /*    */
        }
        /*    */
        /* 37 */
        ListTypelibsDialog dlg = new ListTypelibsDialog(this.shell, this.context);
        /* 38 */
        dlg.setInput(engctx.getTypeLibraryService());
        /* 39 */
        dlg.open();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesTypelibsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */