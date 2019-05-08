package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class FileDeleteHandler extends OperationHandler {
    public FileDeleteHandler() {
        super(Operation.DELETE, null, S.s(482), null, null);
    }
}


