package com.pnfsoftware.jeb.rcpclient.extensions.tab;

import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Point;

public abstract class TabContextMenuManager {
    private static final ILogger logger = GlobalLog.getLogger(TabContextMenuManager.class);
    private CTabFolder folder;
    private CTabItem selectedItem;

    public TabContextMenuManager(CTabFolder folder) {
        this.folder = folder;
    }

    public void bind() {
        this.folder.addMenuDetectListener(new MenuDetectListener() {
            public void menuDetected(MenuDetectEvent e) {
                CTabItem item = TabContextMenuManager.this.folder.getItem(TabContextMenuManager.this.folder.getDisplay().map(null, TabContextMenuManager.this.folder, new Point(e.x, e.y)));
                TabContextMenuManager.logger.i("Menu tab: %s", item);
                TabContextMenuManager.this.selectedItem = item;
            }
        });
        new ContextMenu(this.folder).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                if (TabContextMenuManager.this.selectedItem != null) {
                    TabContextMenuManager.logger.i("Populating fragment context menu");
                    TabContextMenuManager.this.addActions(TabContextMenuManager.this.selectedItem, menuMgr);
                }
            }
        });
    }

    public abstract void addActions(CTabItem paramCTabItem, IMenuManager paramIMenuManager);
}


