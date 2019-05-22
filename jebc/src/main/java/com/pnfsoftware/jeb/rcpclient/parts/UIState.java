package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractDebuggerBreakpoint;
import com.pnfsoftware.jeb.util.events.Event;
import com.pnfsoftware.jeb.util.events.EventSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIState extends EventSource {
    private Map<String, Boolean> breakpoints = new HashMap();
    private Map<String, Integer> temporaryBreakpoints = new HashMap();
    private IDebuggerThread selectedThread;
    private String pcAddress;

    public UIState(IUnit unit) {
    }

    public void setProgramCounter(String address) {
        this.pcAddress = address;
        notifyListeners(new Event());
    }

    public String getProgramCounter() {
        return this.pcAddress;
    }

    public Map<String, Boolean> getBreakpoints() {
        return new HashMap(this.breakpoints);
    }

    public void setBreakpoint(String address, boolean enabled) {
        this.breakpoints.put(address, enabled);
        notifyListeners(new Event());
    }

    public void removeBreakpoint(String address) {
        this.breakpoints.remove(address);
        notifyListeners(new Event());
    }

    public boolean isBreakpoint(String address) {
        return this.breakpoints.get(address) != null;
    }

    public boolean isBreakpointEnabled(String address) {
        return this.breakpoints.get(address) == Boolean.TRUE;
    }

    public boolean isTemporaryBreakpoint(String address) {
        return this.temporaryBreakpoints.get(address) != null;
    }

    public void setTemporaryBreakpoint(String address, int count) {
        this.temporaryBreakpoints.put(address, count);
        setBreakpoint(address, true);
    }

    public boolean removeTemporaryBreakpoint(String address) {
        Integer count = this.temporaryBreakpoints.get(address);
        if (count != null) {
            Integer localInteger1 = count;
            Integer localInteger2 = count = count.intValue() - 1;
            if (count == 0) {
                this.temporaryBreakpoints.remove(address);
            } else {
                this.temporaryBreakpoints.put(address, count);
                return false;
            }
        }
        this.breakpoints.remove(address);
        notifyListeners(new Event());
        return true;
    }

    public void setSelectedThread(IDebuggerThread thread) {
        if (thread == this.selectedThread) {
            return;
        }
        this.selectedThread = thread;
    }

    public IDebuggerThread getSelectedThread() {
        return this.selectedThread;
    }

    List<AbstractDebuggerBreakpoint> breakpoints2 = new ArrayList<>();

    public void copyFrom(IDebuggerUnit unit) {
    }
}


