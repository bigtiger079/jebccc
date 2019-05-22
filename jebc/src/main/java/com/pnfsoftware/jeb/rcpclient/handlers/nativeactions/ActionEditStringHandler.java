package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.DefineStringDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.StringSetupInformation;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.SWT;

public class ActionEditStringHandler extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionEditStringHandler.class);

    public ActionEditStringHandler() {
        super("editString", S.s(494), SWT.MOD1 | SWT.MOD3 | 0x41);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        DefineStringDialog dlg = new DefineStringDialog(this.shell, a, pbcu);
        StringSetupInformation info = dlg.open();
        if (info == null) {
            return;
        }
        if (!pbcu.setStringAt(info.address, info.addressMax, info.stringType, info.minChars, info.maxChars)) {
            logger.error("Failed to define string at address %Xh", info.address);
        }
        postExecute(this.shell);
    }
}


