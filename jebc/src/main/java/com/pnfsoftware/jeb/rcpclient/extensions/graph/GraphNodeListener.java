package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public interface GraphNodeListener extends EventListener {
    void onNodeMouseEnter(GraphNode paramGraphNode);

    void onNodeMouseExit(GraphNode paramGraphNode);

    void onNodeFocusGained(GraphNode paramGraphNode);

    void onNodeFocusLost(GraphNode paramGraphNode);
}


