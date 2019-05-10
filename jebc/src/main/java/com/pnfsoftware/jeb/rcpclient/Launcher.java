package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Launcher {
    private static final ILogger logger = GlobalLog.getLogger(Launcher.class);

    public static void main(String[] args) {
        JebApp app = new JebApp(args);
        app.build();
        app.run();
    }
}


