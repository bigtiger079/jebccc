
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.ListParsersDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileEnginesParsersHandler
        extends JebBaseHandler {
    public FileEnginesParsersHandler() {
        super(null, "Parsers...", null, null);
    }

    public boolean canExecute() {
        return this.context.getEnginesContext() != null;
    }

    public void execute() {
        ListParsersDialog dlg = new ListParsersDialog(this.shell);
        if (this.context.getOpenedProject() != null) {
            dlg.setInput(this.context.getOpenedProject());
        } else {
            dlg.setInput(this.context.getEnginesContext());
        }
        dlg.open();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesParsersHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */