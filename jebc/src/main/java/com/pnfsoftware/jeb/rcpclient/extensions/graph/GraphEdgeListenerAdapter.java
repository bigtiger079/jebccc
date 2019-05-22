package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class GraphEdgeListenerAdapter implements GraphEdgeListener {
    private static final ILogger logger = GlobalLog.getLogger(GraphEdgeListenerAdapter.class);

    public void onEdgeMouseEnter(GraphEdge edge) {
        logger.i("onEdgeMouseEnter: %s", edge);
    }

    public void onEdgeMouseExit(GraphEdge edge) {
        logger.i("onEdgeMouseExit: %s", edge);
    }
}


