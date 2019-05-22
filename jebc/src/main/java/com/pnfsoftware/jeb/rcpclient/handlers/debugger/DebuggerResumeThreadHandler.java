package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;

public class DebuggerResumeThreadHandler extends DebuggerBaseHandler {
    public DebuggerResumeThreadHandler() {
        super("dbgResumeThread", S.s(551), null, null, 0);
    }

    public boolean canExecute() {
        return hasDefaultThread(this.part);
    }

    public void execute() {
        getCurrentDebugger(this.part).getDefaultThread().resume();
    }
}


