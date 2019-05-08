package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileCloseHandler extends JebBaseHandler {
    public FileCloseHandler() {
        super(null, S.s(466), "Close the currently opened project", null);
    }

    public boolean canExecute() {
        return this.context.hasOpenedProject();
    }

    public void execute() {
        this.context.attemptCloseOpenedProject(this.shell);
    }
}


