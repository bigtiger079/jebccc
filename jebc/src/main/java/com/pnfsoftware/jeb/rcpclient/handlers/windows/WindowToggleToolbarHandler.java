
package com.pnfsoftware.jeb.rcpclient.handlers.windows;


import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.SWT;


public class WindowToggleToolbarHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(WindowToggleToolbarHandler.class);


    public WindowToggleToolbarHandler() {

        super(null, "Toggle the toolbar visibility", 0, null, null, SWT.MOD1 | 0x54);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        boolean visible = this.context.getApp().isToolbarVisible();

        this.context.getApp().setToolbarVisibility(!visible);

    }

}


