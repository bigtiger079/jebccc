/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.edition;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.util.Locale;
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
/*    */
/*    */
/*    */ public class EditLanguageHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 27 */   private static final ILogger logger = GlobalLog.getLogger(EditLanguageHandler.class);
    /*    */ String language;

    /*    */
    /*    */
    public EditLanguageHandler(String name, String language)
    /*    */ {
        /* 32 */
        super(null, name, 2, null, null, 0);
        /* 33 */
        this.language = language;
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 38 */
        return true;
        /*    */
    }

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
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 58 */
        S.setLanguage(this.language);
        /* 59 */
        this.context.setPreferredLanguage(this.language);
        /*    */
        /* 61 */
        this.context.getTelemetry().record("languageChange", "code", this.language);
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
        /* 73 */
        String msg = String.format("Your locale was changed to: %s.\n\nPlease restart JEB.", new Object[]{new Locale(this.language, "", "")
                /* 74 */.getDisplayLanguage()});
        /* 75 */
        MessageDialog.openInformation(this.shell, "Locale change", msg);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditLanguageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */