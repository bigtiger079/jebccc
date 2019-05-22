package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import org.eclipse.swt.SWT;

public class WindowFocusLoggerHandler extends JebBaseHandler {
    public WindowFocusLoggerHandler() {
        super("showLogger", S.s(565), null, null);
        setAccelerator(SWT.MOD1 | 0x32);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        this.context.getPartManager().activateLogger(true);
    }
}


