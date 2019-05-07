
package com.pnfsoftware.jeb.rcpclient.handlers.edition;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;


public class EditPasteHandler
        extends OperationHandler {

    public EditPasteHandler() {

        super(Operation.PASTE, null, S.s(537), "", "eclipse/paste_edit.png");

        setAccelerator(SWT.MOD1 | 0x56);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditPasteHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */