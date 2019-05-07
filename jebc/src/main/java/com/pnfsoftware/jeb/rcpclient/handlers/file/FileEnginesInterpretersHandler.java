/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.core.IEnginesContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
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
/*    */ public class FileEnginesInterpretersHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileEnginesInterpretersHandler()
    /*    */ {
        /* 22 */
        super(null, "Interpreters...", null, null);
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
        UI.info("Not available at this time.");
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesInterpretersHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */