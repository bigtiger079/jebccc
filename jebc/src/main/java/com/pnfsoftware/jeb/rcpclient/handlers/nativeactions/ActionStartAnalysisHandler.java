/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;

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
/*    */ public class ActionStartAnalysisHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionStartAnalysisHandler()
    /*    */ {
        /* 20 */
        super("startAnalysis", "Start or resume an analysis", 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 25 */
        if (!canExecuteAndNativeCheck(this.part, false)) {
            /* 26 */
            return false;
            /*    */
        }
        /*    */
        /* 29 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 30 */
        if (pbcu == null) {
            /* 31 */
            return false;
            /*    */
        }
        /*    */
        /* 34 */
        return pbcu.isAnalysisCompleted();
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 39 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /*    */
        /*    */
        /* 42 */
        pbcu.performAnalysis(true, Boolean.valueOf(true), null);
        /*    */
        /* 44 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionStartAnalysisHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */