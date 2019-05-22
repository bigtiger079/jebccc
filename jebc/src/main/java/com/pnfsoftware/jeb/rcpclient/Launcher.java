package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class Launcher {
    private static final ILogger logger = GlobalLog.getLogger(Launcher.class);

    public static void main(String[] args) {
        JebApp app = new JebApp(args);
        app.build();
        app.run();
    }
}


