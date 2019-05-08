
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.EditInstructionDialog;
import org.eclipse.swt.SWT;

public class ActionEditInstructionHandler
        extends NativeCodeBaseHandler {
    public ActionEditInstructionHandler() {
        super("editInstruction", "Edit Instruction...", SWT.MOD1 | 0x45);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true);
    }

    public boolean canExecuteAt(INativeCodeUnit<?> pbcu, long memAddress) {
        if (memAddress == -1L) {
            return false;
        }
        INativeItem item = pbcu.getNativeItemAt(memAddress);
        return item instanceof INativeInstructionItem;
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        EditInstructionDialog dlg = new EditInstructionDialog(this.shell, a, pbcu);
        dlg.open();
        postExecute(this.shell);
    }
}


