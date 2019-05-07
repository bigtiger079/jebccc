/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
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
/*    */ public class NavigationDoNotReplaceViewsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public NavigationDoNotReplaceViewsHandler()
    /*    */ {
        /* 23 */
        super(null, S.s(486), 2, "", "eclipse/new_wiz.png", 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 28 */
        setChecked(this.context.getProperties().getDoNotReplaceViews());
        /* 29 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 34 */
        boolean doNotReplaceViews = !this.context.getProperties().getDoNotReplaceViews();
        /* 35 */
        this.context.getProperties().setDoNotReplaceViews(doNotReplaceViews);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationDoNotReplaceViewsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */