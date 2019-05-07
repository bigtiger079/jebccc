package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitView;

public abstract interface IRcpUnitView
        extends IOperable, IUnitView {
    public abstract boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\IRcpUnitView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */