package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import org.eclipse.jface.action.IMenuManager;

public class WindowFocusPartMenuHandler extends AbstractDynamicMenuHandler {
    public void menuAboutToShow(IMenuManager manager) {
        if (!canExecute()) {
            return;
        }
        for (IMPart part : this.context.getPartManager().getUnitParts()) {
            manager.add(new WindowFocusPartHandler(part));
        }
    }
}


