
package com.pnfsoftware.jeb.rcpclient;


import com.pnfsoftware.jeb.core.events.ClientNotification;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.events.JebEvent;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.UnitAddress;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerEventType;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerEventData;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.LogDocument;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.TerminalPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Document;


public class DebuggerListener {
    private static final ILogger logger = GlobalLog.getLogger(DebuggerListener.class);

    private static Map<IDebuggerUnit, DebuggerListener> map = new IdentityHashMap();
    private int clientCount;


    public static synchronized DebuggerListener listenTo(IDebuggerUnit dbg, RcpClientContext context) {
        DebuggerListener listener = (DebuggerListener) map.get(dbg);

        if (listener == null) {

            listener = new DebuggerListener(dbg, context);

            map.put(dbg, listener);

        }


        listener.clientCount += 1;

        return listener;

    }


    public static synchronized boolean stopListening(IDebuggerUnit dbg) {

        DebuggerListener listener = (DebuggerListener) map.get(dbg);

        if (listener == null) {

            return false;

        }


        listener.clientCount -= 1;

        if (listener.clientCount <= 0) {

            listener.close();

            map.remove(dbg);

        }

        return true;

    }


    public static synchronized DebuggerListener get(IDebuggerUnit dbg) {

        return (DebuggerListener) map.get(dbg);

    }


    private IDebuggerUnit dbg;
    private RcpClientContext context;
    private IEventListener listener;
    private LogDocument eventLog;


    private DebuggerListener(IDebuggerUnit dbg, RcpClientContext context) {

        this.dbg = dbg;

        this.context = context;

        this.eventLog = new LogDocument(1048576);


        dbg.addListener(this.listener = new IEventListener() {

            public void onEvent(IEvent e) {

                DebuggerListener.this.onDebugEvent(e);

            }

        });

    }


    public IDebuggerUnit getDebugger() {

        return this.dbg;

    }


    public RcpClientContext getContext() {

        return this.context;

    }


    public Document getLog() {

        return this.eventLog;

    }


    private void close() {

        if (this.listener != null) {

            this.dbg.removeListener(this.listener);

        }

    }


    private void onDebugEvent(final IEvent e) {

        if (e.getSource() != this.dbg) {

            return;

        }


        UIExecutor.async(UI.getDisplay(), new UIRunnable() {


            public void runi() {


                String addition = String.format("%s", new Object[]{e.getType()});

                if (e.getData() != null) {

                    addition = addition + String.format(" / %s", new Object[]{e.getData()});

                }

                addition = addition + "\n";

                DebuggerListener.this.eventLog.append(addition);

                ClientNotification n;

                if (e.getType() == J.DbgClientNotification) {

                    n = (ClientNotification) e.getData();

                    String message = n.getMessage();

                    int level = RcpClientContext.clientNotificationLevelToLoggerLevel(n.getLevel());

                    UI.log(level, null, "Debugger Notification", message);


                } else if (e.getType() == J.DbgAttach) {

                    for (ICodeUnit targetUnit : DebuggerListener.this.dbg.getPotentialDebuggees()) {

                        if ((targetUnit instanceof INativeCodeUnit)) {


                            targetUnit.notifyListeners(new JebEvent(J.UnitChange));

                        }


                    }

                } else if (e.getType() == J.DbgDetach) {

                    for (ICodeUnit targetUnit : DebuggerListener.this.dbg.getPotentialDebuggees()) {

                        DebuggerListener.this.context.getUIState(targetUnit).setProgramCounter(null);

                        if ((targetUnit instanceof INativeCodeUnit)) {


                            targetUnit.notifyListeners(new JebEvent(J.UnitChange));

                        }

                    }

                } else {

                    IUnit unit;

                    if (e.getType() == J.DbgRun) {

                        if ((DebuggerListener.this.dbg instanceof IDebuggerUnit)) {
                            Iterator<? extends ICodeUnit> iterator = DebuggerListener.this.dbg.getPotentialDebuggees().iterator();
                            while (iterator.hasNext()) {
                                unit = (IUnit)iterator.next();
                                DebuggerListener.this.context.getUIState(unit).setProgramCounter(null);
                            }
                        }

                    } else if (e.getType() != J.DbgPause) {
                        List<? extends IDebuggerBreakpoint> realBreakpoints;

                        UIState uiState;

                        IDebuggerBreakpoint bp;

                        UnitAddress<ICodeUnit> ua;

                        if ((e.getType() == J.DbgBreakpointSet) || (e.getType() == J.DbgBreakpointUnset)) {

                            realBreakpoints = DebuggerListener.this.dbg.getBreakpoints();

                            if (realBreakpoints != null) {

                                for (IUnit target : DebuggerListener.this.dbg.getPotentialDebuggees()) {

                                    uiState = DebuggerListener.this.context.getUIState(target);


                                    List<String> validAddresses = new ArrayList();

                                    for (Iterator localIterator = realBreakpoints.iterator(); localIterator.hasNext(); ) {
                                        bp = (IDebuggerBreakpoint) localIterator.next();

                                        String dbgAddress = bp.getAddress();

                                        ua = DebuggerListener.this.dbg.convertToUnitAddress(dbgAddress);

                                        if ((ua != null) && (ua.getUnit() == target)) {

                                            String addr = ua.getAddress();

                                            validAddresses.add(addr);

                                            if ((!uiState.isBreakpoint(addr)) ||
                                                    (uiState.isBreakpointEnabled(addr) != bp.isEnabled())) {

                                                uiState.setBreakpoint(addr, bp.isEnabled());

                                            }

                                        }

                                    }


                                    List<String> invalidAddresses = new ArrayList();

                                    for (String address : uiState.getBreakpoints().keySet()) {

                                        if (!validAddresses.contains(address)) {

                                            ((List) invalidAddresses).add(address);

                                        }

                                    }

                                    for (String address : invalidAddresses) {

                                        uiState.removeBreakpoint(address);

                                    }


                                }

                            }

                        } else if ((e.getType() == J.DbgTargetEvent) && ((e.getData() instanceof IDebuggerEventData))) {

                            IDebuggerEventData data = (IDebuggerEventData) e.getData();
                            ua = data.getUnitAddress();

                            IUnit targetUnit = ua == null ? null : ua.getUnit();

                            String targetAddress = ua == null ? null : ua.getAddress();

                            if ((targetUnit == null) || (targetAddress == null)) {

                                DebuggerListener.logger.debug("Target unit/address associated with the debuggee event cannot be determined", new Object[0]);


                                DebuggerListener.this.context.refreshHandlersStates();

                                return;

                            }


                            if ((targetUnit instanceof ICodeUnit)) {

                                uiState = DebuggerListener.this.context.getUIState(targetUnit);

                                if ((uiState.isTemporaryBreakpoint(targetAddress)) &&
                                        (uiState.removeTemporaryBreakpoint(targetAddress))) {

                                    bp = DebuggerListener.this.dbg.getBreakpoint(targetAddress, (ICodeUnit) targetUnit);

                                    if (bp != null) {

                                        DebuggerListener.this.dbg.clearBreakpoint(bp);

                                    }

                                }

                            }


                            if (data.getThreadId() != 0L) {

                                DebuggerListener.this.dbg.setDefaultThread(data.getThreadId());

                            }


                            PartManager pman = DebuggerListener.this.context.getPartManager();

                            IMPart activePart = pman.getActivePart();

                            Object activeObject = activePart == null ? null : activePart.getManager();


                            pman.create(targetUnit, true);


                            int focusedCount = 0;

                            for (UnitPartManager o : pman.getPartManagersForUnit(targetUnit)) {

                                IRcpUnitFragment fragment = o.getActiveFragment();

                                if (fragment != null) {

                                    DebuggerListener.logger.i("Setting active address: %s", new Object[]{targetAddress});

                                    if (fragment.setActiveAddress(targetAddress)) {

                                        o.setFocus();

                                        focusedCount++;

                                    }

                                }

                            }


                            if ((activeObject instanceof TerminalPartManager)) {

                                TerminalPartManager consolePart = (TerminalPartManager) activeObject;

                                consolePart.setFocus();

                            }


                            if (focusedCount == 0) {

                                if ((data.getType() == DebuggerEventType.EXCEPTION) || (data.getType() == DebuggerEventType.SIGNAL)) {

                                    UI.warn(null, "Event generated by debugged target", "An exception occurred in unreachable code. The process was paused at:\n\n" + targetAddress);

                                } else {

                                    UI.warn(null, "Event generated by debugged target", "The following address cannot be navigated to:\n\n" + targetAddress);

                                }


                            } else {

                                DebuggerListener.this.context.getUIState(targetUnit).setProgramCounter(targetAddress);

                            }


                            ((JebEvent) e).setStopPropagation(true);

                        }

                    }

                }

                DebuggerListener.this.context.refreshHandlersStates();

            }

        });

    }

}


