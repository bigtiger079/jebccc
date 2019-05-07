/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.core.events.ClientNotification;
/*     */ import com.pnfsoftware.jeb.core.events.J;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.LogDocument;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.IdentityHashMap;
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
/*     */
/*     */ public class DecompilerListener
        /*     */ {
    /*  36 */   private static final ILogger logger = GlobalLog.getLogger(DecompilerListener.class);
    /*     */
    /*  38 */   private static Map<IDecompilerUnit, DecompilerListener> map = new IdentityHashMap();
    /*     */   private int clientCount;

    /*     */
    /*  41 */
    public static synchronized DecompilerListener listenTo(IDecompilerUnit decomp, RcpClientContext context) {
        DecompilerListener listener = (DecompilerListener) map.get(decomp);
        /*  42 */
        if (listener == null) {
            /*  43 */
            listener = new DecompilerListener(decomp, context);
            /*  44 */
            map.put(decomp, listener);
            /*     */
        }
        /*     */
        /*  47 */
        listener.clientCount += 1;
        /*  48 */
        return listener;
        /*     */
    }

    /*     */
    /*     */
    public static synchronized boolean stopListening(IDecompilerUnit decomp) {
        /*  52 */
        DecompilerListener listener = (DecompilerListener) map.get(decomp);
        /*  53 */
        if (listener == null) {
            /*  54 */
            return false;
            /*     */
        }
        /*     */
        /*  57 */
        listener.clientCount -= 1;
        /*  58 */
        if (listener.clientCount <= 0) {
            /*  59 */
            listener.close();
            /*  60 */
            map.remove(decomp);
            /*     */
        }
        /*  62 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public static synchronized DecompilerListener get(IDecompilerUnit decomp) {
        /*  66 */
        return (DecompilerListener) map.get(decomp);
        /*     */
    }

    /*     */
    /*     */
    public static synchronized List<DecompilerListener> getAll() {
        /*  70 */
        return new ArrayList(map.values());
        /*     */
    }

    /*     */
    /*     */
    /*     */   private IDecompilerUnit decomp;
    /*     */   private RcpClientContext context;
    /*     */   private IEventListener listener;
    /*     */   private LogDocument eventLog;
    /*  78 */   private Map<ISourceUnit, Integer> resetMap = new IdentityHashMap();

    /*     */
    /*     */
    private DecompilerListener(IDecompilerUnit decomp, RcpClientContext context) {
        /*  81 */
        this.decomp = decomp;
        /*  82 */
        this.context = context;
        /*  83 */
        this.eventLog = new LogDocument(1048576);
        /*     */
        /*  85 */
        decomp.addListener(this. = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /*  88 */
                DecompilerListener.this.onDebugEvent(e);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public IDecompilerUnit getDecompiler() {
        /*  94 */
        return this.decomp;
        /*     */
    }

    /*     */
    /*     */
    public RcpClientContext getContext() {
        /*  98 */
        return this.context;
        /*     */
    }

    /*     */
    /*     */
    public Document getLog() {
        /* 102 */
        return this.eventLog;
        /*     */
    }

    /*     */
    /*     */
    private void close() {
        /* 106 */
        if (this.listener != null) {
            /* 107 */
            this.decomp.removeListener(this.listener);
            /*     */
        }
        /*     */
        /*     */
        /* 111 */
        pullResetUnits();
        /*     */
    }

    /*     */
    /*     */
    private void onDebugEvent(final IEvent e) {
        /* 115 */
        if (e.getSource() != this.decomp) {
            /* 116 */
            return;
            /*     */
        }
        /*     */
        /* 119 */
        UIExecutor.async(UI.getDisplay(), new UIRunnable()
                /*     */ {
            /*     */
            public void runi() {
                /* 122 */
                DecompilerListener.this.processEvent(e);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    private void processEvent(IEvent e)
    /*     */ {
        /* 131 */
        String addition = String.format("%s", new Object[]{e.getType()});
        /* 132 */
        if (e.getData() != null) {
            /* 133 */
            addition = addition + String.format(" / %s", new Object[]{e.getData()});
            /*     */
        }
        /* 135 */
        addition = addition + "\n";
        /* 136 */
        this.eventLog.append(addition);
        /*     */
        /* 138 */
        if (e.getType() == J.DecompClientNotification) {
            /* 139 */
            ClientNotification n = (ClientNotification) e.getData();
            /* 140 */
            String message = n.getMessage();
            /* 141 */
            int level = RcpClientContext.clientNotificationLevelToLoggerLevel(n.getLevel());
            /* 142 */
            UI.log(level, null, "Decompiler Notification", message);
            /*     */
        }
        /* 144 */
        else if (e.getType() == J.DecompSrcUnitResetEvent) {
            /* 145 */
            ISourceUnit srcUnit = (ISourceUnit) e.getData();
            /* 146 */
            if (srcUnit != null) {
                /* 147 */
                logger.i("The following source unit was reset: %s", new Object[]{srcUnit});
                /* 148 */
                synchronized (this.resetMap) {
                    /* 149 */
                    this.resetMap.put(srcUnit, null);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public List<ISourceUnit> pullResetUnits() {
        /* 156 */
        synchronized (this.resetMap) {
            /* 157 */
            List<ISourceUnit> r = new ArrayList(this.resetMap.keySet());
            /* 158 */
            this.resetMap.clear();
            /* 159 */
            return r;
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\DecompilerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */