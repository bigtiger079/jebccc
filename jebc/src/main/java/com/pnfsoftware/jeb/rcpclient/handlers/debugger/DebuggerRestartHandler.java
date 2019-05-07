/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;

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
/*    */ public class DebuggerRestartHandler
        /*    */ extends DebuggerBaseHandler
        /*    */ {
    /*    */
    public DebuggerRestartHandler()
    /*    */ {
        /* 22 */
        super("dbgRestart", S.s(549), "Attempt to restart the debugging session", null, 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 27 */
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        /* 28 */
        return (dbg != null) && (!dbg.isAttached()) && (canAttachDebugger(this.part));
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 33 */
        PartManager pman = this.context.getPartManager();
        /*    */
        /* 35 */
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        /* 36 */
        boolean success = dbg.restart();
        /* 37 */
        if (!success)
            /*    */ {
            /* 39 */
            return;
            /*    */
        }
        /*    */
        /* 42 */
        restoreUIBreakpoints(dbg);
        /*    */
        /* 44 */
        this.context.setDebuggingMode(true);
        /* 45 */
        pman.create(dbg, true);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerRestartHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */