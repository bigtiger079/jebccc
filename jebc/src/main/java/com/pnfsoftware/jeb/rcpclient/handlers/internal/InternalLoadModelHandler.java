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
/*    */ public class InternalLoadModelHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 21 */   private static final ILogger logger = GlobalLog.getLogger(InternalLoadModelHandler.class);

    /*    */
    /*    */
    public InternalLoadModelHandler() {
        /* 24 */
        super(null, "Load Model", null, null);
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\internal\InternalLoadModelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */