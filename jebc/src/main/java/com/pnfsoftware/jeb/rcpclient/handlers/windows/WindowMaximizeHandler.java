/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

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
/*    */ public class WindowMaximizeHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 19 */   final String tagMaximized = "Maximized";

    /*    */
    /*    */
    public WindowMaximizeHandler() {
        /* 22 */
        super(null, S.s(517), null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 27 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute() {
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowMaximizeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */