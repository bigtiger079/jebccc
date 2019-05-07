/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
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
/*    */ public class StructEditorActionSetType
        /*    */ extends StructEditorAction
        /*    */ {
    /*    */
    public StructEditorActionSetType(NativeTypeEditorView v)
    /*    */ {
        /* 24 */
        super("Set Type", v);
        /* 25 */
        this.keyCode = 121;
        /* 26 */
        setAccelerator(89);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 31 */
        return this.v.getSelectedEntry() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 36 */
        ItemEntry e = this.v.getSelectedEntry();
        /* 37 */
        if (e == null) {
            /* 38 */
            return;
            /*    */
        }
        /*    */
        /* 41 */
        String currentFieldSig = "";
        /* 42 */
        if (e.type != null) {
            /* 43 */
            currentFieldSig = e.type.getSignature(true);
            /*    */
        }
        /*    */
        /* 46 */
        TextDialog dlg = new TextDialog(this.v.getShell(), "Edit type of field", currentFieldSig, null);
        /* 47 */
        dlg.setSelected(true);
        /* 48 */
        String fieldSig = dlg.open();
        /* 49 */
        if (fieldSig == null) {
            /* 50 */
            return;
            /*    */
        }
        /*    */
        /* 53 */
        IStructureType structType = this.v.getInputType();
        /* 54 */
        ITypeManager typeman = structType.getTypeManager();
        /* 55 */
        INativeType fieldType = typeman.getType(fieldSig);
        /* 56 */
        if (fieldType == null) {
            /* 57 */
            UI.error(String.format("The type \"%s\" was not found", new Object[]{fieldSig}));
            /* 58 */
            return;
            /*    */
        }
        /*    */
        /* 61 */
        if (setFieldType(e, structType, fieldType)) {
            /* 62 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    static boolean setFieldType(ItemEntry e, IStructureType structType, INativeType newFieldType)
    /*    */ {
        /* 68 */
        if (newFieldType == e.type) {
            /* 69 */
            return false;
            /*    */
        }
        /*    */
        /*    */
        /*    */
        /* 74 */
        if (structType.isCircular(newFieldType)) {
            /* 75 */
            UI.error("A cycle was detected: a structure cannot contain itself");
            /* 76 */
            return false;
            /*    */
        }
        /*    */
        /* 79 */
        if (!offerClearFields(structType, e.offset, newFieldType.getSize())) {
            /* 80 */
            return false;
            /*    */
        }
        /*    */
        /* 83 */
        if (structType.getTypeManager().addStructureField(structType, e.name, newFieldType, e.offset) == null) {
            /* 84 */
            UI.error(String.format("A field of type \"%s\" could not be created at offset %Xh", new Object[]{newFieldType
                    /* 85 */.getName(true), Integer.valueOf(e.offset)}));
            /* 86 */
            return false;
            /*    */
        }
        /*    */
        /* 89 */
        return true;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorActionSetType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */