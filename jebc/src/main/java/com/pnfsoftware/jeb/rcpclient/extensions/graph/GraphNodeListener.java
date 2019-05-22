package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public interface GraphNodeListener extends EventListener {
    public abstract void onNodeMouseEnter(GraphNode paramGraphNode);

    public abstract void onNodeMouseExit(GraphNode paramGraphNode);

    public abstract void onNodeFocusGained(GraphNode paramGraphNode);

    public abstract void onNodeFocusLost(GraphNode paramGraphNode);
}


