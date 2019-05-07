
package com.pnfsoftware.jeb.rcpclient.handlers.windows;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import org.eclipse.swt.SWT;


public class WindowFocusProjectExplorerHandler
        extends JebBaseHandler {

    public WindowFocusProjectExplorerHandler() {

        super("showProjectExplorer", S.s(566), null, null);

        setAccelerator(SWT.MOD1 | 0x31);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        this.context.getPartManager().activateProjectExplorer(true);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowFocusProjectExplorerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */