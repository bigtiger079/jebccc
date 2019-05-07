/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class StackEditorActionUndefine
        /*    */ extends StackEditorAction
        /*    */ {
    /*    */
    public StackEditorActionUndefine(StackEditorView v)
    /*    */ {
        /* 21 */
        super("Undefine", v);
        /* 22 */
        this.keyCode = 117;
        /* 23 */
        setAccelerator(85);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 28 */
        return getSelectedItem() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 33 */
        INativeContinuousItem item = getSelectedItem();
        /* 34 */
        if (item == null) {
            /* 35 */
            return;
            /*    */
        }
        /*    */
        /* 38 */
        if (!this.v.getInputRoutine().getData().getStackframeManager().undefineItem(item.getMemoryAddress())) {
            /* 39 */
            UI.error(String.format("The item \"%s\" was not discarded", new Object[]{item.getName(true)}));
            /*    */
        }
        /*    */
        else {
            /* 42 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionUndefine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */