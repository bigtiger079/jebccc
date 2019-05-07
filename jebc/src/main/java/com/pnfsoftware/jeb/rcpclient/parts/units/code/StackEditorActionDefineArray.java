/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IMemoryModel;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
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
/*    */ public class StackEditorActionDefineArray
        /*    */ extends StackEditorAction
        /*    */ {
    /*    */
    public StackEditorActionDefineArray(StackEditorView v)
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
        return getSelectedItem() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 39 */
        INativeDataItem item = getSelectedItem();
        /* 40 */
        ITypeManager typeman = this.v.getInputUnit().getTypeManager();
        /* 41 */
        int requestedOffset = (int) item.getMemoryAddress();
        /*    */
        /*    */
        /* 44 */
        INativeType itemType = item.getType();
        /* 45 */
        INativeType eltType = itemType;
        /* 46 */
        if ((itemType instanceof IArrayType)) {
            /* 47 */
            eltType = ((IArrayType) itemType).getElementType();
            /*    */
        }
        /*    */
        /*    */
        /* 51 */
        int maxEltNoUndefine = -1;
        /* 52 */
        INativeContinuousItem item1 = getStackModel().getNextItem(requestedOffset);
        /* 53 */
        if (item1 != null) {
            /* 54 */
            maxEltNoUndefine = ((int) item1.getMemoryAddress() - requestedOffset) / eltType.getSize();
            /*    */
        }
        /*    */
        /* 57 */
        TextDialog dlg = new TextDialog(this.v.getShell(), "Define an Array", "" + maxEltNoUndefine, null);
        /* 58 */
        dlg.setSelected(true);
        /* 59 */
        String val = dlg.open();
        /* 60 */
        if (val != null) {
            /* 61 */
            int cnt = Conversion.stringToInt(val);
            /* 62 */
            if (cnt >= 1) {
                /* 63 */
                if (!offerClearItems(requestedOffset, eltType.getSize() * cnt)) {
                    /* 64 */
                    return;
                    /*    */
                }
                /*    */
                /* 67 */
                IArrayType newItemType = typeman.createArray(eltType, cnt);
                /* 68 */
                INativeContinuousItem newItem = getStackManager().defineItem(requestedOffset, newItemType);
                /* 69 */
                if (newItem == null) {
                    /* 70 */
                    UI.error("An error occurred.");
                    /* 71 */
                    return;
                    /*    */
                }
                /*    */
                /*    */
                /* 75 */
                this.v.refresh();
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionDefineArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */