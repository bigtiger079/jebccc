/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.actions;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.ReferencesDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class ActionViewCommentsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public ActionViewCommentsHandler()
    /*    */ {
        /* 29 */
        super("queryComments", "View Comments", null, "eclipse/tasks_tsk.png");
        /* 30 */
        setAccelerator(SWT.MOD1 | 0x2F);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 35 */
        IUnit unit = getActiveUnit(this.part);
        /* 36 */
        return ((unit instanceof IInteractiveUnit)) && (((IInteractiveUnit) unit).getComments() != null);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 41 */
        IInteractiveUnit unit = (IInteractiveUnit) getActiveUnit(this.part);
        /*    */
        /* 43 */
        List<String> addresses = new ArrayList(unit.getComments().keySet());
        /* 44 */
        ReferencesDialog dlg = new ReferencesDialog(this.shell, "Comments", addresses, null, unit);
        /* 45 */
        int index = dlg.open().intValue();
        /* 46 */
        if (index >= 0) {
            /* 47 */
            String address = (String) addresses.get(index);
            /* 48 */
            GraphicalActionExecutor.gotoAddress(this.context, unit, address);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionViewCommentsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */