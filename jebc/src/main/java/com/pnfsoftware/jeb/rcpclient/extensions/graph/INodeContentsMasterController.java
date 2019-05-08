package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.client.api.Operation;

@Deprecated
public abstract interface INodeContentsMasterController {
    public abstract boolean requestOperation(IGraphNodeContents paramIGraphNodeContents, Operation paramOperation);
}


