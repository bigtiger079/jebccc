
package com.pnfsoftware.jeb.rcpclient.extensions.graph;


import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;


public class GraphEdgeSquareManager
        extends GraphEdgeManager {
    private static final ILogger logger = GlobalLog.getLogger(GraphEdgeSquareManager.class);


    private int SPACING_MIN = 20;
    private int MARGIN_MIN = 10;

    private int EDGE_SEP = 8;

    private boolean minimizeBlockCrossover = true;
    private boolean preferStraightRouting = true;


    public GraphEdgeSquareManager(Graph graph) {

        super(graph);

    }


    public GraphEdgeSquare create(GraphNode src, GraphNode dst) {

        GraphEdgeSquare edge = new GraphEdgeSquare(this.graph, src, dst);


        return edge;

    }


    public void draw(GC gc, int redrawCause, Object redrawObject) {

        determineAnchorPoints();


        for (GraphEdge edge : this.graph.getEdges()) {

            drawEdge((GraphEdgeSquare) edge, gc);

        }

    }


    private void determineAnchorPoints() {

        int xdeltasep = this.graph.virtualOrigin.x % this.EDGE_SEP;


        for (GraphNode src : this.graph.getNodes()) {

            List<GraphNode> outnodes = this.graph.getOutNodes(src);

            if (!outnodes.isEmpty()) {


                orderControls(outnodes, OrderType.CENTER_X);


                int outcnt = outnodes.size();

                Rectangle r0 = src.getBounds();

                int xstart = r0.x + (r0.width - (outcnt - 1) * this.EDGE_SEP) / 2;

                int r = (xstart - xdeltasep) % this.EDGE_SEP;

                if (r != 0) {

                    xstart += this.EDGE_SEP - r;

                }


                int[] xlist = new int[outcnt];

                int i = 0;
                for (int x = xstart; i < outnodes.size(); x += this.EDGE_SEP) {

                    xlist[i] = x;
                    i++;

                }

                src.setData("outedges_ordered_nodes", outnodes);

                src.setData("outedges_ordered_x", xlist);

            }

        }

        for (GraphNode dst : this.graph.getNodes()) {

            List<GraphNode> innodes = this.graph.getInNodes(dst);

            if (!innodes.isEmpty()) {


                orderControls(innodes, OrderType.CENTER_X);


                int incnt = innodes.size();

                Rectangle r1 = dst.getBounds();

                int xstart = r1.x + (r1.width - (incnt - 1) * this.EDGE_SEP) / 2;

                int r = (xstart - xdeltasep) % this.EDGE_SEP;

                if (r != 0) {

                    xstart += this.EDGE_SEP - r;

                }


                int[] xlist = new int[incnt];

                int i = 0;
                for (int x = xstart; i < innodes.size(); x += this.EDGE_SEP) {

                    xlist[i] = x;
                    i++;

                }

                dst.setData("inedges_ordered_nodes", innodes);

                dst.setData("inedges_ordered_x", xlist);

            }

        }


        for (GraphNode src : this.graph.getNodes()) {

            List<GraphNode> outnodes = (List) src.getData("outedges_ordered_nodes");

            if ((outnodes != null) && (outnodes.size() == 2)) {


                GraphNode dst0 = (GraphNode) outnodes.get(0);

                GraphNode dst1 = (GraphNode) outnodes.get(1);


                Rectangle a = src.getBounds();

                int a_x0 = a.x;

                int a_y0 = a.y;

                int a_x1 = a.x + a.width;

                int a_y1 = a.y + a.height;

                int a_xcenter = a.x + a.width / 2;


                int[] dirs = new int[2];

                int i = 0;

                for (GraphNode dst : outnodes) {

                    Rectangle b = dst.getBounds();

                    int b_x0 = b.x;

                    int b_y0 = b.y;

                    int b_x1 = b.x + b.width;

                    int b_y1 = b.y + b.height;

                    int b_xcenter = b.x + b.width / 2;


                    boolean xOverlap = ((b_x0 <= a.x) && (b_x1 > a.x)) || ((b_x0 <= a_x1) && (b_x1 > a_x1)) || ((b_x0 >= a.x) && (b_x1 <= a_x1));


                    if ((b_y0 - a_y1 >= this.SPACING_MIN) || ((b_y0 - a_y1 >= 0) && (xOverlap))) {

                        dirs[i] = 0;

                        if ((this.minimizeBlockCrossover) &&
                                (this.graph.optCheckAvoidVerticalLineOverlapWithNodes(a_xcenter, a_y1, b_y0))) {

                            dirs[i] = 10;

                        }

                    } else if (b_x0 > a_x1 + 2 * this.MARGIN_MIN) {

                        dirs[i] = 1;

                    } else if (b_x1 + 2 * this.MARGIN_MIN < a_x0) {

                        dirs[i] = -1;

                    } else if (b_xcenter >= a_xcenter) {

                        dirs[i] = 1;

                    } else {

                        dirs[i] = -1;

                    }

                    i++;

                }


                boolean flip = false;


                if (((dirs[0] == 1) && (dirs[1] == 0)) || ((dirs[0] == 1) && (dirs[1] == -1)) || ((dirs[0] == 0) && (dirs[1] == -1)) || ((dirs[0] == 1) && (dirs[1] == -1))) {

                    flip = true;


                } else if ((dirs[0] == 10) && (dirs[1] == 0)) {

                    flip = true;


                } else if ((dirs[0] == 0) && (dirs[1] == 0)) {

                    int dst0x = getCenterX(dst0);

                    int dst1x = getCenterX(dst1);

                    int dst0y = getCenterY(dst0);

                    int dst1y = getCenterY(dst1);


                    if (((dst0y >= dst1y) || (dst0x >= a_xcenter)) && ((dst1y >= dst0y) || (dst1x <= a_xcenter))) {

                        flip = true;

                    }

                }

                if (flip) {

                    List<GraphNode> outedges_ordered_nodes = (List) src.getData("outedges_ordered_nodes");

                    Collections.reverse(outedges_ordered_nodes);

                }

            }

        }

    }


    void drawEdge(GraphEdgeSquare edge, GC gc) {

        if ((edge.srcAnchor != Anchor.BOTTOM) || (edge.dstAnchor != Anchor.TOP)) {

            throw new IllegalStateException();

        }


        Rectangle a = edge.src.getBounds();

        Rectangle b = edge.dst.getBounds();


        int a_x0 = a.x;


        int b_x0 = b.x;

        int b_y0 = b.y;

        int a_x1 = a.x + a.width;

        int a_y1 = a.y + a.height;

        int b_x1 = b.x + b.width;

        int b_y1 = b.y + b.height;

        int a_xcenter = a.x + a.width / 2;

        int b_xcenter = b.x + b.width / 2;


        boolean xOverlap = ((b_x0 <= a.x) && (b_x1 > a.x)) || ((b_x0 <= a_x1) && (b_x1 > a_x1)) || ((b_x0 >= a.x) && (b_x1 <= a_x1));


        List<GraphNode> outnodes = (List) edge.src.getData("outedges_ordered_nodes");

        int[] xlist = (int[]) edge.src.getData("outedges_ordered_x");

        int x0 = xlist[outnodes.indexOf(edge.dst)];


        List<GraphNode> innodes = (List) edge.dst.getData("inedges_ordered_nodes");

        xlist = (int[]) edge.dst.getData("inedges_ordered_x");

        int x1 = xlist[innodes.indexOf(edge.src)];


        int originalX0 = x0;

        int originalX1 = x1;


        if ((b_y0 - a_y1 >= this.SPACING_MIN) || ((b_y0 - a_y1 >= 0) && (xOverlap))) {

            int yi = this.graph.requestHorizontalLine(x0, x1, b_y0 - this.MARGIN_MIN, -this.EDGE_SEP);

            x0 = this.graph.requestVerticalLine(a_y1, yi, x0, this.EDGE_SEP);

            x1 = this.graph.requestVerticalLine(yi, b_y0, x1, this.EDGE_SEP);


            int[] r = null;

            if (this.minimizeBlockCrossover) {

                r = this.graph.optAvoidVerticalLineOverlapWithNodes(x0, a_y1, yi, this.MARGIN_MIN);

            }

            if (r != null) {

                int yE = r[0];

                int xE = r[1];

                int yE1 = r[2];


                this.graph.releaseHorizontalLine(originalX0, originalX1, yi);


                yE = this.graph.requestHorizontalLine(x0, xE, yE, -this.EDGE_SEP);

                xE = this.graph.requestVerticalLine(yE, yE1, xE, this.EDGE_SEP);

                yE1 = this.graph.requestHorizontalLine(xE, x1, yE1, this.EDGE_SEP);


                this.graph.releaseVerticalLine(yi, b_y0, x1);

                x1 = this.graph.requestVerticalLine(yE1, b_y0, x1, this.EDGE_SEP);


                this.graph.releaseVerticalLine(a_y1, yi, x0);

                x0 = this.graph.requestVerticalLine(a_y1, yE, x0, this.EDGE_SEP);


                edge.drawLine(gc, x0, a_y1, x0, yE);

                edge.drawLine(gc, x0, yE, xE, yE);

                edge.drawLine(gc, xE, yE, xE, yE1);

                edge.drawLine(gc, xE, yE1, x1, yE1);

                edge.drawLine(gc, x1, yE1, x1, b_y0);

            } else {

                if ((this.preferStraightRouting) && (x0 != x1) && (Math.abs(x0 - x1) < this.SPACING_MIN) && (x0 > b_x0) && (x0 < b_x1)) {

                    this.graph.releaseHorizontalLine(originalX0, originalX1, yi);

                    this.graph.releaseVerticalLine(a_y1, yi, x0);

                    this.graph.releaseVerticalLine(yi, b_y0, x1);


                    x0 = this.graph.requestVerticalLine(a_y1, b_y0, x0, this.EDGE_SEP);

                    x1 = x0;

                }


                if (x1 != x0) {

                    edge.drawLine(gc, x0, a_y1, x0, yi);

                    edge.drawLine(gc, x0, yi, x1, yi);

                    edge.drawLine(gc, x1, yi, x1, b_y0);

                } else {

                    edge.drawLine(gc, x0, a_y1, x0, b_y0);

                }

            }

        } else {

            int adjx0;

            int adjx1;

            int adjxi;
            int xi,yi,yj;

            if (b_x0 > a_x1 + 2 * this.MARGIN_MIN) {

                xi = b_x0 - this.MARGIN_MIN;

                yi = a_y1 + this.MARGIN_MIN;

                yj = b_y0 - this.MARGIN_MIN;

                adjx0 = 1;

                adjx1 = -1;
                adjxi = -1;

            } else {
                if (b_x1 + 2 * this.MARGIN_MIN < a_x0) {

                    xi = b_x1 + this.MARGIN_MIN;

                    yi = a_y1 + this.MARGIN_MIN;

                    yj = b_y0 - this.MARGIN_MIN;

                    adjx0 = -1;

                    adjx1 = 1;

                    adjxi = 1;

                } else {

                    if (b_xcenter >= a_xcenter) {

                        xi = Math.max(b_x1 + this.MARGIN_MIN, a_x1 + this.MARGIN_MIN);

                        yi = Math.max(a_y1 + this.MARGIN_MIN, b_y1 + this.MARGIN_MIN);

                        yj = b_y0 - this.MARGIN_MIN;

                        adjx0 = 1;

                        adjx1 = 1;

                        adjxi = 1;

                    } else {
                        xi = Math.min(b_x0 - this.MARGIN_MIN, a_x0 - this.MARGIN_MIN);
                        yi = Math.max(a_y1 + this.MARGIN_MIN, b_y1 + this.MARGIN_MIN);
                        yj = b_y0 - this.MARGIN_MIN;

                        adjx0 = -1;

                        adjx1 = -1;

                        adjxi = -1;

                    }

                }
            }

            x0 = this.graph.requestVerticalLine(a_y1, yi, x0, adjx0 * this.EDGE_SEP);

            x1 = this.graph.requestVerticalLine(yj, b_y0, x1, adjx1 * this.EDGE_SEP);


            yi = this.graph.requestHorizontalLine(x0, xi, yi, this.EDGE_SEP);

            yj = this.graph.requestHorizontalLine(xi, x1, yj, -this.EDGE_SEP);


            xi = this.graph.requestVerticalLine(yi, yj, xi, adjxi * this.EDGE_SEP);


            edge.drawLine(gc, x0, a_y1, x0, yi);

            edge.drawLine(gc, x0, yi, xi, yi);

            edge.drawLine(gc, xi, yi, xi, yj);

            edge.drawLine(gc, xi, yj, x1, yj);

            edge.drawLine(gc, x1, yj, x1, b_y0);

        }


        edge.drawArrow(gc, x1, b_y0 - 1, x1, b_y0);

    }


    public static enum OrderType {
        TOP, CENTER_Y, BOTTOM, LEFT, CENTER_X, RIGHT;


        private OrderType() {
        }
    }

    static final Comparator<Control> cmpTop = new Comparator<Control>() {

        public int compare(Control o1, Control o2) {

            return o1.getLocation().y - o2.getLocation().y;

        }

    };

    static final Comparator<Control> cmpCenterY = new Comparator<Control>() {

        public int compare(Control o1, Control o2) {

            return GraphEdgeSquareManager.getCenterY(o1) - GraphEdgeSquareManager.getCenterY(o2);

        }

    };

    static final Comparator<Control> cmpLeft = new Comparator<Control>() {

        public int compare(Control o1, Control o2) {

            return o1.getLocation().x - o2.getLocation().x;

        }

    };

    static final Comparator<Control> cmpCenterX = new Comparator<Control>() {

        public int compare(Control o1, Control o2) {

            return GraphEdgeSquareManager.getCenterX(o1) - GraphEdgeSquareManager.getCenterX(o2);

        }

    };


    static void orderControls(List<? extends Control> nodes, OrderType comparePoint) {

        switch (comparePoint) {

            case TOP:

                Collections.sort(nodes, cmpTop);

                break;

            case CENTER_Y:

                Collections.sort(nodes, cmpCenterY);

                break;

            case LEFT:

                Collections.sort(nodes, cmpLeft);

                break;

            case CENTER_X:

                Collections.sort(nodes, cmpCenterX);

                break;

            default:

                throw new RuntimeException("Not supported: " + comparePoint);

        }

    }


    public static int getCenterX(Control ctl) {

        Rectangle r = ctl.getBounds();

        return r.x + r.width / 2;

    }


    public static int getCenterY(Control ctl) {

        Rectangle r = ctl.getBounds();

        return r.y + r.height / 2;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphEdgeSquareManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */