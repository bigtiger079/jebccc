package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

public class StructEditorActionSetType extends StructEditorAction {
    public StructEditorActionSetType(NativeTypeEditorView v) {
        super("Set Type", v);
        this.keyCode = 121;
        setAccelerator(89);
    }

    public boolean isEnabled() {
        return this.v.getSelectedEntry() != null;
    }

    public void run() {
        ItemEntry e = this.v.getSelectedEntry();
        if (e == null) {
            return;
        }
        String currentFieldSig = "";
        if (e.type != null) {
            currentFieldSig = e.type.getSignature(true);
        }
        TextDialog dlg = new TextDialog(this.v.getShell(), "Edit type of field", currentFieldSig, null);
        dlg.setSelected(true);
        String fieldSig = dlg.open();
        if (fieldSig == null) {
            return;
        }
        IStructureType structType = this.v.getInputType();
        ITypeManager typeman = structType.getTypeManager();
        INativeType fieldType = typeman.getType(fieldSig);
        if (fieldType == null) {
            UI.error(String.format("The type \"%s\" was not found", fieldSig));
            return;
        }
        if (setFieldType(e, structType, fieldType)) {
            this.v.refresh();
        }
    }

    static boolean setFieldType(ItemEntry e, IStructureType structType, INativeType newFieldType) {
        if (newFieldType == e.type) {
            return false;
        }
        if (structType.isCircular(newFieldType)) {
            UI.error("A cycle was detected: a structure cannot contain itself");
            return false;
        }
        if (!offerClearFields(structType, e.offset, newFieldType.getSize())) {
            return false;
        }
        if (structType.getTypeManager().addStructureField(structType, e.name, newFieldType, e.offset) == null) {
            UI.error(String.format("A field of type \"%s\" could not be created at offset %Xh", newFieldType.getName(true), e.offset));
            return false;
        }
        return true;
    }
}


