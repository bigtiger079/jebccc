package com.pnfsoftware.jeb.rcpclient.handlers.edition;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;

public class EditLanguageHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(EditLanguageHandler.class);
    String language;

    public EditLanguageHandler(String name, String language) {
        super(null, name, 2, null, null, 0);
        this.language = language;
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        S.setLanguage(this.language);
        this.context.setPreferredLanguage(this.language);
        this.context.getTelemetry().record("languageChange", "code", this.language);
        String msg = String.format("Your locale was changed to: %s.\n\nPlease restart JEB.", new Locale(this.language, "", "").getDisplayLanguage());
        MessageDialog.openInformation(this.shell, "Locale change", msg);
    }
}


