/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.actions;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpErrorHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractLocalGraphView;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class ActionGenerateGraphHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*  32 */   private static final ILogger logger = GlobalLog.getLogger(ActionGenerateGraphHandler.class);

    /*     */
    /*     */
    public ActionGenerateGraphHandler() {
        /*  35 */
        super("graph", "Graph", 0, "Generate a visual graph for the current item (eg, CFG for a routine)", "eclipse/all_sc_obj.png", 32);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  40 */
        UnitPartManager manager = this.context.getPartManager().getUnitPartManager(this.part);
        /*  41 */
        if (manager == null) {
            /*  42 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /*  46 */
        AbstractUnitFragment<?> activeView = manager.getActiveFragment();
        /*  47 */
        if ((!(activeView instanceof InteractiveTextView)) && (!(activeView instanceof AbstractLocalGraphView))) {
            /*  48 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /*  52 */
        IUnit unit = manager.getUnit();
        /*  53 */
        return ((unit instanceof ICodeUnit)) || ((unit instanceof INativeSourceUnit));
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  58 */
        this.context.getTelemetry().record("actionGraph");
        /*     */
        /*  60 */
        UnitPartManager manager = this.context.getPartManager().getUnitPartManager(this.part);
        /*  61 */
        if (manager == null) {
            /*  62 */
            return;
            /*     */
        }
        /*     */
        /*  65 */
        AbstractUnitFragment<?> activeView = manager.getActiveFragment();
        /*  66 */
        if (activeView == null) {
            /*  67 */
            return;
            /*     */
        }
        /*     */
        /*  70 */
        AbstractLocalGraphView<?> graphView = (AbstractLocalGraphView) manager.getFragmentByType(AbstractLocalGraphView.class);
        /*  71 */
        if (graphView == null) {
            /*  72 */
            return;
            /*     */
        }
        /*     */
        /*  75 */
        String address = getActiveAddress(this.part);
        /*  76 */
        if (address == null) {
            /*  77 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /*  81 */
        if (activeView != graphView) {
            /*  82 */
            if (!graphView.canDisplayAtAddress(address)) {
                /*  83 */
                logger.warn("Cannot display graph at address: %s", new Object[]{address});
                /*  84 */
                return;
                /*     */
            }
            /*     */
            /*  87 */
            String msg = GraphPlaceholder.standardHepMessage;
            /*  88 */
            UI.popupOptional(this.shell, 0, "Graph View", msg, "dlgGraphView");
            /*     */
            try
                /*     */ {
                /*  91 */
                manager.setActiveFragment(graphView);
                /*  92 */
                graphView.setActiveAddress(address, null, false);
                /*     */
            }
            /*     */ catch (RuntimeException e) {
                /*  95 */
                if (Licensing.isDebugBuild()) {
                    /*  96 */
                    throw e;
                    /*     */
                }
                /*  98 */
                String details = String.format("requested_address=%s", new Object[]{address});
                /*  99 */
                this.context.getErrorHandler().processThrowable(e, false, false, false, details, null, graphView.getUnit());
                /* 100 */
                UI.error("An error occurred when generating the graph.");
                /*     */
            }
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 105 */
            InteractiveTextView codeView = (InteractiveTextView) manager.getFragmentByType(InteractiveTextView.class);
            /* 106 */
            if (codeView == null) {
                /* 107 */
                return;
                /*     */
            }
            /* 109 */
            manager.setActiveFragment(codeView);
            /* 110 */
            codeView.setActiveAddress(address, null, false);
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionGenerateGraphHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */