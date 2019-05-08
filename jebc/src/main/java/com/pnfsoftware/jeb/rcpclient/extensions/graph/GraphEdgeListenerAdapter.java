
package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class GraphEdgeListenerAdapter
        implements GraphEdgeListener {
    private static final ILogger logger = GlobalLog.getLogger(GraphEdgeListenerAdapter.class);

    public void onEdgeMouseEnter(GraphEdge edge) {
        logger.i("onEdgeMouseEnter: %s", new Object[]{edge});
    }

    public void onEdgeMouseExit(GraphEdge edge) {
        logger.i("onEdgeMouseExit: %s", new Object[]{edge});
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphEdgeListenerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */