
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.ListTypelibsDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileEnginesTypelibsHandler
        extends JebBaseHandler {
    public FileEnginesTypelibsHandler() {
        super(null, "Type Libraries...", null, null);
    }

    public boolean canExecute() {
        return this.context.getEnginesContext() != null;
    }

    public void execute() {
        IEnginesContext engctx = this.context.getEnginesContext();
        if (engctx == null) {
            return;
        }
        ListTypelibsDialog dlg = new ListTypelibsDialog(this.shell, this.context);
        dlg.setInput(engctx.getTypeLibraryService());
        dlg.open();
    }
}


