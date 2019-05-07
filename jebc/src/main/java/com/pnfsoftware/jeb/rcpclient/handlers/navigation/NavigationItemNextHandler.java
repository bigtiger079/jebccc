/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.api.Operation;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
/*    */ import org.eclipse.swt.SWT;

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
/*    */ public class NavigationItemNextHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public NavigationItemNextHandler()
    /*    */ {
        /* 24 */
        super(Operation.ITEM_NEXT, "navNext", S.s(525), "", "eclipse/next_nav.png");
        /* 25 */
        setAccelerator(SWT.MOD1 | 0x2E);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationItemNextHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */