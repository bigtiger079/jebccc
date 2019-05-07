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
/*    */ public class WindowToggleStatusHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 23 */   private static final ILogger logger = GlobalLog.getLogger(WindowToggleToolbarHandler.class);

    /*    */
    /*    */
    public WindowToggleStatusHandler() {
        /* 26 */
        super(null, "Toggle the status bar visibility", 0, null, null, SWT.MOD1 | 0x59);
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
        boolean visible = this.context.getApp().isStatusVisible();
        /* 37 */
        this.context.getApp().setStatusVisibility(!visible);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowToggleStatusHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */