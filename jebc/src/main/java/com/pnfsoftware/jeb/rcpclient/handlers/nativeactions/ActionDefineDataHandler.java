
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.core.exceptions.UnitLockedException;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;


public class ActionDefineDataHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionDefineDataHandler.class);


    public ActionDefineDataHandler() {

        super("defineData", "Create Simple Data", 68);

    }


    public boolean canExecute() {

        return canExecuteAndNativeCheck(this.part, true);

    }


    public void execute() {

        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);

        long a = getActiveMemoryAddress(this.part, pbcu);


        INativeType primType = null;

        boolean typeSet = false;

        INativeType type = pbcu.getDataTypeAt(a);

        while (!typeSet) {

            type = NativeActionUtil.rotateType(pbcu.getTypeManager(), type);

            if (primType == type) {

                break;

            }


            if (primType == null) {

                primType = type;

            }

            try {

                typeSet = pbcu.setDataTypeAt(a, type);

            } catch (UnitLockedException e) {

                throw e;

            } catch (Exception e) {

                logger.catching(e);

            }

        }


        if (!typeSet) {

            logger.error("Failed to define data at address %Xh", new Object[]{Long.valueOf(a)});

        }


        postExecute(this.shell);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */