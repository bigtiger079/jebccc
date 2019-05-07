/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.api.Operation;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
/*    */ import com.pnfsoftware.jeb.util.base.OSType;

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
/*    */
/*    */ public class WindowRefreshHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public WindowRefreshHandler()
    /*    */ {
        /* 25 */
        super(Operation.REFRESH, "refresh", S.s(546), null, null);
        /* 26 */
        setAccelerator(OSType.determine().isMac() ? 4194386 : 16777230);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowRefreshHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */