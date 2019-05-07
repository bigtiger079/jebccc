
package com.pnfsoftware.jeb.rcpclient.extensions.graph;


import org.eclipse.swt.graphics.GC;


public abstract class GraphEdgeManager {
    protected Graph graph;


    public GraphEdgeManager(Graph graph) {

        this.graph = graph;

    }


    public abstract GraphEdge create(GraphNode paramGraphNode1, GraphNode paramGraphNode2);


    public void draw(GC gc) {

        draw(gc, 0, null);

    }


    public abstract void draw(GC paramGC, int paramInt, Object paramObject);

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphEdgeManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */