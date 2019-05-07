package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.List;

public abstract interface IGraph {
    public abstract void refreshGraph();

    public abstract List<GraphMode> getSupportedModes();

    public abstract int getVertexCount();

    public abstract void centerGraph();

    public abstract void positionGraph(double paramDouble1, double paramDouble2);

    public abstract void dragGraph(int paramInt1, int paramInt2);

    public abstract void dragGraph(int paramInt1, int paramInt2, boolean paramBoolean);

    public abstract void zoomGraph(int paramInt);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\IGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */