package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public interface GraphEdgeListener extends EventListener {
    void onEdgeMouseEnter(GraphEdge paramGraphEdge);

    void onEdgeMouseExit(GraphEdge paramGraphEdge);
}


