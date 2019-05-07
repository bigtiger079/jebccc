/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
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
/*    */
/*    */ public class ActionDefineProcedureHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 25 */   private static final ILogger logger = GlobalLog.getLogger(ActionDefineProcedureHandler.class);

    /*    */
    /*    */
    public ActionDefineProcedureHandler()
    /*    */ {
        /* 29 */
        super("defineProcedure", "Create Procedure", 80);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 34 */
        return canExecuteAndNativeCheck(this.part, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 39 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 40 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 42 */
        if (!pbcu.isAnalysisCompleted()) {
            /* 43 */
            UI.warn("Please wait for the code analysis to complete before attempting to define new methods.");
            /* 44 */
            return;
            /*    */
        }
        /*    */
        /* 47 */
        int procmode = 0;
        /* 48 */
        INativeContinuousItem item = pbcu.getNativeItemAt(a);
        /* 49 */
        if ((item != null) && ((item instanceof INativeInstructionItem))) {
            /* 50 */
            procmode = ((INativeInstructionItem) item).getInstruction().getProcessorMode();
            /*    */
        }
        /* 52 */
        if (!pbcu.setRoutineAt(a, procmode)) {
            /* 53 */
            UI.error(String.format("Failed to define routine at address %Xh", new Object[]{Long.valueOf(a)}));
            /*    */
        }
        /*    */
        /* 56 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineProcedureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */