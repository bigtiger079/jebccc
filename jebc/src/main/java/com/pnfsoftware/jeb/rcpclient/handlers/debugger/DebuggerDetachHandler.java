/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
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
/*    */ public class DebuggerDetachHandler
        /*    */ extends DebuggerBaseHandler
        /*    */ {
    /* 23 */   private static final ILogger logger = GlobalLog.getLogger(DebuggerDetachHandler.class);

    /*    */
    /*    */
    public DebuggerDetachHandler() {
        /* 26 */
        super("dbgDetach", S.s(484), "Detach from the actively debugged target", null, 0);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 31 */
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        /*    */
        /* 33 */
        boolean success = dbg.detach();
        /* 34 */
        if (!success) {
            /* 35 */
            UI.error("The target could not be detached");
            /* 36 */
            return;
            /*    */
        }
        /*    */
        /*    */
        /* 40 */
        this.context.setDebuggingMode(false);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerDetachHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */