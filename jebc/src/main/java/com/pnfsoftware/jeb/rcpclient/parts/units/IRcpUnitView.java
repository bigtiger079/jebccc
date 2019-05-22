package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitView;

public interface IRcpUnitView extends IOperable, IUnitView {
    boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);
}


