package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import org.eclipse.swt.SWT;

public class OperationSelectAll extends AbstractOperation {
    public OperationSelectAll(IOperable object) {
        super(object, S.s(721));
        setAccelerator(SWT.MOD1 | 0x41);
    }

    protected String getOperationImageData() {
        return null;
    }

    protected Operation getOperation() {
        return Operation.SELECT_ALL;
    }
}


