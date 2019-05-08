
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IMemoryModel;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.encoding.Conversion;

public class StackEditorActionDefineArray
        extends StackEditorAction {
    public StackEditorActionDefineArray(StackEditorView v) {
        super("Array", v);
        this.keyCode = 97;
        setAccelerator(65);
    }

    public boolean isEnabled() {
        return getSelectedItem() != null;
    }

    public void run() {
        INativeDataItem item = getSelectedItem();
        ITypeManager typeman = this.v.getInputUnit().getTypeManager();
        int requestedOffset = (int) item.getMemoryAddress();
        INativeType itemType = item.getType();
        INativeType eltType = itemType;
        if ((itemType instanceof IArrayType)) {
            eltType = ((IArrayType) itemType).getElementType();
        }
        int maxEltNoUndefine = -1;
        INativeContinuousItem item1 = getStackModel().getNextItem(requestedOffset);
        if (item1 != null) {
            maxEltNoUndefine = ((int) item1.getMemoryAddress() - requestedOffset) / eltType.getSize();
        }
        TextDialog dlg = new TextDialog(this.v.getShell(), "Define an Array", "" + maxEltNoUndefine, null);
        dlg.setSelected(true);
        String val = dlg.open();
        if (val != null) {
            int cnt = Conversion.stringToInt(val);
            if (cnt >= 1) {
                if (!offerClearItems(requestedOffset, eltType.getSize() * cnt)) {
                    return;
                }
                IArrayType newItemType = typeman.createArray(eltType, cnt);
                INativeContinuousItem newItem = getStackManager().defineItem(requestedOffset, newItemType);
                if (newItem == null) {
                    UI.error("An error occurred.");
                    return;
                }
                this.v.refresh();
            }
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionDefineArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */