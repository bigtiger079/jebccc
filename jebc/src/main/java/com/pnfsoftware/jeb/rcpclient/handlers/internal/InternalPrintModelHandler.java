
package com.pnfsoftware.jeb.rcpclient.handlers.internal;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.SWT;

public class InternalPrintModelHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(InternalPrintModelHandler.class);

    public InternalPrintModelHandler() {
        super(null, "Print Model", null, null);
        setAccelerator(SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | 0x4D);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        logger.i(this.context.getApp().getDock().formatStructure(), new Object[0]);
    }
}


