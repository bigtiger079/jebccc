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
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
/*    */ import org.eclipse.swt.SWT;

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
/*    */ public class NavigationLocateInCodeHierarchyHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public NavigationLocateInCodeHierarchyHandler()
    /*    */ {
        /* 29 */
        super("locateInCodeHierarchy", "Locate in Code Hierarchy", null, "eclipse/synced.png");
        /* 30 */
        setAccelerator(SWT.MOD1 | 0x47);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 35 */
        return this.context.getPartManager().getUnitForPart(this.part) instanceof IUnit;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 40 */
        IUnit unit = this.context.getPartManager().getUnitForPart(this.part);
        /* 41 */
        if (unit == null) {
            /* 42 */
            return;
            /*    */
        }
        /* 44 */
        if ((unit instanceof ISourceUnit)) {
            /* 45 */
            unit = DecompilerHelper.getRelatedCodeUnit(unit);
            /*    */
        }
        /* 47 */
        if (!(unit instanceof ICodeUnit)) {
            /* 48 */
            return;
            /*    */
        }
        /*    */
        /* 51 */
        String address = getActiveAddress(this.part);
        /* 52 */
        if (address == null) {
            /* 53 */
            return;
            /*    */
        }
        /*    */
        /* 56 */
        for (IMPart part0 : this.context.getPartManager().getPartsForUnit(unit)) {
            /* 57 */
            UnitPartManager manager = (UnitPartManager) part0.getManager();
            /* 58 */
            if ((manager != null) && ((manager.getActiveFragment() instanceof CodeHierarchyView))) {
                /* 59 */
                CodeHierarchyView v = (CodeHierarchyView) manager.getActiveFragment();
                /* 60 */
                v.focusOnAddress(address);
                /*    */
                /* 62 */
                break;
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationLocateInCodeHierarchyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */