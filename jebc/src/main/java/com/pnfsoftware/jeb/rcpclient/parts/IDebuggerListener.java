package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.util.events.IEvent;

public abstract interface IDebuggerListener {
    public abstract void onDebuggerEvent(IDebuggerUnit paramIDebuggerUnit, IEvent paramIEvent);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\IDebuggerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */