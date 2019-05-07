/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
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
/*    */
/*    */ public class WindowFocusTerminalHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public WindowFocusTerminalHandler()
    /*    */ {
        /* 22 */
        super("showTerminal", "Show Terminal View", null, null);
        /* 23 */
        setAccelerator(SWT.MOD1 | 0x33);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 28 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 33 */
        this.context.getPartManager().activateTerminal(true);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowFocusTerminalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */