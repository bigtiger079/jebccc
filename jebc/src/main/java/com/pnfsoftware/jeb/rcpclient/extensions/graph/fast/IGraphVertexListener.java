package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

public abstract interface IGraphVertexListener {
    public abstract void onVertexHoverIn(XYGraph paramXYGraph, P paramP);

    public abstract void onVertexHoverOut(XYGraph paramXYGraph, P paramP);

    public abstract void onVertexClicked(XYGraph paramXYGraph, P paramP);

    public abstract void onVertexDoubleClicked(XYGraph paramXYGraph, P paramP);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\fast\IGraphVertexListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */