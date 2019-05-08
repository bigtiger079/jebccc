package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

import java.util.Locale;

import org.eclipse.jface.action.IMenuManager;

public class EditLanguageMenuHandler extends AbstractDynamicMenuHandler {
    public void menuAboutToShow(IMenuManager manager) {
        if (!canExecute()) {
            return;
        }
        String currentLanguage = S.getLanguage();
        for (String language : S.languages) {
            Locale loc = new Locale(language, "", "");
            String name0 = loc.getDisplayLanguage(loc);
            String name1 = loc.getDisplayLanguage();
            this.handler = new EditLanguageHandler(String.format("%s (%s)", new Object[]{name0, name1}), language);
            if (currentLanguage.equals(language)) {
                this.handler.setChecked(true);
            }
            manager.add(this.handler);
        }
    }
}


