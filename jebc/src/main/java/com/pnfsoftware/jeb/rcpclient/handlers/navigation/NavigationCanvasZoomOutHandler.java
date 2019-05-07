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
/*    */ public class NavigationCanvasZoomOutHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public NavigationCanvasZoomOutHandler()
    /*    */ {
        /* 15 */
        super(Operation.ZOOM_OUT, null, "Zoom Out", "", null);
        /* 16 */
        setAccelerator(91);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationCanvasZoomOutHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */