package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.util.events.IEvent;

public abstract interface IDebuggerListener {
    public abstract void onDebuggerEvent(IDebuggerUnit paramIDebuggerUnit, IEvent paramIEvent);
}


