package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IMemoryModel;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.operations.JebAction;

import java.util.ArrayList;
import java.util.List;

public abstract class StackEditorAction extends JebAction {
    protected StackEditorView v;

    public StackEditorAction(String name, StackEditorView v) {
        super(null, name);
        this.v = v;
        this.isContextual = true;
    }

    IStackframeManager getStackManager() {
        return this.v.getInputRoutine().getData().getStackframeManager();
    }

    IMemoryModel getStackModel() {
        return this.v.getInputRoutine().getData().getStackframeModel();
    }

    protected INativeDataItem getSelectedItem() {
        ItemEntry e = this.v.getSelectedEntry();
        if (e != null) {
            INativeContinuousItem item = getStackModel().getItemAt(e.offset);
            if ((item instanceof INativeDataItem)) {
                return (INativeDataItem) item;
            }
        }
        return null;
    }

    protected List<INativeContinuousItem> collectItemsToBeUndefined(int startOffset, int wantedSize) {
        return collectItemsToBeUndefined(this.v.getInputRoutine(), startOffset, wantedSize);
    }

    static List<INativeContinuousItem> collectItemsToBeUndefined(INativeMethodItem routine, int startOffset, int wantedSize) {
        IMemoryModel model = routine.getData().getStackframeModel();
        return new ArrayList<>(model.getItemsInRange(startOffset, true, startOffset + wantedSize, true).values());
    }

    protected boolean undefineItems(List<INativeContinuousItem> items) {
        return undefineItems(this.v.getInputRoutine(), items);
    }

    static boolean undefineItems(INativeMethodItem routine, List<INativeContinuousItem> items) {
        IStackframeManager manager = routine.getData().getStackframeManager();
        for (INativeContinuousItem item : items) {
            if (!manager.undefineItem(item.getMemoryAddress())) {
                return false;
            }
        }
        return true;
    }

    protected boolean offerClearItems(int offset, int size) {
        return offerClearItems(this.v.getInputRoutine(), offset, size);
    }

    static boolean offerClearItems(INativeMethodItem routine, int offset, int size) {
        List<INativeContinuousItem> r = collectItemsToBeUndefined(routine, offset, size);
        if (r.size() > 1) {
            String msg = String.format("This action will undefine %d fields. Proceed?", r.size() - 1);
            AdaptivePopupDialog dlg2 = new AdaptivePopupDialog(UI.getShellTracker().get(), 2, S.s(207), msg, null);
            if (dlg2.open() == 0) {
                return false;
            }
        }
        if (!undefineItems(routine, r)) {
            UI.error("An error occurred while undefining fields.");
            return false;
        }
        return true;
    }
}


