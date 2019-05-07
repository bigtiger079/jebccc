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
/*    */ public class OperationFindNext
        /*    */ extends AbstractOperation
        /*    */ {
    /*    */
    public OperationFindNext(IOperable object)
    /*    */ {
        /* 24 */
        super(object, S.s(346));
        /* 25 */
        setAccelerator(SWT.MOD1 | 0x4B);
        /*    */
    }

    /*    */
    /*    */
    protected Operation getOperation()
    /*    */ {
        /* 30 */
        return Operation.FIND_NEXT;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationFindNext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */