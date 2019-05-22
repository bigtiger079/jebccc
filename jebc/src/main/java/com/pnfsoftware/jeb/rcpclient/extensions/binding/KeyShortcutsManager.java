package com.pnfsoftware.jeb.rcpclient.extensions.binding;

import com.pnfsoftware.jeb.core.properties.impl.SimplePropertyManager;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;

public class KeyShortcutsManager {
    private static final ILogger logger = GlobalLog.getLogger(KeyShortcutsManager.class);
    SimplePropertyManager pm;
    Map<String, Integer> map = new HashMap<>();
    Map<Integer, String> rmap = new HashMap<>();

    public KeyShortcutsManager(SimplePropertyManager pm) {
        this.pm = pm;
        for (String actionId : pm.getConfiguration().getAllPropertyKeys()) {
            if (Strings.isBlank(actionId)) {
                logger.error("Action id is blank \"%s\"", actionId);
            }
            int keycode = processEntry(actionId);
            if (keycode == 0) {
                logger.error("Illegal action definition \"%s=%s\"", actionId, pm.getString(actionId));
            }
            if (this.map.containsKey(actionId)) {
                logger.error("Action \"%s\" is already defined", actionId);
            }
            if (this.rmap.containsKey(keycode)) {
                logger.error("Action \"%s\" is attempting to use a key reserved by action \"%s\"", actionId, this.rmap.get(keycode));
            }
            this.map.put(actionId, keycode);
            this.rmap.put(keycode, actionId);
        }
    }

    public boolean isReserved(int keycode) {
        return getActionIdForKeycode(keycode) != null;
    }

    public String getActionIdForKeycode(int keycode) {
        return this.rmap.get(keycode);
    }

    public int getShortcutKeycode(String actionId) {
        Integer r = this.map.get(actionId);
        return r == null ? 0 : r;
    }

    private int processEntry(String actionId) {
        if (Strings.isBlank(actionId)) {
            return 0;
        }
        String s = this.pm.getString(actionId);
        if (Strings.isBlank(s)) {
            return 0;
        }
        KeyStroke ks;
        try {
            ks = KeyStroke.getInstance(s.trim());
        } catch (ParseException e) {
            return 0;
        }
        return ks.getModifierKeys() | ks.getNaturalKey();
    }
}
