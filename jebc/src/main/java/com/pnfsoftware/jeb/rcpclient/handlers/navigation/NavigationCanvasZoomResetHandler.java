
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class NavigationCanvasZoomResetHandler
        extends OperationHandler {
    public NavigationCanvasZoomResetHandler() {
        super(Operation.ZOOM_RESET, null, "Reset Zoom Level", "", null);
        setAccelerator(SWT.MOD1 + 92);
    }
}


