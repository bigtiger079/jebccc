package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public interface GraphChangeListener extends EventListener {
    void onGraphChange(AbstractGraph paramAbstractGraph);
}


