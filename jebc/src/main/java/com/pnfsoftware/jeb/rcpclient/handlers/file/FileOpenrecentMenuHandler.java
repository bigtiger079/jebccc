package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

public class FileOpenrecentMenuHandler extends AbstractDynamicMenuHandler {
    public void menuAboutToShow(IMenuManager manager) {
        if (!canExecute()) {
            return;
        }
        int cnt = 0;
        for (String path : this.context.getRecentlyOpenedFiles()) {
            if (path.length() != 0) {
                manager.add(new FileOpenrecentHandler(path));
                cnt++;
            }
        }
        if (cnt > 0) {
            manager.add(new Separator());
            manager.add(new FileOpenrecentHandler());
        }
    }
}


