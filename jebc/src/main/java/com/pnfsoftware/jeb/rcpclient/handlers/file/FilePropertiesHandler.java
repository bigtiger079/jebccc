/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
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
/*    */ public class FilePropertiesHandler
        /*    */ extends OperationHandler
        /*    */ {
    /*    */
    public FilePropertiesHandler()
    /*    */ {
        /* 24 */
        super(Operation.PROPERTIES, null, S.s(543), null, null);
        /* 25 */
        setAccelerator(SWT.MOD3 | 0xD);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FilePropertiesHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */