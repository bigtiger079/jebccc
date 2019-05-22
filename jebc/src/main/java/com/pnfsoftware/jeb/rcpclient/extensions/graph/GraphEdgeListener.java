package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public interface GraphEdgeListener extends EventListener {
    public abstract void onEdgeMouseEnter(GraphEdge paramGraphEdge);

    public abstract void onEdgeMouseExit(GraphEdge paramGraphEdge);
}


