
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.operations.JebAction;

import java.util.ArrayList;
import java.util.List;

public abstract class StructEditorAction
        extends JebAction {
    protected NativeTypeEditorView v;

    public StructEditorAction(String name, NativeTypeEditorView v) {
        super(null, name);
        this.v = v;
        this.isContextual = true;
    }

    protected IStructureTypeField getSelectedField() {
        ItemEntry e = this.v.getSelectedEntry();
        if (e != null) {
            return this.v.getInputType().getFieldAt(e.offset);
        }
        return null;
    }

    protected List<IStructureTypeField> collectFieldsToBeUndefined(int startOffset, int wantedSize) {
        return collectFieldsToBeUndefined(this.v.getInputType(), startOffset, wantedSize);
    }

    static List<IStructureTypeField> collectFieldsToBeUndefined(IStructureType type, int startOffset, int wantedSize) {
        List<IStructureTypeField> r = new ArrayList();
        int offset = startOffset;
        IStructureTypeField f = type.getFieldOver(offset);
        if (f != null) {
            r.add(f);
        }
        for (; ; ) {
            f = type.getFieldAfter(offset);
            if (f == null) {
                break;
            }
            if (f.getOffset() - startOffset >= wantedSize) {
                break;
            }
            offset = f.getOffset();
            r.add(f);
        }
        return r;
    }

    protected boolean undefineFields(List<IStructureTypeField> fields) {
        return undefineFields(this.v.getInputType(), fields);
    }

    static boolean undefineFields(IStructureType type, List<IStructureTypeField> fields) {
        ITypeManager typeman = type.getTypeManager();
        for (IStructureTypeField field : fields) {
            if (!typeman.removeStructureField(type, field)) {
                return false;
            }
        }
        return true;
    }

    protected boolean offerClearFields(int offset, int size) {
        return offerClearFields(this.v.getInputType(), offset, size);
    }

    static boolean offerClearFields(IStructureType type, int offset, int size) {
        List<IStructureTypeField> r = collectFieldsToBeUndefined(type, offset, size);
        if (r.size() > 1) {
            String msg = String.format("This action will undefine %d fields. Proceed?", new Object[]{Integer.valueOf(r.size() - 1)});
            AdaptivePopupDialog dlg2 = new AdaptivePopupDialog(UI.getShellTracker().get(), 2, S.s(207), msg, null);
            if (dlg2.open().intValue() == 0) {
                return false;
            }
        }
        if (!undefineFields(type, r)) {
            UI.error("An error occurred while undefining fields.");
            return false;
        }
        return true;
    }
}


