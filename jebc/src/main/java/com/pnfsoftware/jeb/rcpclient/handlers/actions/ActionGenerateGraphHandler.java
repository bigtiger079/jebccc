
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.RcpErrorHandler;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractLocalGraphView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class ActionGenerateGraphHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionGenerateGraphHandler.class);

    public ActionGenerateGraphHandler() {
        super("graph", "Graph", 0, "Generate a visual graph for the current item (eg, CFG for a routine)", "eclipse/all_sc_obj.png", 32);
    }

    public boolean canExecute() {
        UnitPartManager manager = this.context.getPartManager().getUnitPartManager(this.part);
        if (manager == null) {
            return false;
        }
        AbstractUnitFragment<?> activeView = manager.getActiveFragment();
        if ((!(activeView instanceof InteractiveTextView)) && (!(activeView instanceof AbstractLocalGraphView))) {
            return false;
        }
        IUnit unit = manager.getUnit();
        return ((unit instanceof ICodeUnit)) || ((unit instanceof INativeSourceUnit));
    }

    public void execute() {
        this.context.getTelemetry().record("actionGraph");
        UnitPartManager manager = this.context.getPartManager().getUnitPartManager(this.part);
        if (manager == null) {
            return;
        }
        AbstractUnitFragment<?> activeView = manager.getActiveFragment();
        if (activeView == null) {
            return;
        }
        AbstractLocalGraphView<?> graphView = (AbstractLocalGraphView) manager.getFragmentByType(AbstractLocalGraphView.class);
        if (graphView == null) {
            return;
        }
        String address = getActiveAddress(this.part);
        if (address == null) {
            return;
        }
        if (activeView != graphView) {
            if (!graphView.canDisplayAtAddress(address)) {
                logger.warn("Cannot display graph at address: %s", new Object[]{address});
                return;
            }
            String msg = GraphPlaceholder.standardHepMessage;
            UI.popupOptional(this.shell, 0, "Graph View", msg, "dlgGraphView");
            try {
                manager.setActiveFragment(graphView);
                graphView.setActiveAddress(address, null, false);
            } catch (RuntimeException e) {
                if (Licensing.isDebugBuild()) {
                    throw e;
                }
                String details = String.format("requested_address=%s", new Object[]{address});
                this.context.getErrorHandler().processThrowable(e, false, false, false, details, null, graphView.getUnit());
                UI.error("An error occurred when generating the graph.");
            }
        } else {
            InteractiveTextView codeView = (InteractiveTextView) manager.getFragmentByType(InteractiveTextView.class);
            if (codeView == null) {
                return;
            }
            manager.setActiveFragment(codeView);
            codeView.setActiveAddress(address, null, false);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionGenerateGraphHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */