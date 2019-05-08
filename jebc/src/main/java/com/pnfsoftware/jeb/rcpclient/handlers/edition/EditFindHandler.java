package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class EditFindHandler extends OperationHandler {
    public EditFindHandler() {
        super(Operation.FIND, null, S.s(505), "", null);
        setAccelerator(SWT.MOD1 | 0x46);
    }
}


