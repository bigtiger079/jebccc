/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.EditMethodDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.MethodSetupInformation;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import java.util.Objects;
/*    */ import org.eclipse.swt.SWT;

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
/*    */
/*    */ public class ActionEditProcedureHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionEditProcedureHandler()
    /*    */ {
        /* 29 */
        super("editProcedure", S.s(492), SWT.MOD1 | SWT.MOD3 | 0x50);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 34 */
        return canExecuteAndNativeCheck(this.part, true, false, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 39 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 40 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 42 */
        INativeMethodItem m = pbcu.getInternalMethod(a, false);
        /* 43 */
        if (m == null) {
            /* 44 */
            UI.error("No routine was found at this address");
            /* 45 */
            return;
            /*    */
        }
        /*    */
        /* 48 */
        EditMethodDialog dlg = new EditMethodDialog(this.shell, pbcu, m);
        /* 49 */
        MethodSetupInformation info = dlg.open();
        /* 50 */
        if (info == null) {
            /* 51 */
            return;
            /*    */
        }
        /*    */
        /*    */
        /* 55 */
        if (!Objects.equals(info.getRoutineNonReturning(), m.getNonReturning())) {
            /* 56 */
            m.setNonReturning(info.getRoutineNonReturning());
            /*    */
        }
        /* 58 */
        if (!Objects.equals(info.getRoutineDataSPDeltaOnReturn(), m.getData().getSPDeltaOnReturn())) {
            /* 59 */
            m.getData().setSPDeltaOnReturn(info.getRoutineDataSPDeltaOnReturn());
            /*    */
        }
        /*    */
        /* 62 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditProcedureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */