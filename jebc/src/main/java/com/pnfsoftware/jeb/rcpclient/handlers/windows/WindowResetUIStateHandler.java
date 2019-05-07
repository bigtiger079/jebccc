/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import org.eclipse.jface.dialogs.MessageDialog;

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
/*    */ public class WindowResetUIStateHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public WindowResetUIStateHandler()
    /*    */ {
        /* 23 */
        super(null, S.s(548), null, null);
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
    /*    */
    public void execute()
    /*    */ {
        /* 34 */
        this.context.requestResetUIState();
        /*    */
        /* 36 */
        String msg = S.s(784) + ".\n\n" + S.s(191) + ".";
        /* 37 */
        MessageDialog.openInformation(this.shell, S.s(683), msg);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowResetUIStateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */