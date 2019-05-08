package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;

public class StackEditorActionRename extends StackEditorAction {
    public StackEditorActionRename(StackEditorView v) {
        super("Rename", v);
        this.keyCode = 110;
        setAccelerator(78);
    }

    public boolean isEnabled() {
        return getSelectedItem() != null;
    }

    public void run() {
        ItemEntry e = this.v.getSelectedEntry();
        if ((e == null) || (e.slack)) {
            return;
        }
        INativeContinuousItem item = getSelectedItem();
        if (item != null) {
            TextDialog dlg = new TextDialog(this.v.getShell(), "Rename an Item", e.name, null);
            dlg.setSelected(true);
            String newName = dlg.open();
            if (newName != null) {
                item.setName(newName);
                this.v.refresh();
            }
        }
    }
}


