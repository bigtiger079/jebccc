package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class ActionGenerateHookHandler extends JebBaseHandler {
    //TODO: Action Menu
    public ActionGenerateHookHandler(){
        super("hook", "HookCode", 0, "Generate Hook Code for Xposed", "eclipse/all_sc_obj.png", 32);
    }

    @Override
    public boolean canExecute() {
        return false;
    }

    @Override
    public void execute() {

    }
}
