package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileExitHandler extends JebBaseHandler {
    public FileExitHandler() {
        super(null, S.s(500), null, null);
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        this.context.getApp().close();
    }
}


