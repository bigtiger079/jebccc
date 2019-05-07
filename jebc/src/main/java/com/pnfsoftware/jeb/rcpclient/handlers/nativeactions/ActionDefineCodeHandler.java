/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.CodeSetupInformation;
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
/*    */ public class ActionDefineCodeHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 22 */   private static final ILogger logger = GlobalLog.getLogger(ActionDefineCodeHandler.class);

    /*    */
    /*    */
    public ActionDefineCodeHandler()
    /*    */ {
        /* 26 */
        super("defineCode", "Disassemble", 67);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 31 */
        return canExecuteAndNativeCheck(this.part, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 36 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 37 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 39 */
        CodeSetupInformation info = new CodeSetupInformation(a, 0, 1);
        /*    */
        /* 41 */
        if (!ActionEditCodeHandler.disassemble(this.shell, pbcu, info)) {
            /* 42 */
            logger.error("Failed to define code at address %Xh", new Object[]{Long.valueOf(a)});
            /*    */
        }
        /*    */
        /* 45 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineCodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */