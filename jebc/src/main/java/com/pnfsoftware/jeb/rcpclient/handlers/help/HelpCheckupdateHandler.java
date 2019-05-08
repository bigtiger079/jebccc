package com.pnfsoftware.jeb.rcpclient.handlers.help;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.dialogs.SoftwareUpdateDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class HelpCheckupdateHandler extends JebBaseHandler {
    public HelpCheckupdateHandler() {
        super(null, S.s(465), null, null);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        new SoftwareUpdateDialog(this.shell, this.context, false).open();
    }
}


