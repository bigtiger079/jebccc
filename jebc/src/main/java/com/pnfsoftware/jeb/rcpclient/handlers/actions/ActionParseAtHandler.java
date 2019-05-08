
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class ActionParseAtHandler
        extends OperationHandler {
    public ActionParseAtHandler() {
        super(Operation.PARSE_AT, "parseAt", S.s(535), null, null);
    }
}


