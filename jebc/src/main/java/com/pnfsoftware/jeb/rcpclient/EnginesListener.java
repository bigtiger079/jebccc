package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.events.AbstractQuestionNotification;
import com.pnfsoftware.jeb.core.events.ClientNotification;
import com.pnfsoftware.jeb.core.events.ExceptionNotification;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.events.JebEvent;
import com.pnfsoftware.jeb.core.events.QuestionNotificationYesNo;
import com.pnfsoftware.jeb.core.units.ICommandInterpreter;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitInterpreter;
import com.pnfsoftware.jeb.core.units.UnitUtil;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.MultiInterpreter;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.util.collect.ItemHistory;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.events.IEventSource;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnginesListener implements IEventListener {
    private static final ILogger logger = GlobalLog.getLogger(EnginesListener.class);
    private static EnginesListener instance;
    private RcpClientContext context;

    public static synchronized EnginesListener initialize(RcpClientContext context, IEnginesContext engctx) {
        if (instance != null) {
            throw new RuntimeException("The context's engines listener was already initialized");
        }
        instance = new EnginesListener(context);
        engctx.addListener(instance);
        return instance;
    }

    private WeakIdentityHashMap<IUnit, Integer> interpreterIds = new WeakIdentityHashMap();

    private EnginesListener(RcpClientContext context) {
        this.context = context;
    }

    public void onEvent(IEvent e) {
        if (!(e instanceof JebEvent)) {
            return;
        }
        J type = ((JebEvent) e).getType();
        if (type == J.UnitStatusChanged) {
            JebEvent event = (JebEvent) e;
            IUnit unit = (IUnit) event.getSource();
            logger.debug("Unit \"%s\" status has changed to: %s", unit.getName(), unit.getStatus());
        } else if (type == J.UnitCreated) {
            JebEvent event = (JebEvent) e;
            final IUnit unit = (IUnit) event.getData();
            logger.debug("Unit \"%s\" (%s) was created", unit.getName(), unit.getFormatType());
            UIExecutor.sync(this.context.getDisplay(), new UIRunnable() {
                public void runi() {
                    if (UnitUtil.isTopLevelUnit(unit)) {
                        IArtifact a = (IArtifact) unit.getParent();
                        long artifactSize = a.getInput() == null ? 0L : a.getInput().getCurrentSize();
                        EnginesListener.this.context.getTelemetry().record("topLevelUnitCreated", "unitType", unit.getFormatType(), "artifactSize", "" + artifactSize);
                    }
                    if ((unit instanceof IDebuggerUnit)) {
                        IDebuggerUnit dbg = (IDebuggerUnit) unit;
                        DebuggerListener.listenTo(dbg, EnginesListener.this.context);
                    } else if ((unit instanceof IDecompilerUnit)) {
                        IDecompilerUnit decomp = (IDecompilerUnit) unit;
                        DecompilerListener.listenTo(decomp, EnginesListener.this.context);
                    }
                    List<IUnitInterpreter> interpreters = unit.getInterpreters();
                    if ((interpreters != null) && (!interpreters.isEmpty())) {
                        MultiInterpreter mi = EnginesListener.this.context.getMasterInterpreter();
                        int interpreterIndex = mi.registerInterpreter(interpreters.get(0));
                        if (interpreterIndex >= 0) {
                            EnginesListener.this.interpreterIds.put(unit, interpreterIndex);
                            EnginesListener.logger.info("A command interpreter for unit \"%s\" was registered to the console view\nSwitch to it by issuing the \"use %d\" command", unit.getName(), interpreterIndex);
                        }
                    }
                }
            });
        } else if (type == J.UnitDestroyed) {
            JebEvent event = (JebEvent) e;
            final IUnit unit = (IUnit) event.getData();
            logger.debug("Unit \"%s\" was destroyed", unit.getName());
            UIExecutor.sync(this.context.getDisplay(), new UIRunnable() {
                public void runi() {
                    PartManager pman = EnginesListener.this.context.getPartManager();
                    for (IMPart part : pman.getPartsForUnit(unit)) {
                        pman.unbindUnitPart(part);
                    }
                    Integer interpreterIndex = EnginesListener.this.interpreterIds.get(unit);
                    if (interpreterIndex != null) {
                        MultiInterpreter mi = EnginesListener.this.context.getMasterInterpreter();
                        mi.unregisterInterpreter(interpreterIndex);
                    }
                    if ((unit instanceof IDecompilerUnit)) {
                        IDecompilerUnit decomp = (IDecompilerUnit) unit;
                        DecompilerListener.stopListening(decomp);
                    }
                    ItemHistory<GlobalPosition> history = EnginesListener.this.context.getPartManager().getGlobalPositionHistory();
                    List<GlobalPosition> globalPositionList = history.getList();
                    if (!globalPositionList.isEmpty()) {
                        List<GlobalPosition> toRemove = new ArrayList<>();
                        for (GlobalPosition pos : globalPositionList) {
                            if (pos.getUnit() == unit) {
                                toRemove.add(pos);
                            }
                        }
                        if (!toRemove.isEmpty()) {
                            for (GlobalPosition rem : toRemove) {
                                history.remove(rem);
                            }
                            EnginesListener.this.context.refreshHandlersStates();
                        }
                    }
                }
            });
        } else if (type == J.Notification) {
            if (!(e.getData() instanceof ClientNotification)) {
                return;
            }
            ClientNotification n = (ClientNotification) e.getData();
            processNotification(this.context, e.getSource(), n);
        } else if (type == J.ProjectUnloaded) {
            UIExecutor.sync(this.context.getDisplay(), new UIRunnable() {
                public void runi() {
                    EnginesListener.this.context.getViewManager().getGlobalPositionHistory().reset();
                    EnginesListener.this.context.refreshHandlersStates();
                }
            });
        }
    }

    static void processNotification(final RcpClientContext context, final IEventSource source, ClientNotification notif) {
        if ((notif instanceof AbstractQuestionNotification)) {
            AbstractQuestionNotification<?> n = (AbstractQuestionNotification) notif;
            UIExecutor.sync(UI.getDisplay(), new UIRunnable() {
                public void runi() {
                    if ((n instanceof QuestionNotificationYesNo)) {
                        boolean r = UI.question(null, "Question", n.getMessage());
                        ((QuestionNotificationYesNo) n).setResponse(r);
                    } else {
                    }
                }
            });
        } else {
            UIExecutor.async(UI.getDisplay(), new UIRunnable() {
                public void runi() {
                    if ((notif instanceof ExceptionNotification)) {
                        Throwable t = ((ExceptionNotification) notif).getThrowable();
                        if (t == null) {
                            return;
                        }
                        int flags = ((ExceptionNotification) notif).getFlags();
                        boolean silent = (flags & 0x1) != 0;
                        boolean forceUpload = (flags & 0x2) != 0;
                        boolean doNotUploadSample = (flags & 0x4) != 0;
                        Map<String, Object> extramap = ((ExceptionNotification) notif).getExtraData();
                        context.getErrorHandler().processThrowable(t, !silent, forceUpload, doNotUploadSample, null, extramap, null);
                    } else {
                        String msg = notif.getMessage();
                        msg = String.format("Notification from: %s\n\nMessage: %s", source, msg);
                        int level = RcpClientContext.clientNotificationLevelToLoggerLevel(notif.getLevel());
                        UI.log(level, null, "Notification", msg);
                    }
                }
            });
        }
    }
}


