package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

public class StructEditorActionRename extends StructEditorAction {
    public StructEditorActionRename(NativeTypeEditorView v) {
        super("Rename", v);
        this.keyCode = 110;
        setAccelerator(78);
    }

    public boolean isEnabled() {
        return getSelectedField() != null;
    }

    public void run() {
        ItemEntry e = this.v.getSelectedEntry();
        if ((e == null) || (e.slack)) {
            return;
        }
        IStructureType type = this.v.getInputType();
        IStructureTypeField field = type.getFieldAt(e.offset);
        if (field != null) {
            TextDialog dlg = new TextDialog(this.v.getShell(), "Rename a Field", e.name, null);
            dlg.setSelected(true);
            String newName = dlg.open();
            if (newName != null) {
                ITypeManager typeman = type.getTypeManager();
                boolean r = typeman.renameStructureField(type, field, newName);
                if (!r) {
                    UI.error("The field was not renamed");
                } else {
                    this.v.refresh();
                }
            }
        }
    }
}


