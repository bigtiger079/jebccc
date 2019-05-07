
package com.pnfsoftware.jeb.rcpclient.extensions.graph;


public class GraphEdgeSquare
        extends GraphEdge {
    GraphEdgeSquare(Graph graph, GraphNode src, GraphNode dst) {

        super(graph, src, dst);

        setAnchors(Anchor.BOTTOM, Anchor.TOP);

        setOrientation(Orientation.ORIENTED);

    }


    public boolean applyZoom(int zoom, boolean dryRun) {

        if (!super.applyZoom(zoom, dryRun)) {

            return false;

        }


        return false;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphEdgeSquare.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */