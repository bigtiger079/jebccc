/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeChooserDialog;

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
/*    */ public class StructEditorActionSelectType
        /*    */ extends StructEditorAction
        /*    */ {
    /*    */
    public StructEditorActionSelectType(NativeTypeEditorView v)
    /*    */ {
        /* 21 */
        super("Select Type", v);
        /* 22 */
        this.keyCode = 116;
        /* 23 */
        setAccelerator(84);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 28 */
        return this.v.getSelectedEntry() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 33 */
        ItemEntry e = this.v.getSelectedEntry();
        /* 34 */
        if (e == null) {
            /* 35 */
            return;
            /*    */
        }
        /*    */
        /* 38 */
        NativeTypeChooserDialog dlg = new NativeTypeChooserDialog(this.v.getShell(), this.v.getInputUnit());
        /* 39 */
        INativeType type = dlg.open();
        /* 40 */
        if (type == null) {
            /* 41 */
            return;
            /*    */
        }
        /*    */
        /* 44 */
        if (StructEditorActionSetType.setFieldType(e, this.v.getInputType(), type)) {
            /* 45 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorActionSelectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */