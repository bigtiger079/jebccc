package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class NavigationDoNotReplaceViewsHandler extends JebBaseHandler {
    public NavigationDoNotReplaceViewsHandler() {
        super(null, S.s(486), 2, "", "eclipse/new_wiz.png", 0);
    }

    public boolean canExecute() {
        setChecked(this.context.getProperties().getDoNotReplaceViews());
        return true;
    }

    public void execute() {
        boolean doNotReplaceViews = !this.context.getProperties().getDoNotReplaceViews();
        this.context.getProperties().setDoNotReplaceViews(doNotReplaceViews);
    }
}


