package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

public class StackEditorActionSetType extends StackEditorAction {
    public StackEditorActionSetType(StackEditorView v) {
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
        TextDialog dlg = new TextDialog(this.v.getShell(), "Edit type of item", currentFieldSig, null);
        dlg.setSelected(true);
        String fieldSig = dlg.open();
        if (fieldSig == null) {
            return;
        }
        ITypeManager typeman = this.v.getInputUnit().getTypeManager();
        INativeType itemType = typeman.getType(fieldSig);
        if (itemType == null) {
            UI.error(String.format("The type \"%s\" was not found", fieldSig));
            return;
        }
        if (setItemType(e, this.v.getInputRoutine(), itemType)) {
            this.v.refresh();
        }
    }

    static boolean setItemType(ItemEntry e, INativeMethodItem routine, INativeType newItemType) {
        if (newItemType == e.type) {
            return false;
        }
        if (!offerClearItems(routine, e.offset, newItemType.getSize())) {
            return false;
        }
        INativeDataItem item = routine.getData().getStackframeManager().defineItem(e.offset, newItemType);
        if (item == null) {
            UI.error(String.format("A item of type \"%s\" could not be created at offset %Xh", newItemType.getName(true), e.offset));
            return false;
        }
        item.setName(e.name);
        return true;
    }
}


