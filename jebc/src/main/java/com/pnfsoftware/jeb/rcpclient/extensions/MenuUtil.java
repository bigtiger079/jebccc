
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\MenuUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */