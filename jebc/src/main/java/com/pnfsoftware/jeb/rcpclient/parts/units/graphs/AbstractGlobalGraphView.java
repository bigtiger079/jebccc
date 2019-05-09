package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.AllHandlers;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphMode;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.GraphVertexAdapter;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.L;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.R;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.XYGraph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.ILabelProvider;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.AddressNavigator;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.ILocationListener;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolTip;

public abstract class AbstractGlobalGraphView<T extends IUnit> extends AbstractUnitFragment<T> {
    private static final ILogger logger = GlobalLog.getLogger(AbstractGlobalGraphView.class);
    protected ICallgraphBuilder callgraphBuilder;
    private volatile String disasAddress;
    private volatile long disasAddressUpdateTs;
    protected GraphPlaceholder<CallgraphComposite> gp;
    private List<L> lines;
    private Digraph model;
    private List<P> points;
    private boolean propLockView;
    private boolean propShowTooltips;
    private ILocationListener textLocationListener;
    private InteractiveTextView textView;
    private Thread threadAutoLocate;

    static class AnonymousClass10 {
        static final int[] $SwitchMap$com$pnfsoftware$jeb$client$api$Operation = new int[Operation.values().length];

        static {
            try {
                $SwitchMap$com$pnfsoftware$jeb$client$api$Operation[Operation.REFRESH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$pnfsoftware$jeb$client$api$Operation[Operation.CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$pnfsoftware$jeb$client$api$Operation[Operation.ZOOM_IN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$pnfsoftware$jeb$client$api$Operation[Operation.ZOOM_OUT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$pnfsoftware$jeb$client$api$Operation[Operation.ZOOM_RESET.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private class CallgraphComposite extends XYGraph {
        final int MAX_NODE_DENSITY = 30000;
        final int MODE_ALL = 1;
        final int MODE_DEFAUT = 0;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView.CallgraphComposite.determineVisibleVertices(java.util.Collection):java.util.Collection<com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P>, dex: 
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:254)
            	at jadx.core.ProcessClass.process(ProcessClass.java:29)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JavaClass.getCode(JavaClass.java:48)
            Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
            	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
            	... 6 more
            */
        protected java.util.Collection<com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P> determineVisibleVertices(java.util.Collection<com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P> r1) {
            /*
            // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView.CallgraphComposite.determineVisibleVertices(java.util.Collection):java.util.Collection<com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P>, dex: 
            */
            throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView$CallgraphComposite.determineVisibleVertices(java.util.Collection):java.util.Collection");
        }

        CallgraphComposite(Composite parent) {
            super(parent);
            addSupportedMode(new GraphMode(0, "Display relevant nodes", null));
            addSupportedMode(new GraphMode(1, "Display all nodes", null));
            addMouseListener(new MouseAdapter() {
                public void mouseDown(MouseEvent e) {
                    if (CallgraphComposite.this.getVertexCount() == 0) {
                        AbstractGlobalGraphView.this.prepare();
                    }
                }
            });
        }

        public void preDrawing(GC gc) {
            if (getVertexCount() == 0) {
                Rectangle r = getClientArea();
                String text = "Click here to generate the callgraph";
                gc.drawText(text, (r.width - gc.stringExtent(text).x) / 2, r.height / 2);
            }
        }

        public void postDrawing(GC gc) {
            if (!isDragging()) {
                P p = getHoveredPoint();
                if (p != null) {
                    String text = generateLabelForVertex(p);
                    Point dim = gc.stringExtent(text);
                    gc.setForeground(getDisplay().getSystemColor(2));
                    gc.drawString(text, 2, getClientArea().height - dim.y, true);
                }
            }
        }

        private int lambda$determineVisibleVertices$0(P a, P b) {
            return -Double.compare(AbstractGlobalGraphView.this.model.getVertex(a.getId()).weight.doubleValue(), AbstractGlobalGraphView.this.model.getVertex(b.getId()).weight.doubleValue());
        }

        public Collection<L> determineVisibleEdges(Collection<P> visiblePoints, Collection<L> edges) {
            if (getModeId() == 1) {
                return edges;
            }
            Set<Integer> visibleVertexIds = new HashSet();
            for (P p : visiblePoints) {
                visibleVertexIds.add(Integer.valueOf(p.getId()));
            }
            List<L> r = new ArrayList<>();
            for (L edge : edges) {
                int srcId = edge.getSrcId();
                int dstId = edge.getDstId();
                if (visibleVertexIds.contains(Integer.valueOf(srcId)) || visibleVertexIds.contains(Integer.valueOf(dstId))) {
                    r.add(edge);
                }
            }
            return r;
        }

        public void drawVertex(GC gc, P p, Point pt) {
            double w = AbstractGlobalGraphView.this.model.getVertex(p.getId()).getWeight().doubleValue();
            int r = Math.min(192, (int) ((1.0d - w) * 255.0d));
            gc.setBackground(UIAssetManager.getInstance().getColor(255, r, r));
            int pr = ((int) (28.0d * w)) + 10;
            if (p == getHoveredPoint() || p == getSelectedPoint() || (getActivePointsStatus() && isActivePoint(p))) {
                gc.setBackground(UIAssetManager.getInstance().getColor(0));
            }
            gc.fillOval(pt.x - (pr / 2), pt.y - (pr / 2), pr, pr);
        }

        public void drawVertexLabel(GC gc, P p, Point pt) {
            super.drawVertexLabel(gc, p, pt);
        }

        public void drawEdge(GC gc, L l, Point a, Point b) {
            int id;
            boolean highlight = false;
            P pt = getHoveredPoint();
            if (pt != null) {
                id = pt.getId();
                if (id == l.getSrcId() || id == l.getDstId()) {
                    highlight = true;
                } else {
                    highlight = false;
                }
            }
            if (!highlight) {
                pt = getSelectedPoint();
                if (pt != null) {
                    id = pt.getId();
                    if (id == l.getSrcId() || id == l.getDstId()) {
                        highlight = true;
                    } else {
                        highlight = false;
                    }
                }
            }
            if (highlight) {
                gc.setForeground(getDisplay().getSystemColor(2));
            } else {
                gc.setForeground(getDisplay().getSystemColor(15));
            }
            super.drawEdge(gc, l, a, b);
        }
    }

    public abstract boolean preFirstBuild();

    public AbstractGlobalGraphView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context) {
        super(parent, style, unit, unitView, context);
        setLayout(new FillLayout());
        setBackground(UIAssetManager.getInstance().getColor(16777215));
        newGraphWithControls();
    }

    public void dispose() {
        if (this.textLocationListener != null) {
            this.textView.removeLocationListener(this.textLocationListener);
            this.textLocationListener = null;
        }
        if (this.threadAutoLocate != null) {
            this.threadAutoLocate.interrupt();
            this.threadAutoLocate = null;
        }
        super.dispose();
    }

    public final void newGraphWithControls() {
        this.gp = new GraphPlaceholder<CallgraphComposite>(this, 0, true, true) {
            public CallgraphComposite createGraph(GraphPlaceholder<CallgraphComposite> gp, CallgraphComposite callgraphComposite) {
                return new CallgraphComposite(gp);
            }
        };
        this.gp.addHelpButtonToToolbar("Callgraph view.\n\nThis view displays invocation relationships between routines of the code unit.\n\nStandard graph keyboard shortcuts can be used to zoom in/out/reset/center the graph.\nAt a given zoom level, relevant nodes, based on their weights, will be displayed.\nDouble-click a node to navigate to the corresponding routine in the matching disassembly view.\nIn a code view, use the " + UI.MOD1 + "+G shortcut to sync up the graph to your current location.\n");
        this.gp.addModesBoxToToolbar();
        this.propLockView = this.context.getPropertyManager().getBoolean("graphs.LockView", false);
        Button btnLockView = this.gp.addCheckbox("Lock", this.propLockView);
        btnLockView.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                AbstractGlobalGraphView.this.propLockView = ((Button) e.widget).getSelection();
                AbstractGlobalGraphView.this.context.getPropertyManager().setBoolean("graphs.LockView", Boolean.valueOf(AbstractGlobalGraphView.this.propLockView));
            }
        });
        btnLockView.setToolTipText("Lock the graph view, that is, do not attempt to sync the graph with the current position in the disassembly listing. Synchronization can be done manually using the " + UI.MOD1 + "+G keyboard shortcut.");
        Button btnKeepDocked = this.gp.addCheckbox("Keep Docked", this.context.getPropertyManager().getBoolean("graphs.KeepInMainDock", false));
        btnKeepDocked.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                AbstractGlobalGraphView.this.context.getPropertyManager().setBoolean("graphs.KeepInMainDock", Boolean.valueOf(((Button) e.widget).getSelection()));
            }
        });
        btnKeepDocked.setToolTipText("Keep the global graphs in the main dock instead of a separate window. Will take effect when processing the next file.");
        new ContextMenu(this.gp.getGraph()).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                if (AbstractGlobalGraphView.this.getContext() != null) {
                    AllHandlers.getInstance().fillManager(menuMgr, 4);
                }
            }
        });
    }

    public GraphPlaceholder<CallgraphComposite> getGraphWithControls() {
        return this.gp;
    }

    public CallgraphComposite getGraph() {
        return (CallgraphComposite) this.gp.getGraph();
    }

    public boolean setFocus() {
        return this.gp.setFocus();
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        P sel = ((CallgraphComposite) this.gp.getGraph()).getSelectedPoint();
        if (sel == null) {
            return null;
        }
        return this.callgraphBuilder.getAddressForVertexId(sel.getId());
    }

    public boolean setActiveAddress(String address, Object extraAddressDetails, boolean recordPosition) {
        if (!isPrepared()) {
            return false;
        }
        try {
            this.disasAddress = null;
            Integer vertexId = this.callgraphBuilder.getVertexIdForAddress(address);
            if (vertexId == null) {
                return false;
            }
            CallgraphComposite g = getGraph();
            g.centerGraph(vertexId.intValue(), 0, true);
            if (!g.isVertexVisible(vertexId.intValue())) {
                for (int i = 0; i < 20; i++) {
                    g.zoomGraph(1);
                    if (g.isVertexVisible(vertexId.intValue())) {
                        break;
                    }
                }
            }
            g.setSelection(vertexId);
            g.refreshGraph();
            return true;
        } catch (Exception e) {
            logger.error("Cannot set active address in callgraph: %s", new Object[]{address});
            this.context.getErrorHandler().processThrowable(e, false, false, false, "Requested address: " + address, null, this.unit);
            return false;
        }
    }

    public boolean verifyOperation(OperationRequest req) {
        if (!isPrepared()) {
            return false;
        }
        switch (AnonymousClass10.$SwitchMap$com$pnfsoftware$jeb$client$api$Operation[req.getOperation().ordinal()]) {
            case 1:
                return true;
            case 3:
            case AllHandlers.GRP_NAVCANVAS:
            case 5:
                return true;
            default:
                return false;
        }
    }

    public boolean doOperation(OperationRequest req) {
        if (!isPrepared()) {
            return false;
        }
        switch (AnonymousClass10.$SwitchMap$com$pnfsoftware$jeb$client$api$Operation[req.getOperation().ordinal()]) {
            case 1:
                ((CallgraphComposite) this.gp.getGraph()).reset();
                prepare();
                return true;
            case 3:
                getGraph().zoomGraph(1);
                return true;
            case AllHandlers.GRP_NAVCANVAS:
                getGraph().zoomGraph(-1);
                return true;
            case 5:
                getGraph().fitGraph();
                return true;
            default:
                return false;
        }
    }

    public boolean isPrepared() {
        return this.model != null;
    }

    public void prepare() {
        this.context.getTelemetry().record("actionCallgraphGeneration");
        if (preFirstBuild()) {
            Runnable graphPreparer = new CallgraphPreparer(this.callgraphBuilder);
            this.context.executeTaskWithPopupDelay(2000, "Generating callgraph", false, graphPreparer);
//            this.model = graphPreparer.model;
//            this.points = graphPreparer.points;
//            this.lines = graphPreparer.lines;
            if (this.model != null && this.points != null && this.lines != null) {
                if (this.textLocationListener == null) {
                    for (UnitPartManager pman : this.context.getPartManager().getPartManagersForUnit(this.unit)) {
                        InteractiveTextView view = (InteractiveTextView) pman.getFragmentByType(InteractiveTextView.class);
                        if (view != null) {
                            this.textView = view;
                            break;
                        }
                    }
                    if (this.textView != null) {
                        InteractiveTextView interactiveTextView = this.textView;
                        this.textLocationListener = new ILocationListener() {
                            public void locationChanged(String address) {
                                if (!AbstractGlobalGraphView.this.propLockView && AbstractGlobalGraphView.this.isPrepared()) {
                                    AbstractGlobalGraphView.this.disasAddress = address;
                                    AbstractGlobalGraphView.this.disasAddressUpdateTs = System.currentTimeMillis();
                                }
                            }
                        };
                        interactiveTextView.addLocationListener(this.textLocationListener);
                        this.threadAutoLocate = ThreadUtil.start(new Runnable() {
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(1000);
                                        if (!(AbstractGlobalGraphView.this.disasAddress == null || AbstractGlobalGraphView.this.disasAddressUpdateTs == 0)) {
                                            AbstractGlobalGraphView.logger.i("Will attempt to setAddress: %s", new Object[]{AbstractGlobalGraphView.this.disasAddress});
                                            if (AbstractGlobalGraphView.this.disasAddress != null && System.currentTimeMillis() - AbstractGlobalGraphView.this.disasAddressUpdateTs > 1000) {
                                                try {
                                                    AbstractGlobalGraphView.this.getDisplay().syncExec(new Runnable() {
                                                        public void run() {
                                                            if (AbstractGlobalGraphView.this.disasAddress != null) {
                                                                AbstractGlobalGraphView.this.setActiveAddress(AbstractGlobalGraphView.this.disasAddress);
                                                                AbstractGlobalGraphView.this.disasAddress = null;
                                                            }
                                                        }
                                                    });
                                                } catch (SWTException e) {
                                                    return;
                                                }
                                            }
                                        }
                                    } catch (InterruptedException e2) {
                                        return;
                                    }
                                }
                            }
                        });
                    }
                }
                CallgraphComposite gg = (CallgraphComposite) this.gp.getGraph();
                gg.addGraphVertexListener(new GraphVertexAdapter() {
                    public void onVertexClicked(XYGraph gg, P p) {
                        AbstractGlobalGraphView.logger.info("Node: %s", new Object[]{AbstractGlobalGraphView.this.model.getVertex(p.getId()).getLabel()});
                    }

                    public void onVertexDoubleClicked(XYGraph graph, P p) {
                        String address = AbstractGlobalGraphView.this.getActiveAddress();
                        if (address != null) {
                            new AddressNavigator(AbstractGlobalGraphView.this.context, null, AbstractGlobalGraphView.this, AbstractGlobalGraphView.this.unit).forceNavigation(address);
                        }
                    }

                    public void onVertexHoverIn(XYGraph graph, P p) {
                        if (AbstractGlobalGraphView.this.propShowTooltips) {
                            String s = graph.generateLabelForVertex(p);
                            if (!Strings.isBlank(s)) {
                                ToolTip tip = new ToolTip(graph.getShell(), 0);
                                Point pt = AbstractGlobalGraphView.this.getDisplay().map(graph, null, graph.convertCoord(p));
                                tip.setLocation(pt.x + 20, pt.y + 20);
                                tip.setMessage(s);
                                tip.setAutoHide(true);
                                tip.setVisible(true);
                            }
                        }
                    }
                });
                gg.setLabelProvider(new ILabelProvider() {
                    public String getLabel(int vertexId) {
                        return AbstractGlobalGraphView.this.model.getVertex(vertexId).getLabel();
                    }
                });
                gg.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                    }
                });
                gg.setParameters(this.points, this.lines, true);
                gg.refreshGraph();
            }
        }
    }

    private List<P> selectPointsInBounds(Collection<P> points, R bounds) {
        List<P> r = new ArrayList<>();
        for (P p : points) {
            if (bounds.contains(p)) {
                r.add(p);
            }
        }
        return r;
    }
}