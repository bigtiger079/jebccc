/*    */
package com.pnfsoftware.jeb.rcpclient.operations;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.api.IOperable;
/*    */ import com.pnfsoftware.jeb.client.api.Operation;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class OperationRefresh
        /*    */ extends AbstractOperation
        /*    */ {
    /*    */
    public OperationRefresh(IOperable object)
    /*    */ {
        /* 20 */
        super(object, S.s(33));
        /*    */
    }

    /*    */
    /*    */
    /*    */
    protected String getOperationImageData()
    /*    */ {
        /* 26 */
        return "eclipse/refresh_tab.png";
        /*    */
    }

    /*    */
    /*    */
    protected Operation getOperation()
    /*    */ {
        /* 31 */
        return Operation.REFRESH;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationRefresh.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */