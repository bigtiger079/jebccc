
package com.pnfsoftware.jeb.rcpclient.handlers.edition;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;


public class EditCutHandler
        extends OperationHandler {

    public EditCutHandler() {

        super(Operation.CUT, null, S.s(473), "", "eclipse/cut_edit.png");

        setAccelerator(SWT.MOD1 | 0x58);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditCutHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */