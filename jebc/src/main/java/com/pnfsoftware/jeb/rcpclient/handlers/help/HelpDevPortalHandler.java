package com.pnfsoftware.jeb.rcpclient.handlers.help;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;

public class HelpDevPortalHandler extends JebBaseHandler {
    public HelpDevPortalHandler() {
        super(null, S.s(485), null, null);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/devportal");
    }
}


