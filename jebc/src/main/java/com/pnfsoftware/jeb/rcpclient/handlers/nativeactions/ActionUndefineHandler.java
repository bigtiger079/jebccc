
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class ActionUndefineHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionUndefineHandler.class);

    public ActionUndefineHandler() {
        super("undefine", S.s(580), 85);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true);
    }

    public boolean canExecuteAt(INativeCodeUnit<?> pbcu, long memAddress) {
        if (memAddress == -1L) {
            return false;
        }
        INativeItem item = pbcu.getNativeItemAt(memAddress);
        return item != null;
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        if (!pbcu.undefineItem(a)) {
            logger.error("Failed to undefine item at address %Xh", new Object[]{Long.valueOf(a)});
        }
        postExecute(this.shell);
    }
}


