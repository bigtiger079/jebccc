package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class NavigationCanvasZoomInHandler extends OperationHandler {
    public NavigationCanvasZoomInHandler() {
        super(Operation.ZOOM_IN, null, "Zoom In", "", null);
        setAccelerator(93);
    }
}


