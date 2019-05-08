
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeChooserDialog;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class ActionSelectTypeHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionSelectTypeHandler.class);

    public ActionSelectTypeHandler() {
        super("selectType", S.s(560), 84);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        NativeTypeChooserDialog dlg = new NativeTypeChooserDialog(this.shell, pbcu);
        INativeType t = dlg.open();
        if (t == null) {
            return;
        }
        if (!pbcu.setDataTypeAt(a, t)) {
            logger.error("Failed to change type at address %Xh", new Object[]{Long.valueOf(a)});
        }
        postExecute(this.shell);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionSelectTypeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */