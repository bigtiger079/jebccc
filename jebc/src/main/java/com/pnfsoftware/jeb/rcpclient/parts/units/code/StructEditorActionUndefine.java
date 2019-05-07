
package com.pnfsoftware.jeb.rcpclient.parts.units.code;


import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;


public class StructEditorActionUndefine
        extends StructEditorAction {

    public StructEditorActionUndefine(NativeTypeEditorView v) {

        super("Undefine", v);

        this.keyCode = 117;

        setAccelerator(85);

    }


    public boolean isEnabled() {

        return getSelectedField() != null;

    }


    public void run() {

        IStructureTypeField f = getSelectedField();

        if (f == null) {

            return;

        }


        IStructureType type = this.v.getInputType();

        ITypeManager typeman = type.getTypeManager();

        if (f.getType() == null) {

            return;

        }


        if (!typeman.removeStructureField(type, f)) {

            UI.error(String.format("The field \"%s\" was not discarded", new Object[]{f.getName()}));

        } else {

            this.v.refresh();

        }

    }

}


