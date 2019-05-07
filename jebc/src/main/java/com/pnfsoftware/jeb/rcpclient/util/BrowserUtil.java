/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.io.File;
/*    */ import org.eclipse.swt.program.Program;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class BrowserUtil
        /*    */ {
    /* 23 */   private static final ILogger logger = GlobalLog.getLogger(BrowserUtil.class);

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public static boolean openInBrowser(String url)
    /*    */ {
        /*    */
        try
            /*    */ {
            /* 33 */
            if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
                /* 34 */
                return false;
                /*    */
            }
            /* 36 */
            return Program.launch(url);
            /*    */
        }
        /*    */ catch (Exception e) {
            /* 39 */
            logger.catching(e);
        }
        /* 40 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public static boolean openInBrowser(File file)
    /*    */ {
        /*    */
        try
            /*    */ {
            /* 52 */
            return Program.launch(file.getAbsolutePath());
            /*    */
        }
        /*    */ catch (Exception e) {
            /* 55 */
            logger.catching(e);
        }
        /* 56 */
        return false;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\BrowserUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */