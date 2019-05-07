/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
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
/*    */ public class StackEditorActionSetType
        /*    */ extends StackEditorAction
        /*    */ {
    /*    */
    public StackEditorActionSetType(StackEditorView v)
    /*    */ {
        /* 25 */
        super("Set Type", v);
        /* 26 */
        this.keyCode = 121;
        /* 27 */
        setAccelerator(89);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 32 */
        return this.v.getSelectedEntry() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 37 */
        ItemEntry e = this.v.getSelectedEntry();
        /* 38 */
        if (e == null) {
            /* 39 */
            return;
            /*    */
        }
        /*    */
        /* 42 */
        String currentFieldSig = "";
        /* 43 */
        if (e.type != null) {
            /* 44 */
            currentFieldSig = e.type.getSignature(true);
            /*    */
        }
        /*    */
        /* 47 */
        TextDialog dlg = new TextDialog(this.v.getShell(), "Edit type of item", currentFieldSig, null);
        /* 48 */
        dlg.setSelected(true);
        /* 49 */
        String fieldSig = dlg.open();
        /* 50 */
        if (fieldSig == null) {
            /* 51 */
            return;
            /*    */
        }
        /*    */
        /* 54 */
        ITypeManager typeman = this.v.getInputUnit().getTypeManager();
        /* 55 */
        INativeType itemType = typeman.getType(fieldSig);
        /* 56 */
        if (itemType == null) {
            /* 57 */
            UI.error(String.format("The type \"%s\" was not found", new Object[]{fieldSig}));
            /* 58 */
            return;
            /*    */
        }
        /*    */
        /* 61 */
        if (setItemType(e, this.v.getInputRoutine(), itemType)) {
            /* 62 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    static boolean setItemType(ItemEntry e, INativeMethodItem routine, INativeType newItemType)
    /*    */ {
        /* 68 */
        if (newItemType == e.type) {
            /* 69 */
            return false;
            /*    */
        }
        /*    */
        /* 72 */
        if (!offerClearItems(routine, e.offset, newItemType.getSize())) {
            /* 73 */
            return false;
            /*    */
        }
        /*    */
        /* 76 */
        INativeDataItem item = routine.getData().getStackframeManager().defineItem(e.offset, newItemType);
        /* 77 */
        if (item == null) {
            /* 78 */
            UI.error(String.format("A item of type \"%s\" could not be created at offset %Xh", new Object[]{newItemType
                    /* 79 */.getName(true), Integer.valueOf(e.offset)}));
            /* 80 */
            return false;
            /*    */
        }
        /*    */
        /* 83 */
        item.setName(e.name);
        /* 84 */
        return true;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionSetType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */