/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.DbgAttachDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.DbgAttachDialog.DbgAttachInfo;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import org.apache.commons.lang3.BooleanUtils;

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
/*     */ public class DebuggerAttachHandler
        /*     */ extends DebuggerBaseHandler
        /*     */ {
    /*  32 */   private static final ILogger logger = GlobalLog.getLogger(DebuggerAttachHandler.class);

    /*     */
    /*     */
    public DebuggerAttachHandler() {
        /*  35 */
        super("dbgAttach", S.s(570), null, "eclipse/debug_view.png", 0);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  41 */
        IDebuggerUnit dbg = getCurrentDebugger(this.part);
        /*  42 */
        return ((dbg == null) || (!dbg.isAttached())) && (canAttachDebugger(this.part));
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  47 */
        IUnit unit = getCurrentUnit(this.part);
        /*  48 */
        if (unit == null) {
            /*  49 */
            String msg = "You must first load an artifact before starting a debugging.\n\n(This limitation will be lifted in the future.)";
            /*     */
            /*  51 */
            UI.warn(msg);
            /*  52 */
            return;
            /*     */
        }
        /*     */
        /*  55 */
        this.context.getTelemetry().record("handlerAttachDebugger", "targetUnitType", unit.getFormatType());
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*  63 */
        DbgAttachDialog dlg = new DbgAttachDialog(this.shell, this.context, null, unit);
        /*  64 */
        final DbgAttachDialog.DbgAttachInfo result = dlg.open();
        /*  65 */
        if (result == null) {
            /*  66 */
            return;
            /*     */
        }
        /*     */
        /*  69 */
        final IDebuggerUnit dbg = createDebuggerFor(unit);
        /*  70 */
        if (dbg == null) {
            /*  71 */
            UI.warn("No debugger available");
            /*  72 */
            return;
            /*     */
        }
        /*     */
        /*  75 */
        if (!HandlerUtil.processUnit(this.shell, this.context, dbg, false)) {
            /*  76 */
            UI.warn("The debugger was not set up properly");
            /*  77 */
            return;
            /*     */
        }
        /*     */
        /*  80 */
        boolean success = BooleanUtils.toBoolean((Boolean) this.context.executeTask("Attaching to target...", new Callable()
                /*     */ {
            /*     */
            public Boolean call() throws Exception {
                /*  83 */
                return Boolean.valueOf(dbg.attach(result.info));
                /*     */
            }
            /*     */
        }));
        /*     */
        /*  87 */
        if (!success) {
            /*  88 */
            UI.error("Could not attach to target");
            /*     */
            /*  90 */
            dbg.detach();
            /*  91 */
            return;
            /*     */
        }
        /*     */
        /*  94 */
        restoreUIBreakpoints(dbg);
        /*     */
        /*     */
        /*  97 */
        this.context.setDebuggingMode(true);
        /*     */
        /*  99 */
        PartManager pman = this.context.getPartManager();
        /*     */
        /* 101 */
        pman.create(dbg, true);
        /*     */
        /* 103 */
        List<? extends IUnit> children = dbg.getChildren();
        /* 104 */
        for (IUnit child : children) {
            /* 105 */
            if ((child instanceof IDebuggerUnit))
                /*     */ {
                /* 107 */
                pman.create(child, true);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private IDebuggerUnit createDebuggerFor(IUnit unit) {
        /* 113 */
        IDebuggerUnit dbg = HandlerUtil.getCurrentDebugger(this.context, unit);
        /* 114 */
        if (dbg == null) {
            /* 115 */
            while (unit != null) {
                /* 116 */
                dbg = unit.getUnitProcessor().createDebugger("", unit);
                /* 117 */
                if (dbg != null) {
                    /*     */
                    break;
                    /*     */
                }
                /* 120 */
                if (!(unit.getParent() instanceof IUnit)) {
                    /*     */
                    break;
                    /*     */
                }
                /* 123 */
                unit = (IUnit) unit.getParent();
                /*     */
            }
            /*     */
        }
        /* 126 */
        return dbg;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerAttachHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */