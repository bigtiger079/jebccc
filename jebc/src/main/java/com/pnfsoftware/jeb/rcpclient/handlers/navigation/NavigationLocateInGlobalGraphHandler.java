package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView;

public class NavigationLocateInGlobalGraphHandler extends JebBaseHandler {
    public NavigationLocateInGlobalGraphHandler() {
        super("locateInGlobalGraph", "Locate in Global Graph", null, "eclipse/synced.png");
    }

    public boolean canExecute() {
        return this.context.getPartManager().getUnitForPart(this.part) instanceof IUnit;
    }

    public void execute() {
        IUnit unit = this.context.getPartManager().getUnitForPart(this.part);
        if (unit == null) {
            return;
        }
        if ((unit instanceof ISourceUnit)) {
            unit = DecompilerHelper.getRelatedCodeUnit(unit);
        }
        if (!(unit instanceof ICodeUnit)) {
            return;
        }
        String address = getActiveAddress(this.part);
        if (address == null) {
            return;
        }
        for (IMPart part0 : this.context.getPartManager().getPartsForUnit(unit)) {
            UnitPartManager manager = (UnitPartManager) part0.getManager();
            if ((manager != null) && ((manager.getActiveFragment() instanceof AbstractGlobalGraphView))) {
                AbstractGlobalGraphView<?> v = (AbstractGlobalGraphView) manager.getActiveFragment();
                v.setActiveAddress(address);
                break;
            }
        }
    }
}
