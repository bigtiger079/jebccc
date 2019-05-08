
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;

public class ActionStartAnalysisHandler
        extends NativeCodeBaseHandler {
    public ActionStartAnalysisHandler() {
        super("startAnalysis", "Start or resume an analysis", 0);
    }

    public boolean canExecute() {
        if (!canExecuteAndNativeCheck(this.part, false)) {
            return false;
        }
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        if (pbcu == null) {
            return false;
        }
        return pbcu.isAnalysisCompleted();
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        pbcu.performAnalysis(true, Boolean.valueOf(true), null);
        postExecute(this.shell);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionStartAnalysisHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */