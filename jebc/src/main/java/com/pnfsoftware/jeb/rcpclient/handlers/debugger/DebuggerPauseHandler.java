package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;

public class DebuggerPauseHandler extends DebuggerBaseHandler {
    public DebuggerPauseHandler() {
        super("dbgPause", S.s(538), null, "eclipse/suspend_co.png", 0);
    }

    public boolean canExecute() {
        return (isDebuggerAttached(this.part)) && (!canStepOperation(this.part));
    }

    public void execute() {
        getCurrentDebugger(this.part).pause();
    }
}


