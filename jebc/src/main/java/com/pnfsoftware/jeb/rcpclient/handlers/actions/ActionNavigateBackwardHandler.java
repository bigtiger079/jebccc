/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.actions;
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
/*    */ public class ActionNavigateBackwardHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public ActionNavigateBackwardHandler()
    /*    */ {
        /* 24 */
        super(Operation.NAVIGATE_BACKWARD, "navBackward", S.s(520), "Navigate to the previous view in the history stack of views", "eclipse/backward_nav.png");
        /* 25 */
        setAccelerator(SWT.MOD3 | 0x1000003);
        /* 26 */
        addExtraAccelerator(27);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionNavigateBackwardHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */