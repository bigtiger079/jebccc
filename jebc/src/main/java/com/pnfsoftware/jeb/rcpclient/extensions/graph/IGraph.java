package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.List;

public interface IGraph {
    void refreshGraph();

    List<GraphMode> getSupportedModes();

    int getVertexCount();

    void centerGraph();

    void positionGraph(double paramDouble1, double paramDouble2);

    void dragGraph(int paramInt1, int paramInt2);

    void dragGraph(int paramInt1, int paramInt2, boolean paramBoolean);

    void zoomGraph(int paramInt);
}


