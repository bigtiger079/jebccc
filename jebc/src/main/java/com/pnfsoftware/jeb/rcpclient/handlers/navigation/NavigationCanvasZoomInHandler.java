
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;


import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;


public class NavigationCanvasZoomInHandler
        extends OperationHandler {

    public NavigationCanvasZoomInHandler() {

        super(Operation.ZOOM_IN, null, "Zoom In", "", null);

        setAccelerator(93);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationCanvasZoomInHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */