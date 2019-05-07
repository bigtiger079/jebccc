/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;

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
/*    */ public class StructEditorActionRename
        /*    */ extends StructEditorAction
        /*    */ {
    /*    */
    public StructEditorActionRename(NativeTypeEditorView v)
    /*    */ {
        /* 24 */
        super("Rename", v);
        /* 25 */
        this.keyCode = 110;
        /* 26 */
        setAccelerator(78);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 31 */
        return getSelectedField() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 36 */
        ItemEntry e = this.v.getSelectedEntry();
        /* 37 */
        if ((e == null) || (e.slack)) {
            /* 38 */
            return;
            /*    */
        }
        /*    */
        /* 41 */
        IStructureType type = this.v.getInputType();
        /* 42 */
        IStructureTypeField field = type.getFieldAt(e.offset);
        /* 43 */
        if (field != null) {
            /* 44 */
            TextDialog dlg = new TextDialog(this.v.getShell(), "Rename a Field", e.name, null);
            /* 45 */
            dlg.setSelected(true);
            /* 46 */
            String newName = dlg.open();
            /* 47 */
            if (newName != null) {
                /* 48 */
                ITypeManager typeman = type.getTypeManager();
                /* 49 */
                boolean r = typeman.renameStructureField(type, field, newName);
                /* 50 */
                if (!r) {
                    /* 51 */
                    UI.error("The field was not renamed");
                    /*    */
                }
                /*    */
                else {
                    /* 54 */
                    this.v.refresh();
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorActionRename.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */