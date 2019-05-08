package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;

public class ActionDeleteHandler extends ActionGenericHandler {
    public ActionDeleteHandler() {
        super(1, "delete", S.s(482), "Delete an item and cascade changes to connected items", "eclipse/delete_obj.png", 127);
    }
}


