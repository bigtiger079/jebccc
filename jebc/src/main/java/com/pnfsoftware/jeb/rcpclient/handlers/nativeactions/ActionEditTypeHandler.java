package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeType;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IPrototypeItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeLibrary;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeUtil;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.util.collect.Lists;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class ActionEditTypeHandler extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionEditTypeHandler.class);

    public ActionEditTypeHandler() {
        super("editType", S.s(495), 89);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part, true);
        long a = getActiveMemoryAddress(this.part, pbcu, true);
        String currentTypeSig = null;
        INativeMethodItem m = pbcu.getInternalMethod(a, true);
        if (m != null) {
            IPrototypeItem proto = m.getPrototype();
            currentTypeSig = proto == null ? null : proto.getSignature(true);
        } else {
            ICodeType type = pbcu.getDataTypeAt(a);
            currentTypeSig = type == null ? null : type.getSignature(true);
        }
        String caption = String.format("Edit type at %Xh", a);
        TextDialog dlg = new TextDialog(this.shell, caption, currentTypeSig, null);
        dlg.setLineCount(1);
        dlg.setSelected(true);
        String typestr = dlg.open();
        if (typestr == null) {
            return;
        }
        if (m != null) {
            boolean r = pbcu.setRoutinePrototype(m, typestr);
            if (!r) {
                logger.error("Failed to set prototype of method %s (%Xh)", m.getName(true), a);
            }
        } else {
            ITypeManager typeman = pbcu.getTypeManager();
            INativeType t = typeman.getType(typestr);
            if (t == null) {
                t = Lists.getFirst(TypeUtil.findType(pbcu.getTypeManager().getTypes(), typestr, true));
                if (t == null) {
                    for (ITypeLibrary typelib : pbcu.getTypeLibraryService().getLoadedTypeLibraries()) {
                        t = Lists.getFirst(TypeUtil.findType(typelib.getTypes(), typestr, true));
                        if (t != null) {
                            break;
                        }
                    }
                }
            }
            if (t == null) {
                return;
            }
            if (!pbcu.setDataTypeAt(a, t)) {
                logger.error("Failed to set type at address %Xh", a);
            }
        }
        postExecute(this.shell);
    }
}


