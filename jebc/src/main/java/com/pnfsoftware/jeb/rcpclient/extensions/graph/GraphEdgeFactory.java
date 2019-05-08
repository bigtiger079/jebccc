
package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class GraphEdgeFactory {
    Anchor srcAnchor = Anchor.AUTO;
    Anchor dstAnchor = Anchor.AUTO;
    int style = 1;
    int thickness = 2;
    Color color = Display.getCurrent().getSystemColor(2);
    Orientation orientation = Orientation.NONE;

    public void setAnchors(Anchor srcAnchor, Anchor dstAnchor) {
        this.srcAnchor = srcAnchor;
        this.dstAnchor = dstAnchor;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public void setColor(Color color) {
        if (color == null) {
            color = Display.getCurrent().getSystemColor(2);
        }
        this.color = color;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public GraphEdge createEdge(Graph graph, GraphNode src, GraphNode dst) {
        GraphEdge e = new GraphEdge(graph, src, dst);
        e.setAnchors(this.srcAnchor, this.dstAnchor);
        e.setStyle(this.style);
        e.setThickness(this.thickness);
        e.setColor(0, this.color);
        e.setOrientation(this.orientation);
        return e;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphEdgeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */