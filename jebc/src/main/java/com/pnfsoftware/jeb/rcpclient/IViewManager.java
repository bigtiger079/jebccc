package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.util.collect.ItemHistory;

public interface IViewManager {
    ItemHistory<GlobalPosition> getGlobalPositionHistory();

    GlobalPosition getCurrentGlobalPosition();

    boolean recordGlobalPosition(GlobalPosition paramGlobalPosition);
}


