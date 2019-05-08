package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitView;

public abstract interface IRcpUnitView extends IOperable, IUnitView {
    public abstract boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);
}


