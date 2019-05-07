
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;


public class DebuggerRunHandler
        extends DebuggerBaseHandler {

    public DebuggerRunHandler() {

        super("dbgRun", S.s(552), null, "eclipse/resume_co.png", 0);

        setAccelerator(16777233);

    }


    public boolean canExecute() {

        return canStepOperation(this.part);

    }


    public void execute() {

        getCurrentDebugger(this.part).run();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerRunHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */