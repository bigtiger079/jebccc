/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.ListParsersDialog;
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
/*    */
/*    */ public class FileEnginesParsersHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileEnginesParsersHandler()
    /*    */ {
        /* 22 */
        super(null, "Parsers...", null, null);
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
        ListParsersDialog dlg = new ListParsersDialog(this.shell);
        /* 33 */
        if (this.context.getOpenedProject() != null) {
            /* 34 */
            dlg.setInput(this.context.getOpenedProject());
            /*    */
        }
        /*    */
        else {
            /* 37 */
            dlg.setInput(this.context.getEnginesContext());
            /*    */
        }
        /* 39 */
        dlg.open();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesParsersHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */