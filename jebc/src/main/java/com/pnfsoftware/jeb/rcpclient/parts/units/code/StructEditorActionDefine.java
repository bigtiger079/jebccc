package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IPrimitiveTypeManager;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

public class StructEditorActionDefine extends StructEditorAction {
    public StructEditorActionDefine(NativeTypeEditorView v) {
        super("Define", v);
        this.keyCode = 100;
        setAccelerator(68);
    }

    public boolean isEnabled() {
        return this.v.getSelectedEntry() != null;
    }

    public void run() {
        ItemEntry e = this.v.getSelectedEntry();
        if (e == null) {
            return;
        }
        IStructureType type = this.v.getInputType();
        ITypeManager typeman = type.getTypeManager();
        INativeType tUINT8 = typeman.getPrimitives().getIntegerBySize(1, false);
        INativeType tUINT16 = typeman.getPrimitives().getIntegerBySize(2, false);
        INativeType tUINT32 = typeman.getPrimitives().getIntegerBySize(4, false);
        INativeType tUINT64 = typeman.getPrimitives().getIntegerBySize(8, false);
        INativeType fieldType = e.type;
        if (fieldType == tUINT8) {
            fieldType = tUINT16;
        } else if (fieldType == tUINT16) {
            fieldType = tUINT32;
        } else if (fieldType == tUINT32) {
            fieldType = tUINT64;
        } else {
            fieldType = tUINT8;
        }
        if (!offerClearFields(e.offset, fieldType.getSize())) {
            return;
        }
        if (typeman.addStructureField(type, e.name, fieldType, e.offset) == null) {
            UI.error(String.format("A field of type \"%s\" could not be created at offset %Xh", new Object[]{fieldType.getName(true), Integer.valueOf(e.offset)}));
        } else {
            this.v.refresh();
        }
    }
}


