package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.IEnginesPlugin;
import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;

public class FileEnginesExecutepluginMenuHandler extends AbstractDynamicMenuHandler {
    public void menuAboutToShow(IMenuManager manager) {
        if (!canExecute()) {
            return;
        }
        IEnginesContext engctx = this.context.getEnginesContext();
        if (engctx == null) {
            return;
        }
        List<IEnginesPlugin> enginePlugins = engctx.getEnginesPlugins();
        for (IEnginesPlugin p : enginePlugins) {
            String classname = p.getClass().getName();
            String name = null;
            if (p.getPluginInformation() != null) {
                name = p.getPluginInformation().getName();
            }
            name = Strings.safe(name, classname);
            manager.add(new FileEnginesExecutepluginHandler(name, p));
        }
    }
}


