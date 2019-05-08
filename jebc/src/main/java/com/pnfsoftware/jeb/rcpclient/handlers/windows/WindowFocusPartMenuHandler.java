
package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import org.eclipse.jface.action.IMenuManager;

public class WindowFocusPartMenuHandler
        extends AbstractDynamicMenuHandler {
    public void menuAboutToShow(IMenuManager manager) {
        if (!canExecute()) {
            return;
        }
        for (IMPart part : this.context.getPartManager().getUnitParts()) {
            manager.add(new WindowFocusPartHandler(part));
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowFocusPartMenuHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */