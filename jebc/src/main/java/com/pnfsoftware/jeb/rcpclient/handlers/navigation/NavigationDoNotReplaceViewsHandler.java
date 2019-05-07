
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;


public class NavigationDoNotReplaceViewsHandler
        extends JebBaseHandler {

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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationDoNotReplaceViewsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */