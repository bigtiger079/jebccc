
package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class EditFindnextHandler
        extends OperationHandler {
    public EditFindnextHandler() {
        super(Operation.FIND_NEXT, null, S.s(506), "", null);
        setAccelerator(SWT.MOD1 | 0x4B);
    }
}


