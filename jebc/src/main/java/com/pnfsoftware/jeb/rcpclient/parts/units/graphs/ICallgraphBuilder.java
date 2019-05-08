package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;

public abstract interface ICallgraphBuilder {
    public abstract Digraph buildModel();

    public abstract String getAddressForVertexId(int paramInt);

    public abstract Integer getVertexIdForAddress(String paramString);
}


