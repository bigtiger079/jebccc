/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.exceptions.UnitLockedException;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
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
/*    */ public class ActionDefineDataHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 22 */   private static final ILogger logger = GlobalLog.getLogger(ActionDefineDataHandler.class);

    /*    */
    /*    */
    public ActionDefineDataHandler()
    /*    */ {
        /* 26 */
        super("defineData", "Create Simple Data", 68);
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
        /*    */
        /* 40 */
        INativeType primType = null;
        /* 41 */
        boolean typeSet = false;
        /* 42 */
        INativeType type = pbcu.getDataTypeAt(a);
        /* 43 */
        while (!typeSet) {
            /* 44 */
            type = NativeActionUtil.rotateType(pbcu.getTypeManager(), type);
            /* 45 */
            if (primType == type) {
                /*    */
                break;
                /*    */
            }
            /*    */
            /* 49 */
            if (primType == null)
                /*    */ {
                /* 51 */
                primType = type;
                /*    */
            }
            /*    */
            try
                /*    */ {
                /* 55 */
                typeSet = pbcu.setDataTypeAt(a, type);
                /*    */
            }
            /*    */ catch (UnitLockedException e)
                /*    */ {
                /* 59 */
                throw e;
                /*    */
            }
            /*    */ catch (Exception e)
                /*    */ {
                /* 63 */
                logger.catching(e);
                /*    */
            }
            /*    */
        }
        /*    */
        /* 67 */
        if (!typeSet) {
            /* 68 */
            logger.error("Failed to define data at address %Xh", new Object[]{Long.valueOf(a)});
            /*    */
        }
        /*    */
        /* 71 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionDefineDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */