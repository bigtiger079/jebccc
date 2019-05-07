package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public abstract interface GraphNodeListener
        extends EventListener {
    public abstract void onNodeMouseEnter(GraphNode paramGraphNode);

    public abstract void onNodeMouseExit(GraphNode paramGraphNode);

    public abstract void onNodeFocusGained(GraphNode paramGraphNode);

    public abstract void onNodeFocusLost(GraphNode paramGraphNode);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphNodeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */