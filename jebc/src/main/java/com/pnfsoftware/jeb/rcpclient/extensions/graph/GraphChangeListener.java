package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public abstract interface GraphChangeListener extends EventListener {
    public abstract void onGraphChange(AbstractGraph paramAbstractGraph);
}


