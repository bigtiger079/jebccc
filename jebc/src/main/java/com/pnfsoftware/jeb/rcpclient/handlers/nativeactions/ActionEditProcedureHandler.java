package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.EditMethodDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.MethodSetupInformation;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;

import java.util.Objects;

import org.eclipse.swt.SWT;

public class ActionEditProcedureHandler extends NativeCodeBaseHandler {
    public ActionEditProcedureHandler() {
        super("editProcedure", S.s(492), SWT.MOD1 | SWT.MOD3 | 0x50);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true, false, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        INativeMethodItem m = pbcu.getInternalMethod(a, false);
        if (m == null) {
            UI.error("No routine was found at this address");
            return;
        }
        EditMethodDialog dlg = new EditMethodDialog(this.shell, pbcu, m);
        MethodSetupInformation info = dlg.open();
        if (info == null) {
            return;
        }
        if (!Objects.equals(info.getRoutineNonReturning(), m.getNonReturning())) {
            m.setNonReturning(info.getRoutineNonReturning());
        }
        if (!Objects.equals(info.getRoutineDataSPDeltaOnReturn(), m.getData().getSPDeltaOnReturn())) {
            m.getData().setSPDeltaOnReturn(info.getRoutineDataSPDeltaOnReturn());
        }
        postExecute(this.shell);
    }
}
