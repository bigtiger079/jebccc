
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeChooserDialog;

public class StackEditorActionSelectType
        extends StackEditorAction {
    public StackEditorActionSelectType(StackEditorView v) {
        super("Select Type", v);
        this.keyCode = 116;
        setAccelerator(84);
    }

    public boolean isEnabled() {
        return this.v.getSelectedEntry() != null;
    }

    public void run() {
        ItemEntry e = this.v.getSelectedEntry();
        if (e == null) {
            return;
        }
        NativeTypeChooserDialog dlg = new NativeTypeChooserDialog(this.v.getShell(), this.v.getInputUnit());
        INativeType type = dlg.open();
        if (type == null) {
            return;
        }
        if (StackEditorActionSetType.setItemType(e, this.v.getInputRoutine(), type)) {
            this.v.refresh();
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionSelectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */