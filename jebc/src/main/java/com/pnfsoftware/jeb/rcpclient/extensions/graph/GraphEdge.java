
package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.IZoomable;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class GraphEdge
        implements IZoomable {
    private static final ILogger logger = GlobalLog.getLogger(GraphEdge.class);
    protected int edgeId;
    protected Graph graph;
    protected GraphNode src;
    protected GraphNode dst;
    protected Anchor srcAnchor = Anchor.AUTO;
    protected Anchor dstAnchor = Anchor.AUTO;
    protected int style = 1;
    protected int thickness = 2;
    protected Orientation orientation = Orientation.NONE;
    protected Map<Integer, Color> colors = new HashMap();
    protected int state = 0;

    public GraphEdge(Graph graph, GraphNode src, GraphNode dst) {
        this.graph = graph;
        this.src = src;
        this.dst = dst;
        this.edgeId = graph.registerEdge(this);
        this.colors.put(Integer.valueOf(0), SwtRegistry.getInstance().getColor(4210752));
        this.colors.put(Integer.valueOf(1), SwtRegistry.getInstance().getColor(16750848));
    }

    public Graph getGraph() {
        return this.graph;
    }

    public GraphNode getSource() {
        return this.src;
    }

    public GraphNode getDestination() {
        return this.dst;
    }

    public void setAnchors(Anchor srcAnchor, Anchor dstAnchor) {
        this.srcAnchor = srcAnchor;
        this.dstAnchor = dstAnchor;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public void setColor(int state, Color color) {
        this.colors.put(Integer.valueOf(state), color);
    }

    public Color getColor(int state) {
        return (Color) this.colors.get(Integer.valueOf(state));
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public int getZoomLevel() {
        return this.graph.getZoomLevel();
    }

    public boolean applyZoom(int zoom, boolean dryRun) {
        return false;
    }

    protected void draw(GC gc) {
        Anchor _srcAnchor;
        Anchor _dstAnchor;
        if ((this.srcAnchor != Anchor.AUTO) && (this.dstAnchor != Anchor.AUTO)) {
            _srcAnchor = this.srcAnchor;
            _dstAnchor = this.dstAnchor;
        } else {
            if ((this.srcAnchor == Anchor.AUTO) && (this.dstAnchor != Anchor.AUTO)) {
                _srcAnchor = this.dstAnchor;
                _dstAnchor = this.dstAnchor;
            } else {
                if ((this.srcAnchor != Anchor.AUTO) && (this.dstAnchor == Anchor.AUTO)) {
                    _srcAnchor = this.dstAnchor;
                    _dstAnchor = this.srcAnchor;
                } else {
                    Anchor[] a = determineBestAnchors();
                    _srcAnchor = a[0];
                    _dstAnchor = a[1];
                }
            }
        }
        Point pa = determineSimpleAnchorCoords(this.src, _srcAnchor);
        Point pb = determineSimpleAnchorCoords(this.dst, _dstAnchor);
        drawLine(gc, pb.x, pb.y, pa.x, pa.y);
    }

    protected void drawLine(GC gc, int x0, int y0, int x1, int y1) {
        drawLine(gc, x0, y0, x1, y1, this.edgeId);
    }

    protected void drawLine(GC gc, int x0, int y0, int x1, int y1, int groupId) {
        setForeground(gc);
        int th = Math.max(1, this.thickness);
        gc.setLineWidth(th);
        gc.setLineStyle(this.style);
        gc.drawLine(x0, y0, x1, y1);
        if ((th >= 2) && (this.style == 1)) {
            gc.setLineWidth(1);
            gc.setForeground(Display.getCurrent().getSystemColor(16));
            gc.drawLine(x0, y0, x1, y1);
        }
        if (groupId >= 0) {
            if (x0 == x1) {
                this.graph.registerActiveVerticalLine(y0, y1, x0, groupId);
            } else if (y0 == y1) {
                this.graph.registerActiveHorizontalLine(x0, x1, y0, groupId);
            }
        }
    }

    protected void drawArrow(GC gc, int x0, int y0, int x1, int y1) {
        if ((x0 == x1) && (y0 == y1)) {
            throw new IllegalArgumentException("A point was provided; need a line for orientation");
        }
        if (this.orientation == Orientation.NONE) {
            return;
        }
        if ((x0 != x1) && (y0 != y1)) {
            throw new RuntimeException("Non straight arrows are TBI until a use-case shows up");
        }
        setBackground(gc);
        if ((this.orientation == Orientation.ORIENTED) || (this.orientation == Orientation.ORIENTED_DUAL)) {
            int x = x1;
            int y = y1;
            if (x0 == x1) {
                int deltaX = 3;
                int deltaY = 6;
                if (y0 < y1) {
                    gc.fillPolygon(new int[]{x, y, x - 3, y - 6, x + 3, y - 6});
                } else if (y0 > y1) {
                    gc.fillPolygon(new int[]{x, y, x - 3, y + 6, x + 3, y + 6});
                }
            } else if (y0 == y1) {
                int deltaX = 6;
                int deltaY = 3;
                if (x0 < x1) {
                    gc.fillPolygon(new int[]{x, y, x - 6, y - 3, x - 6, y + 3});
                } else if (x0 > x1) {
                    gc.fillPolygon(new int[]{x, y, x + 6, y - 3, x + 6, y + 3});
                }
            }
        } else if ((this.orientation == Orientation.ORIENTED_BACKWARD) || (this.orientation == Orientation.ORIENTED_DUAL)) {
            throw new RuntimeException("Dual orientation is TBI");
        }
    }

    private Color determineEdgeColor() {
        return (Color) this.colors.get(Integer.valueOf(this.state));
    }

    private void setForeground(GC gc) {
        Color color = determineEdgeColor();
        if (color != null) {
            gc.setForeground(color);
        }
    }

    private void setBackground(GC gc) {
        Color color = determineEdgeColor();
        if (color != null) {
            gc.setBackground(color);
        }
    }

    private Point determineSimpleAnchorCoords(GraphNode node, Anchor anchor) {
        Rectangle r = node.getBounds();
        int y;
        int x;
        switch (anchor) {
            case CENTER:
                x = r.x + r.width / 2;
                y = r.y + r.height / 2;
                break;
            case TOP:
                x = r.x + r.width / 2;
                y = r.y;
                break;
            case BOTTOM:
                x = r.x + r.width / 2;
                y = r.y + r.height;
                break;
            case LEFT:
                x = r.x;
                y = r.y + r.height / 2;
                break;
            case RIGHT:
                x = r.x + r.width;
                y = r.y + r.height / 2;
                break;
            default:
                x = r.x;
                y = r.y;
        }
        return new Point(x, y);
    }

    private Anchor[] determineBestAnchors() {
        Rectangle r0 = this.src.getBounds();
        Rectangle r1 = this.dst.getBounds();
        if (r1.x + r1.width < r0.x) {
            int dY = r0.y - (r1.y + r1.height);
            int dX = r0.x - (r1.x + r1.width);
            int dZ = r1.y - (r0.y + r0.height);
            if ((dX >= dY) && (dX >= dZ)) {
                return new Anchor[]{Anchor.LEFT, Anchor.RIGHT};
            }
            if (dY > dZ) {
                return new Anchor[]{Anchor.TOP, Anchor.BOTTOM};
            }
            return new Anchor[]{Anchor.BOTTOM, Anchor.TOP};
        }
        if (r1.x > r0.x + r0.width) {
            int dY = r0.y - (r1.y + r1.height);
            int dX = r1.x - (r0.x + r0.width);
            int dZ = r1.y - (r0.y + r0.height);
            if ((dX >= dY) && (dX >= dZ)) {
                return new Anchor[]{Anchor.RIGHT, Anchor.LEFT};
            }
            if (dY > dZ) {
                return new Anchor[]{Anchor.TOP, Anchor.BOTTOM};
            }
            return new Anchor[]{Anchor.BOTTOM, Anchor.TOP};
        }
        if (r1.y + r1.height < r0.y) {
            return new Anchor[]{Anchor.TOP, Anchor.BOTTOM};
        }
        if (r1.y > r0.y + r0.height) {
            return new Anchor[]{Anchor.BOTTOM, Anchor.TOP};
        }
        return new Anchor[]{Anchor.CENTER, Anchor.CENTER};
    }

    public String toString() {
        return String.format("Edge{%s->%s}", new Object[]{this.src, this.dst});
    }
}


