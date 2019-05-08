
package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class EditClearHandler
        extends OperationHandler {
    public EditClearHandler() {
        super(Operation.CLEAR, null, S.s(198), null, "eclipse/clear_co.png");
        setAccelerator(SWT.MOD1 | 0x4C);
    }
}


