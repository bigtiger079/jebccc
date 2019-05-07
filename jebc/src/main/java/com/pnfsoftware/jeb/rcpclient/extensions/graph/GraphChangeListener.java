package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import java.util.EventListener;

public abstract interface GraphChangeListener
        extends EventListener {
    public abstract void onGraphChange(AbstractGraph paramAbstractGraph);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */