
package com.pnfsoftware.jeb.rcpclient.handlers.windows;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import org.eclipse.swt.SWT;


public class WindowFocusLoggerHandler
        extends JebBaseHandler {

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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowFocusLoggerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */