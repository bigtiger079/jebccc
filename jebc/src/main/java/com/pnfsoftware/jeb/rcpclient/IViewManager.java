package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.util.collect.ItemHistory;

public abstract interface IViewManager {
    public abstract ItemHistory<GlobalPosition> getGlobalPositionHistory();

    public abstract GlobalPosition getCurrentGlobalPosition();

    public abstract boolean recordGlobalPosition(GlobalPosition paramGlobalPosition);
}


