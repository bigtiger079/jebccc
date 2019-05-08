package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import org.eclipse.swt.SWT;

public class OperationFindNext extends AbstractOperation {
    public OperationFindNext(IOperable object) {
        super(object, S.s(346));
        setAccelerator(SWT.MOD1 | 0x4B);
    }

    protected Operation getOperation() {
        return Operation.FIND_NEXT;
    }
}


