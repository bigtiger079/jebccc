package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public abstract interface GraphEdgeListener
        extends EventListener {
    public abstract void onEdgeMouseEnter(GraphEdge paramGraphEdge);

    public abstract void onEdgeMouseExit(GraphEdge paramGraphEdge);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphEdgeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */