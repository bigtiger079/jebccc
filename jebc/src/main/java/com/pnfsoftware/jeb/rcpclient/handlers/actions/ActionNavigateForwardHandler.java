
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class ActionNavigateForwardHandler
        extends OperationHandler {
    public ActionNavigateForwardHandler() {
        super(Operation.NAVIGATE_FORWARD, "navForward", S.s(521), "Navigate to the next view in the history stack of views", "eclipse/forward_nav.png");
        setAccelerator(SWT.MOD3 | 0x1000004);
    }
}


