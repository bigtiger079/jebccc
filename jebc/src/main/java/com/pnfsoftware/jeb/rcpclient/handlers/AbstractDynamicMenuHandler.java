
package com.pnfsoftware.jeb.rcpclient.handlers;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;

public abstract class AbstractDynamicMenuHandler
        extends JebBaseHandler
        implements IMenuListener2 {
    private static final ILogger logger = GlobalLog.getLogger(AbstractDynamicMenuHandler.class);

    public AbstractDynamicMenuHandler() {
        super(null, "", null, null);
    }

    public boolean canExecute() {
        initialize();
        return true;
    }

    public void execute() {
    }

    public void menuAboutToHide(IMenuManager manager) {
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\AbstractDynamicMenuHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */