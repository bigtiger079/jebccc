
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class NavigationItemNextHandler
        extends OperationHandler {
    public NavigationItemNextHandler() {
        super(Operation.ITEM_NEXT, "navNext", S.s(525), "", "eclipse/next_nav.png");
        setAccelerator(SWT.MOD1 | 0x2E);
    }
}


