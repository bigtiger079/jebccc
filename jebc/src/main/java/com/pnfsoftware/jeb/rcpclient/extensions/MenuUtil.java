package com.pnfsoftware.jeb.rcpclient.extensions;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;

public class MenuUtil {
    public static MenuManager createAutoRefreshMenu(String name) {
        MenuManager menu = new MenuManager(name, null);
        menu.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                for (IContributionItem item : manager.getItems()) {
                    item.update();
                }
            }
        });
        return menu;
    }
}


