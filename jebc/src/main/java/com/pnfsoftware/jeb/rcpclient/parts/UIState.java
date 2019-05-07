/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractDebuggerBreakpoint;
/*     */ import com.pnfsoftware.jeb.util.events.Event;
/*     */ import com.pnfsoftware.jeb.util.events.EventSource;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class UIState
        /*     */ extends EventSource
        /*     */ {
    /*  34 */   private Map<String, Boolean> breakpoints = new HashMap();
    /*  35 */   private Map<String, Integer> temporaryBreakpoints = new HashMap();
    /*     */   private IDebuggerThread selectedThread;
    /*     */   private String pcAddress;

    /*     */
    /*     */
    public UIState(IUnit unit) {
    }

    /*     */
    /*     */
    public void setProgramCounter(String address)
    /*     */ {
        /*  43 */
        this.pcAddress = address;
        /*  44 */
        notifyListeners(new Event());
        /*     */
    }

    /*     */
    /*     */
    public String getProgramCounter() {
        /*  48 */
        return this.pcAddress;
        /*     */
    }

    /*     */
    /*     */
    public Map<String, Boolean> getBreakpoints() {
        /*  52 */
        return new HashMap(this.breakpoints);
        /*     */
    }

    /*     */
    /*     */
    public void setBreakpoint(String address, boolean enabled) {
        /*  56 */
        this.breakpoints.put(address, Boolean.valueOf(enabled));
        /*  57 */
        notifyListeners(new Event());
        /*     */
    }

    /*     */
    /*     */
    public void removeBreakpoint(String address) {
        /*  61 */
        this.breakpoints.remove(address);
        /*  62 */
        notifyListeners(new Event());
        /*     */
    }

    /*     */
    /*     */
    public boolean isBreakpoint(String address) {
        /*  66 */
        return this.breakpoints.get(address) != null;
        /*     */
    }

    /*     */
    /*     */
    public boolean isBreakpointEnabled(String address) {
        /*  70 */
        return this.breakpoints.get(address) == Boolean.TRUE;
        /*     */
    }

    /*     */
    /*     */
    public boolean isTemporaryBreakpoint(String address) {
        /*  74 */
        return this.temporaryBreakpoints.get(address) != null;
        /*     */
    }

    /*     */
    /*     */
    public void setTemporaryBreakpoint(String address, int count) {
        /*  78 */
        this.temporaryBreakpoints.put(address, Integer.valueOf(count));
        /*  79 */
        setBreakpoint(address, true);
        /*     */
    }

    /*     */
    /*     */
    public boolean removeTemporaryBreakpoint(String address) {
        /*  83 */
        Integer count = (Integer) this.temporaryBreakpoints.get(address);
        /*  84 */
        if (count != null) {
            /*  85 */
            Integer localInteger1 = count;
            Integer localInteger2 = count = Integer.valueOf(count.intValue() - 1);
            /*  86 */
            if (count.intValue() == 0) {
                /*  87 */
                this.temporaryBreakpoints.remove(address);
                /*     */
            }
            /*     */
            else {
                /*  90 */
                this.temporaryBreakpoints.put(address, count);
                /*     */
                /*  92 */
                return false;
                /*     */
            }
            /*     */
        }
        /*  95 */
        this.breakpoints.remove(address);
        /*  96 */
        notifyListeners(new Event());
        /*  97 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public void setSelectedThread(IDebuggerThread thread) {
        /* 101 */
        if (thread == this.selectedThread) {
            /* 102 */
            return;
            /*     */
        }
        /* 104 */
        this.selectedThread = thread;
        /*     */
    }

    /*     */
    /*     */
    public IDebuggerThread getSelectedThread()
    /*     */ {
        /* 109 */
        return this.selectedThread;
        /*     */
    }

    /*     */
    /* 112 */ List<AbstractDebuggerBreakpoint> breakpoints2 = new ArrayList();

    /*     */
    /*     */
    public void copyFrom(IDebuggerUnit unit) {
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\UIState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */