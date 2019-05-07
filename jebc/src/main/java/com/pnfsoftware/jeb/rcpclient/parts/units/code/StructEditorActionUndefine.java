/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
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
/*    */
/*    */ public class StructEditorActionUndefine
        /*    */ extends StructEditorAction
        /*    */ {
    /*    */
    public StructEditorActionUndefine(NativeTypeEditorView v)
    /*    */ {
        /* 23 */
        super("Undefine", v);
        /* 24 */
        this.keyCode = 117;
        /* 25 */
        setAccelerator(85);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEnabled()
    /*    */ {
        /* 30 */
        return getSelectedField() != null;
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 35 */
        IStructureTypeField f = getSelectedField();
        /* 36 */
        if (f == null) {
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
        if (f.getType() == null) {
            /* 43 */
            return;
            /*    */
        }
        /*    */
        /* 46 */
        if (!typeman.removeStructureField(type, f)) {
            /* 47 */
            UI.error(String.format("The field \"%s\" was not discarded", new Object[]{f.getName()}));
            /*    */
        }
        /*    */
        else {
            /* 50 */
            this.v.refresh();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorActionUndefine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */