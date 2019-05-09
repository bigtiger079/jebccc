package com.pnfsoftware.jeb.rcpclient.extensions.binding;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.rcpclient.AllHandlers;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class KeyAcceleratorManager {
    private static final ILogger logger = GlobalLog.getLogger(KeyAcceleratorManager.class);
    private Display display;
    private WeakIdentityHashMap<Shell, Integer> shellmap = new WeakIdentityHashMap();
    private Listener filter;
    private boolean disregardIncomingTraversalKeyDown;
    private Map<Integer, ActionEx> keyToHandlers = new HashMap();

    public KeyAcceleratorManager(Display display) {
        this.display = display;
        for (Shell shell : display.getShells()) {
            processShell(shell);
        }
        this.filter = new Listener() {
            public void handleEvent(Event event) {
                if (event.type == 1) {
                    if (KeyAcceleratorManager.this.disregardIncomingTraversalKeyDown) {
                        KeyAcceleratorManager.this.disregardIncomingTraversalKeyDown = false;
                        if ((event.keyCode == 27) || (event.keyCode == 9)) {
                            return;
                        }
                    }
                    if (KeyAcceleratorManager.this.processKey(event)) {
                        event.type = 0;
                        event.doit = false;
                    }
                } else if (event.type == 31) {
                    if (KeyAcceleratorManager.this.processKey(event)) {
                        event.type = 0;
                        event.doit = false;
                        KeyAcceleratorManager.this.disregardIncomingTraversalKeyDown = true;
                    }
                } else if (event.type == 26) {
                    if ((event.widget instanceof Shell)) {
                        Shell shell = (Shell) event.widget;
                        KeyAcceleratorManager.this.processShell(shell);
                    }
                }
            }
        };
        display.addFilter(1, this.filter);
        display.addFilter(31, this.filter);
        display.addFilter(26, this.filter);
    }

    public void dispose() {
        this.display.removeFilter(1, this.filter);
        this.display.removeFilter(31, this.filter);
        this.display.removeFilter(26, this.filter);
    }

    private void processShell(Shell shell) {
        if (this.shellmap.get(shell) != null) {
            return;
        }
        this.shellmap.put(shell, Integer.valueOf(0));
    }

    private boolean processKey(Event event) {
        if ((event.widget instanceof Control)) {
            if ((event.keyCode & SWT.MODIFIER_MASK) == 0) {
                ActionEx h = (ActionEx) this.keyToHandlers.get(Integer.valueOf(event.stateMask | event.keyCode));
                if (h == null) {
                    h = (ActionEx) this.keyToHandlers.get(Integer.valueOf(event.character));
                }
                if (h != null) {
                    if ((h instanceof JebBaseHandler)) {
                        JebBaseHandler h2 = AllHandlers.getInstance().create(((JebBaseHandler) h).getClass());
                        if (h2 != null) {
                            h = h2;
                        }
                    }
                    Control ctl = (Control) event.widget;
                    if ((h.checkExecutionContext(ctl)) && (h.canExecute())) {
                        UIExecutor.sync(this.display, h);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void registerHandlers(Collection<? extends ActionEx> handlers) {
        for (ActionEx handler : handlers) {
            registerHandler(handler);
        }
    }

    public void registerHandler(ActionEx handler) {
        for (Iterator localIterator = handler.getExtraAccelerators().iterator(); localIterator.hasNext(); ) {
            int keycode = ((Integer) localIterator.next()).intValue();
            ActionEx h0 = (ActionEx) this.keyToHandlers.get(Integer.valueOf(keycode));
            if (h0 != null) {
                if (h0 != handler) {
                    logger.error("The accelerator %s is already used by handler %s (handler %s cannot steal it)", new Object[]{KeyStroke.getInstance(keycode & 0xFEFF0000, keycode & 0x100FFFF).toString(), h0.getClass().getSimpleName(), handler.getClass().getSimpleName()});
                    if (Licensing.isDebugBuild()) {
                        throw new RuntimeException("Keyboard shortcut conflict must be resolved in debug mode");
                    }
                }
            } else this.keyToHandlers.put(Integer.valueOf(keycode), handler);
        }
    }

    public void unregisterHandler(ActionEx handler) {
        List<Integer> tbd = new ArrayList<>();
        for (Map.Entry<Integer, ActionEx> e : this.keyToHandlers.entrySet()) {
            if (e.getValue() == handler) {
                tbd.add(e.getKey());
            }
        }
        Iterator<Integer> iterator = tbd.iterator();
        while (iterator.hasNext()) {
            int keycode = iterator.next();
            this.keyToHandlers.remove(keycode);
        }
//        for (??? =tbd.iterator(); ???.hasNext();){
//            int keycode = ((Integer) ? ??.next()).intValue();
//
//            this.keyToHandlers.remove(Integer.valueOf(keycode));
//
//        }
    }
}


