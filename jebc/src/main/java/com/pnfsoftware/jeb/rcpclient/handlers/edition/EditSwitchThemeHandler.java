/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.edition;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.themes.ThemeManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
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
/*    */ public class EditSwitchThemeHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public EditSwitchThemeHandler()
    /*    */ {
        /* 21 */
        super("switchTheme", "Switch Theme", "Switch from a light to a dark color theme", "eclipse/fastview_restore.png");
        /* 22 */
        setAccelerator(SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | 0x54);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 27 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 32 */
        this.context.getThemeManager().setNextTheme();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditSwitchThemeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */