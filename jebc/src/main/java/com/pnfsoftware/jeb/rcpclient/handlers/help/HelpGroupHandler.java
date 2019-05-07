
package com.pnfsoftware.jeb.rcpclient.handlers.help;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;


public class HelpGroupHandler
        extends JebBaseHandler {

    public HelpGroupHandler() {

        super(null, S.s(462), null, null);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/forum");

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\help\HelpGroupHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */