/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.EditInstructionDialog;
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
/*    */
/*    */ public class ActionEditInstructionHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionEditInstructionHandler()
    /*    */ {
        /* 26 */
        super("editInstruction", "Edit Instruction...", SWT.MOD1 | 0x45);
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
    public boolean canExecuteAt(INativeCodeUnit<?> pbcu, long memAddress)
    /*    */ {
        /* 36 */
        if (memAddress == -1L) {
            /* 37 */
            return false;
            /*    */
        }
        /* 39 */
        INativeItem item = pbcu.getNativeItemAt(memAddress);
        /* 40 */
        return item instanceof INativeInstructionItem;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 45 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 46 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 48 */
        EditInstructionDialog dlg = new EditInstructionDialog(this.shell, a, pbcu);
        /* 49 */
        dlg.open();
        /*    */
        /* 51 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditInstructionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */