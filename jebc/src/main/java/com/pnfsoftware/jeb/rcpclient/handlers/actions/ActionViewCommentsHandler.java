package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
import com.pnfsoftware.jeb.rcpclient.dialogs.ReferencesDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

public class ActionViewCommentsHandler extends JebBaseHandler {
    public ActionViewCommentsHandler() {
        super("queryComments", "View Comments", null, "eclipse/tasks_tsk.png");
        setAccelerator(SWT.MOD1 | 0x2F);
    }

    public boolean canExecute() {
        IUnit unit = getActiveUnit(this.part);
        return ((unit instanceof IInteractiveUnit)) && (((IInteractiveUnit) unit).getComments() != null);
    }

    public void execute() {
        IInteractiveUnit unit = (IInteractiveUnit) getActiveUnit(this.part);
        List<String> addresses = new ArrayList<>(unit.getComments().keySet());
        ReferencesDialog dlg = new ReferencesDialog(this.shell, "Comments", addresses, null, unit);
        int index = dlg.open();
        if (index >= 0) {
            String address = addresses.get(index);
            GraphicalActionExecutor.gotoAddress(this.context, unit, address);
        }
    }
}


