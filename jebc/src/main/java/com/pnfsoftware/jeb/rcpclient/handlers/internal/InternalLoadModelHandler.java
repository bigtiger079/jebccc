package com.pnfsoftware.jeb.rcpclient.handlers.internal;

import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class InternalLoadModelHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(InternalLoadModelHandler.class);

    public InternalLoadModelHandler() {
        super(null, "Load Model", null, null);
    }

    public boolean canExecute() {
        return false;
    }

    public void execute() {
    }
}


