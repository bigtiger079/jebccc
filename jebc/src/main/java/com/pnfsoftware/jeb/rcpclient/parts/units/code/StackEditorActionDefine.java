package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IPrimitiveTypeManager;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

public class StackEditorActionDefine extends StackEditorAction {
    public StackEditorActionDefine(StackEditorView v) {
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
        ITypeManager typeman = this.v.getInputUnit().getTypeManager();
        INativeMethodItem routine = this.v.getInputRoutine();
        INativeType tUINT8 = typeman.getPrimitives().getIntegerBySize(1, false);
        INativeType tUINT16 = typeman.getPrimitives().getIntegerBySize(2, false);
        INativeType tUINT32 = typeman.getPrimitives().getIntegerBySize(4, false);
        INativeType tUINT64 = typeman.getPrimitives().getIntegerBySize(8, false);
        INativeType itemType = e.type;
        if (itemType == tUINT8) {
            itemType = tUINT16;
        } else if (itemType == tUINT16) {
            itemType = tUINT32;
        } else if (itemType == tUINT32) {
            itemType = tUINT64;
        } else {
            itemType = tUINT8;
        }
        if (!offerClearItems(e.offset, itemType.getSize())) {
            return;
        }
        if (routine.getData().getStackframeManager().defineItem(e.offset, itemType) == null) {
            UI.error(String.format("An item of type \"%s\" could not be created at offset %Xh", new Object[]{itemType.getName(true), Integer.valueOf(e.offset)}));
        } else {
            this.v.refresh();
        }
    }
}


