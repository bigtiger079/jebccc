
package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class WindowOpenView
        extends OperationHandler {
    public WindowOpenView() {
        super(Operation.VIEW, null, S.s(532), null, null);
    }
}


