/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*    */ import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.IUnitNotification;
/*    */ import com.pnfsoftware.jeb.core.units.IUnitNotificationManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.AllNotificationsDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.base.Couple;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.eclipse.swt.widgets.Shell;

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
/*    */ public class FileNotificationsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 34 */   private static final ILogger logger = GlobalLog.getLogger(FileNotificationsHandler.class);

    /*    */
    /*    */
    public FileNotificationsHandler() {
        /* 37 */
        super(null, S.s(526), "Display the notifications reported by all units in the project", "eclipse/warning.png");
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 42 */
        IRuntimeProject prj = this.context.getOpenedProject();
        /* 43 */
        if (prj == null) {
            /* 44 */
            return false;
            /*    */
        }
        /*    */
        /* 47 */
        return RuntimeProjectUtil.hasNotification(prj);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 52 */
        IRuntimeProject prj = this.context.getOpenedProject();
        /* 53 */
        if (prj == null) {
            /* 54 */
            return;
            /*    */
        }
        /*    */
        /* 57 */
        showUnitNotificationsDialog(this.shell, this.context, prj);
        /*    */
    }

    /*    */
    /*    */
    public static void showUnitNotificationsDialog(Shell shell, RcpClientContext context, IRuntimeProject prj)
    /*    */ {
        /* 62 */
        AllNotificationsDialog dlg = AllNotificationsDialog.getInstance();
        /* 63 */
        if (dlg != null) {
            /* 64 */
            dlg.setFocus();
            /* 65 */
            return;
            /*    */
        }
        /*    */
        /* 68 */
        List<Couple<IUnit, IUnitNotification>> elements = new ArrayList();
        /* 69 */
        for (Iterator localIterator1 = RuntimeProjectUtil.getAllUnits(prj).iterator(); localIterator1.hasNext(); ) {
            unit = (IUnit) localIterator1.next();
            /* 70 */
            for (IUnitNotification notif : unit.getNotificationManager().getNotifications()) {
                /* 71 */
                elements.add(new Couple(unit, notif));
                /*    */
            }
            /*    */
        }
        /*    */
        IUnit unit;
        /* 75 */
        dlg = new AllNotificationsDialog(shell, elements);
        /* 76 */
        int index = dlg.open().intValue();
        /* 77 */
        logger.debug("Selected row: %d", new Object[]{Integer.valueOf(index)});
        /* 78 */
        if (index >= 0) {
            /* 79 */
            IUnit unit = (IUnit) ((Couple) elements.get(index)).getFirst();
            /* 80 */
            IUnitNotification notif = (IUnitNotification) ((Couple) elements.get(index)).getSecond();
            /* 81 */
            String address = notif.getAddress();
            /* 82 */
            if (address != null) {
                /* 83 */
                logger.debug("address= %s", new Object[]{address});
                /*    */
                /* 85 */
                GraphicalActionExecutor.gotoAddress(context, unit, address);
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileNotificationsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */