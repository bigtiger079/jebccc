package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;

public abstract interface ICallgraphBuilder {
    public abstract Digraph buildModel();

    public abstract String getAddressForVertexId(int paramInt);

    public abstract Integer getVertexIdForAddress(String paramString);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\ICallgraphBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */