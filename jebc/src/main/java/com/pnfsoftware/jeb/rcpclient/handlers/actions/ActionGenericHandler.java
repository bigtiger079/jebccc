package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.actions.Actions;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.actions.ActionUIContext;
import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public abstract class ActionGenericHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionGenericHandler.class);
    private int actionId;

    public ActionGenericHandler(int actionId, String id, String name, String tooltip, String icon, int accelerator) {
        super(id, name, 0, tooltip, icon, accelerator);
        this.actionId = actionId;
    }

    public boolean canExecute() {
        if (isDisableHandlers(this.part)) {
            return false;
        }
        try {
            return prepare(this.part) != null;
        } catch (Exception e) {
            logger.catching(e);
            throw e;
        }
    }

    public final void execute() {
        this.context.getTelemetry().record("action" + Actions.idToName(this.actionId));
        ActionUIContext uictx = prepare(this.part);
        if (uictx != null) {
            GraphicalActionExecutor executor = new GraphicalActionExecutor(this.shell, this.context);
            executor.execute(uictx);
        }
    }

    private ActionUIContext prepare(IMPart part) {
        if (part != null) {
            Object object = part.getManager();
            if ((object instanceof UnitPartManager)) {
                IUnit unit0 = ((UnitPartManager) object).getUnit();
                if ((unit0 instanceof IInteractiveUnit)) {
                    IInteractiveUnit unit = (IInteractiveUnit) unit0;
                    IItem item = ((UnitPartManager) object).getActiveItem();
                    long itemId = (item != null) && ((item instanceof IActionableItem)) ? ((IActionableItem) item).getItemId() : 0L;
                    String address = ((UnitPartManager) object).getActiveAddress(AddressConversionPrecision.COARSE);
                    ActionContext info = new ActionContext(unit, this.actionId, itemId, address);
                    if (unit.canExecuteAction(info)) {
                        AbstractUnitFragment<?> fragment = ((UnitPartManager) object).getActiveFragment();
                        return new ActionUIContext(info, fragment);
                    }
                }
            }
        }
        return null;
    }
}


