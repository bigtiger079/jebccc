package com.pnfsoftware.jeb.rcpclient.handlers.help;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;

import java.io.File;

public class HelpUserManualHandler extends JebBaseHandler {
    public HelpUserManualHandler() {
        super(null, S.s(581), null, null);
        setAccelerator(16777226);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        File f = new File(this.context.getBaseDirectory(), "doc" + File.separator + "manual" + File.separator + "index.html");
        if (f.isFile()) {
            BrowserUtil.openInBrowser(f);
        } else {
            BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/manual");
        }
    }
}


