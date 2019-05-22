package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;

public class NavigationLocateUnitHandler extends JebBaseHandler {
    public NavigationLocateUnitHandler() {
        super("locateUnit", S.s(516), "Locate the currently active unit view in the project explorer view", "eclipse/synced.png");
    }

    public boolean canExecute() {
        return this.context.getPartManager().getUnitForPart(this.part) != null;
    }

    public void execute() {
        IUnit unit = this.context.getPartManager().getUnitForPart(this.part);
        if (unit == null) {
            return;
        }
        ProjectExplorerPartManager p = this.context.getPartManager().getProjectExplorer();
        if (p == null) {
            return;
        }
        p.focusOnNode(unit);
        p.setFocus();
    }
}


