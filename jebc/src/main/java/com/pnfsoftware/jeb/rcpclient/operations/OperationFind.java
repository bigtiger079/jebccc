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
/*    */ public class OperationFind
        /*    */ extends AbstractOperation
        /*    */ {
    /*    */
    public OperationFind(IOperable object)
    /*    */ {
        /* 24 */
        super(object, S.s(345));
        /* 25 */
        setAccelerator(SWT.MOD1 | 0x46);
        /*    */
    }

    /*    */
    /*    */
    protected Operation getOperation()
    /*    */ {
        /* 30 */
        return Operation.FIND;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationFind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */