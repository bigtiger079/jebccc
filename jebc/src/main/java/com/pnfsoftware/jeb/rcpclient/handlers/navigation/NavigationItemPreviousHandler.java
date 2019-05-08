
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class NavigationItemPreviousHandler
        extends OperationHandler {
    public NavigationItemPreviousHandler() {
        super(Operation.ITEM_PREVIOUS, "navPrevious", S.s(541), "", "eclipse/prev_nav.png");
        setAccelerator(SWT.MOD1 | 0x2C);
    }
}


