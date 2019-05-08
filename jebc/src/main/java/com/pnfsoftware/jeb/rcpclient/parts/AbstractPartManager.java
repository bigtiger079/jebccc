
package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPartManager;
import com.pnfsoftware.jeb.util.events.EventSource;

public abstract class AbstractPartManager
        extends EventSource
        implements IMPartManager {
    protected RcpClientContext context;

    public AbstractPartManager(RcpClientContext context) {
        this.context = context;
    }

    public void deleteView() {
    }

    public void setFocus() {
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\AbstractPartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */