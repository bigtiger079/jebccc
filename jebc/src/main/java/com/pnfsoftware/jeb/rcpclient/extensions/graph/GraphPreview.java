package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class GraphPreview<G extends AbstractGraph> extends Canvas {
    G graph;
    GraphChangeListener graphListener;
    Rectangle activeArea;
    boolean renderEdges;

    public GraphPreview(Composite parent, int style, final boolean renderEdges) {
        super(parent, 0x20100000 | style);
        this.renderEdges = renderEdges;
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                if ((GraphPreview.this.graph == null) || (GraphPreview.this.graph.isDisposed())) {
                    e.gc.fillRectangle(GraphPreview.this.getClientArea());
                    return;
                }
                GraphPreview.this.activeArea = GraphPreview.this.graph.generatePreview(e.gc, GraphPreview.this.getClientArea(), null, renderEdges);
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent e) {
                if ((GraphPreview.this.graph == null) || (GraphPreview.this.graph.isDisposed()) || (GraphPreview.this.activeArea == null)) {
                    return;
                }
                double xRatio = (e.x - GraphPreview.this.activeArea.x) / GraphPreview.this.activeArea.width;
                double yRatio = (e.y - GraphPreview.this.activeArea.y) / GraphPreview.this.activeArea.height;
                GraphPreview.this.graph.positionGraph(xRatio, yRatio);
            }
        });
    }

    public G getGraph() {
        return this.graph;
    }

    public void setGraph(G g) {
        if (g == this.graph) {
            return;
        }
        if (this.graphListener != null) {
            this.graph.removeGraphChangeListener(this.graphListener);
            this.graphListener = null;
        }
        this.graph = g;
        if (this.graph == null) {
            this.activeArea = null;
            return;
        }
        this.graphListener = new GraphChangeListener() {
            public void onGraphChange(AbstractGraph g) {
                GraphPreview.this.redraw();
            }
        };
        this.graph.addGraphChangeListener(this.graphListener);
        this.graph.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                GraphPreview.this.setGraph(null);
            }
        });
    }
}


