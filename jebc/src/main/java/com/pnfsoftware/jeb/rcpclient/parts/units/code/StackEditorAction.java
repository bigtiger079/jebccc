/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IMemoryModel;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IStackframeManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.JebAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.SortedMap;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public abstract class StackEditorAction
        /*     */ extends JebAction
        /*     */ {
    /*     */   protected StackEditorView v;

    /*     */
    /*     */
    public StackEditorAction(String name, StackEditorView v)
    /*     */ {
        /*  32 */
        super(null, name);
        /*  33 */
        this.v = v;
        /*  34 */
        this.isContextual = true;
        /*     */
    }

    /*     */
    /*     */   IStackframeManager getStackManager() {
        /*  38 */
        return this.v.getInputRoutine().getData().getStackframeManager();
        /*     */
    }

    /*     */
    /*     */   IMemoryModel getStackModel() {
        /*  42 */
        return this.v.getInputRoutine().getData().getStackframeModel();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected INativeDataItem getSelectedItem()
    /*     */ {
        /*  51 */
        ItemEntry e = this.v.getSelectedEntry();
        /*  52 */
        if (e != null) {
            /*  53 */
            INativeContinuousItem item = getStackModel().getItemAt(e.offset);
            /*  54 */
            if ((item instanceof INativeDataItem)) {
                /*  55 */
                return (INativeDataItem) item;
                /*     */
            }
            /*     */
        }
        /*  58 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    protected List<INativeContinuousItem> collectItemsToBeUndefined(int startOffset, int wantedSize) {
        /*  62 */
        return collectItemsToBeUndefined(this.v.getInputRoutine(), startOffset, wantedSize);
        /*     */
    }

    /*     */
    /*     */
    static List<INativeContinuousItem> collectItemsToBeUndefined(INativeMethodItem routine, int startOffset, int wantedSize)
    /*     */ {
        /*  67 */
        IMemoryModel model = routine.getData().getStackframeModel();
        /*  68 */
        return new ArrayList(model.getItemsInRange(startOffset, true, startOffset + wantedSize, true).values());
        /*     */
    }

    /*     */
    /*     */
    protected boolean undefineItems(List<INativeContinuousItem> items) {
        /*  72 */
        return undefineItems(this.v.getInputRoutine(), items);
        /*     */
    }

    /*     */
    /*     */
    static boolean undefineItems(INativeMethodItem routine, List<INativeContinuousItem> items) {
        /*  76 */
        IStackframeManager manager = routine.getData().getStackframeManager();
        /*  77 */
        for (INativeContinuousItem item : items) {
            /*  78 */
            if (!manager.undefineItem(item.getMemoryAddress())) {
                /*  79 */
                return false;
                /*     */
            }
            /*     */
        }
        /*  82 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    protected boolean offerClearItems(int offset, int size) {
        /*  86 */
        return offerClearItems(this.v.getInputRoutine(), offset, size);
        /*     */
    }

    /*     */
    /*     */
    static boolean offerClearItems(INativeMethodItem routine, int offset, int size) {
        /*  90 */
        List<INativeContinuousItem> r = collectItemsToBeUndefined(routine, offset, size);
        /*  91 */
        if (r.size() > 1) {
            /*  92 */
            String msg = String.format("This action will undefine %d fields. Proceed?", new Object[]{Integer.valueOf(r.size() - 1)});
            /*     */
            /*  94 */
            AdaptivePopupDialog dlg2 = new AdaptivePopupDialog(UI.getShellTracker().get(), 2, S.s(207), msg, null);
            /*  95 */
            if (dlg2.open().intValue() == 0) {
                /*  96 */
                return false;
                /*     */
            }
            /*     */
        }
        /*  99 */
        if (!undefineItems(routine, r)) {
            /* 100 */
            UI.error("An error occurred while undefining fields.");
            /* 101 */
            return false;
            /*     */
        }
        /* 103 */
        return true;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */