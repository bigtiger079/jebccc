package com.pnfsoftware.jeb.rcpclient.handlers;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class OperationHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(OperationHandler.class);
    private Operation op;

    public OperationHandler(Operation op, String id, String name, String tooltip, String icon) {
        super(id, name, 0, tooltip, icon, 0);
        this.op = op;
    }

    public boolean canExecute() {
        if (this.part == null) {
            return false;
        }
        switch (this.op) {
            case JUMP_TO:
                if (isDisableHandlers(this.part)) {
                    return false;
                }
                break;
        }
        Object object = this.part.getManager();
        if ((object instanceof IOperable)) {
            return ((IOperable) object).verifyOperation(new OperationRequest(this.op));
        }
        return false;
    }

    public void execute() {
        if ((this.op == Operation.PARSE_AT) || (this.op == Operation.EXTRACT_TO)) {
            this.context.getTelemetry().record("operation" + this.op.toString());
        }
        Object object = this.part.getManager();
        if ((object instanceof IOperable)) {
            ((IOperable) object).doOperation(new OperationRequest(this.op));
        }
    }
}


