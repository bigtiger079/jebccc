
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitNotification;
import com.pnfsoftware.jeb.core.units.IUnitNotificationManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
import com.pnfsoftware.jeb.rcpclient.dialogs.AllNotificationsDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.base.Couple;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

public class FileNotificationsHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileNotificationsHandler.class);

    public FileNotificationsHandler() {
        super(null, S.s(526), "Display the notifications reported by all units in the project", "eclipse/warning.png");
    }

    public boolean canExecute() {
        IRuntimeProject prj = this.context.getOpenedProject();
        if (prj == null) {
            return false;
        }
        return RuntimeProjectUtil.hasNotification(prj);
    }

    public void execute() {
        IRuntimeProject prj = this.context.getOpenedProject();
        if (prj == null) {
            return;
        }
        showUnitNotificationsDialog(this.shell, this.context, prj);
    }

    public static void showUnitNotificationsDialog(Shell shell, RcpClientContext context, IRuntimeProject prj) {
        AllNotificationsDialog dlg = AllNotificationsDialog.getInstance();
        if (dlg != null) {
            dlg.setFocus();
            return;
        }
        List<Couple<IUnit, IUnitNotification>> elements = new ArrayList();
        Iterator localIterator1 = RuntimeProjectUtil.getAllUnits(prj).iterator();
        IUnit unit;
        while (localIterator1.hasNext()) {
            unit = (IUnit) localIterator1.next();
            for (IUnitNotification notif : unit.getNotificationManager().getNotifications()) {
                elements.add(new Couple(unit, notif));
            }
        }
        dlg = new AllNotificationsDialog(shell, elements);
        int index = dlg.open().intValue();
        logger.debug("Selected row: %d", new Object[]{Integer.valueOf(index)});
        if (index >= 0) {
            unit = (IUnit) ((Couple) elements.get(index)).getFirst();
            IUnitNotification notif = (IUnitNotification) ((Couple) elements.get(index)).getSecond();
            String address = notif.getAddress();
            if (address != null) {
                logger.debug("address= %s", new Object[]{address});
                GraphicalActionExecutor.gotoAddress(context, unit, address);
            }
        }
    }
}


