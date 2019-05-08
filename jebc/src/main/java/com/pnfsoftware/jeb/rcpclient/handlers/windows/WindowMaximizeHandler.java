package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class WindowMaximizeHandler extends JebBaseHandler {
    final String tagMaximized = "Maximized";

    public WindowMaximizeHandler() {
        super(null, S.s(517), null, null);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
    }
}


