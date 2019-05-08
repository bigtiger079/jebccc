
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileEnginesContributionsHandler
        extends JebBaseHandler {
    public FileEnginesContributionsHandler() {
        super(null, "Contributions...", null, null);
    }

    public boolean canExecute() {
        return this.context.getEnginesContext() != null;
    }

    public void execute() {
        IEnginesContext engctx = this.context.getEnginesContext();
        if (engctx == null) {
            return;
        }
        UI.info("Not available at this time.");
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesContributionsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */