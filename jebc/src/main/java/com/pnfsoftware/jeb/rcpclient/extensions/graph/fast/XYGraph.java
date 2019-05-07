
package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;


import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.AbstractGraph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphStyleData;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.ILabelProvider;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class XYGraph
        extends AbstractGraph {
    private static final ILogger logger = GlobalLog.getLogger(XYGraph.class);

    public static final double DEFAULT_ZOOM_LEVEL_IN = 0.8D;

    public static final double DEFAULT_ZOOM_LEVEL_OUT = 1.2D;

    private static final long DEFAULT_ACTIVE_VERTEX_ANIMATION_PERIOD_MS = 600L;

    private static final long DEFAULT_DRAG_ANIMATION_PERIOD_MS = 50L;

    private List<IGraphVertexListener> vertexListeners = new ArrayList();
    private List<IGraphBoundsListener> boundsListeners = new ArrayList();

    private Thread aniThread;

    private boolean directed;

    private Map<Integer, P> pointmap = new HashMap();
    private List<L> lines = new ArrayList();
    private P selectedVertex;
    private P hoveredVertex;
    private Collection<P> visiblePoints = new HashSet();
    private Collection<L> visibleLines = new HashSet();

    private Map<Integer, Point> pointsCoordMap;

    protected Set<P> activePoints = new HashSet();
    protected Set<Integer> activeVertices = new HashSet();
    protected Set<L> activeLines = new HashSet();
    private volatile int activeState;
    private volatile boolean activeNodeAnimationEnabled = true;

    private ILabelProvider labelProvider;

    private Rectangle clientArea = new Rectangle(0, 0, 0, 0);
    private double gx;
    private double gy;
    private double gw;
    private double gh;
    private P mouseCoord = new P();

    private boolean dragging;
    private Point dragPoint = new Point(0, 0);
    private Point mouseDownPoint = new Point(0, 0);
    private Point mousePoint = new Point(0, 0);

    private boolean trackVertexHovering = true;


    public XYGraph(Composite parent) {

        super(parent, 536870912);

        setLayout(null);


        this.clientArea = getClientArea();


        UI.initialize();


        addControlListener(new ControlAdapter() {

            public void controlResized(ControlEvent e) {

                XYGraph.this.clientArea = XYGraph.this.getClientArea();

                XYGraph.this.adjustGraphBounds();

            }


        });

        addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {

                GC gc = e.gc;

                try {

                    gc.setAntialias(1);

                } catch (SWTException localSWTException) {
                }


                Map<Integer, Point> m = XYGraph.this.getVertexViewportCoordinates();


                XYGraph.this.visiblePoints = XYGraph.this.determineVisibleVertices(XYGraph.this.pointmap.values());

                XYGraph.this.visibleLines = XYGraph.this.determineVisibleEdges(XYGraph.this.visiblePoints, XYGraph.this.lines);


                XYGraph.this.preDrawing(gc);


                XYGraph.this.preEdgesDrawing(gc);

                for (L line : XYGraph.this.visibleLines) {

                    Point a = (Point) m.get(Integer.valueOf(line.getSrcId()));

                    Point b = (Point) m.get(Integer.valueOf(line.getDstId()));

                    XYGraph.this.drawEdge(gc, line, a, b);

                }

                XYGraph.this.postEdgesDrawing(gc);


                XYGraph.this.preVerticesDrawing(gc);

                for (P p : XYGraph.this.visiblePoints) {

                    Point pt = (Point) m.get(Integer.valueOf(p.getId()));

                    XYGraph.this.drawVertex(gc, p, pt);

                }

                XYGraph.this.postVerticesDrawing(gc);


                XYGraph.this.preVertexLabelsDrawing(gc);

                for (P p : XYGraph.this.visiblePoints) {

                    Point pt = (Point) m.get(Integer.valueOf(p.getId()));

                    XYGraph.this.drawVertexLabel(gc, p, pt);

                }

                XYGraph.this.postVertexLabelsDrawing(gc);


                XYGraph.this.postDrawing(gc);

            }


        });

        addMouseListener(new MouseAdapter() {

            public void mouseDoubleClick(MouseEvent e) {

                if (e.button != 1) {

                    return;

                }


                if (XYGraph.this.hoveredVertex != null) {

                    XYGraph.this.notifyVertexDoubleClicked(XYGraph.this.hoveredVertex);

                }

            }


            public void mouseDown(MouseEvent e) {

                if (e.button != 1) {

                    return;

                }


                XYGraph.this.mouseDownPoint.x = e.x;

                XYGraph.this.mouseDownPoint.y = e.y;


                XYGraph.this.dragging = true;

                XYGraph.this.dragPoint.x = e.x;

                XYGraph.this.dragPoint.y = e.y;

            }


            public void mouseUp(MouseEvent e) {

                if (e.button != 1) {

                    return;

                }


                if ((XYGraph.this.mouseDownPoint != null) && (new Point(e.x, e.y).equals(XYGraph.this.mouseDownPoint))) {

                    if (XYGraph.this.hoveredVertex != null) {

                        XYGraph.this.selectedVertex = XYGraph.this.hoveredVertex;

                        XYGraph.this.refreshGraph();

                        XYGraph.this.notifyVertexClicked(XYGraph.this.selectedVertex);

                    }

                }

                XYGraph.this.dragging = false;

            }

        });

        addMouseMoveListener(new MouseMoveListener() {

            public void mouseMove(MouseEvent e) {

                XYGraph.this.mousePoint.x = e.x;

                XYGraph.this.mousePoint.y = e.y;

                XYGraph.this.mouseCoord = XYGraph.this.convertCoord(XYGraph.this.mousePoint);


                if (XYGraph.this.dragging) {

                    int deltaX = e.x - XYGraph.this.dragPoint.x;

                    int deltaY = e.y - XYGraph.this.dragPoint.y;

                    XYGraph.this.dragPoint.x = e.x;

                    XYGraph.this.dragPoint.y = e.y;

                    XYGraph.this.dragGraph(deltaX, deltaY);


                } else if (XYGraph.this.trackVertexHovering) {

                    P closestVertex = XYGraph.this.findClosestVertex(XYGraph.this.mouseCoord, XYGraph.this.visiblePoints);

                    if (closestVertex != null) {

                        double dist = closestVertex.dist(XYGraph.this.mouseCoord);

                        double distPx = dist * XYGraph.this.clientArea.width / XYGraph.this.gw;

                        if (distPx > 20.0D) {

                            closestVertex = null;

                        }

                    }


                    if ((XYGraph.this.hoveredVertex != null) && (closestVertex != XYGraph.this.hoveredVertex)) {

                        XYGraph.this.notifyVertexHoverOut(XYGraph.this.hoveredVertex);

                    }

                    XYGraph.this.hoveredVertex = null;

                    if (closestVertex != null) {

                        XYGraph.this.hoveredVertex = closestVertex;

                        XYGraph.this.notifyVertexHoverIn(XYGraph.this.hoveredVertex);

                    }

                    XYGraph.this.redraw();

                }


            }

        });

        this.aniThread = ThreadUtil.start(new Runnable() {

            public void run() {

                for (; ; ) {

                    if (!XYGraph.this.isDisposed()) {

                        try {

                            Thread.sleep(600L);

                        } catch (InterruptedException e) {

                            return;

                        }

                        if ((XYGraph.this.activeNodeAnimationEnabled) && (


                                (!XYGraph.this.activePoints.isEmpty()) || (!XYGraph.this.activeLines.isEmpty()))) {

                            try {


                                if (!XYGraph.this.getDisplay().isDisposed()) {


                                    XYGraph.this.getDisplay().syncExec(new Runnable() {

                                        public void run() {

                                            if (!XYGraph.this.isDisposed()) {

                                                XYGraph.this.redraw();

                                                XYGraph.this.activeState = (1 - XYGraph.this.activeState);

                                            }

                                        }

                                    });

                                }

                            } catch (SWTException e) {
                            }

                        }

                    }

                }

            }

        });

    }


    public void dispose() {

        if (this.aniThread != null) {

            this.aniThread.interrupt();

            this.aniThread = null;

        }

        super.dispose();

    }


    public void reset() {

        this.pointmap.clear();

        this.lines.clear();


        this.selectedVertex = null;

        this.hoveredVertex = null;

        this.visiblePoints.clear();

        this.visibleLines.clear();


        this.pointsCoordMap = null;


        this.activePoints.clear();

        this.activeVertices.clear();

        this.activeLines.clear();


        this.labelProvider = null;

    }


    public int getVertexCount() {

        return this.pointmap.size();

    }


    public Collection<P> getPoints() {

        return this.pointmap.values();

    }


    public Collection<P> getVisiblePoints() {

        return this.visiblePoints;

    }


    public boolean isVertexVisible(int vertexId) {

        for (P p : this.visiblePoints) {

            if (p.id.intValue() == vertexId) {

                return true;

            }

        }

        return false;

    }


    public int getEdgeCount() {

        return this.lines.size();

    }


    public Collection<L> getLines() {

        return this.lines;

    }


    public Collection<L> getVisibleLines() {

        return this.visibleLines;

    }


    public Rectangle getContainerArea() {

        int xmin = Integer.MAX_VALUE;

        int ymin = Integer.MAX_VALUE;

        int xmax = Integer.MIN_VALUE;

        int ymax = Integer.MIN_VALUE;

        Map<Integer, Point> m = getVertexViewportCoordinates();

        for (Point pt : m.values()) {

            if (pt.x < xmin) {

                xmin = pt.x;

            }

            if (pt.x > xmax) {

                xmax = pt.x;

            }

            if (pt.y < ymin) {

                ymin = pt.y;

            }

            if (pt.y > ymax) {

                ymax = pt.y;

            }

        }

        return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);

    }


    public P getSelectedPoint() {

        return this.selectedVertex;

    }


    public void setSelectedPoint(P p) {

        this.selectedVertex = p;

    }


    public Integer getSelection() {

        return this.selectedVertex == null ? null : this.selectedVertex.id;

    }


    public void setSelection(Integer vertexId) {

        this.selectedVertex = (vertexId == null ? null : (P) this.pointmap.get(vertexId));

    }


    public void setTrackVertexHovering(boolean trackVertexHovering) {

        this.trackVertexHovering = trackVertexHovering;

    }


    public boolean isTrackVertexHovering() {

        return this.trackVertexHovering;

    }


    public P getHoveredPoint() {

        return this.hoveredVertex;

    }


    public void setActiveNodeAnimationEnabled(boolean activeNodeAnimationEnabled) {

        this.activeNodeAnimationEnabled = activeNodeAnimationEnabled;

    }


    public boolean isActiveNodeAnimationEnabled() {

        return this.activeNodeAnimationEnabled;

    }


    public Collection<P> getActivePoints() {

        return this.activePoints;

    }


    public boolean getActivePointsStatus() {

        return this.activeState == 1;

    }


    public boolean isActivePoint(P p) {

        return this.activePoints.contains(p);

    }


    public boolean isActiveVertex(int id) {

        return this.activeVertices.contains(Integer.valueOf(id));

    }


    public void addActivePoint(P p) {

        this.activePoints.add(p);

        if (this.activeVertices.add(p.id)) {

            notifyGraphChange();

        }

    }


    public void removeActivePoint(P p) {

        this.activePoints.remove(p);

        if (this.activeVertices.remove(p.id)) {

            notifyGraphChange();

        }

    }


    public boolean isDragging() {

        return this.dragging;

    }


    public void dragGraph(int deltaX, int deltaY) {

        dragGraph(deltaX, deltaY, false);

    }


    public void dragGraph(int deltaX, int deltaY, boolean progressive) {

        Rectangle r = this.clientArea;

        double gx1 = this.gx - this.gw * deltaX / r.width;

        double gy1 = this.gy + this.gh * deltaY / r.height;


        if (progressive) {

            int frameCount = 10;

            int incrX = deltaX / 10;

            int incrY = deltaY / 10;


            if ((incrX != 0) || (incrY != 0)) {

                for (int i = 0; i < 9; i++) {

                    this.gx -= this.gw * incrX / r.width;

                    this.gy += this.gh * incrY / r.height;

                    scroll(incrX, incrY, 0, 0, r.width, r.height, true);

                    refreshGraph();


                    try {

                        Thread.sleep(50L);

                    } catch (InterruptedException e) {

                        break;

                    }

                }

                deltaX -= 10 * incrX;

                deltaY -= 10 * incrY;

            }

        }


        this.gx = gx1;

        this.gy = gy1;


        notifyBoundsChange();


        scroll(deltaX, deltaY, 0, 0, r.width, r.height, true);

        refreshGraph();

    }


    public void refreshGraph() {

        this.pointsCoordMap = null;

        redraw();

        update();

    }


    public void zoomGraph(int zoom) {

        zoomGraph(zoom, null);

    }


    public void zoomGraph(int zoom, Point centerPoint) {

        double ratio = 1.0D;

        if (zoom > 0) {

            ratio = 0.8D;

        } else if (zoom < 0) {

            ratio = 1.2D;

        }

        applyZoom(ratio, centerPoint == null ? null : convertCoord(centerPoint));

    }


    void applyZoom(double ratio, P centerPoint) {

        Assert.a(ratio > 0.0D);

        if (ratio == 1.0D) {

            return;

        }


        if (centerPoint == null) {

            centerPoint = new P(this.gx + this.gw / 2.0D, this.gy + this.gh / 2.0D);

        }

        double rx = (centerPoint.x - this.gx) / this.gw;

        double ry = (centerPoint.y - this.gy) / this.gh;

        this.gw = (ratio * this.gw);

        this.gh = (ratio * this.gh);

        this.gx = (centerPoint.x - rx * this.gw);

        this.gy = (centerPoint.y - ry * this.gh);


        notifyBoundsChange();


        redraw();

        update();

    }


    public void setParameters(Collection<P> allpoints, Collection<L> alllines, boolean directedGraph) {

        if (allpoints == null) {

            throw new IllegalArgumentException();

        }

        if (alllines == null) {

            throw new IllegalArgumentException();

        }


        this.pointmap.clear();

        for (P p : allpoints) {

            this.pointmap.put(Integer.valueOf(p.getId()), p);

        }


        this.lines.clear();

        this.lines.addAll(alllines);


        this.directed = directedGraph;


        this.hoveredVertex = null;

        this.visiblePoints = null;

        this.visibleLines = null;


        fitGraph(0.05D, false);

        notifyBoundsChange();

    }


    public void reportParametersUpdate() {

        this.pointsCoordMap = null;

        redraw();

    }


    public void centerGraph() {

        fitGraph(0.1D, true);

    }


    public void centerGraph(int vertexId) {

        P p = (P) this.pointmap.get(Integer.valueOf(vertexId));

        Assert.a(p != null, "Vertex id " + vertexId + " does not exist");

        setGraphLocation(p.x - this.gw / 2.0D, p.y - this.gh / 2.0D);

    }


    public void positionGraph(double xRatio, double yRatio) {

        Rectangle container = getContainerArea();

        int x0 = (int) (container.x + xRatio * container.width);

        int y0 = (int) (container.y + yRatio * container.height);


        int deltaX = this.clientArea.width / 2 - x0;

        int deltaY = this.clientArea.height / 2 - y0;

        dragGraph(deltaX, deltaY);

    }


    public void centerGraph(int vertexId, int clientAnchorFlags, boolean progressive) {

        Point pt = (Point) getVertexViewportCoordinates().get(Integer.valueOf(vertexId));


        Rectangle client = this.clientArea;


        int x0 = pt.x;

        int y0 = pt.y;


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


    public void fitGraph() {

        fitGraph(0.05D, true);

    }


    private void fitGraph(double marginRatio, boolean redraw) {

        double x = Double.MAX_VALUE;

        double xmax = -1.7976931348623157E308D;

        double y = Double.MAX_VALUE;

        double ymax = -1.7976931348623157E308D;

        for (P p : this.pointmap.values()) {

            if (p.getX() < x) {

                x = p.getX();

            }

            if (p.getX() > xmax) {

                xmax = p.getX();

            }

            if (p.getY() < y) {

                y = p.getY();

            }

            if (p.getY() > ymax) {

                ymax = p.getY();

            }

        }

        double w = xmax - x;

        double h = ymax - y;

        double dist = Math.sqrt(Math.pow(w, 2.0D) + Math.pow(h, 2.0D));

        double margin = dist * marginRatio;

        x -= margin;

        y -= margin;

        w += 2.0D * margin;

        h += 2.0D * margin;

        setGraphBounds(x, y, w, h, redraw);

    }


    private boolean setGraphBounds(R bounds, boolean redraw) {

        return setGraphBounds(bounds.x, bounds.y, bounds.w, bounds.h, redraw);

    }


    private boolean setGraphBounds(double x, double y, double w, double h, boolean redrawOnChange) {

        boolean changed = false;

        if (this.gx != x) {

            this.gx = x;

            changed = true;

        }

        if (this.gy != y) {

            this.gy = y;

            changed = true;

        }

        if (this.gw != w) {

            this.gw = w;

            changed = true;

        }

        if (this.gh != h) {

            this.gh = h;

            changed = true;

        }


        if (changed) {

            if (!adjustGraphBounds()) {

                notifyBoundsChange();

            }

            if (redrawOnChange) {

                redraw();

            }

        }

        return changed;

    }


    public boolean setGraphBounds(R bounds) {

        return setGraphBounds(bounds, true);

    }


    public R getGraphBounds() {

        return new R(this.gx, this.gy, this.gw, this.gh);

    }


    public void setGraphLocation(double x, double y) {

        setGraphBounds(new R(x, y, this.gw, this.gh));

    }


    public void setGraphSize(double w, double h) {

        setGraphBounds(new R(this.gx, this.gy, w, h));

    }


    private boolean adjustGraphBounds() {

        boolean changed = false;

        Rectangle r = this.clientArea;

        double viewport_ratio = r.width / r.height;

        double graph_ratio = this.gw / this.gh;

        if (graph_ratio > viewport_ratio) {

            double updated = this.gw * r.height / r.width;

            this.gy -= (updated - this.gh) / 2.0D;

            this.gh = updated;

            changed = true;

        } else if (graph_ratio < viewport_ratio) {

            double updated = this.gh * r.width / r.height;

            this.gx -= (updated - this.gw) / 2.0D;

            this.gw = updated;

            changed = true;

        }

        if (changed) {

            notifyBoundsChange();

        }

        return changed;

    }


    public Point convertCoord(P p) {

        Rectangle r = this.clientArea;

        int x = (int) ((p.getX() - this.gx) / this.gw * r.width);

        int y = r.height - (int) ((p.getY() - this.gy) / this.gh * r.height);

        return new Point(x, y);

    }


    public P convertCoord(Point p) {

        Rectangle r = this.clientArea;

        double x = p.x / r.width * this.gw + this.gx;

        double y = (p.y - r.height) / -r.height * this.gh + this.gy;

        return new P(x, y);

    }


    public Map<Integer, Point> getVertexViewportCoordinates() {

        if (this.pointsCoordMap == null) {

            this.pointsCoordMap = new HashMap();

            for (P p : this.pointmap.values()) {

                Point pc = convertCoord(p);

                this.pointsCoordMap.put(Integer.valueOf(p.getId()), pc);

            }

        }

        return this.pointsCoordMap;

    }


    public void setLabelProvider(ILabelProvider labelProvider) {

        this.labelProvider = labelProvider;

    }


    public ILabelProvider getLabelProvider() {

        return this.labelProvider;

    }


    public String generateLabelForVertex(P p) {

        if (this.labelProvider != null) {

            String s = this.labelProvider.getLabel(p.getId());

            if (!Strings.isBlank(s)) {

                return s;

            }

        }

        return "" + p.getId();

    }


    protected void preDrawing(GC gc) {
    }


    protected void postDrawing(GC gc) {
    }


    protected Collection<P> determineVisibleVertices(Collection<P> points) {

        return points;

    }


    protected Collection<L> determineVisibleEdges(Collection<P> visiblePoints, Collection<L> edges) {

        return edges;

    }


    protected void preEdgesDrawing(GC gc) {

        gc.setForeground(getDisplay().getSystemColor(15));

    }


    protected void drawEdge(GC gc, L l, Point a, Point b) {

        gc.drawLine(a.x, a.y, b.x, b.y);

    }


    protected void postEdgesDrawing(GC gc) {
    }


    protected void preVerticesDrawing(GC gc) {

        gc.setBackground(getDisplay().getSystemColor(2));

    }


    protected void drawVertex(GC gc, P p, Point pt) {

        int pr = 6;

        gc.fillOval(pt.x - 3, pt.y - 3, 6, 6);

    }


    protected void postVerticesDrawing(GC gc) {
    }


    protected void preVertexLabelsDrawing(GC gc) {

        gc.setForeground(getDisplay().getSystemColor(4));

    }


    protected void drawVertexLabel(GC gc, P p, Point pt) {

        gc.drawText(generateLabelForVertex(p), pt.x, pt.y, true);

    }


    protected void postVertexLabelsDrawing(GC gc) {
    }


    public P findClosestVertex(P p0, Collection<P> candidates) {

        if (candidates == null) {

            if (this.pointmap == null) {

                return null;

            }

            candidates = this.pointmap.values();

        }

        P closestVertex = null;

        double min = Double.MAX_VALUE;

        for (P p : candidates) {

            double dx = Math.abs(p.getX() - p0.getX());

            if (dx < min) {


                double dy = Math.abs(p.getY() - p0.getY());

                if (dy < min) {


                    double dx2 = dx * dx;

                    if (dx2 < min) {


                        double dy2 = dy * dy;

                        if (dy2 < min) {


                            double sum2 = dx2 + dy2;

                            if (sum2 < min) {


                                min = sum2;

                                closestVertex = p;

                            }
                        }
                    }
                }
            }
        }

        return closestVertex;

    }


    public void addGraphVertexListener(IGraphVertexListener listener) {

        this.vertexListeners.add(listener);

    }


    public void removeGraphVertexListener(IGraphVertexListener listener) {

        this.vertexListeners.remove(listener);

    }


    private void notifyVertexHoverIn(P p) {

        for (IGraphVertexListener listener : this.vertexListeners) {

            listener.onVertexHoverIn(this, p);

        }

    }


    private void notifyVertexHoverOut(P p) {

        for (IGraphVertexListener listener : this.vertexListeners) {

            listener.onVertexHoverOut(this, p);

        }

    }


    private void notifyVertexClicked(P p) {

        for (IGraphVertexListener listener : this.vertexListeners) {

            listener.onVertexClicked(this, p);

        }

    }


    private void notifyVertexDoubleClicked(P p) {

        for (IGraphVertexListener listener : this.vertexListeners) {

            listener.onVertexDoubleClicked(this, p);

        }

    }


    public void addGraphBoundsListener(IGraphBoundsListener listener) {

        this.boundsListeners.add(listener);

    }


    public void removeGraphBoundsListener(IGraphBoundsListener listener) {

        this.boundsListeners.remove(listener);

    }


    private void notifyBoundsChange() {

        this.pointsCoordMap = null;

        for (IGraphBoundsListener listener : this.boundsListeners) {

            listener.onBoundsUpdate(this, new R(this.gx, this.gy, this.gw, this.gh));

        }


        notifyGraphChange();

    }


    public Rectangle generatePreview(GC gc, Rectangle preview, GraphStyleData styleDataOverride, boolean renderEdges) {

        Rectangle container = getContainerArea();

        if ((container.width == 0) || (container.height == 0) || (preview.width == 0) || (preview.height == 0)) {

            return null;

        }


        GraphStyleData styles = styleDataOverride != null ? styleDataOverride : GraphStyleData.buildDefault();


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


        Rectangle b = this.clientArea;

        int x = (int) ((b.x - container.x) * xRatio) + offsetWidth;

        int y = (int) ((b.y - container.y) * yRatio) + offsetHeight;

        int w = Math.max(3, (int) (b.width * xRatio));

        int h = Math.max(3, (int) (b.height * yRatio));

        gc.setBackground(styles.cCanvas);

        gc.fillRectangle(x, y, w, h);


        List<Point> activePts = new ArrayList();

        for (Map.Entry<Integer, Point> entry : getVertexViewportCoordinates().entrySet()) {

            int id = ((Integer) entry.getKey()).intValue();

            Point pt = (Point) entry.getValue();

            if (isActiveVertex(id)) {

                activePts.add(pt);

            } else {

                x = (int) ((pt.x - container.x) * xRatio) + offsetWidth;

                y = (int) ((pt.y - container.y) * yRatio) + offsetHeight;

                gc.setBackground(styles.cNode);

                gc.fillOval(x, y, 3, 3);

            }
        }

        for (Point pt : activePts) {

            x = (int) ((pt.x - container.x) * xRatio) + offsetWidth;

            y = (int) ((pt.y - container.y) * yRatio) + offsetHeight;

            gc.setBackground(styles.cActiveNode);

            gc.fillOval(x, y, 6, 6);

        }


        return new Rectangle(offsetWidth, offsetHeight, usedWidth, usedHeight);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\fast\XYGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */