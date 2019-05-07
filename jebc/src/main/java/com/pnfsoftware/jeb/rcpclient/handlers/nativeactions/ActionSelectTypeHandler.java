/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeChooserDialog;
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
/*    */ public class ActionSelectTypeHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 23 */   private static final ILogger logger = GlobalLog.getLogger(ActionSelectTypeHandler.class);

    /*    */
    /*    */
    public ActionSelectTypeHandler() {
        /* 26 */
        super("selectType", S.s(560), 84);
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
        /* 39 */
        NativeTypeChooserDialog dlg = new NativeTypeChooserDialog(this.shell, pbcu);
        /* 40 */
        INativeType t = dlg.open();
        /*    */
        /* 42 */
        if (t == null) {
            /* 43 */
            return;
            /*    */
        }
        /*    */
        /* 46 */
        if (!pbcu.setDataTypeAt(a, t)) {
            /* 47 */
            logger.error("Failed to change type at address %Xh", new Object[]{Long.valueOf(a)});
            /*    */
        }
        /*    */
        /* 50 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionSelectTypeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */