
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;

public class DebuggerRestartHandler
        extends DebuggerBaseHandler {
    public DebuggerRestartHandler() {
        super("dbgRestart", S.s(549), "Attempt to restart the debugging session", null, 0);
    }

    public boolean canExecute() {
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        return (dbg != null) && (!dbg.isAttached()) && (canAttachDebugger(this.part));
    }

    public void execute() {
        PartManager pman = this.context.getPartManager();
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        boolean success = dbg.restart();
        if (!success) {
            return;
        }
        restoreUIBreakpoints(dbg);
        this.context.setDebuggingMode(true);
        pman.create(dbg, true);
    }
}


