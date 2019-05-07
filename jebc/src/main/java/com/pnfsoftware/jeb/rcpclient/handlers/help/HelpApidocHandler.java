/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.help;
/*    */
/*    */

import com.pnfsoftware.jeb.client.Licensing;
/*    */ import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;

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
/*    */ public class HelpApidocHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public HelpApidocHandler()
    /*    */ {
        /* 25 */
        super(null, S.s(461), null, null);
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
        if (Licensing.isDemoBuild()) {
            /* 36 */
            UI.warn(this.shell, S.s(249), S.s(250));
            /* 37 */
            return;
            /*    */
        }
        /*    */
        /* 40 */
        BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/apidoc");
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\help\HelpApidocHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */