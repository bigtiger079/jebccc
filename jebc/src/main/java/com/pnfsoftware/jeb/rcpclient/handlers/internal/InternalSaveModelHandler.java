/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.internal;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;

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
/*    */ public class InternalSaveModelHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 21 */   private static final ILogger logger = GlobalLog.getLogger(InternalSaveModelHandler.class);

    /*    */
    /*    */
    public InternalSaveModelHandler() {
        /* 24 */
        super(null, "Save Model", null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 29 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public void execute() {
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\internal\InternalSaveModelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */