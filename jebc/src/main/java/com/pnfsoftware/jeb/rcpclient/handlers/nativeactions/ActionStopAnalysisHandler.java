/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import java.util.concurrent.Callable;

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
/*    */ public class ActionStopAnalysisHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionStopAnalysisHandler()
    /*    */ {
        /* 24 */
        super("stopAnalysis", "Interrupt the analysis", 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 29 */
        if (!canExecuteAndNativeCheck(this.part, false)) {
            /* 30 */
            return false;
            /*    */
        }
        /*    */
        /* 33 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 34 */
        if (pbcu == null) {
            /* 35 */
            return false;
            /*    */
        }
        /*    */
        /* 38 */
        return !pbcu.isAnalysisCompleted();
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 43 */
        final INativeCodeAnalyzer<?> gca = getNativeCodeUnit(this.part).getCodeAnalyzer();
        /*    */
        /* 45 */
        gca.requestAnalysisInterruption();
        /*    */
        /* 47 */
        boolean success = ((Boolean) this.context.executeTaskWithPopupDelay(1000, "Please wait...", false, new Callable()
                /*    */ {
            /*    */
            public Boolean call() {
                /* 50 */
                while (gca.isAnalyzing()) {
                    /*    */
                    try {
                        /* 52 */
                        Thread.sleep(100L);
                        /*    */
                    }
                    /*    */ catch (InterruptedException e) {
                        /* 55 */
                        return Boolean.valueOf(false);
                        /*    */
                    }
                    /*    */
                }
                /* 58 */
                return Boolean.valueOf(true);
                /*    */
            }
            /*    */
        })).booleanValue();
        /* 61 */
        if (!success) {
            /* 62 */
            UI.error("Not interrupted!");
            /*    */
        }
        /*    */
        /* 65 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionStopAnalysisHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */