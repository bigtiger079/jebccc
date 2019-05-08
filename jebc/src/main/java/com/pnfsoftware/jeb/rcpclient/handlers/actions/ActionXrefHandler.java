
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;

public class ActionXrefHandler
        extends ActionGenericHandler {
    public ActionXrefHandler() {
        super(4, "queryXrefs", S.s(583), "View the cross-references of an item", "eclipse/search_ref_obj.png", 88);
    }
}


