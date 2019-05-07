
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;


public class ActionDefineStringHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionDefineStringHandler.class);


    public ActionDefineStringHandler() {

        super("defineString", "Create String", 65);

    }


    public boolean canExecute() {

        return canExecuteAndNativeCheck(this.part, true);

    }


    public void execute() {

        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);

        long a = getActiveMemoryAddress(this.part, pbcu);


        if (!pbcu.setStringAt(a, -1L, null, -1, -1)) {

            logger.error("Failed to define string at address %Xh", new Object[]{Long.valueOf(a)});

        }


        postExecute(this.shell);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineStringHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */