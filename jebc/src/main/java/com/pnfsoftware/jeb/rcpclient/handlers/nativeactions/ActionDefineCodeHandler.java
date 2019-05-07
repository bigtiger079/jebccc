
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.CodeSetupInformation;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;


public class ActionDefineCodeHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionDefineCodeHandler.class);


    public ActionDefineCodeHandler() {

        super("defineCode", "Disassemble", 67);

    }


    public boolean canExecute() {

        return canExecuteAndNativeCheck(this.part, true);

    }


    public void execute() {

        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);

        long a = getActiveMemoryAddress(this.part, pbcu);


        CodeSetupInformation info = new CodeSetupInformation(a, 0, 1);


        if (!ActionEditCodeHandler.disassemble(this.shell, pbcu, info)) {

            logger.error("Failed to define code at address %Xh", new Object[]{Long.valueOf(a)});

        }


        postExecute(this.shell);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineCodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */