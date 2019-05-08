package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class NavigationCanvasZoomOutHandler extends OperationHandler {
    public NavigationCanvasZoomOutHandler() {
        super(Operation.ZOOM_OUT, null, "Zoom Out", "", null);
        setAccelerator(91);
    }
}


