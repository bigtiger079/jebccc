package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.util.events.IEvent;

public interface IDebuggerListener {
    void onDebuggerEvent(IDebuggerUnit paramIDebuggerUnit, IEvent paramIEvent);
}


