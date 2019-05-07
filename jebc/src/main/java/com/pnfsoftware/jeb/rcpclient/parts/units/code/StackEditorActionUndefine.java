
package com.pnfsoftware.jeb.rcpclient.parts.units.code;


import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;


public class StackEditorActionUndefine
        extends StackEditorAction {

    public StackEditorActionUndefine(StackEditorView v) {

        super("Undefine", v);

        this.keyCode = 117;

        setAccelerator(85);

    }


    public boolean isEnabled() {

        return getSelectedItem() != null;

    }


    public void run() {

        INativeContinuousItem item = getSelectedItem();

        if (item == null) {

            return;

        }


        if (!this.v.getInputRoutine().getData().getStackframeManager().undefineItem(item.getMemoryAddress())) {

            UI.error(String.format("The item \"%s\" was not discarded", new Object[]{item.getName(true)}));

        } else {

            this.v.refresh();

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorActionUndefine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */