
package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import org.eclipse.swt.SWT;

public class OperationCopy
        extends AbstractOperation {
    public OperationCopy(IOperable object) {
        super(object, S.s(211));
        setAccelerator(SWT.MOD1 | 0x43);
    }

    protected String getOperationImageData() {
        return "eclipse/copy_edit.png";
    }

    protected Operation getOperation() {
        return Operation.COPY;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationCopy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */