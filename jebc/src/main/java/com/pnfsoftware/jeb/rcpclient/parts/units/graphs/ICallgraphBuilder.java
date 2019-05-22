package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;

public interface ICallgraphBuilder {
    Digraph buildModel();

    String getAddressForVertexId(int paramInt);

    Integer getVertexIdForAddress(String paramString);
}


