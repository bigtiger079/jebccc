package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public interface GraphChangeListener extends EventListener {
    public abstract void onGraphChange(AbstractGraph paramAbstractGraph);
}


