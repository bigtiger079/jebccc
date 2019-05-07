
package com.pnfsoftware.jeb.rcpclient.handlers.windows;


import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import org.eclipse.swt.SWT;


public class WindowFocusTerminalHandler
        extends JebBaseHandler {

    public WindowFocusTerminalHandler() {

        super("showTerminal", "Show Terminal View", null, null);

        setAccelerator(SWT.MOD1 | 0x33);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        this.context.getPartManager().activateTerminal(true);

    }

}


