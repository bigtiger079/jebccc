
package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

public class EditCopyHandler
        extends OperationHandler {
    public EditCopyHandler() {
        super(Operation.COPY, null, S.s(470), "", "eclipse/copy_edit.png");
        setAccelerator(SWT.MOD1 | 0x43);
    }

    public void execute() {
        if (Licensing.isDemoBuild()) {
            MessageDialog.openWarning(this.shell, S.s(249), "Copying text is not a feature available in the demo version");
            return;
        }
        super.execute();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditCopyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */