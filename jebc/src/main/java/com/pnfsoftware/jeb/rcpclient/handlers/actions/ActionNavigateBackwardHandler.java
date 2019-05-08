package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class ActionNavigateBackwardHandler extends OperationHandler {
    public ActionNavigateBackwardHandler() {
        super(Operation.NAVIGATE_BACKWARD, "navBackward", S.s(520), "Navigate to the previous view in the history stack of views", "eclipse/backward_nav.png");
        setAccelerator(SWT.MOD3 | 0x1000003);
        addExtraAccelerator(27);
    }
}


