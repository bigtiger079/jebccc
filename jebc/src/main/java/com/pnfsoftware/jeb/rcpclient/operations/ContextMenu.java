package com.pnfsoftware.jeb.rcpclient.operations;

import java.util.ArrayList;
import java.util.List;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class ContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(ContextMenu.class);
    final List<IContextMenu> contextMenus = new ArrayList();
    private MenuManager menuManager;
    private Menu menu;

    public ContextMenu(Control control) {
        this.menuManager = new MenuManager();
        this.menuManager.setRemoveAllWhenShown(true);
        this.menuManager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager menuMgr) {
                logger.error("menuAboutToShow+++++++++");
                for (IContextMenu cm : ContextMenu.this.contextMenus) {
                    cm.fillContextMenu(menuMgr);
                }
            }
        });
        this.menu = this.menuManager.createContextMenu(control);
        control.setMenu(this.menu);
    }

    public void addContextMenu(IContextMenu contextMenu) {
        this.contextMenus.add(contextMenu);
    }

    public void add(IContributionItem item) {
        this.menuManager.add(item);
    }

    public MenuManager getMenuMgr() {
        return this.menuManager;
    }

    public Menu getMenu() {
        return this.menuManager.getMenu();
    }

    public void addSubMenu(final ContextMenu submenu) {
        addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                menuMgr.add(submenu.getMenuMgr());
            }
        });
    }
}


