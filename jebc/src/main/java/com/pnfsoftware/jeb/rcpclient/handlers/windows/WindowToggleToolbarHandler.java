/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
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
/*    */
/*    */
/*    */ public class WindowToggleToolbarHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 23 */   private static final ILogger logger = GlobalLog.getLogger(WindowToggleToolbarHandler.class);

    /*    */
    /*    */
    public WindowToggleToolbarHandler() {
        /* 26 */
        super(null, "Toggle the toolbar visibility", 0, null, null, SWT.MOD1 | 0x54);
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
        boolean visible = this.context.getApp().isToolbarVisible();
        /* 37 */
        this.context.getApp().setToolbarVisibility(!visible);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowToggleToolbarHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */