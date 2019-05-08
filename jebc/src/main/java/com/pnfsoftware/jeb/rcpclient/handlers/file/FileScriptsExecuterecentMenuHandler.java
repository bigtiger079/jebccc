
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import org.eclipse.jface.action.IMenuManager;

public class FileScriptsExecuterecentMenuHandler
        extends AbstractDynamicMenuHandler {
    public void menuAboutToShow(IMenuManager manager) {
        if (!canExecute()) {
            return;
        }
        for (String path : this.context.getRecentlyExecutedScripts()) {
            if (path.length() != 0) {
                manager.add(new FileScriptsExecuterecentHandler(path));
            }
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileScriptsExecuterecentMenuHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */