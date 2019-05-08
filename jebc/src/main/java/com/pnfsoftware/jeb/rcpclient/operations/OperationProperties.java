package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import org.eclipse.swt.SWT;

public class OperationProperties extends AbstractOperation {
    public OperationProperties(IOperable object) {
        super(object, S.s(666));
        setAccelerator(SWT.MOD3 | 0xD);
    }

    protected String getOperationImageData() {
        return null;
    }

    protected Operation getOperation() {
        return Operation.PROPERTIES;
    }
}


