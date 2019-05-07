/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.JebAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public abstract class StructEditorAction
        /*     */ extends JebAction
        /*     */ {
    /*     */   protected NativeTypeEditorView v;

    /*     */
    /*     */
    public StructEditorAction(String name, NativeTypeEditorView v)
    /*     */ {
        /*  30 */
        super(null, name);
        /*  31 */
        this.v = v;
        /*  32 */
        this.isContextual = true;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected IStructureTypeField getSelectedField()
    /*     */ {
        /*  41 */
        ItemEntry e = this.v.getSelectedEntry();
        /*  42 */
        if (e != null) {
            /*  43 */
            return this.v.getInputType().getFieldAt(e.offset);
            /*     */
        }
        /*  45 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    protected List<IStructureTypeField> collectFieldsToBeUndefined(int startOffset, int wantedSize) {
        /*  49 */
        return collectFieldsToBeUndefined(this.v.getInputType(), startOffset, wantedSize);
        /*     */
    }

    /*     */
    /*     */
    static List<IStructureTypeField> collectFieldsToBeUndefined(IStructureType type, int startOffset, int wantedSize) {
        /*  53 */
        List<IStructureTypeField> r = new ArrayList();
        /*     */
        /*  55 */
        int offset = startOffset;
        /*  56 */
        IStructureTypeField f = type.getFieldOver(offset);
        /*  57 */
        if (f != null) {
            /*  58 */
            r.add(f);
            /*     */
        }
        /*     */
        for (; ; )
            /*     */ {
            /*  62 */
            f = type.getFieldAfter(offset);
            /*  63 */
            if (f == null) {
                /*     */
                break;
                /*     */
            }
            /*     */
            /*  67 */
            if (f.getOffset() - startOffset >= wantedSize) {
                /*     */
                break;
                /*     */
            }
            /*     */
            /*  71 */
            offset = f.getOffset();
            /*  72 */
            r.add(f);
            /*     */
        }
        /*  74 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    protected boolean undefineFields(List<IStructureTypeField> fields) {
        /*  78 */
        return undefineFields(this.v.getInputType(), fields);
        /*     */
    }

    /*     */
    /*     */
    static boolean undefineFields(IStructureType type, List<IStructureTypeField> fields) {
        /*  82 */
        ITypeManager typeman = type.getTypeManager();
        /*     */
        /*  84 */
        for (IStructureTypeField field : fields) {
            /*  85 */
            if (!typeman.removeStructureField(type, field)) {
                /*  86 */
                return false;
                /*     */
            }
            /*     */
        }
        /*  89 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    protected boolean offerClearFields(int offset, int size) {
        /*  93 */
        return offerClearFields(this.v.getInputType(), offset, size);
        /*     */
    }

    /*     */
    /*     */
    static boolean offerClearFields(IStructureType type, int offset, int size) {
        /*  97 */
        List<IStructureTypeField> r = collectFieldsToBeUndefined(type, offset, size);
        /*  98 */
        if (r.size() > 1) {
            /*  99 */
            String msg = String.format("This action will undefine %d fields. Proceed?", new Object[]{Integer.valueOf(r.size() - 1)});
            /*     */
            /* 101 */
            AdaptivePopupDialog dlg2 = new AdaptivePopupDialog(UI.getShellTracker().get(), 2, S.s(207), msg, null);
            /* 102 */
            if (dlg2.open().intValue() == 0) {
                /* 103 */
                return false;
                /*     */
            }
            /*     */
        }
        /* 106 */
        if (!undefineFields(type, r)) {
            /* 107 */
            UI.error("An error occurred while undefining fields.");
            /* 108 */
            return false;
            /*     */
        }
        /* 110 */
        return true;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StructEditorAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */