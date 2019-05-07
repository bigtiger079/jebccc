/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
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
/*    */ public class ActionDefineStringHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 20 */   private static final ILogger logger = GlobalLog.getLogger(ActionDefineStringHandler.class);

    /*    */
    /*    */
    public ActionDefineStringHandler()
    /*    */ {
        /* 24 */
        super("defineString", "Create String", 65);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 29 */
        return canExecuteAndNativeCheck(this.part, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 34 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 35 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 37 */
        if (!pbcu.setStringAt(a, -1L, null, -1, -1)) {
            /* 38 */
            logger.error("Failed to define string at address %Xh", new Object[]{Long.valueOf(a)});
            /*    */
        }
        /*    */
        /* 41 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineStringHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */