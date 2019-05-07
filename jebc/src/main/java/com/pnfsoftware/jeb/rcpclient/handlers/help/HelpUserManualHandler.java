/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.help;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
/*    */ import java.io.File;

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
/*    */ public class HelpUserManualHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public HelpUserManualHandler()
    /*    */ {
        /* 27 */
        super(null, S.s(581), null, null);
        /* 28 */
        setAccelerator(16777226);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 33 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 38 */
        File f = new File(this.context.getBaseDirectory(), "doc" + File.separator + "manual" + File.separator + "index.html");
        /* 39 */
        if (f.isFile()) {
            /* 40 */
            BrowserUtil.openInBrowser(f);
            /*    */
        }
        /*    */
        else {
            /* 43 */
            BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/manual");
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\help\HelpUserManualHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */