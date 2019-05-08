
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class ActionJumpToHandler
        extends OperationHandler {
    public ActionJumpToHandler() {
        super(Operation.JUMP_TO, "jump", S.s(513), "Jump to (navigate to) a given address or location", "eclipse/runtoline_co.png");
        setAccelerator(71);
    }
}


