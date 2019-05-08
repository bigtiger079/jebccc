
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.StackframeEditorDialog;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.SWT;

public class ActionEditStackframeHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionEditStackframeHandler.class);

    public ActionEditStackframeHandler() {
        super("editStackframe", S.s(493), SWT.MOD1 | SWT.MOD3 | 0x4B);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true, true, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part, true);
        long a = getActiveMemoryAddress(this.part, pbcu);
        INativeMethodItem routine = pbcu.getInternalMethod(a, false);
        if (routine == null) {
            return;
        }
        StackframeEditorDialog dlg = new StackframeEditorDialog(this.shell, pbcu, routine, this.context.getFontManager());
        dlg.open();
        postExecute(this.shell);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditStackframeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */