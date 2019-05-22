package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

public interface IGraphVertexListener {
    public abstract void onVertexHoverIn(XYGraph paramXYGraph, P paramP);

    public abstract void onVertexHoverOut(XYGraph paramXYGraph, P paramP);

    public abstract void onVertexClicked(XYGraph paramXYGraph, P paramP);

    public abstract void onVertexDoubleClicked(XYGraph paramXYGraph, P paramP);
}


