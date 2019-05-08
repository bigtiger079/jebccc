
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
import org.eclipse.swt.SWT;

public class NavigationLocateInCodeHierarchyHandler
        extends JebBaseHandler {
    public NavigationLocateInCodeHierarchyHandler() {
        super("locateInCodeHierarchy", "Locate in Code Hierarchy", null, "eclipse/synced.png");
        setAccelerator(SWT.MOD1 | 0x47);
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
            if ((manager != null) && ((manager.getActiveFragment() instanceof CodeHierarchyView))) {
                CodeHierarchyView v = (CodeHierarchyView) manager.getActiveFragment();
                v.focusOnAddress(address);
                break;
            }
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationLocateInCodeHierarchyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */