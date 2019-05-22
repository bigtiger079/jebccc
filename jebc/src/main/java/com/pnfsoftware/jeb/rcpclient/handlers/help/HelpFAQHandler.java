package com.pnfsoftware.jeb.rcpclient.handlers.help;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;

import java.io.File;

public class HelpFAQHandler extends JebBaseHandler {
    public HelpFAQHandler() {
        super(null, S.s(503), null, null);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        File f = new File(this.context.getBaseDirectory(), "doc" + File.separator + "manual" + File.separator + "faq" + File.separator + "index.html");
        if (f.isFile()) {
            BrowserUtil.openInBrowser(f);
        } else {
            BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/manual/faq");
        }
    }
}


