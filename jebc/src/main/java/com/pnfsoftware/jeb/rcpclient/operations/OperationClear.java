
package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import org.eclipse.swt.SWT;

public class OperationClear
        extends AbstractOperation {
    public OperationClear(IOperable object) {
        super(object, S.s(198));
        setAccelerator(SWT.MOD1 | 0x4C);
    }

    protected String getOperationImageData() {
        return "eclipse/clear_co.png";
    }

    protected Operation getOperation() {
        return Operation.CLEAR;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationClear.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */