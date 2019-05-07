/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
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
/*    */ public class WindowClosepartHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public WindowClosepartHandler()
    /*    */ {
        /* 23 */
        super(null, S.s(467), null, null);
        /* 24 */
        setAccelerator(SWT.MOD1 | 0x57);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 29 */
        return (this.part != null) && (this.part.isHideable());
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 34 */
        this.context.getPartManager().closePart(this.part);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowClosepartHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */