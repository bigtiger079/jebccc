/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*    */ import com.pnfsoftware.jeb.core.util.DecompilerHelper;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class NavigationLocateInGlobalGraphHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public NavigationLocateInGlobalGraphHandler()
    /*    */ {
        /* 27 */
        super("locateInGlobalGraph", "Locate in Global Graph", null, "eclipse/synced.png");
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 33 */
        return this.context.getPartManager().getUnitForPart(this.part) instanceof IUnit;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 38 */
        IUnit unit = this.context.getPartManager().getUnitForPart(this.part);
        /* 39 */
        if (unit == null) {
            /* 40 */
            return;
            /*    */
        }
        /* 42 */
        if ((unit instanceof ISourceUnit)) {
            /* 43 */
            unit = DecompilerHelper.getRelatedCodeUnit(unit);
            /*    */
        }
        /* 45 */
        if (!(unit instanceof ICodeUnit)) {
            /* 46 */
            return;
            /*    */
        }
        /*    */
        /* 49 */
        String address = getActiveAddress(this.part);
        /* 50 */
        if (address == null) {
            /* 51 */
            return;
            /*    */
        }
        /*    */
        /* 54 */
        for (IMPart part0 : this.context.getPartManager().getPartsForUnit(unit)) {
            /* 55 */
            UnitPartManager manager = (UnitPartManager) part0.getManager();
            /* 56 */
            if ((manager != null) && ((manager.getActiveFragment() instanceof AbstractGlobalGraphView))) {
                /* 57 */
                AbstractGlobalGraphView<?> v = (AbstractGlobalGraphView) manager.getActiveFragment();
                /*    */
                /* 59 */
                v.setActiveAddress(address);
                /* 60 */
                break;
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationLocateInGlobalGraphHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */