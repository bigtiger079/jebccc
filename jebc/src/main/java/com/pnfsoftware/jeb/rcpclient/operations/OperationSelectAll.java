/*    */
package com.pnfsoftware.jeb.rcpclient.operations;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.api.IOperable;
/*    */ import com.pnfsoftware.jeb.client.api.Operation;
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
/*    */ public class OperationSelectAll
        /*    */ extends AbstractOperation
        /*    */ {
    /*    */
    public OperationSelectAll(IOperable object)
    /*    */ {
        /* 24 */
        super(object, S.s(721));
        /* 25 */
        setAccelerator(SWT.MOD1 | 0x41);
        /*    */
    }

    /*    */
    /*    */
    protected String getOperationImageData()
    /*    */ {
        /* 30 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    protected Operation getOperation()
    /*    */ {
        /* 35 */
        return Operation.SELECT_ALL;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationSelectAll.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */