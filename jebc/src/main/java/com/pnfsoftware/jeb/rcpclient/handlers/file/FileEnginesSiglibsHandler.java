
package com.pnfsoftware.jeb.rcpclient.handlers.file;


import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.ListSiglibsDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;


public class FileEnginesSiglibsHandler
        extends JebBaseHandler {

    public FileEnginesSiglibsHandler() {

        super(null, "Signature Libraries...", null, null);

    }


    public boolean canExecute() {

        return this.context.getEnginesContext() != null;

    }


    public void execute() {

        IEnginesContext engctx = this.context.getEnginesContext();

        if (engctx == null) {

            return;

        }


        ListSiglibsDialog dlg = new ListSiglibsDialog(this.shell, this.context);

        dlg.setInput(engctx.getNativeSignatureDBManager());

        dlg.open();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesSiglibsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */