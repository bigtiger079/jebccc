
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

import java.util.concurrent.Callable;


public class ActionStopAnalysisHandler
        extends NativeCodeBaseHandler {

    public ActionStopAnalysisHandler() {

        super("stopAnalysis", "Interrupt the analysis", 0);

    }


    public boolean canExecute() {

        if (!canExecuteAndNativeCheck(this.part, false)) {

            return false;

        }


        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);

        if (pbcu == null) {

            return false;

        }


        return !pbcu.isAnalysisCompleted();

    }


    public void execute() {

        final INativeCodeAnalyzer<?> gca = getNativeCodeUnit(this.part).getCodeAnalyzer();


        gca.requestAnalysisInterruption();


        boolean success = ((Boolean) this.context.executeTaskWithPopupDelay(1000, "Please wait...", false, new Callable() {

            public Boolean call() {

                while (gca.isAnalyzing()) {

                    try {

                        Thread.sleep(100L);

                    } catch (InterruptedException e) {

                        return Boolean.valueOf(false);

                    }

                }

                return Boolean.valueOf(true);

            }

        })).booleanValue();

        if (!success) {

            UI.error("Not interrupted!");

        }


        postExecute(this.shell);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionStopAnalysisHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */