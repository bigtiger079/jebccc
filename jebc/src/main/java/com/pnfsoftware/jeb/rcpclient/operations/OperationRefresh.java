package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;

public class OperationRefresh extends AbstractOperation {
    public OperationRefresh(IOperable object) {
        super(object, S.s(33));
    }

    protected String getOperationImageData() {
        return "eclipse/refresh_tab.png";
    }

    protected Operation getOperation() {
        return Operation.REFRESH;
    }
}


