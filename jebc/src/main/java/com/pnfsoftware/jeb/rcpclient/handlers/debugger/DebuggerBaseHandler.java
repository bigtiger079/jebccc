
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerOperationType;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class DebuggerBaseHandler
        extends JebBaseHandler {
    public DebuggerBaseHandler(String id, String name, String tooltip, String icon, int accelerator) {
        super(id, name, 0, null, icon, accelerator);
    }

    public boolean canExecute() {
        return isDebuggerAttached(this.part);
    }

    public IUnit getCurrentUnit(IMPart part) {
        if (part == null) {
            return null;
        }
        IUnit unit = this.context.getPartManager().getUnitForPart(part);
        if ((unit == null) && ((part.getManager() instanceof ProjectExplorerPartManager))) {
            ProjectExplorerPartManager tree = (ProjectExplorerPartManager) part.getManager();
            if ((tree.getSelectedNode() instanceof IUnit)) {
                unit = (IUnit) tree.getSelectedNode();
            }
        }
        return unit;
    }

    public IDebuggerUnit getCurrentDebugger(IMPart part) {
        IUnit unit = getCurrentUnit(part);
        return HandlerUtil.getCurrentDebugger(this.context, unit);
    }

    public boolean isDebuggerAttached(IMPart part) {
        IDebuggerUnit dbg = getCurrentDebugger(part);
        return (dbg != null) && (dbg.isAttached());
    }

    public String getCodeUnitAddress(IMPart part) {
        if (part != null) {
            Object object = part.getManager();
            if ((object instanceof UnitPartManager)) {
                IUnit unit = ((UnitPartManager) object).getUnit();
                if ((unit instanceof ICodeUnit)) {
                    return ((UnitPartManager) object).getActiveAddress();
                }
            }
        }
        return null;
    }

    public boolean hasDefaultThread(IMPart part) {
        IDebuggerUnit dbg = getCurrentDebugger(part);
        return (dbg != null) && (dbg.isAttached()) && (dbg.hasDefaultThread());
    }

    public boolean canStepOperation(IMPart part) {
        IDebuggerUnit dbg = getCurrentDebugger(part);
        if ((dbg != null) && (dbg.isAttached()) && (dbg.hasDefaultThread()) &&
                (dbg.canPerformOperation(DebuggerOperationType.UNKNOWN))) {
            return dbg.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED;
        }
        return false;
    }

    public boolean canAttachDebugger(IMPart part) {
        IUnit unit = getCurrentUnit(part);
        if (unit == null) {
            return false;
        }
        List<IUnit> candidates = new ArrayList();
        candidates.add(unit);
        for (int i = 0; i < 3; i++) {
            IUnit last = (IUnit) candidates.get(candidates.size() - 1);
            IUnitCreator parent = last.getParent();
            if (!(parent instanceof IUnit)) break;
            candidates.add((IUnit) parent);
        }
        IUnitIdentifier identifier;
        Iterator<IUnitIdentifier> iterator = unit.getUnitProcessor().getUnitIdentifiers().iterator();
        while (iterator.hasNext()) {
            identifier = iterator.next();
            String ftype = identifier.getFormatType();
            if ((ftype != null) && (ftype.startsWith("dbug_"))) {
                for (IUnit candidate : candidates) {
                    boolean success = identifier.canIdentify(null, candidate);
                    if (success)
                        return true;
                }
            }
        }
        return false;
    }

    public void restoreUIBreakpoints(IDebuggerUnit dbg) {
        ICodeUnit target;
        Map<String, Boolean> uiBreakpoints;
        Iterator<? extends ICodeUnit> iterator = dbg.getPotentialDebuggees().iterator();
        while (iterator.hasNext()) {
            target = iterator.next();
            uiBreakpoints = this.context.getUIState(target).getBreakpoints();
            for (String bpAddress : uiBreakpoints.keySet()) {
                IDebuggerBreakpoint bp = dbg.setBreakpoint(bpAddress, target);
                if (bp != null)
                    bp.setEnabled(((Boolean) uiBreakpoints.get(bpAddress)).booleanValue());
            }
        }
    }

    protected void executeWithPopup(Runnable r) {
        this.context.executeTaskWithPopupDelay(1000, "Please wait...", false, r);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerBaseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */