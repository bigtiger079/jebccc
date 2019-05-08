package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class DebuggerDetachHandler extends DebuggerBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(DebuggerDetachHandler.class);

    public DebuggerDetachHandler() {
        super("dbgDetach", S.s(484), "Detach from the actively debugged target", null, 0);
    }

    public void execute() {
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        boolean success = dbg.detach();
        if (!success) {
            UI.error("The target could not be detached");
            return;
        }
        this.context.setDebuggingMode(false);
    }
}


