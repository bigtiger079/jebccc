/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;

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
/*    */ public class StackEditorActionRename
        /*    */ extends StackEditorAction
        /*    */ {
    /*    */
    public StackEditorActionRename(StackEditorView v)
    /*    */ {
        /* 21 */
        super("Rename", v);
        /* 22 */
        this.keyCode = 110;
        /* 23 */
        setAccelerator(78);
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
        ItemEntry e = this.v.getSelectedEntry();
        /* 34 */
        if ((e == null) || (e.slack)) {
            /* 35 */
            return;
            /*    */
        }
        /*    */
        /* 38 */
        INativeContinuousItem item = getSelectedItem();
        /* 39 */
        if (item != null) {
            /* 40 */
            TextDialog dlg = new TextDialog(this.v.getShell(), "Rename an Item", e.name, null);
            /* 41 */
            dlg.setSelected(true);
            /* 42 */
            String newName = dlg.open();
            /* 43 */
            if (newName != null) {
                /* 44 */
                item.setName(newName);
                /*    */
                /*    */
                /*    */
                /*    */
                /* 49 */
                this.v.refresh();
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionRename.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */