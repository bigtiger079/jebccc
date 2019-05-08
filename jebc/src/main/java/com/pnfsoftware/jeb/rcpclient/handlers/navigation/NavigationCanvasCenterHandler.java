package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class NavigationCanvasCenterHandler extends OperationHandler {
    public NavigationCanvasCenterHandler() {
        super(Operation.CENTER, null, "Center", "", null);
        setAccelerator(92);
    }
}


