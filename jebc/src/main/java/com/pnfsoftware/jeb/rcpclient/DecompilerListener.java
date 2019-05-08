package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.core.events.ClientNotification;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.LogDocument;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Document;

public class DecompilerListener {
    private static final ILogger logger = GlobalLog.getLogger(DecompilerListener.class);
    private static Map<IDecompilerUnit, DecompilerListener> map = new IdentityHashMap();
    private int clientCount;

    public static synchronized DecompilerListener listenTo(IDecompilerUnit decomp, RcpClientContext context) {
        DecompilerListener listener = (DecompilerListener) map.get(decomp);
        if (listener == null) {
            listener = new DecompilerListener(decomp, context);
            map.put(decomp, listener);
        }
        listener.clientCount += 1;
        return listener;
    }

    public static synchronized boolean stopListening(IDecompilerUnit decomp) {
        DecompilerListener listener = (DecompilerListener) map.get(decomp);
        if (listener == null) {
            return false;
        }
        listener.clientCount -= 1;
        if (listener.clientCount <= 0) {
            listener.close();
            map.remove(decomp);
        }
        return true;
    }

    public static synchronized DecompilerListener get(IDecompilerUnit decomp) {
        return (DecompilerListener) map.get(decomp);
    }

    public static synchronized List<DecompilerListener> getAll() {
        return new ArrayList(map.values());
    }

    private IDecompilerUnit decomp;
    private RcpClientContext context;
    private IEventListener listener;
    private LogDocument eventLog;
    private Map<ISourceUnit, Integer> resetMap = new IdentityHashMap();

    private DecompilerListener(IDecompilerUnit decomp, RcpClientContext context) {
        this.decomp = decomp;
        this.context = context;
        this.eventLog = new LogDocument(1048576);
        decomp.addListener(this.listener = new IEventListener() {
            public void onEvent(IEvent e) {
                DecompilerListener.this.onDebugEvent(e);
            }
        });
    }

    public IDecompilerUnit getDecompiler() {
        return this.decomp;
    }

    public RcpClientContext getContext() {
        return this.context;
    }

    public Document getLog() {
        return this.eventLog;
    }

    private void close() {
        if (this.listener != null) {
            this.decomp.removeListener(this.listener);
        }
        pullResetUnits();
    }

    private void onDebugEvent(final IEvent e) {
        if (e.getSource() != this.decomp) {
            return;
        }
        UIExecutor.async(UI.getDisplay(), new UIRunnable() {
            public void runi() {
                DecompilerListener.this.processEvent(e);
            }
        });
    }

    private void processEvent(IEvent e) {
        String addition = String.format("%s", new Object[]{e.getType()});
        if (e.getData() != null) {
            addition = addition + String.format(" / %s", new Object[]{e.getData()});
        }
        addition = addition + "\n";
        this.eventLog.append(addition);
        if (e.getType() == J.DecompClientNotification) {
            ClientNotification n = (ClientNotification) e.getData();
            String message = n.getMessage();
            int level = RcpClientContext.clientNotificationLevelToLoggerLevel(n.getLevel());
            UI.log(level, null, "Decompiler Notification", message);
        } else if (e.getType() == J.DecompSrcUnitResetEvent) {
            ISourceUnit srcUnit = (ISourceUnit) e.getData();
            if (srcUnit != null) {
                logger.i("The following source unit was reset: %s", new Object[]{srcUnit});
                synchronized (this.resetMap) {
                    this.resetMap.put(srcUnit, null);
                }
            }
        }
    }

    public List<ISourceUnit> pullResetUnits() {
        synchronized (this.resetMap) {
            List<ISourceUnit> r = new ArrayList(this.resetMap.keySet());
            this.resetMap.clear();
            return r;
        }
    }
}


