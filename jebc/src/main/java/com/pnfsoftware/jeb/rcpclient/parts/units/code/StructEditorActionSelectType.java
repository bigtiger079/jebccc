
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeChooserDialog;

public class StructEditorActionSelectType
        extends StructEditorAction {
    public StructEditorActionSelectType(NativeTypeEditorView v) {
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
        if (StructEditorActionSetType.setFieldType(e, this.v.getInputType(), type)) {
            this.v.refresh();
        }
    }
}


