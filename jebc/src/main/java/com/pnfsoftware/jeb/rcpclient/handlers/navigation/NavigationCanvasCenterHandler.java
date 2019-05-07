/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;
/*    */
/*    */

import com.pnfsoftware.jeb.client.api.Operation;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class NavigationCanvasCenterHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public NavigationCanvasCenterHandler()
    /*    */ {
        /* 15 */
        super(Operation.CENTER, null, "Center", "", null);
        /* 16 */
        setAccelerator(92);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationCanvasCenterHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */