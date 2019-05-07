/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.actions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.actions.ActionContext;
/*    */ import com.pnfsoftware.jeb.core.actions.Actions;
/*    */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*    */ import com.pnfsoftware.jeb.core.output.IActionableItem;
/*    */ import com.pnfsoftware.jeb.core.output.IItem;
/*    */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.actions.ActionUIContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public abstract class ActionGenericHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 32 */   private static final ILogger logger = GlobalLog.getLogger(ActionGenericHandler.class);
    /*    */   private int actionId;

    /*    */
    /*    */
    public ActionGenericHandler(int actionId, String id, String name, String tooltip, String icon, int accelerator)
    /*    */ {
        /* 37 */
        super(id, name, 0, tooltip, icon, accelerator);
        /* 38 */
        this.actionId = actionId;
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 43 */
        if (isDisableHandlers(this.part)) {
            /* 44 */
            return false;
            /*    */
        }
        /*    */
        /*    */
        /*    */
        /*    */
        try
            /*    */ {
            /* 51 */
            return prepare(this.part) != null;
            /*    */
        }
        /*    */ catch (Exception e) {
            /* 54 */
            logger.catching(e);
            /* 55 */
            throw e;
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    public final void execute()
    /*    */ {
        /* 61 */
        this.context.getTelemetry().record("action" + Actions.idToName(this.actionId));
        /*    */
        /* 63 */
        ActionUIContext uictx = prepare(this.part);
        /* 64 */
        if (uictx != null) {
            /* 65 */
            GraphicalActionExecutor executor = new GraphicalActionExecutor(this.shell, this.context);
            /* 66 */
            executor.execute(uictx);
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    private ActionUIContext prepare(IMPart part) {
        /* 71 */
        if (part != null) {
            /* 72 */
            Object object = part.getManager();
            /* 73 */
            if ((object instanceof UnitPartManager)) {
                /* 74 */
                IUnit unit0 = ((UnitPartManager) object).getUnit();
                /* 75 */
                if ((unit0 instanceof IInteractiveUnit)) {
                    /* 76 */
                    IInteractiveUnit unit = (IInteractiveUnit) unit0;
                    /*    */
                    /* 78 */
                    IItem item = ((UnitPartManager) object).getActiveItem();
                    /* 79 */
                    long itemId = (item != null) && ((item instanceof IActionableItem)) ? ((IActionableItem) item).getItemId() : 0L;
                    /*    */
                    /* 81 */
                    String address = ((UnitPartManager) object).getActiveAddress(AddressConversionPrecision.COARSE);
                    /*    */
                    /* 83 */
                    ActionContext info = new ActionContext(unit, this.actionId, itemId, address);
                    /* 84 */
                    if (unit.canExecuteAction(info)) {
                        /* 85 */
                        AbstractUnitFragment<?> fragment = ((UnitPartManager) object).getActiveFragment();
                        /* 86 */
                        return new ActionUIContext(info, fragment);
                        /*    */
                    }
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /* 91 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionGenericHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */