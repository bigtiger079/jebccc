
package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;

import org.eclipse.swt.program.Program;

public class BrowserUtil {
    private static final ILogger logger = GlobalLog.getLogger(BrowserUtil.class);

    public static boolean openInBrowser(String url) {
        try {
            if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
                return false;
            }
            return Program.launch(url);
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    public static boolean openInBrowser(File file) {
        try {
            return Program.launch(file.getAbsolutePath());
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }
}


