/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.core.events.ClientNotification;
/*     */ import com.pnfsoftware.jeb.core.events.J;
/*     */ import com.pnfsoftware.jeb.core.events.JebEvent;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.UnitAddress;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerEventType;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerEventData;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.LogDocument;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.TerminalPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UIState;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jface.text.Document;

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
/*     */ public class DebuggerListener
        /*     */ {
    /*  49 */   private static final ILogger logger = GlobalLog.getLogger(DebuggerListener.class);
    /*     */
    /*  51 */   private static Map<IDebuggerUnit, DebuggerListener> map = new IdentityHashMap();
    /*     */   private int clientCount;

    /*     */
    /*  54 */
    public static synchronized DebuggerListener listenTo(IDebuggerUnit dbg, RcpClientContext context) {
        DebuggerListener listener = (DebuggerListener) map.get(dbg);
        /*  55 */
        if (listener == null) {
            /*  56 */
            listener = new DebuggerListener(dbg, context);
            /*  57 */
            map.put(dbg, listener);
            /*     */
        }
        /*     */
        /*  60 */
        listener.clientCount += 1;
        /*  61 */
        return listener;
        /*     */
    }

    /*     */
    /*     */
    public static synchronized boolean stopListening(IDebuggerUnit dbg) {
        /*  65 */
        DebuggerListener listener = (DebuggerListener) map.get(dbg);
        /*  66 */
        if (listener == null) {
            /*  67 */
            return false;
            /*     */
        }
        /*     */
        /*  70 */
        listener.clientCount -= 1;
        /*  71 */
        if (listener.clientCount <= 0) {
            /*  72 */
            listener.close();
            /*  73 */
            map.remove(dbg);
            /*     */
        }
        /*  75 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public static synchronized DebuggerListener get(IDebuggerUnit dbg) {
        /*  79 */
        return (DebuggerListener) map.get(dbg);
        /*     */
    }

    /*     */
    /*     */
    /*     */   private IDebuggerUnit dbg;
    /*     */   private RcpClientContext context;
    /*     */   private IEventListener listener;
    /*     */   private LogDocument eventLog;

    /*     */
    private DebuggerListener(IDebuggerUnit dbg, RcpClientContext context)
    /*     */ {
        /*  89 */
        this.dbg = dbg;
        /*  90 */
        this.context = context;
        /*  91 */
        this.eventLog = new LogDocument(1048576);
        /*     */
        /*  93 */
        dbg.addListener(this. = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /*  96 */
                DebuggerListener.this.onDebugEvent(e);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public IDebuggerUnit getDebugger() {
        /* 102 */
        return this.dbg;
        /*     */
    }

    /*     */
    /*     */
    public RcpClientContext getContext() {
        /* 106 */
        return this.context;
        /*     */
    }

    /*     */
    /*     */
    public Document getLog() {
        /* 110 */
        return this.eventLog;
        /*     */
    }

    /*     */
    /*     */
    private void close() {
        /* 114 */
        if (this.listener != null) {
            /* 115 */
            this.dbg.removeListener(this.listener);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void onDebugEvent(final IEvent e) {
        /* 120 */
        if (e.getSource() != this.dbg) {
            /* 121 */
            return;
            /*     */
        }
        /*     */
        /* 124 */
        UIExecutor.async(UI.getDisplay(), new UIRunnable()
                /*     */ {
            /*     */
            /*     */
            public void runi()
            /*     */ {
                /*     */
                /* 130 */
                String addition = String.format("%s", new Object[]{e.getType()});
                /* 131 */
                if (e.getData() != null) {
                    /* 132 */
                    addition = addition + String.format(" / %s", new Object[]{e.getData()});
                    /*     */
                }
                /* 134 */
                addition = addition + "\n";
                /* 135 */
                DebuggerListener.this.eventLog.append(addition);
                /*     */
                ClientNotification n;
                /* 137 */
                if (e.getType() == J.DbgClientNotification) {
                    /* 138 */
                    n = (ClientNotification) e.getData();
                    /* 139 */
                    String message = n.getMessage();
                    /* 140 */
                    int level = RcpClientContext.clientNotificationLevelToLoggerLevel(n.getLevel());
                    /* 141 */
                    UI.log(level, null, "Debugger Notification", message);
                    /*     */
                    /*     */
                    /*     */
                }
                /* 145 */
                else if (e.getType() == J.DbgAttach) {
                    /* 146 */
                    for (ICodeUnit targetUnit : DebuggerListener.this.dbg.getPotentialDebuggees()) {
                        /* 147 */
                        if ((targetUnit instanceof INativeCodeUnit))
                            /*     */ {
                            /*     */
                            /* 150 */
                            targetUnit.notifyListeners(new JebEvent(J.UnitChange));
                            /*     */
                        }
                        /*     */
                        /*     */
                    }
                    /*     */
                }
                /* 155 */
                else if (e.getType() == J.DbgDetach) {
                    /* 156 */
                    for (ICodeUnit targetUnit : DebuggerListener.this.dbg.getPotentialDebuggees()) {
                        /* 157 */
                        DebuggerListener.this.context.getUIState(targetUnit).setProgramCounter(null);
                        /* 158 */
                        if ((targetUnit instanceof INativeCodeUnit))
                            /*     */ {
                            /*     */
                            /* 161 */
                            targetUnit.notifyListeners(new JebEvent(J.UnitChange));
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                } else {
                    /*     */
                    IUnit unit;
                    /* 166 */
                    if (e.getType() == J.DbgRun) {
                        /* 167 */
                        if ((DebuggerListener.this.dbg instanceof IDebuggerUnit)) {
                            /* 168 */
                            for (n = DebuggerListener.this.dbg.getPotentialDebuggees().iterator(); n.hasNext(); ) {
                                unit = (IUnit) n.next();
                                /* 169 */
                                DebuggerListener.this.context.getUIState(unit).setProgramCounter(null);
                                /*     */
                            }
                            /*     */
                            /*     */
                        }
                        /*     */
                    }
                    /* 174 */
                    else if (e.getType() != J.DbgPause) {
                        List<? extends IDebuggerBreakpoint> realBreakpoints;
                        /*     */
                        UIState uiState;
                        /*     */
                        IDebuggerBreakpoint bp;
                        /*     */
                        UnitAddress<ICodeUnit> ua;
                        /* 178 */
                        if ((e.getType() == J.DbgBreakpointSet) || (e.getType() == J.DbgBreakpointUnset)) {
                            /* 179 */
                            realBreakpoints = DebuggerListener.this.dbg.getBreakpoints();
                            /* 180 */
                            if (realBreakpoints != null) {
                                /* 181 */
                                for (IUnit target : DebuggerListener.this.dbg.getPotentialDebuggees()) {
                                    /* 182 */
                                    uiState = DebuggerListener.this.context.getUIState(target);
                                    /*     */
                                    /* 184 */
                                    List<String> validAddresses = new ArrayList();
                                    /* 185 */
                                    for (Iterator localIterator = realBreakpoints.iterator(); localIterator.hasNext(); ) {
                                        bp = (IDebuggerBreakpoint) localIterator.next();
                                        /* 186 */
                                        String dbgAddress = bp.getAddress();
                                        /* 187 */
                                        ua = DebuggerListener.this.dbg.convertToUnitAddress(dbgAddress);
                                        /* 188 */
                                        if ((ua != null) && (ua.getUnit() == target)) {
                                            /* 189 */
                                            String addr = ua.getAddress();
                                            /* 190 */
                                            validAddresses.add(addr);
                                            /* 191 */
                                            if ((!uiState.isBreakpoint(addr)) ||
                                                    /* 192 */                         (uiState.isBreakpointEnabled(addr) != bp.isEnabled())) {
                                                /* 193 */
                                                uiState.setBreakpoint(addr, bp.isEnabled());
                                                /*     */
                                            }
                                            /*     */
                                        }
                                        /*     */
                                    }
                                    /*     */
                                    /* 198 */
                                    Object invalidAddresses = new ArrayList();
                                    /* 199 */
                                    for (String address : uiState.getBreakpoints().keySet()) {
                                        /* 200 */
                                        if (!validAddresses.contains(address)) {
                                            /* 201 */
                                            ((List) invalidAddresses).add(address);
                                            /*     */
                                        }
                                        /*     */
                                    }
                                    /* 204 */
                                    for (String address : (List) invalidAddresses) {
                                        /* 205 */
                                        uiState.removeBreakpoint(address);
                                        /*     */
                                    }
                                    /*     */
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                        }
                        /* 211 */
                        else if ((e.getType() == J.DbgTargetEvent) &&
                                /* 212 */               ((e.getData() instanceof IDebuggerEventData))) {
                            /* 213 */
                            IDebuggerEventData data = (IDebuggerEventData) e.getData();
                            /*     */
                            /* 215 */
                            UnitAddress<ICodeUnit> ua = data.getUnitAddress();
                            /* 216 */
                            IUnit targetUnit = ua == null ? null : ua.getUnit();
                            /* 217 */
                            String targetAddress = ua == null ? null : ua.getAddress();
                            /* 218 */
                            if ((targetUnit == null) || (targetAddress == null)) {
                                /* 219 */
                                DebuggerListener.logger.debug("Target unit/address associated with the debuggee event cannot be determined", new Object[0]);
                                /*     */
                                /* 221 */
                                DebuggerListener.this.context.refreshHandlersStates();
                                /* 222 */
                                return;
                                /*     */
                            }
                            /*     */
                            /*     */
                            /* 226 */
                            if ((targetUnit instanceof ICodeUnit)) {
                                /* 227 */
                                UIState uiState = DebuggerListener.this.context.getUIState(targetUnit);
                                /* 228 */
                                if ((uiState.isTemporaryBreakpoint(targetAddress)) &&
                                        /* 229 */                   (uiState.removeTemporaryBreakpoint(targetAddress))) {
                                    /* 230 */
                                    IDebuggerBreakpoint bp = DebuggerListener.this.dbg.getBreakpoint(targetAddress, (ICodeUnit) targetUnit);
                                    /* 231 */
                                    if (bp != null) {
                                        /* 232 */
                                        DebuggerListener.this.dbg.clearBreakpoint(bp);
                                        /*     */
                                    }
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                            /*     */
                            /*     */
                            /* 239 */
                            if (data.getThreadId() != 0L) {
                                /* 240 */
                                DebuggerListener.this.dbg.setDefaultThread(data.getThreadId());
                                /*     */
                            }
                            /*     */
                            /*     */
                            /* 244 */
                            PartManager pman = DebuggerListener.this.context.getPartManager();
                            /* 245 */
                            IMPart activePart = pman.getActivePart();
                            /* 246 */
                            Object activeObject = activePart == null ? null : activePart.getManager();
                            /*     */
                            /*     */
                            /* 249 */
                            pman.create(targetUnit, true);
                            /*     */
                            /* 251 */
                            int focusedCount = 0;
                            /* 252 */
                            for (UnitPartManager o : pman.getPartManagersForUnit(targetUnit)) {
                                /* 253 */
                                IRcpUnitFragment fragment = o.getActiveFragment();
                                /* 254 */
                                if (fragment != null) {
                                    /* 255 */
                                    DebuggerListener.logger.i("Setting active address: %s", new Object[]{targetAddress});
                                    /* 256 */
                                    if (fragment.setActiveAddress(targetAddress)) {
                                        /* 257 */
                                        o.setFocus();
                                        /* 258 */
                                        focusedCount++;
                                        /*     */
                                    }
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                            /*     */
                            /* 264 */
                            if ((activeObject instanceof TerminalPartManager)) {
                                /* 265 */
                                TerminalPartManager consolePart = (TerminalPartManager) activeObject;
                                /* 266 */
                                consolePart.setFocus();
                                /*     */
                            }
                            /*     */
                            /* 269 */
                            if (focusedCount == 0) {
                                /* 270 */
                                if ((data.getType() == DebuggerEventType.EXCEPTION) || (data.getType() == DebuggerEventType.SIGNAL)) {
                                    /* 271 */
                                    UI.warn(null, "Event generated by debugged target", "An exception occurred in unreachable code. The process was paused at:\n\n" + targetAddress);
                                    /*     */
                                }
                                /*     */
                                else
                                    /*     */ {
                                    /* 275 */
                                    UI.warn(null, "Event generated by debugged target", "The following address cannot be navigated to:\n\n" + targetAddress);
                                    /*     */
                                }
                                /*     */
                                /*     */
                            }
                            /*     */
                            else {
                                /* 280 */
                                DebuggerListener.this.context.getUIState(targetUnit).setProgramCounter(targetAddress);
                                /*     */
                            }
                            /*     */
                            /*     */
                            /* 284 */
                            ((JebEvent) e).setStopPropagation(true);
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /* 288 */
                DebuggerListener.this.context.refreshHandlersStates();
                /*     */
            }
            /*     */
        });
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\DebuggerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */