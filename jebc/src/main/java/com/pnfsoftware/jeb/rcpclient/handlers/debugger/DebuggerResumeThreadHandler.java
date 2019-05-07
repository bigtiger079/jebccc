
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;


public class DebuggerResumeThreadHandler
        extends DebuggerBaseHandler {

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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerResumeThreadHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */