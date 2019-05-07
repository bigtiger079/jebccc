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
/*    */ public class NavigationItemPreviousHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public NavigationItemPreviousHandler()
    /*    */ {
        /* 24 */
        super(Operation.ITEM_PREVIOUS, "navPrevious", S.s(541), "", "eclipse/prev_nav.png");
        /* 25 */
        setAccelerator(SWT.MOD1 | 0x2C);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationItemPreviousHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */