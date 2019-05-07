/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IPrimitiveTypeManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
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
/*    */ public class StructEditorActionDefine
        /*    */ extends StructEditorAction
        /*    */ {
    /*    */
    public StructEditorActionDefine(NativeTypeEditorView v)
    /*    */ {
        /* 23 */
        super("Define", v);
        /* 24 */
        this.keyCode = 100;
        /* 25 */
        setAccelerator(68);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 30 */
        return this.v.getSelectedEntry() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 35 */
        ItemEntry e = this.v.getSelectedEntry();
        /* 36 */
        if (e == null) {
            /* 37 */
            return;
            /*    */
        }
        /*    */
        /* 40 */
        IStructureType type = this.v.getInputType();
        /* 41 */
        ITypeManager typeman = type.getTypeManager();
        /* 42 */
        INativeType tUINT8 = typeman.getPrimitives().getIntegerBySize(1, false);
        /* 43 */
        INativeType tUINT16 = typeman.getPrimitives().getIntegerBySize(2, false);
        /* 44 */
        INativeType tUINT32 = typeman.getPrimitives().getIntegerBySize(4, false);
        /* 45 */
        INativeType tUINT64 = typeman.getPrimitives().getIntegerBySize(8, false);
        /*    */
        /* 47 */
        INativeType fieldType = e.type;
        /* 48 */
        if (fieldType == tUINT8) {
            /* 49 */
            fieldType = tUINT16;
            /*    */
        }
        /* 51 */
        else if (fieldType == tUINT16) {
            /* 52 */
            fieldType = tUINT32;
            /*    */
        }
        /* 54 */
        else if (fieldType == tUINT32) {
            /* 55 */
            fieldType = tUINT64;
            /*    */
        }
        /*    */
        else {
            /* 58 */
            fieldType = tUINT8;
            /*    */
        }
        /*    */
        /* 61 */
        if (!offerClearFields(e.offset, fieldType.getSize())) {
            /* 62 */
            return;
            /*    */
        }
        /*    */
        /* 65 */
        if (typeman.addStructureField(type, e.name, fieldType, e.offset) == null) {
            /* 66 */
            UI.error(String.format("A field of type \"%s\" could not be created at offset %Xh", new Object[]{fieldType
                    /* 67 */.getName(true), Integer.valueOf(e.offset)}));
            /*    */
        }
        /*    */
        else {
            /* 70 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorActionDefine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */