package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.util.collect.ItemHistory;

public abstract interface IViewManager {
    public abstract ItemHistory<GlobalPosition> getGlobalPositionHistory();

    public abstract GlobalPosition getCurrentGlobalPosition();

    public abstract boolean recordGlobalPosition(GlobalPosition paramGlobalPosition);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\IViewManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */