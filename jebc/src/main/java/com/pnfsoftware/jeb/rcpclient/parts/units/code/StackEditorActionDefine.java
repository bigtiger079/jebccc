/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IPrimitiveTypeManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class StackEditorActionDefine
        /*    */ extends StackEditorAction
        /*    */ {
    /*    */
    public StackEditorActionDefine(StackEditorView v)
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
        ITypeManager typeman = this.v.getInputUnit().getTypeManager();
        /* 41 */
        INativeMethodItem routine = this.v.getInputRoutine();
        /*    */
        /* 43 */
        INativeType tUINT8 = typeman.getPrimitives().getIntegerBySize(1, false);
        /* 44 */
        INativeType tUINT16 = typeman.getPrimitives().getIntegerBySize(2, false);
        /* 45 */
        INativeType tUINT32 = typeman.getPrimitives().getIntegerBySize(4, false);
        /* 46 */
        INativeType tUINT64 = typeman.getPrimitives().getIntegerBySize(8, false);
        /*    */
        /* 48 */
        INativeType itemType = e.type;
        /* 49 */
        if (itemType == tUINT8) {
            /* 50 */
            itemType = tUINT16;
            /*    */
        }
        /* 52 */
        else if (itemType == tUINT16) {
            /* 53 */
            itemType = tUINT32;
            /*    */
        }
        /* 55 */
        else if (itemType == tUINT32) {
            /* 56 */
            itemType = tUINT64;
            /*    */
        }
        /*    */
        else {
            /* 59 */
            itemType = tUINT8;
            /*    */
        }
        /*    */
        /*    */
        /* 63 */
        if (!offerClearItems(e.offset, itemType.getSize())) {
            /* 64 */
            return;
            /*    */
        }
        /*    */
        /* 67 */
        if (routine.getData().getStackframeManager().defineItem(e.offset, itemType) == null) {
            /* 68 */
            UI.error(String.format("An item of type \"%s\" could not be created at offset %Xh", new Object[]{itemType
                    /* 69 */.getName(true), Integer.valueOf(e.offset)}));
            /*    */
        }
        /*    */
        else {
            /* 72 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionDefine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */