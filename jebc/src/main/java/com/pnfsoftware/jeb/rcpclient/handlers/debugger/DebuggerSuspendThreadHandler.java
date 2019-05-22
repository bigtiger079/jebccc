package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;

public class DebuggerSuspendThreadHandler extends DebuggerBaseHandler {
    public DebuggerSuspendThreadHandler() {
        super("dbgSuspendThread", S.s(576), null, null, 0);
    }

    public boolean canExecute() {
        return hasDefaultThread(this.part);
    }

    public void execute() {
        getCurrentDebugger(this.part).getDefaultThread().suspend();
    }
}


