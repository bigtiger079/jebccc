/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.windows;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
/*    */ import java.util.List;
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
/*    */
/*    */
/*    */
/*    */
/*    */ public class WindowFocusHierarchyHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public WindowFocusHierarchyHandler()
    /*    */ {
        /* 31 */
        super("showCurrentCodeHierarchy", "Show Current Code Hierarchy View", null, null);
        /* 32 */
        setAccelerator(SWT.MOD1 | 0x34);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 37 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 42 */
        PartManager pman = this.context.getPartManager();
        /*    */
        /*    */
        /* 45 */
        IUnit unit = pman.getUnitForPart(this.part);
        /* 46 */
        if (attemptFocus(pman, unit)) {
            /* 47 */
            return;
            /*    */
        }
        /*    */
        /* 50 */
        List<IMPart> parts = pman.getUnitParts();
        /*    */
        /*    */
        /* 53 */
        for (IMPart p : parts) {
            /* 54 */
            unit = pman.getUnitForPart(p);
            /* 55 */
            if (attemptFocus(pman, unit)) {
                /* 56 */
                return;
                /*    */
            }
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    private boolean attemptFocus(PartManager pman, IUnit unit) {
        /* 62 */
        if (unit != null) {
            /* 63 */
            for (IMPart part : pman.getPartsForUnit(unit)) {
                /* 64 */
                UnitPartManager manager = (UnitPartManager) part.getManager();
                /* 65 */
                CodeHierarchyView fragment = (CodeHierarchyView) manager.getFragmentByType(CodeHierarchyView.class);
                /* 66 */
                if (fragment != null) {
                    /* 67 */
                    pman.activatePart(part, true);
                    /* 68 */
                    manager.setActiveFragment(fragment);
                    /* 69 */
                    return true;
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /* 73 */
        return false;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\windows\WindowFocusHierarchyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */