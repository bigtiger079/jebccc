
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;


public class ActionDefineProcedureHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionDefineProcedureHandler.class);


    public ActionDefineProcedureHandler() {

        super("defineProcedure", "Create Procedure", 80);

    }


    public boolean canExecute() {

        return canExecuteAndNativeCheck(this.part, true);

    }


    public void execute() {

        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);

        long a = getActiveMemoryAddress(this.part, pbcu);


        if (!pbcu.isAnalysisCompleted()) {

            UI.warn("Please wait for the code analysis to complete before attempting to define new methods.");

            return;

        }


        int procmode = 0;

        INativeContinuousItem item = pbcu.getNativeItemAt(a);

        if ((item != null) && ((item instanceof INativeInstructionItem))) {

            procmode = ((INativeInstructionItem) item).getInstruction().getProcessorMode();

        }

        if (!pbcu.setRoutineAt(a, procmode)) {

            UI.error(String.format("Failed to define routine at address %Xh", new Object[]{Long.valueOf(a)}));

        }


        postExecute(this.shell);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineProcedureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */