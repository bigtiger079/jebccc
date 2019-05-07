/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;

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
/*    */ public class DebuggerTerminateHandler
        /*    */ extends DebuggerBaseHandler
        /*    */ {
    /*    */
    public DebuggerTerminateHandler()
    /*    */ {
        /* 22 */
        super("dbgTerminate", S.s(577), null, "eclipse/terminate_co.png", 0);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 27 */
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        /*    */
        /* 29 */
        boolean success = dbg.terminate();
        /* 30 */
        if (!success) {
            /* 31 */
            UI.error("The target could not be detached");
            /* 32 */
            return;
            /*    */
        }
        /*    */
        /*    */
        /* 36 */
        this.context.setDebuggingMode(false);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerTerminateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */