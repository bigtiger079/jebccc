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
/*    */ public class HelpFAQHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public HelpFAQHandler()
    /*    */ {
        /* 25 */
        super(null, S.s(503), null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 30 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 35 */
        File f = new File(this.context.getBaseDirectory(), "doc" + File.separator + "manual" + File.separator + "faq" + File.separator + "index.html");
        /* 36 */
        if (f.isFile()) {
            /* 37 */
            BrowserUtil.openInBrowser(f);
            /*    */
        }
        /*    */
        else {
            /* 40 */
            BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/manual/faq");
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\help\HelpFAQHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */