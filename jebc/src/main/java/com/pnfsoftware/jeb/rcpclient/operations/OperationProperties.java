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
/*    */ public class OperationProperties
        /*    */ extends AbstractOperation
        /*    */ {
    /*    */
    public OperationProperties(IOperable object)
    /*    */ {
        /* 24 */
        super(object, S.s(666));
        /* 25 */
        setAccelerator(SWT.MOD3 | 0xD);
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
        return Operation.PROPERTIES;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\OperationProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */