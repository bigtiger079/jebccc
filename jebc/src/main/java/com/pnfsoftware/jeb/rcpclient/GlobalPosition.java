package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.parts.units.Position;

import java.lang.ref.WeakReference;

public class GlobalPosition {
    private WeakReference<IUnit> unitRef;
    private int partId;
    private long fragmentId;
    private Position position;

    public GlobalPosition(IUnit unit, int partId, long fragmentId, Position position) {
        this.unitRef = new WeakReference(unit);
        this.partId = partId;
        this.fragmentId = fragmentId;
        this.position = position;
    }

    public IUnit getUnit() {
        return this.unitRef.get();
    }

    public int getPartId() {
        return this.partId;
    }

    public long getFragmentId() {
        return this.fragmentId;
    }

    public Position getPosition() {
        return this.position;
    }

    public String toString() {
        return String.format("%s @ %s", this.unitRef.get(), this.position);
    }
}


