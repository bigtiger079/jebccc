/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.internal;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
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
/*    */ public class InternalPrintModelHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 22 */   private static final ILogger logger = GlobalLog.getLogger(InternalPrintModelHandler.class);

    /*    */
    /*    */
    public InternalPrintModelHandler() {
        /* 25 */
        super(null, "Print Model", null, null);
        /* 26 */
        setAccelerator(SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | 0x4D);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 31 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 36 */
        logger.i(this.context.getApp().getDock().formatStructure(), new Object[0]);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\internal\InternalPrintModelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */