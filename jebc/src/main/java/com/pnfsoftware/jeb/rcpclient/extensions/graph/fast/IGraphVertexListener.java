package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

public interface IGraphVertexListener {
    void onVertexHoverIn(XYGraph paramXYGraph, P paramP);

    void onVertexHoverOut(XYGraph paramXYGraph, P paramP);

    void onVertexClicked(XYGraph paramXYGraph, P paramP);

    void onVertexDoubleClicked(XYGraph paramXYGraph, P paramP);
}


