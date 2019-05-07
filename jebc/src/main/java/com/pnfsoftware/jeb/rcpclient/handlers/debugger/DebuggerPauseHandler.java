/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;

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
/*    */ public class DebuggerPauseHandler
        /*    */ extends DebuggerBaseHandler
        /*    */ {
    /*    */
    public DebuggerPauseHandler()
    /*    */ {
        /* 20 */
        super("dbgPause", S.s(538), null, "eclipse/suspend_co.png", 0);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 26 */
        return (isDebuggerAttached(this.part)) && (!canStepOperation(this.part));
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 31 */
        getCurrentDebugger(this.part).pause();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerPauseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */