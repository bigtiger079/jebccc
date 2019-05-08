
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.encoding.Conversion;

public class StructEditorActionDefineArray
        extends StructEditorAction {
    public StructEditorActionDefineArray(NativeTypeEditorView v) {
        super("Array", v);
        this.keyCode = 97;
        setAccelerator(65);
    }

    public boolean isEnabled() {
        return getSelectedField() != null;
    }

    public void run() {
        IStructureTypeField f = getSelectedField();
        IStructureType type = this.v.getInputType();
        ITypeManager typeman = type.getTypeManager();
        int requestedOffset = f.getOffset();
        INativeType fieldType = f.getType();
        INativeType eltType = fieldType;
        if ((fieldType instanceof IArrayType)) {
            eltType = ((IArrayType) fieldType).getElementType();
        }
        int maxEltNoUndefine = -1;
        IStructureTypeField f1 = type.getFieldAfter(requestedOffset);
        if (f1 != null) {
            maxEltNoUndefine = (f1.getOffset() - requestedOffset) / eltType.getSize();
        }
        TextDialog dlg = new TextDialog(this.v.getShell(), "Define an Array", "" + maxEltNoUndefine, null);
        dlg.setSelected(true);
        String val = dlg.open();
        if (val != null) {
            int cnt = Conversion.stringToInt(val);
            if (cnt >= 1) {
                if (!offerClearFields(requestedOffset, eltType.getSize() * cnt)) {
                    return;
                }
                IArrayType newFieldType = typeman.createArray(eltType, cnt);
                IStructureTypeField newField = typeman.addStructureField(type, f.getName(), newFieldType, requestedOffset);
                if (newField == null) {
                    UI.error("An error occurred.");
                } else {
                    this.v.refresh();
                }
            }
        }
    }
}


