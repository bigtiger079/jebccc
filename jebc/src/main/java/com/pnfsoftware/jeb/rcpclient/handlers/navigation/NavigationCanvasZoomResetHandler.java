/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;
/*    */
/*    */

import com.pnfsoftware.jeb.client.api.Operation;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
/*    */ import org.eclipse.swt.SWT;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class NavigationCanvasZoomResetHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public NavigationCanvasZoomResetHandler()
    /*    */ {
        /* 17 */
        super(Operation.ZOOM_RESET, null, "Reset Zoom Level", "", null);
        /* 18 */
        setAccelerator(SWT.MOD1 + 92);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationCanvasZoomResetHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */