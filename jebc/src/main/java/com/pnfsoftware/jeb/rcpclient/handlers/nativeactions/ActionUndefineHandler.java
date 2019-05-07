/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
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
/*    */ public class ActionUndefineHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 22 */   private static final ILogger logger = GlobalLog.getLogger(ActionUndefineHandler.class);

    /*    */
    /*    */
    public ActionUndefineHandler() {
        /* 25 */
        super("undefine", S.s(580), 85);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 30 */
        return canExecuteAndNativeCheck(this.part, true);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecuteAt(INativeCodeUnit<?> pbcu, long memAddress)
    /*    */ {
        /* 35 */
        if (memAddress == -1L) {
            /* 36 */
            return false;
            /*    */
        }
        /* 38 */
        INativeItem item = pbcu.getNativeItemAt(memAddress);
        /* 39 */
        return item != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 44 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 45 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 47 */
        if (!pbcu.undefineItem(a)) {
            /* 48 */
            logger.error("Failed to undefine item at address %Xh", new Object[]{Long.valueOf(a)});
            /*    */
        }
        /*    */
        /* 51 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionUndefineHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */