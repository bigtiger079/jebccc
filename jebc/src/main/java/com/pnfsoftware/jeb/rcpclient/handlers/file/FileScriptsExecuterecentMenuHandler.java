package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import org.eclipse.jface.action.IMenuManager;

public class FileScriptsExecuterecentMenuHandler extends AbstractDynamicMenuHandler {
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


