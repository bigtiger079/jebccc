/*    */
package com.pnfsoftware.jeb.rcpclient.handlers;
/*    */
/*    */

import com.pnfsoftware.jeb.client.api.IOperable;
/*    */ import com.pnfsoftware.jeb.client.api.Operation;
/*    */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*    */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class OperationHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 23 */   private static final ILogger logger = GlobalLog.getLogger(OperationHandler.class);
    /*    */   private Operation op;

    /*    */
    /*    */
    public OperationHandler(Operation op, String id, String name, String tooltip, String icon)
    /*    */ {
        /* 28 */
        super(id, name, 0, tooltip, icon, 0);
        /* 29 */
        this.op = op;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 35 */
        if (this.part == null) {
            /* 36 */
            return false;
            /*    */
        }
        /*    */
        /*    */
        /* 40 */
        switch (this.op) {
            /*    */
            case JUMP_TO:
                /* 42 */
                if (isDisableHandlers(this.part)) {
                    /* 43 */
                    return false;
                    /*    */
                }
                /*    */
                /*    */
                break;
            /*    */
        }
        /*    */
        /*    */
        /* 50 */
        Object object = this.part.getManager();
        /* 51 */
        if ((object instanceof IOperable)) {
            /* 52 */
            return ((IOperable) object).verifyOperation(new OperationRequest(this.op));
            /*    */
        }
        /* 54 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 59 */
        if ((this.op == Operation.PARSE_AT) || (this.op == Operation.EXTRACT_TO)) {
            /* 60 */
            this.context.getTelemetry().record("operation" + this.op.toString());
            /*    */
        }
        /*    */
        /* 63 */
        Object object = this.part.getManager();
        /* 64 */
        if ((object instanceof IOperable)) {
            /* 65 */
            ((IOperable) object).doOperation(new OperationRequest(this.op));
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\OperationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */