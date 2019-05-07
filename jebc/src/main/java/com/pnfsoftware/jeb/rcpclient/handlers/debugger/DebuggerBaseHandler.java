/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*     */
/*     */

import com.pnfsoftware.jeb.core.IUnitCreator;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerOperationType;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UIState;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public abstract class DebuggerBaseHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*     */
    public DebuggerBaseHandler(String id, String name, String tooltip, String icon, int accelerator)
    /*     */ {
        /*  40 */
        super(id, name, 0, null, icon, accelerator);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  45 */
        return isDebuggerAttached(this.part);
        /*     */
    }

    /*     */
    /*     */
    public IUnit getCurrentUnit(IMPart part) {
        /*  49 */
        if (part == null) {
            /*  50 */
            return null;
            /*     */
        }
        /*  52 */
        IUnit unit = this.context.getPartManager().getUnitForPart(part);
        /*  53 */
        if ((unit == null) && ((part.getManager() instanceof ProjectExplorerPartManager)))
            /*     */ {
            /*  55 */
            ProjectExplorerPartManager tree = (ProjectExplorerPartManager) part.getManager();
            /*  56 */
            if ((tree.getSelectedNode() instanceof IUnit)) {
                /*  57 */
                unit = (IUnit) tree.getSelectedNode();
                /*     */
            }
            /*     */
        }
        /*  60 */
        return unit;
        /*     */
    }

    /*     */
    /*     */
    public IDebuggerUnit getCurrentDebugger(IMPart part) {
        /*  64 */
        IUnit unit = getCurrentUnit(part);
        /*  65 */
        return HandlerUtil.getCurrentDebugger(this.context, unit);
        /*     */
    }

    /*     */
    /*     */
    public boolean isDebuggerAttached(IMPart part) {
        /*  69 */
        IDebuggerUnit dbg = getCurrentDebugger(part);
        /*  70 */
        return (dbg != null) && (dbg.isAttached());
        /*     */
    }

    /*     */
    /*     */
    public String getCodeUnitAddress(IMPart part) {
        /*  74 */
        if (part != null) {
            /*  75 */
            Object object = part.getManager();
            /*  76 */
            if ((object instanceof UnitPartManager)) {
                /*  77 */
                IUnit unit = ((UnitPartManager) object).getUnit();
                /*  78 */
                if ((unit instanceof ICodeUnit)) {
                    /*  79 */
                    return ((UnitPartManager) object).getActiveAddress();
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*  83 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public boolean hasDefaultThread(IMPart part) {
        /*  87 */
        IDebuggerUnit dbg = getCurrentDebugger(part);
        /*  88 */
        return (dbg != null) && (dbg.isAttached()) && (dbg.hasDefaultThread());
        /*     */
    }

    /*     */
    /*     */
    public boolean canStepOperation(IMPart part) {
        /*  92 */
        IDebuggerUnit dbg = getCurrentDebugger(part);
        /*  93 */
        if ((dbg != null) && (dbg.isAttached()) && (dbg.hasDefaultThread()) &&
                /*  94 */       (dbg.canPerformOperation(DebuggerOperationType.UNKNOWN))) {
            /*  95 */
            return dbg.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED;
            /*     */
        }
        /*  97 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public boolean canAttachDebugger(IMPart part)
    /*     */ {
        /* 108 */
        IUnit unit = getCurrentUnit(part);
        /* 109 */
        if (unit == null) {
            /* 110 */
            return false;
            /*     */
        }
        /*     */
        /* 113 */
        List<IUnit> candidates = new ArrayList();
        /* 114 */
        candidates.add(unit);
        /*     */
        /* 116 */
        for (int i = 0; i < 3; i++) {
            /* 117 */
            IUnit last = (IUnit) candidates.get(candidates.size() - 1);
            /* 118 */
            IUnitCreator parent = last.getParent();
            /* 119 */
            if (!(parent instanceof IUnit)) break;
            /* 120 */
            candidates.add((IUnit) parent);
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 128 */
        for (i = unit.getUnitProcessor().getUnitIdentifiers().iterator(); i.hasNext(); ) {
            identifier = (IUnitIdentifier) i.next();
            /* 129 */
            String ftype = identifier.getFormatType();
            /* 130 */
            if ((ftype != null) && (ftype.startsWith("dbug_"))) {
                /* 131 */
                for (IUnit candidate : candidates) {
                    /* 132 */
                    boolean success = identifier.canIdentify(null, candidate);
                    /* 133 */
                    if (success)
                        /* 134 */ return true;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        IUnitIdentifier identifier;
        /* 139 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public void restoreUIBreakpoints(IDebuggerUnit dbg)
    /*     */ {
        /* 144 */
        for (Iterator localIterator1 = dbg.getPotentialDebuggees().iterator(); localIterator1.hasNext(); ) {
            target = (ICodeUnit) localIterator1.next();
            /* 145 */
            uiBreakpoints = this.context.getUIState(target).getBreakpoints();
            /* 146 */
            for (String bpAddress : uiBreakpoints.keySet()) {
                /* 147 */
                IDebuggerBreakpoint bp = dbg.setBreakpoint(bpAddress, target);
                /* 148 */
                if (bp != null)
                    /* 149 */ bp.setEnabled(((Boolean) uiBreakpoints.get(bpAddress)).booleanValue());
                /*     */
            }
            /*     */
        }
        /*     */
        ICodeUnit target;
        /*     */
        Map<String, Boolean> uiBreakpoints;
        /*     */
    }

    /*     */
    /* 156 */
    protected void executeWithPopup(Runnable r) {
        this.context.executeTaskWithPopupDelay(1000, "Please wait...", false, r);
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerBaseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */