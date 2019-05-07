/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.IEnginesContext;
/*     */ import com.pnfsoftware.jeb.core.events.AbstractQuestionNotification;
/*     */ import com.pnfsoftware.jeb.core.events.ClientNotification;
/*     */ import com.pnfsoftware.jeb.core.events.ExceptionNotification;
/*     */ import com.pnfsoftware.jeb.core.events.J;
/*     */ import com.pnfsoftware.jeb.core.events.JebEvent;
/*     */ import com.pnfsoftware.jeb.core.events.QuestionNotificationYesNo;
/*     */ import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.units.ICommandInterpreter;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitInterpreter;
/*     */ import com.pnfsoftware.jeb.core.units.UnitUtil;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.MultiInterpreter;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.util.collect.ItemHistory;
/*     */ import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.events.IEventSource;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
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
/*     */ public class EnginesListener
        /*     */ implements IEventListener
        /*     */ {
    /*  47 */   private static final ILogger logger = GlobalLog.getLogger(EnginesListener.class);
    /*     */   private static EnginesListener instance;
    /*     */   private RcpClientContext context;

    /*     */
    /*     */
    public static synchronized EnginesListener initialize(RcpClientContext context, IEnginesContext engctx) {
        /*  52 */
        if (instance != null) {
            /*  53 */
            throw new RuntimeException("The context's engines listener was already initialized");
            /*     */
        }
        /*     */
        /*  56 */
        instance = new EnginesListener(context);
        /*  57 */
        engctx.addListener(instance);
        /*  58 */
        return instance;
        /*     */
    }

    /*     */
    /*     */
    /*  62 */   private WeakIdentityHashMap<IUnit, Integer> interpreterIds = new WeakIdentityHashMap();

    /*     */
    /*     */
    private EnginesListener(RcpClientContext context) {
        /*  65 */
        this.context = context;
        /*     */
    }

    /*     */
    /*     */
    public void onEvent(IEvent e)
    /*     */ {
        /*  70 */
        if (!(e instanceof JebEvent)) {
            /*  71 */
            return;
            /*     */
        }
        /*  73 */
        J type = ((JebEvent) e).getType();
        /*     */
        /*  75 */
        if (type == J.UnitStatusChanged) {
            /*  76 */
            JebEvent event = (JebEvent) e;
            /*  77 */
            IUnit unit = (IUnit) event.getSource();
            /*  78 */
            logger.debug("Unit \"%s\" status has changed to: %s", new Object[]{unit.getName(), unit.getStatus()});
            /*     */
        }
        /*  80 */
        else if (type == J.UnitCreated) {
            /*  81 */
            JebEvent event = (JebEvent) e;
            /*  82 */
            final IUnit unit = (IUnit) event.getData();
            /*  83 */
            logger.debug("Unit \"%s\" (%s) was created", new Object[]{unit.getName(), unit.getFormatType()});
            /*     */
            /*     */
            /*  86 */
            UIExecutor.sync(this.context.getDisplay(), new UIRunnable()
                    /*     */ {
                /*     */
                public void runi()
                /*     */ {
                    /*  90 */
                    if (UnitUtil.isTopLevelUnit(unit)) {
                        /*  91 */
                        IArtifact a = (IArtifact) unit.getParent();
                        /*  92 */
                        long artifactSize = a.getInput() == null ? 0L : a.getInput().getCurrentSize();
                        /*  93 */
                        EnginesListener.this.context.getTelemetry().record("topLevelUnitCreated", "unitType", unit.getFormatType(), "artifactSize", "" + artifactSize);
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*  97 */
                    if ((unit instanceof IDebuggerUnit)) {
                        /*  98 */
                        IDebuggerUnit dbg = (IDebuggerUnit) unit;
                        /*  99 */
                        DebuggerListener.listenTo(dbg, EnginesListener.this.context);
                        /*     */
                    }
                    /* 101 */
                    else if ((unit instanceof IDecompilerUnit)) {
                        /* 102 */
                        IDecompilerUnit decomp = (IDecompilerUnit) unit;
                        /* 103 */
                        DecompilerListener.listenTo(decomp, EnginesListener.this.context);
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 107 */
                    List<IUnitInterpreter> interpreters = unit.getInterpreters();
                    /* 108 */
                    if ((interpreters != null) && (!interpreters.isEmpty())) {
                        /* 109 */
                        MultiInterpreter mi = EnginesListener.this.context.getMasterInterpreter();
                        /*     */
                        /* 111 */
                        int interpreterIndex = mi.registerInterpreter((ICommandInterpreter) interpreters.get(0));
                        /* 112 */
                        if (interpreterIndex >= 0) {
                            /* 113 */
                            EnginesListener.this.interpreterIds.put(unit, Integer.valueOf(interpreterIndex));
                            /* 114 */
                            EnginesListener.logger.info("A command interpreter for unit \"%s\" was registered to the console view\nSwitch to it by issuing the \"use %d\" command", new Object[]{unit
                                    /*     */
                                    /*     */
                                    /* 117 */.getName(), Integer.valueOf(interpreterIndex)});
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /* 123 */
        else if (type == J.UnitDestroyed) {
            /* 124 */
            JebEvent event = (JebEvent) e;
            /* 125 */
            final IUnit unit = (IUnit) event.getData();
            /* 126 */
            logger.debug("Unit \"%s\" was destroyed", new Object[]{unit.getName()});
            /*     */
            /*     */
            /* 129 */
            UIExecutor.sync(this.context.getDisplay(), new UIRunnable()
                    /*     */ {
                /*     */
                public void runi()
                /*     */ {
                    /* 133 */
                    PartManager pman = EnginesListener.this.context.getPartManager();
                    /* 134 */
                    for (IMPart part : pman.getPartsForUnit(unit)) {
                        /* 135 */
                        pman.unbindUnitPart(part);
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 139 */
                    Integer interpreterIndex = (Integer) EnginesListener.this.interpreterIds.get(unit);
                    /* 140 */
                    if (interpreterIndex != null) {
                        /* 141 */
                        MultiInterpreter mi = EnginesListener.this.context.getMasterInterpreter();
                        /* 142 */
                        mi.unregisterInterpreter(interpreterIndex.intValue());
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /* 152 */
                    if ((unit instanceof IDecompilerUnit)) {
                        /* 153 */
                        IDecompilerUnit decomp = (IDecompilerUnit) unit;
                        /* 154 */
                        DecompilerListener.stopListening(decomp);
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 158 */
                    ItemHistory<GlobalPosition> history = EnginesListener.this.context.getPartManager().getGlobalPositionHistory();
                    /* 159 */
                    List<GlobalPosition> globalPositionList = history.getList();
                    /* 160 */
                    if (!globalPositionList.isEmpty()) {
                        /* 161 */
                        List<GlobalPosition> toRemove = new ArrayList();
                        /* 162 */
                        for (GlobalPosition pos : globalPositionList) {
                            /* 163 */
                            if (pos.getUnit() == unit) {
                                /* 164 */
                                toRemove.add(pos);
                                /*     */
                            }
                            /*     */
                        }
                        /* 167 */
                        if (!toRemove.isEmpty()) {
                            /* 168 */
                            for (GlobalPosition rem : toRemove) {
                                /* 169 */
                                history.remove(rem);
                                /*     */
                            }
                            /*     */
                            /* 172 */
                            EnginesListener.this.context.refreshHandlersStates();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /* 178 */
        else if (type == J.Notification) {
            /* 179 */
            if (!(e.getData() instanceof ClientNotification)) {
                /* 180 */
                return;
                /*     */
            }
            /* 182 */
            ClientNotification n = (ClientNotification) e.getData();
            /* 183 */
            processNotification(this.context, e.getSource(), n);
            /*     */
        }
        /* 185 */
        else if (type == J.ProjectUnloaded)
            /*     */ {
            /* 187 */
            UIExecutor.sync(this.context.getDisplay(), new UIRunnable()
                    /*     */ {
                /*     */
                public void runi()
                /*     */ {
                    /* 191 */
                    EnginesListener.this.context.getViewManager().getGlobalPositionHistory().reset();
                    /*     */
                    /* 193 */
                    EnginesListener.this.context.refreshHandlersStates();
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    static void processNotification(final RcpClientContext context, final IEventSource source, ClientNotification notif)
    /*     */ {
        /* 201 */
        if ((notif instanceof AbstractQuestionNotification)) {
            /* 202 */
            AbstractQuestionNotification<?> n = (AbstractQuestionNotification) notif;
            /* 203 */
            UIExecutor.sync(UI.getDisplay(), new UIRunnable()
                    /*     */ {
                /*     */
                public void runi() {
                    /* 206 */
                    if ((this.n instanceof QuestionNotificationYesNo)) {
                        /* 207 */
                        boolean r = UI.question(null, "Question", this.val$n.getMessage());
                        /* 208 */
                        ((QuestionNotificationYesNo) this.val$n).setResponse(Boolean.valueOf(r));
                        /*     */
                        /*     */
                    }
                    /*     */
                    else {
                    }
                    /*     */
                }
                /*     */
                /*     */
            });
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 218 */
            UIExecutor.async(UI.getDisplay(), new UIRunnable()
                    /*     */ {
                /*     */
                public void runi() {
                    /* 221 */
                    if ((this.val$notif instanceof ExceptionNotification)) {
                        /* 222 */
                        Throwable t = ((ExceptionNotification) this.val$notif).getThrowable();
                        /* 223 */
                        if (t == null) {
                            /* 224 */
                            return;
                            /*     */
                        }
                        /* 226 */
                        int flags = ((ExceptionNotification) this.val$notif).getFlags();
                        /* 227 */
                        boolean silent = (flags & 0x1) != 0;
                        /* 228 */
                        boolean forceUpload = (flags & 0x2) != 0;
                        /* 229 */
                        boolean doNotUploadSample = (flags & 0x4) != 0;
                        /* 230 */
                        Map<String, Object> extramap = ((ExceptionNotification) this.val$notif).getExtraData();
                        /* 231 */
                        context.getErrorHandler().processThrowable(t, !silent, forceUpload, doNotUploadSample, null, extramap, null);
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /* 235 */
                        String msg = this.val$notif.getMessage();
                        /* 236 */
                        msg = String.format("Notification from: %s\n\nMessage: %s", new Object[]{source, msg});
                        /* 237 */
                        int level = RcpClientContext.clientNotificationLevelToLoggerLevel(this.val$notif.getLevel());
                        /* 238 */
                        UI.log(level, null, "Notification", msg);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\EnginesListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */