/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.navigation;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class NavigationLocateUnitHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public NavigationLocateUnitHandler()
    /*    */ {
        /* 23 */
        super("locateUnit", S.s(516), "Locate the currently active unit view in the project explorer view", "eclipse/synced.png");
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 28 */
        return this.context.getPartManager().getUnitForPart(this.part) != null;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 34 */
        IUnit unit = this.context.getPartManager().getUnitForPart(this.part);
        /* 35 */
        if (unit == null) {
            /* 36 */
            return;
            /*    */
        }
        /*    */
        /* 39 */
        ProjectExplorerPartManager p = this.context.getPartManager().getProjectExplorer();
        /* 40 */
        if (p == null) {
            /* 41 */
            return;
            /*    */
        }
        /*    */
        /* 44 */
        p.focusOnNode(unit);
        /* 45 */
        p.setFocus();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\navigation\NavigationLocateUnitHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */