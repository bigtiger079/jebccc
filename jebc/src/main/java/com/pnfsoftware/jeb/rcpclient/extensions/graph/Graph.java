
package com.pnfsoftware.jeb.rcpclient.extensions.graph;


import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.IZoomable;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ZoomableUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Cell;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.ConstraintSolver;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Spreadsheet;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Vector;
import com.pnfsoftware.jeb.rcpclient.util.ColorsGradient;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.base.Flags;
import com.pnfsoftware.jeb.util.collect.IMultiSegmentMap;
import com.pnfsoftware.jeb.util.collect.ISegmentMap;
import com.pnfsoftware.jeb.util.collect.IntegerSegment;
import com.pnfsoftware.jeb.util.collect.MultiMap;
import com.pnfsoftware.jeb.util.collect.MultiSegmentMap;
import com.pnfsoftware.jeb.util.collect.SegmentMap;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class Graph
        extends AbstractGraph
        implements IZoomable {
    private static final ILogger logger = GlobalLog.getLogger(Graph.class);

    static final int FLAG_NODE_NO_HANDLES = 1;

    static final int FLAG_NODE_NO_CONTENTS = 2;
    private boolean keyboardControls = true;
    private boolean mouseControls = true;

    private boolean kbModifier1Pressed;
    private GraphStyleData styleData = GraphStyleData.buildDefault();

    private List<GraphNode> nodes = new ArrayList();
    private List<GraphEdge> edges = new ArrayList();

    private Spreadsheet<GraphNode> grid;
    private Point lastMouseCursor = new Point(-1, -1);
    private Point lastMouseCursorDown = new Point(-1, -1);

    private boolean zoomingAllowed = true;

    private int initialZoomLevel;

    private int zoomLevel;
    Point virtualOrigin = new Point(0, 0);

    private int[] xConstraints = new int[0];
    private int[] yConstraints = new int[0];

    private boolean dragging;

    private int draggingX;

    private int draggingY;
    private boolean activeEdgeColoringEnabled;
    private int activeEdgeId = -1;
    private GraphNode hoverNode = null;
    private GraphNode activeNode = null;

    private Flags nodeFlags = new Flags();

    private static final int defaultNodeWidth = 150;

    private static final int defaultNodeHeight = 100;

    private static final double nodeMarginTop = 10.0D;

    private static final double nodeMarginBottom = 10.0D;

    private static final double nodeMarginLeft = 10.0D;
    private static final double nodeMarginRight = 10.0D;
    private boolean debugging = false;
    private boolean dbgDoNotDisplayNodesGrid = false;
    private Color[] dbgNodeColors = {SwtRegistry.getInstance().getColor(14737408),
            SwtRegistry.getInstance().getColor(57568)};

    private long lastRedrawExectime;

    static final int REDRAW_CAUSE_OTHER = 0;

    static final int REDRAW_CAUSE_DRAG = 1;
    private int redrawCause;
    private Object redrawObject;
    private GraphEdgeManager edgeman;


    public Graph(Composite parent, int style) {

        super(parent, 537919488);

        setLayout(null);


        UI.initialize();


        addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {

                Assert.a(Graph.this.nodes.size() == Graph.this.getChildren().length, "Illegal children were detected");


                try {

                    e.gc.setAntialias(1);
                } catch (SWTException localSWTException) {
                }

                int[] xBases;

                int[] yBases;

                int icol;

                Iterator<Integer> colors;

                if ((Graph.this.debugging) && (!Graph.this.dbgDoNotDisplayNodesGrid)) {

                    boolean betterNodeGridColoring = true;


                    xBases = Graph.this.buildCoordsFromSizes(Graph.this.virtualOrigin.x, Graph.this.xConstraints);

                    yBases = Graph.this.buildCoordsFromSizes(Graph.this.virtualOrigin.y, Graph.this.yConstraints);

                    List<Cell<GraphNode>> cells = Graph.this.grid.getRealCells();

                    icol = 0;

                    colors = ColorsGradient.getSequentialIterator(0, 10);

                    for (Cell<GraphNode> cell : cells) {

                        int x0 = xBases[cell.getColumn()];

                        int x1 = xBases[cell.getNextColumn()];

                        int y0 = yBases[cell.getRow()];

                        int y1 = yBases[cell.getNextRow()];

                        e.gc.setBackground(SwtRegistry.getInstance().getColor(((Integer) colors.next()).intValue()));

                        e.gc.fillRectangle(x0, y0, x1 - x0, y1 - y0);

                        icol = 1 - icol;

                    }

                }


                Graph.this.redrawEdges(e.gc);


            }


        });

        addMouseListener(new MouseListener() {

            public void mouseDown(MouseEvent e) {

                if (e.button != 1) {

                    return;

                }


                Graph.this.lastMouseCursorDown.x = e.x;

                Graph.this.lastMouseCursorDown.y = e.y;


                if (Graph.this.activeEdgeId != -1) {

                    Graph.logger.i("Mouse click on Edge: %d", new Object[]{Integer.valueOf(Graph.this.activeEdgeId)});


                    GraphNode target = Graph.this.isPrimaryModifierKeyPressed() ? ((GraphEdge) Graph.this.edges.get(Graph.this.activeEdgeId)).src : ((GraphEdge) Graph.this.edges.get(Graph.this.activeEdgeId)).dst;

                    Graph.this.centerGraph(target, 16777216, 16777216, true);

                    Graph.this.setActiveNode(target, false);


                } else if (Graph.this.hoverNode != null) {

                    Graph.logger.i("Mouse click on Node: %s", new Object[]{Graph.this.activeNode});

                } else {

                    Graph.this.dragging = true;

                    Graph.this.draggingX = e.x;

                    Graph.this.draggingY = e.y;

                }

            }


            public void mouseUp(MouseEvent e) {

                if (e.button != 1) {

                    return;

                }


                if (Graph.this.dragging) {


                    if (new Point(e.x, e.y).equals(Graph.this.lastMouseCursorDown)) {

                        Graph.this.setFocus();

                    }

                    Graph.this.dragging = false;

                }

            }


            public void mouseDoubleClick(MouseEvent e) {

                if (e.button != 1) {

                    return;

                }

                Graph.logger.i("DBLCLK: active edge: " + Graph.this.activeEdgeId, new Object[0]);

            }

        });

        addMouseMoveListener(new MouseMoveListener() {


            public void mouseMove(MouseEvent e) {

                Graph.this.lastMouseCursor.x = e.x;

                Graph.this.lastMouseCursor.y = e.y;


                if (Graph.this.dragging) {

                    int deltaX = e.x - Graph.this.draggingX;

                    int deltaY = e.y - Graph.this.draggingY;

                    Graph.this.draggingX = e.x;

                    Graph.this.draggingY = e.y;

                    Graph.this.dragGraph(deltaX, deltaY);

                    return;

                }


                Graph.this.determineActiveEdge(null);


            }


        });

        final Listener mouseMoveFilter = new Listener() {

            public void handleEvent(Event e) {

                if (!(e.widget instanceof Control)) {

                    return;

                }

                Control ctl = (Control) e.widget;


                if (!Graph.this.isVisible()) {

                    return;

                }


                Point p = Display.getCurrent().map(ctl, Graph.this, new Point(e.x, e.y));


                GraphNode currentNode = null;

                for (GraphNode node : Graph.this.nodes) {

                    Rectangle b = node.getBounds();

                    if ((p.x >= b.x) && (p.x < b.x + b.width) && (p.y >= b.y) && (p.y < b.y + b.height)) {

                        currentNode = node;

                        break;

                    }

                }

                if ((Graph.this.hoverNode != null) && ((currentNode == null) || (currentNode != Graph.this.hoverNode))) {

                    Graph.this.notifyNodeMouseExit(Graph.this.hoverNode);

                    Graph.this.hoverNode = null;

                }

                if ((Graph.this.hoverNode == null) && (currentNode != null)) {

                    Graph.this.notifyNodeMouseEnter(currentNode);

                    Graph.this.hoverNode = currentNode;

                }

            }

        };

        getDisplay().addFilter(5, mouseMoveFilter);


        addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {

                Graph.this.getDisplay().removeFilter(5, mouseMoveFilter);

            }

        });

    }


    public void setStyleData(GraphStyleData styleData) {

        this.styleData = (styleData == null ? GraphStyleData.buildDefault() : styleData);

    }


    public GraphStyleData getStyleData() {

        return this.styleData;

    }


    private boolean isPrimaryModifierKeyPressed() {

        return (this.kbModifier1Pressed) || ((UI.getKeyboardModifiersState() & SWT.MOD1) != 0);

    }


    private int[] buildCoordsFromSizes(int start, int[] sizes) {

        int[] coords = new int[sizes.length + 1];

        for (int i = 0; i < coords.length; i++) {

            coords[i] = start;

        }

        for (int i = 1; i < coords.length; i++) {

            int size = sizes[(i - 1)];

            for (int j = i; j < coords.length; j++) {

                coords[j] += size;

            }

        }

        return coords;

    }


    public boolean setFocus() {

        return forceFocus();

    }


    public void setDebugging(boolean debugging) {

        this.debugging = debugging;

    }


    public boolean isDebugging() {

        return this.debugging;

    }


    public void setZoomingAllowed(boolean zoomingAllowed) {

        this.zoomingAllowed = zoomingAllowed;

    }


    public boolean isZoomingAllowed() {

        return this.zoomingAllowed;

    }


    public void setInitialZoomLevel(int initialZoomLevel) {

        this.initialZoomLevel = initialZoomLevel;

    }


    public int getInitialZoomLevel() {

        return this.initialZoomLevel;

    }


    public void setKeyboardControls(boolean keyboardControls) {

        this.keyboardControls = keyboardControls;

    }


    public boolean isKeyboardControls() {

        return this.keyboardControls;

    }


    public void setMouseControls(boolean mouseControls) {

        this.mouseControls = mouseControls;

    }


    public boolean isMouseControls() {

        return this.mouseControls;

    }


    public void setActiveEdgeColoringEnabled(boolean activeEdgeColoringEnabled) {

        this.activeEdgeColoringEnabled = activeEdgeColoringEnabled;

    }


    public boolean isActiveEdgeColoringEnabled() {

        return this.activeEdgeColoringEnabled;

    }


    private void determineActiveEdge(Point pt) {

        if (pt == null) {

            pt = this.lastMouseCursor;

        }

        int edgeId = getActiveLineGroupId(pt.x, pt.y, 4, 4);

        changeActiveEdge(edgeId);

    }


    private void changeActiveEdge(int edgeId) {

        if (edgeId >= 0) {

            if (this.activeEdgeId != edgeId) {


                if (this.activeEdgeId >= 0) {

                    GraphEdge edge = (GraphEdge) this.edges.get(this.activeEdgeId);

                    edge.setState(0);

                    notifyEdgeMouseExit(edge);

                }

                GraphEdge edge = (GraphEdge) this.edges.get(edgeId);

                edge.setState(1);

                this.activeEdgeId = edgeId;

                if (!this.activeEdgeColoringEnabled) {

                    refreshGraph();

                }

                notifyEdgeMouseEnter(edge);

            }


        } else if (this.activeEdgeId >= 0) {

            GraphEdge edge = (GraphEdge) this.edges.get(this.activeEdgeId);

            edge.setState(0);

            this.activeEdgeId = -1;

            if (!this.activeEdgeColoringEnabled) {

                refreshGraph();

            }

            notifyEdgeMouseExit(edge);

        }

    }


    public int getVertexCount() {

        return getNodeCount();

    }


    public int getNodeCount() {

        return this.nodes.size();

    }


    public GraphNode getNode(int index) {

        return (GraphNode) this.nodes.get(index);

    }


    public List<GraphNode> getNodes() {

        return this.nodes;

    }


    public GraphNode getActiveNode() {

        return this.activeNode;

    }


    public void setActiveNode(GraphNode node, boolean centerOnNode) {

        if (node == null) {

            if (this.activeNode != null) {

                setFocus();

            }

        } else if (node != this.activeNode) {

            node.setFocus();

            if (centerOnNode) {

                centerGraph(node);

            }

        }

    }


    public boolean checkNodeVisibility(GraphNode node, boolean wantFullyVisible) {

        Rectangle a = getClientArea();

        int left0 = a.x;

        int right0 = a.x + a.width;

        int top0 = a.y;

        int bottom0 = a.y + a.height;


        Rectangle b = node.getBounds();

        int left1 = b.x;

        int right1 = b.x + b.width;

        int top1 = b.y;

        int bottom1 = b.y + b.height;


        if ((left1 >= left0) && (right1 <= right0) && (top1 >= top0) && (bottom1 <= bottom0)) {

            return true;

        }


        if (wantFullyVisible) {

            return false;

        }


        left0 -= b.width;

        right0 += b.width;

        top0 -= b.height;

        bottom0 += b.height;

        if ((left1 > left0) && (right1 < right0) && (top1 > top0) && (bottom1 < bottom0)) {

            return true;

        }


        return false;

    }


    public void showNode(GraphNode node, boolean wantFullyVisible) {

        if (!checkNodeVisibility(node, wantFullyVisible)) {

            centerGraph(node, 128, 128, true);

        }

    }


    public List<GraphEdge> getEdges() {

        return this.edges;

    }


    public GraphEdge getActiveEdge() {

        if (this.activeEdgeId < 0) {

            return null;

        }

        return (GraphEdge) this.edges.get(this.activeEdgeId);

    }


    public void setActiveEdge(GraphEdge edge) {

        int edgeId = this.edges.indexOf(edge);

        changeActiveEdge(edgeId);

    }


    public void dragGraph(int deltaX, int deltaY) {

        dragGraph(deltaX, deltaY, false);

    }


    public void dragGraph(int deltaX, int deltaY, boolean progressive) {

        int virtualOriginX1 = this.virtualOrigin.x + deltaX;

        int virtualOriginY1 = this.virtualOrigin.y + deltaY;

        int frameCount;

        if (progressive) {


            frameCount = 10;

            int incrX = deltaX / 10;

            int incrY = deltaY / 10;


            if ((incrX != 0) || (incrY != 0)) {

                for (int i = 0; i < 9; i++) {

                    this.virtualOrigin.x += incrX;

                    this.virtualOrigin.y += incrY;

                    for (GraphNode node : this.nodes) {

                        Point loc = node.getLocation();

                        loc.x += incrX;

                        loc.y += incrY;

                        node.setLocation(loc);

                    }

                    refreshGraph(1, new Point(incrX, incrY));


                    try {

                        Thread.sleep(10L);

                    } catch (InterruptedException e) {

                        break;

                    }

                }

            }

        }


        deltaX = virtualOriginX1 - this.virtualOrigin.x;

        deltaY = virtualOriginY1 - this.virtualOrigin.y;

        this.virtualOrigin.x = virtualOriginX1;

        this.virtualOrigin.y = virtualOriginY1;

        for (GraphNode node : this.nodes) {

            Point loc = node.getLocation();

            loc.x += deltaX;

            loc.y += deltaY;

            node.setLocation(loc);

        }

        refreshGraph(1, new Point(deltaX, deltaY));

    }


    public void centerGraph(int nodeIndex) {

        if ((nodeIndex < 0) || (nodeIndex >= this.nodes.size())) {

            return;

        }

        centerGraph((GraphNode) this.nodes.get(nodeIndex));

    }


    public void centerGraph(GraphNode node) {

        centerGraph(node, 128, 128, false);

    }


    public void centerGraph(GraphNode node, int nodeAnchorFlags, int clientAnchorFlags, boolean progressive) {

        verifyNode(node);

        logger.debug("Centering on node: %s", new Object[]{node});


        Rectangle bounds = node.getBounds();


        Rectangle client = getClientArea();


        int x0 = bounds.x + bounds.width / 2;

        int y0 = bounds.y + bounds.height / 2;

        if ((nodeAnchorFlags & 0x80) != 0) {

            y0 = bounds.y;

        } else if ((nodeAnchorFlags & 0x400) != 0) {

            y0 = bounds.y + bounds.height;

        }

        if ((nodeAnchorFlags & 0x4000) != 0) {

            x0 = bounds.x;

        } else if ((nodeAnchorFlags & 0x20000) != 0) {

            x0 = bounds.x + bounds.width;

        }


        int x1 = client.width / 2;

        int y1 = client.height / 2;

        if ((clientAnchorFlags & 0x80) != 0) {

            y1 = client.height / 10;

        } else if ((clientAnchorFlags & 0x400) != 0) {

            y1 = client.height * 9 / 10;

        }

        if ((clientAnchorFlags & 0x4000) != 0) {

            x1 = client.width / 10;

        } else if ((clientAnchorFlags & 0x20000) != 0) {

            x1 = client.width * 9 / 10;

        }


        int deltaX = x1 - x0;

        int deltaY = y1 - y0;

        dragGraph(deltaX, deltaY, progressive);

    }


    public void positionGraph(double xRatio, double yRatio) {

        Rectangle container = getContainerArea();

        Rectangle client = getClientArea();


        int x0 = (int) (container.x + xRatio * container.width);

        int y0 = (int) (container.y + yRatio * container.height);


        int deltaX = client.width / 2 - x0;

        int deltaY = client.height / 2 - y0;

        dragGraph(deltaX, deltaY);

    }


    public void centerGraph() {

        Rectangle container = getContainerArea();

        Rectangle client = getClientArea();


        int x = (client.width - container.width) / 2;

        int y = (client.height - container.height) / 2;


        int deltaX = x - container.x;

        int deltaY = y - container.y;

        dragGraph(deltaX, deltaY);

    }


    public void zoomGraph(int zoom) {

        zoomGraph(zoom, null);

    }


    public void zoomGraph(int zoom, Point centerPoint) {

        applyZoom(zoom, false);

    }


    public int getZoomLevel() {

        return this.zoomLevel;

    }


    public boolean applyZoom(int zoom, boolean dryRun) {

        if (!this.zoomingAllowed) {

            return false;

        }

        return internalZoom(zoom, dryRun, true);

    }


    private boolean internalZoom(int zoom, boolean dryRun, boolean refresh) {

        zoom = ZoomableUtil.sanitizeZoom(zoom);

        int newZoomLevel = ZoomableUtil.updateZoom(this.zoomLevel, zoom);

        if (newZoomLevel == 0) {

            zoom = 0;

        }


        for (GraphNode node : this.nodes) {

            if (!node.applyZoom(zoom, true)) {

                return false;

            }

        }

        if (dryRun) {

            return true;

        }


        if (this.zoomLevel == 0) {

            for (GraphNode node : this.nodes) {

                node.setData("noZoomBounds", node.getBounds());

            }

        }


        this.dbgDoNotDisplayNodesGrid = true;

        this.nodeFlags.update(1, newZoomLevel < 0);

        this.nodeFlags.update(2, newZoomLevel < -5);


        double xratio = 1.0D;

        double yratio = 1.0D;

        int i = 0;

        for (GraphNode node : this.nodes) {

            Point size0 = node.getSize();


            if (!node.applyZoom(zoom, false)) {

                throw new RuntimeException();

            }

            if ((i == 0) && (zoom != 0)) {

                Point size1 = node.getSize();

                xratio = 1.0D + (size1.x - size0.x) / size0.x;

                yratio = 1.0D + (size1.y - size0.y) / size0.y;

            }


            i++;

        }


        if (zoom == 0) {

            for (GraphNode node : this.nodes) {

                Rectangle bounds = (Rectangle) node.getData("noZoomBounds");

                if (bounds != null) {

                    node.setBounds(bounds);

                    node.setData("noZoomBounds", null);

                }

            }

        } else {

            for (GraphNode node : this.nodes) {

                Point loc = node.getLocation();

                loc.x = ((int) (loc.x * xratio));

                loc.y = ((int) (loc.y * yratio));

                node.setLocation(loc);

            }


            this.virtualOrigin.x = ((int) (this.virtualOrigin.x * xratio));

            this.virtualOrigin.y = ((int) (this.virtualOrigin.y * xratio));

        }


        if (refresh) {

            refreshGraph();

        }


        this.zoomLevel = newZoomLevel;

        return true;

    }


    public Flags getNodeFlags() {

        return this.nodeFlags;

    }


    public void refreshGraph() {

        refreshGraph(0, null);

    }


    public void refreshGraph(int cause, Object object) {

        long t0 = System.currentTimeMillis();

        try {

            this.redrawCause = cause;

            this.redrawObject = object;

            redraw(0, 0, getSize().x, getSize().y, true);

            update();

        } finally {

            this.redrawCause = 0;

            this.redrawObject = null;

        }


        long exectime = System.currentTimeMillis() - t0;


        this.lastRedrawExectime = exectime;


        determineActiveEdge(null);

        notifyGraphChange();

    }


    public void setEdgeManager(GraphEdgeManager edgeman) {

        this.edgeman = edgeman;

    }


    private void redrawEdges(GC gc) {

        invalidateCaches();


        if (this.edgeman != null) {

            this.edgeman.draw(gc, this.redrawCause, this.redrawObject);

        } else {

            for (GraphEdge edge : this.edges) {

                edge.draw(gc);

            }

        }

    }


    private void verifyNode(GraphNode node) {

        if (node == null) {

            throw new IllegalArgumentException("Null node");

        }

        if (!this.nodes.contains(node)) {

            throw new RuntimeException("The node does not belong to the current graph: " + node);

        }

        if (node.getParent() != this) {

            throw new RuntimeException("Parent of composite node must be the owner composite graph: " + node);

        }

    }


    public GraphNode registerNode(GraphNode node, boolean autoAssignBounds) {

        if (this.nodes.contains(node)) {

            throw new RuntimeException("The node already belongs to the current graph");

        }


        int i = this.nodes.size();

        node.id = i;

        this.nodes.add(node);


        if (autoAssignBounds) {

            node.setBounds(50 + i * 200, 50 + i * 200, 150, 100);

        }

        return node;

    }


    public void registerNodesGrid(Spreadsheet<GraphNode> grid) {

        Assert.a(this.nodes.isEmpty(), "Nodes were already registered");

        this.grid = grid;


        int rowcnt = grid.getRowCount();

        int colcnt = grid.getColumnCount();


        ConstraintSolver xsolver = new ConstraintSolver(colcnt);

        ConstraintSolver ysolver = new ConstraintSolver(rowcnt);


        boolean[] seenRows = new boolean[grid.getRowCount()];

        boolean[] seenCols = new boolean[grid.getColumnCount()];


        List<Cell<GraphNode>> cells = grid.getRealCells();
        int outcnt;
        for (Cell<GraphNode> cell : cells) {

            seenRows[cell.getRow()] = true;

            seenCols[cell.getColumn()] = true;


            GraphNode node = (GraphNode) cell.getObject();

            Point nodesize = node.computeSize(-1, -1);

            node.setSize(nodesize);


            int incnt = 2 + Math.max(0, node.getNodeInputEdgeCount() - 2);

            outcnt = 2 + Math.max(0, node.getNodeOutputEdgeCount() - 2);


            int x = cell.getColumn();

            int xspan = cell.getHorizontalSpan();

            xsolver.add(new Vector(colcnt).onRange(x, xspan).get(), (int) (nodesize.x + 10.0D * incnt + 10.0D * outcnt));


            int y = cell.getRow();

            int yspan = cell.getVerticalSpan();

            ysolver.add(new Vector(rowcnt).onRange(y, yspan).get(), (int) (nodesize.y + 10.0D * incnt + 10.0D * outcnt));

        }

        this.xConstraints = xsolver.solve();

        logger.i("x-constraints= %s", new Object[]{Arrays.toString(this.xConstraints)});

        this.yConstraints = ysolver.solve();

        logger.i("y-constraints= %s", new Object[]{Arrays.toString(this.yConstraints)});


        int[] xBases = buildCoordsFromSizes(this.virtualOrigin.x, this.xConstraints);

        int[] yBases = buildCoordsFromSizes(this.virtualOrigin.y, this.yConstraints);


        int[] xNodes = new int[cells.size()];

        int[] yNodes = new int[cells.size()];

        int i = 0;

        for (Cell<GraphNode> cell : cells) {

            GraphNode node = (GraphNode) cell.getObject();

            Point psize = node.getSize();

            logger.i("- node size: %s", new Object[]{psize});


            int x0 = xBases[cell.getColumn()];

            int x1 = xBases[cell.getNextColumn()];

            int y0 = yBases[cell.getRow()];

            int y1 = yBases[cell.getNextRow()];


            xNodes[i] = (x0 + (x1 - x0 - psize.x) / 2);


            int in = 2 + Math.max(0, node.getNodeInputEdgeCount() - 2);

            int out = 2 + Math.max(0, node.getNodeOutputEdgeCount() - 2);

            int sliceHeight = (y1 - y0 - psize.y) / (in + out);

            yNodes[i] = (y0 + in * sliceHeight);


            i++;

        }


        i = 0;

        for (Cell<GraphNode> cell : cells) {

            GraphNode node = (GraphNode) cell.getObject();

            registerNode(node, false);

            node.setLocation(xNodes[i], yNodes[i]);

            i++;

        }


        if (this.initialZoomLevel != 0) {

            int count = Math.abs(this.initialZoomLevel);

            while (count-- > 0) {

                internalZoom(this.initialZoomLevel, false, false);

            }

        }

    }


    public void onNodeContentsUpdate(GraphNode node) {

        verifyNode(node);


        node.pack(true);

    }


    public int registerEdge(GraphEdge edge) {

        verifyNode(edge.src);

        verifyNode(edge.dst);


        int id = this.edges.size();

        this.edges.add(edge);

        return id;

    }


    public void bringNodeForward(GraphNode node) {

        verifyNode(node);

        node.moveAbove(null);

    }


    public void bringNodeBackward(GraphNode node) {

        verifyNode(node);

        node.moveBelow(null);

    }


    public void dragNode(GraphNode node, int deltaX, int deltaY) {

        verifyNode(node);


        Point loc = node.getLocation();

        loc.x += deltaX;

        loc.y += deltaY;

        node.setLocation(loc);


        refreshGraph();

    }


    public void resizeNode(GraphNode node, int deltaX, int deltaY) {

        verifyNode(node);


        Point size = node.getSize();

        size.x += deltaX;

        size.y += deltaY;

        node.setSize(size);


        refreshGraph();

    }


    public void setNodeBounds(GraphNode node, Rectangle bounds) {

        verifyNode(node);


        Rectangle bounds0 = node.getBounds();

        if (bounds0.equals(bounds)) {

            return;

        }


        node.setBounds(bounds);

        refreshGraph();

    }


    public Rectangle getNodeBounds(GraphNode node) {

        verifyNode(node);


        return node.getBounds();

    }


    public Rectangle generatePreview(GC gc, Rectangle preview, GraphStyleData styleDataOverride, boolean renderEdges) {

        GraphStyleData styles = styleDataOverride != null ? styleDataOverride : getStyleData();


        Rectangle container = getContainerArea();

        if ((container.width == 0) || (container.height == 0) || (preview.width == 0) || (preview.height == 0)) {

            return null;

        }


        int usedHeight = Math.max(1, preview.width * container.height / container.width);

        int offsetHeight;
        int usedWidth;
        int offsetWidth;
        if (usedHeight > preview.height) {

            usedWidth = Math.max(1, preview.height * container.width / container.height);

            Assert.a(usedWidth <= preview.width);

            offsetWidth = (preview.width - usedWidth) / 2;

            usedHeight = preview.height;

            offsetHeight = 0;

        } else {

            offsetHeight = (preview.height - usedHeight) / 2;

            usedWidth = preview.width;

            offsetWidth = 0;

        }


        double xRatio = usedWidth / container.width;

        double yRatio = usedHeight / container.height;


        Rectangle b = getClientArea();

        int x = (int) ((b.x - container.x) * xRatio) + offsetWidth;

        int y = (int) ((b.y - container.y) * yRatio) + offsetHeight;

        int w = Math.max(3, (int) (b.width * xRatio));

        int h = Math.max(3, (int) (b.height * yRatio));

        gc.setBackground(styles.cCanvas);

        gc.fillRectangle(x, y, w, h);


        List<PreviewNode> pnodes = new ArrayList();

        Map<GraphNode, PreviewNode> pnodemap = new IdentityHashMap();

        for (GraphNode node : this.nodes) {

            b = node.getBounds();

            x = (int) ((b.x - container.x) * xRatio) + offsetWidth;

            y = (int) ((b.y - container.y) * yRatio) + offsetHeight;

            w = Math.max(3, (int) (b.width * xRatio));

            h = Math.max(3, (int) (b.height * yRatio));


            PreviewNode pnode = new PreviewNode();

            pnode.r = new Rectangle(x, y, w, h);

            pnode.active = (getActiveNode() == node);

            pnodes.add(pnode);

            pnodemap.put(node, pnode);

        }


        if (renderEdges) {

            gc.setLineWidth(1);

            gc.setLineStyle(1);

            gc.setForeground(styles.cEdge);

            for (GraphEdge edge : this.edges) {

                PreviewNode src = (PreviewNode) pnodemap.get(edge.src);

                PreviewNode dst = (PreviewNode) pnodemap.get(edge.dst);

                Point p1 = UIUtil.getRectangleCenter(src.r);

                Point p2 = UIUtil.getRectangleCenter(dst.r);

                gc.drawLine(p1.x, p1.y, p2.x, p2.y);

            }

        }


        for (PreviewNode pnode : pnodes) {

            gc.setBackground(pnode.active ? styles.cActiveNode : styles.cNode);

            gc.fillRectangle(pnode.r);

        }


        return new Rectangle(offsetWidth, offsetHeight, usedWidth, usedHeight);

    }


    public Rectangle getContainerArea() {

        int xmin = Integer.MAX_VALUE;

        int ymin = Integer.MAX_VALUE;

        int xmax = Integer.MIN_VALUE;

        int ymax = Integer.MIN_VALUE;

        for (GraphNode n : this.nodes) {

            Rectangle r = n.getBounds();

            int x0 = r.x;

            int y0 = r.y;

            int x1 = r.x + r.width;

            int y1 = r.y + r.height;

            if (x0 < xmin) {

                xmin = x0;

            }

            if (y0 < ymin) {

                ymin = y0;

            }

            if (x1 > xmax) {

                xmax = x1;

            }

            if (y1 > ymax) {

                ymax = y1;

            }

        }

        return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);

    }


    int getOutEdgeCount(GraphNode node) {

        verifyNode(node);


        int cnt = 0;

        for (GraphEdge e : this.edges) {

            if (e.src == node) {

                cnt++;

            }

        }

        return cnt;

    }


    List<GraphNode> getOutNodes(GraphNode node) {

        verifyNode(node);


        List<GraphNode> r = new ArrayList();

        for (GraphEdge e : this.edges) {

            if (e.src == node) {

                r.add(e.dst);

            }

        }

        return r;

    }


    int getInEdgeCount(GraphNode node) {

        verifyNode(node);


        int cnt = 0;

        for (GraphEdge e : this.edges) {

            if (e.dst == node) {

                cnt++;

            }

        }

        return cnt;

    }


    List<GraphNode> getInNodes(GraphNode targetNode) {

        verifyNode(targetNode);


        List<GraphNode> r = new ArrayList();

        for (GraphEdge e : this.edges) {

            if (e.dst == targetNode) {

                r.add(e.src);

            }

        }

        return r;

    }


    private void invalidateCaches() {

        this.horzLineCache.clear();

        this.vertLineCache.clear();


        this.linesByGroup.clear();

        this.activeHoriLineCache.clear();

        this.activeVertLineCache.clear();

    }


    private Map<Integer, ISegmentMap<Integer, IntegerSegment>> horzLineCache = new HashMap();
    private Map<Integer, ISegmentMap<Integer, IntegerSegment>> vertLineCache = new HashMap();


    int requestHorizontalLine(int x0, int x1, int yReq, int adjustStep) {

        Assert.a(adjustStep != 0);

        if (x0 == x1) {

            return yReq;

        }

        if (x0 > x1) {

            int tmp = x1;

            x1 = x0;

            x0 = tmp;

        }

        for (; ; ) {

            ISegmentMap<Integer, IntegerSegment> m = (ISegmentMap) this.horzLineCache.get(Integer.valueOf(yReq));

            if (m == null) {

                m = new SegmentMap();

                this.horzLineCache.put(Integer.valueOf(yReq), m);

                m.add(new IntegerSegment(x0, x1 - x0 + 1));

                return yReq;

            }

            if (m.isEmptyRange(Integer.valueOf(x0), Integer.valueOf(x1 + 1))) {

                m.add(new IntegerSegment(x0, x1 - x0 + 1));

                return yReq;

            }

            yReq += adjustStep;

        }

    }


    void releaseHorizontalLine(int x0, int x1, int y) {

        if (x0 == x1) {

            return;

        }

        ((ISegmentMap) this.horzLineCache.get(Integer.valueOf(y))).remove(Integer.valueOf(Math.min(x0, x1)));

    }


    int requestVerticalLine(int y0, int y1, int xReq, int adjustStep) {

        Assert.a(adjustStep != 0);

        if (y0 == y1) {

            return xReq;

        }

        if (y0 > y1) {

            int tmp = y1;

            y1 = y0;

            y0 = tmp;

        }

        for (; ; ) {

            ISegmentMap<Integer, IntegerSegment> m = (ISegmentMap) this.vertLineCache.get(Integer.valueOf(xReq));

            if (m == null) {

                m = new SegmentMap();

                this.vertLineCache.put(Integer.valueOf(xReq), m);

                m.add(new IntegerSegment(y0, y1 - y0 + 1));

                return xReq;

            }

            if (m.isEmptyRange(Integer.valueOf(y0), Integer.valueOf(y1 + 1))) {

                m.add(new IntegerSegment(y0, y1 - y0 + 1));

                return xReq;

            }

            xReq += adjustStep;

        }

    }


    void releaseVerticalLine(int y0, int y1, int x) {

        if (y0 == y1) {

            return;

        }

        ((ISegmentMap) this.vertLineCache.get(Integer.valueOf(x))).remove(Integer.valueOf(Math.min(y0, y1)));

    }


    boolean optCheckAvoidVerticalLineOverlapWithNodes(int x, int y0, int y1) {

        for (GraphNode node : this.nodes) {

            Rectangle r = node.getBounds();

            if ((r.x < x) && (r.x + r.width > x) &&
                    (y0 < r.y) && (y1 > r.y + r.height)) {

                return true;

            }

        }


        return false;

    }


    int[] optAvoidVerticalLineOverlapWithNodes(int x, int y0, int y1, int margin) {

        List<Rectangle> tba = new ArrayList();

        for (GraphNode node : this.nodes) {

            Rectangle r = node.getBounds();

            if ((r.x < x) && (r.x + r.width > x) &&
                    (y0 < r.y) && (y1 > r.y + r.height)) {

                tba.add(r);

            }

        }


        if (tba.isEmpty()) {

            return null;

        }

        int yE = Integer.MAX_VALUE;

        int xE = Integer.MIN_VALUE;

        int yE1 = Integer.MIN_VALUE;

        for (Rectangle r : tba) {

            if (r.y < yE) {

                yE = r.y;

            }

            if (r.x + r.width > xE) {

                xE = r.x + r.width;

            }

            if (r.y + r.height > yE1) {

                yE1 = r.y + r.height;

            }

        }

        return new int[]{yE - margin, xE + margin, yE1 + margin};

    }


    static class ActiveLine extends IntegerSegment {
        int groupId;


        public ActiveLine(int groupId, int address, int size) {

            super(address, size);

            if (size <= 0) {
                throw new RuntimeException();

            }

            this.groupId = groupId;

        }


        public String toString() {

            return String.format("%d:[%d-%d)", new Object[]{Integer.valueOf(this.groupId), getBegin(), getEnd()});

        }

    }


    private MultiMap<Integer, ActiveLine> linesByGroup = new MultiMap();
    private Map<Integer, IMultiSegmentMap<Integer, ActiveLine>> activeHoriLineCache = new HashMap();
    private Map<Integer, IMultiSegmentMap<Integer, ActiveLine>> activeVertLineCache = new HashMap();


    void registerActiveHorizontalLine(int x0, int x1, int y, int groupId) {

        if (x0 == x1) {

            return;

        }

        IMultiSegmentMap<Integer, ActiveLine> map = (IMultiSegmentMap) this.activeHoriLineCache.get(Integer.valueOf(y));

        if (map == null) {

            map = new MultiSegmentMap();

            this.activeHoriLineCache.put(Integer.valueOf(y), map);

        }

        ActiveLine line = (ActiveLine) map.add(new ActiveLine(groupId, Math.min(x0, x1), Math.abs(x1 - x0)));

        this.linesByGroup.put(Integer.valueOf(groupId), line);

    }


    void registerActiveVerticalLine(int y0, int y1, int x, int groupId) {

        if (y0 == y1) {

            return;

        }

        IMultiSegmentMap<Integer, ActiveLine> map = (IMultiSegmentMap) this.activeVertLineCache.get(Integer.valueOf(x));

        if (map == null) {

            map = new MultiSegmentMap();

            this.activeVertLineCache.put(Integer.valueOf(x), map);

        }

        ActiveLine line = (ActiveLine) map.add(new ActiveLine(groupId, Math.min(y0, y1), Math.abs(y1 - y0)));

        this.linesByGroup.put(Integer.valueOf(groupId), line);

    }


    int getActiveLineGroupId(int x, int y) {

        IMultiSegmentMap<Integer, ActiveLine> map = (IMultiSegmentMap) this.activeHoriLineCache.get(Integer.valueOf(y));

        if (map != null) {

            ActiveLine line = (ActiveLine) map.getFirstSegmentContaining(Integer.valueOf(x));

            if (line != null) {

                return line.groupId;

            }

        }

        map = (IMultiSegmentMap) this.activeVertLineCache.get(Integer.valueOf(x));

        if (map != null) {

            ActiveLine line = (ActiveLine) map.getFirstSegmentContaining(Integer.valueOf(y));

            if (line != null) {

                return line.groupId;

            }

        }

        return -1;

    }


    int getActiveLineGroupId(int x, int y, int toleranceX, int toleranceY) {

        int id = getActiveLineGroupId(x, y);

        if (id >= 0) {

            return id;

        }

        for (int x0 = x - toleranceX; x0 <= x + toleranceX; x0++) {

            for (int y0 = y - toleranceY; y0 <= y + toleranceY; y0++) {

                if ((x0 != x) || (y0 != y)) {


                    id = getActiveLineGroupId(x0, y0);

                    if (id >= 0)
                        return id;

                }

            }

        }

        return -1;

    }


    private List<GraphEdgeListener> graphEdgeListeners = new ArrayList();


    public void addGraphEdgeListener(GraphEdgeListener listener) {

        this.graphEdgeListeners.add(listener);

    }


    public void removeGraphEdgeListener(GraphEdgeListener listener) {

        this.graphEdgeListeners.remove(listener);

    }


    private void notifyEdgeMouseEnter(GraphEdge edge) {

        for (GraphEdgeListener listener : this.graphEdgeListeners) {

            listener.onEdgeMouseEnter(edge);

        }

    }


    private void notifyEdgeMouseExit(GraphEdge edge) {

        for (GraphEdgeListener listener : this.graphEdgeListeners) {

            listener.onEdgeMouseExit(edge);

        }

    }


    private List<GraphNodeListener> graphNodeListeners = new ArrayList();


    public void addGraphNodeListener(GraphNodeListener listener) {

        this.graphNodeListeners.add(listener);

    }


    public void removeGraphNodeListener(GraphNodeListener listener) {

        this.graphNodeListeners.add(listener);

    }


    void notifyNodeMouseEnter(GraphNode node) {

        for (GraphNodeListener listener : this.graphNodeListeners) {

            listener.onNodeMouseEnter(node);

        }

    }


    void notifyNodeMouseExit(GraphNode node) {

        for (GraphNodeListener listener : this.graphNodeListeners) {

            listener.onNodeMouseExit(node);

        }

    }


    void reportNodeFocusChange(GraphNode node, boolean gained) {

        boolean needsRedrawAndNotify = false;

        if (gained) {

            notifyNodeFocusGained(node);

            if (this.activeNode != node) {

                this.activeNode = node;

                this.activeNode.active = true;

                needsRedrawAndNotify = true;

            }

        } else {

            notifyNodeFocusLost(node);

            if (this.activeNode == node) {

                this.activeNode.active = false;

                this.activeNode = null;

                needsRedrawAndNotify = true;

            }

        }


        if (needsRedrawAndNotify) {

            refreshGraph();

            notifyGraphChange();

        }

    }


    void notifyNodeFocusGained(GraphNode node) {

        for (GraphNodeListener listener : this.graphNodeListeners) {

            listener.onNodeFocusGained(node);

        }

    }


    void notifyNodeFocusLost(GraphNode node) {

        for (GraphNodeListener listener : this.graphNodeListeners) {

            listener.onNodeFocusLost(node);

        }

    }


    public String formatDebugInfo() {

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("redrawExectime: %s\n", new Object[]{Long.valueOf(this.lastRedrawExectime)}));

        sb.append(String.format("mouseCursor: %s\n", new Object[]{this.lastMouseCursor}));

        sb.append(String.format("horzLineCache: %s\n", new Object[]{this.horzLineCache}));

        sb.append(String.format("vertLineCache: %s", new Object[]{this.vertLineCache}));

        return sb.toString();

    }


    private static class PreviewNode {
        Rectangle r;
        boolean active;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\Graph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */