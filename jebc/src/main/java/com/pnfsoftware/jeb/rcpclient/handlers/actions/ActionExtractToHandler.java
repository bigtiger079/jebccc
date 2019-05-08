
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class ActionExtractToHandler
        extends OperationHandler {
    public ActionExtractToHandler() {
        super(Operation.EXTRACT_TO, "extract", S.s(502), null, null);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionExtractToHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */