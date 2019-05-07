/*    */
package com.pnfsoftware.jeb.rcpclient.operations;
/*    */
/*    */

import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.eclipse.jface.action.IContributionItem;
/*    */ import org.eclipse.jface.action.IMenuListener;
/*    */ import org.eclipse.jface.action.IMenuManager;
/*    */ import org.eclipse.jface.action.MenuManager;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Menu;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class ContextMenu
        /*    */ {
    /* 28 */   final List<IContextMenu> contextMenus = new ArrayList();
    /*    */   private MenuManager menuManager;
    /*    */   private Menu menu;

    /*    */
    /*    */
    public ContextMenu(Control control)
    /*    */ {
        /* 34 */
        this.menuManager = new MenuManager();
        /* 35 */
        this.menuManager.setRemoveAllWhenShown(true);
        /* 36 */
        this.menuManager.addMenuListener(new IMenuListener()
                /*    */ {
            /*    */
            public void menuAboutToShow(IMenuManager menuMgr) {
                /* 39 */
                for (IContextMenu cm : ContextMenu.this.contextMenus) {
                    /* 40 */
                    cm.fillContextMenu(menuMgr);
                    /*    */
                }
                /*    */
                /*    */
            }
            /* 44 */
        });
        /* 45 */
        this.menu = this.menuManager.createContextMenu(control);
        /* 46 */
        control.setMenu(this.menu);
        /*    */
    }

    /*    */
    /*    */
    public void addContextMenu(IContextMenu contextMenu) {
        /* 50 */
        this.contextMenus.add(contextMenu);
        /*    */
    }

    /*    */
    /*    */
    public void add(IContributionItem item) {
        /* 54 */
        this.menuManager.add(item);
        /*    */
    }

    /*    */
    /*    */
    public MenuManager getMenuMgr() {
        /* 58 */
        return this.menuManager;
        /*    */
    }

    /*    */
    /*    */
    public Menu getMenu() {
        /* 62 */
        return this.menuManager.getMenu();
        /*    */
    }

    /*    */
    /*    */
    public void addSubMenu(final ContextMenu submenu) {
        /* 66 */
        addContextMenu(new IContextMenu()
                /*    */ {
            /*    */
            public void fillContextMenu(IMenuManager menuMgr) {
                /* 69 */
                menuMgr.add(submenu.getMenuMgr());
                /*    */
            }
            /*    */
        });
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\ContextMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */