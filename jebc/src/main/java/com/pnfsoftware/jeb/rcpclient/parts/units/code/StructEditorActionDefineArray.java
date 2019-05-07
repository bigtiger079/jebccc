/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import com.pnfsoftware.jeb.util.encoding.Conversion;

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
/*    */ public class StructEditorActionDefineArray
        /*    */ extends StructEditorAction
        /*    */ {
    /*    */
    public StructEditorActionDefineArray(NativeTypeEditorView v)
    /*    */ {
        /* 27 */
        super("Array", v);
        /* 28 */
        this.keyCode = 97;
        /* 29 */
        setAccelerator(65);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 34 */
        return getSelectedField() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 39 */
        IStructureTypeField f = getSelectedField();
        /* 40 */
        IStructureType type = this.v.getInputType();
        /* 41 */
        ITypeManager typeman = type.getTypeManager();
        /* 42 */
        int requestedOffset = f.getOffset();
        /*    */
        /*    */
        /* 45 */
        INativeType fieldType = f.getType();
        /* 46 */
        INativeType eltType = fieldType;
        /* 47 */
        if ((fieldType instanceof IArrayType)) {
            /* 48 */
            eltType = ((IArrayType) fieldType).getElementType();
            /*    */
        }
        /*    */
        /*    */
        /* 52 */
        int maxEltNoUndefine = -1;
        /* 53 */
        IStructureTypeField f1 = type.getFieldAfter(requestedOffset);
        /* 54 */
        if (f1 != null) {
            /* 55 */
            maxEltNoUndefine = (f1.getOffset() - requestedOffset) / eltType.getSize();
            /*    */
        }
        /*    */
        /* 58 */
        TextDialog dlg = new TextDialog(this.v.getShell(), "Define an Array", "" + maxEltNoUndefine, null);
        /* 59 */
        dlg.setSelected(true);
        /* 60 */
        String val = dlg.open();
        /* 61 */
        if (val != null) {
            /* 62 */
            int cnt = Conversion.stringToInt(val);
            /* 63 */
            if (cnt >= 1) {
                /* 64 */
                if (!offerClearFields(requestedOffset, eltType.getSize() * cnt)) {
                    /* 65 */
                    return;
                    /*    */
                }
                /*    */
                /* 68 */
                IArrayType newFieldType = typeman.createArray(eltType, cnt);
                /* 69 */
                IStructureTypeField newField = typeman.addStructureField(type, f.getName(), newFieldType, requestedOffset);
                /* 70 */
                if (newField == null) {
                    /* 71 */
                    UI.error("An error occurred.");
                    /*    */
                }
                /*    */
                else {
                    /* 74 */
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorActionDefineArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */