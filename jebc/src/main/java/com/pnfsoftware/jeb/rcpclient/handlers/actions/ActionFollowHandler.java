
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;

public class ActionFollowHandler
        extends OperationHandler {
    public ActionFollowHandler() {
        super(Operation.ITEM_FOLLOW, "follow", S.s(508), "Follow (navigate to) the currently active item", "eclipse/goto_obj.png");
        setAccelerator(13);
    }
}


