package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class WindowOpenNewView extends OperationHandler {
    public WindowOpenNewView() {
        super(Operation.VIEW_NEW, null, S.s(530), null, null);
    }
}


