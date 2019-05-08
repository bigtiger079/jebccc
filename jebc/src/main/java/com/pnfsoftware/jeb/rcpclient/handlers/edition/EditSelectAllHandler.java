
package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class EditSelectAllHandler
        extends OperationHandler {
    public EditSelectAllHandler() {
        super(Operation.SELECT_ALL, null, S.s(559), "", null);
        setAccelerator(SWT.MOD1 | 0x41);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditSelectAllHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */