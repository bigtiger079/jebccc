package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IReferenceType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeUtil;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeEditorDialog;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.SWT;

public class ActionOpenTypeEditorHandler extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionOpenTypeEditorHandler.class);

    public ActionOpenTypeEditorHandler() {
        super("openTypeEditor", S.s(531), SWT.MOD1 | SWT.MOD3 | 0x54);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, false, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part, true);
        INativeType type0 = null;
        long itemId = getActiveItemId(this.part);
        if (itemId != 0L) {
            INativeItem item = pbcu.getItemObject(itemId);
            if ((item instanceof INativeType)) {
                type0 = (INativeType) item;
            }
        }
        if (type0 == null) {
            long a = getActiveMemoryAddress(this.part, pbcu, true);
            if (a != -1L) {
                INativeContinuousItem item = pbcu.getNativeItemAt(a);
                if ((item instanceof INativeDataItem)) {
                    type0 = ((INativeDataItem) item).getType();
                }
            }
        }
        if ((type0 != null) && (!(type0 instanceof IStructureType))) {
            type0 = TypeUtil.getNonAlias(type0);
            if ((type0 instanceof IReferenceType)) {
                type0 = ((IReferenceType) type0).getMainType();
            } else if ((type0 instanceof IArrayType)) {
                type0 = ((IArrayType) type0).getElementType();
            }
            type0 = TypeUtil.getNonAlias(type0);
        }
        IStructureType type = null;
        if ((type0 instanceof IStructureType)) {
            type = (IStructureType) type0;
        }
        NativeTypeEditorDialog dlg = new NativeTypeEditorDialog(this.shell, pbcu, type, this.context.getFontManager());
        dlg.open();
        postExecute(this.shell);
    }
}


