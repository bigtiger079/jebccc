/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.edition;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import java.util.Locale;
/*    */ import org.eclipse.jface.action.IMenuManager;

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
/*    */ public class EditLanguageMenuHandler
        /*    */ extends AbstractDynamicMenuHandler
        /*    */ {
    /*    */
    public void menuAboutToShow(IMenuManager manager)
    /*    */ {
        /* 30 */
        if (!canExecute()) {
            /* 31 */
            return;
            /*    */
        }
        /*    */
        /* 34 */
        String currentLanguage = S.getLanguage();
        /* 35 */
        for (String language : S.languages) {
            /* 36 */
            Locale loc = new Locale(language, "", "");
            /* 37 */
            String name0 = loc.getDisplayLanguage(loc);
            /* 38 */
            String name1 = loc.getDisplayLanguage();
            /* 39 */
            this.handler = new EditLanguageHandler(String.format("%s (%s)", new Object[]{name0, name1}), language);
            /* 40 */
            if (currentLanguage.equals(language)) {
                /* 41 */
                this.handler.setChecked(true);
                /*    */
            }
            /* 43 */
            manager.add(this.handler);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditLanguageMenuHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */