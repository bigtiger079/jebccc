package com.pnfsoftware.jeb.rcpclient.handlers.help;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class HelpChangelistHandler extends JebBaseHandler {
    public HelpChangelistHandler() {
        super(null, S.s(464), null, null);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        this.context.openChangelistDialog(this.shell, null);
    }
}


