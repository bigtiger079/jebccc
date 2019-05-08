package com.pnfsoftware.jeb.rcpclient.handlers.help;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.dialogs.AboutDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class HelpAboutHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(HelpAboutHandler.class);

    public HelpAboutHandler() {
        super(null, S.s(458), null, "jeb1/icon-jeb.png");
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        new AboutDialog(this.shell, this.context).open();
    }
}


