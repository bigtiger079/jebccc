package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import org.eclipse.swt.SWT;

public class OperationFind extends AbstractOperation {
    public OperationFind(IOperable object) {
        super(object, S.s(345));
        setAccelerator(SWT.MOD1 | 0x46);
    }

    protected Operation getOperation() {
        return Operation.FIND;
    }
}


