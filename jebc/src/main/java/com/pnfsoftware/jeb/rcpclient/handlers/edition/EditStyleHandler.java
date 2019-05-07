/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.edition;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.StyleOptionsDialog;
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
/*    */ public class EditStyleHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 24 */   private static final ILogger logger = GlobalLog.getLogger(EditStyleHandler.class);

    /*    */
    /*    */
    public EditStyleHandler() {
        /* 27 */
        super(null, S.s(509), "", null);
        /* 28 */
        setAccelerator(SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | 0x53);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 33 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 38 */
        boolean r = new StyleOptionsDialog(this.shell, this.context.getThemeManager(), this.context.getStyleManager(), this.context
/* 39 */.getFontManager()).open().booleanValue();
        /*    */
        /* 40 */
        if (r) {
            /* 41 */
            logger.debug("Font and styles were changed", new Object[0]);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditStyleHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */