
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.DbgAttachDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.DbgAttachDialog.DbgAttachInfo;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.BooleanUtils;

public class DebuggerAttachHandler
        extends DebuggerBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(DebuggerAttachHandler.class);

    public DebuggerAttachHandler() {
        super("dbgAttach", S.s(570), null, "eclipse/debug_view.png", 0);
    }

    public boolean canExecute() {
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        return ((dbg == null) || (!dbg.isAttached())) && (canAttachDebugger(this.part));
    }

    public void execute() {
        IUnit unit = getCurrentUnit(this.part);
        if (unit == null) {
            String msg = "You must first load an artifact before starting a debugging.\n\n(This limitation will be lifted in the future.)";
            UI.warn(msg);
            return;
        }
        this.context.getTelemetry().record("handlerAttachDebugger", "targetUnitType", unit.getFormatType());
        DbgAttachDialog dlg = new DbgAttachDialog(this.shell, this.context, null, unit);
        final DbgAttachDialog.DbgAttachInfo result = dlg.open();
        if (result == null) {
            return;
        }
        final IDebuggerUnit dbg = createDebuggerFor(unit);
        if (dbg == null) {
            UI.warn("No debugger available");
            return;
        }
        if (!HandlerUtil.processUnit(this.shell, this.context, dbg, false)) {
            UI.warn("The debugger was not set up properly");
            return;
        }
        boolean success = BooleanUtils.toBoolean((Boolean) this.context.executeTask("Attaching to target...", new Callable() {
            public Boolean call() throws Exception {
                return Boolean.valueOf(dbg.attach(result.info));
            }
        }));
        if (!success) {
            UI.error("Could not attach to target");
            dbg.detach();
            return;
        }
        restoreUIBreakpoints(dbg);
        this.context.setDebuggingMode(true);
        PartManager pman = this.context.getPartManager();
        pman.create(dbg, true);
        List<? extends IUnit> children = dbg.getChildren();
        for (IUnit child : children) {
            if ((child instanceof IDebuggerUnit)) {
                pman.create(child, true);
            }
        }
    }

    private IDebuggerUnit createDebuggerFor(IUnit unit) {
        IDebuggerUnit dbg = HandlerUtil.getCurrentDebugger(this.context, unit);
        if (dbg == null) {
            while (unit != null) {
                dbg = unit.getUnitProcessor().createDebugger("", unit);
                if (dbg != null) {
                    break;
                }
                if (!(unit.getParent() instanceof IUnit)) {
                    break;
                }
                unit = (IUnit) unit.getParent();
            }
        }
        return dbg;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerAttachHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */