package com.pnfsoftware.jeb.rcpclient.extensions.binding;

import com.pnfsoftware.jeb.core.properties.IConfiguration;
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
    Map<String, Integer> map = new HashMap();
    Map<Integer, String> rmap = new HashMap();

    public KeyShortcutsManager(SimplePropertyManager pm) {
        this.pm = pm;
        for (String actionId : pm.getConfiguration().getAllPropertyKeys()) {
            if (Strings.isBlank(actionId)) {
                logger.error("Action id is blank \"%s\"", new Object[]{actionId});
            }
            int keycode = processEntry(actionId);
            if (keycode == 0) {
                logger.error("Illegal action definition \"%s=%s\"", new Object[]{actionId, pm.getString(actionId)});
            }
            if (this.map.containsKey(actionId)) {
                logger.error("Action \"%s\" is already defined", new Object[]{actionId});
            }
            if (this.rmap.containsKey(Integer.valueOf(keycode))) {
                logger.error("Action \"%s\" is attempting to use a key reserved by action \"%s\"", new Object[]{actionId, this.rmap.get(Integer.valueOf(keycode))});
            }
            this.map.put(actionId, Integer.valueOf(keycode));
            this.rmap.put(Integer.valueOf(keycode), actionId);
        }
    }

    public boolean isReserved(int keycode) {
        return getActionIdForKeycode(keycode) != null;
    }

    public String getActionIdForKeycode(int keycode) {
        return this.rmap.get(Integer.valueOf(keycode));
    }

    public int getShortcutKeycode(String actionId) {
        Integer r = this.map.get(actionId);
        return r == null ? 0 : r.intValue();
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
        int keycode = ks.getModifierKeys() | ks.getNaturalKey();
        return keycode;
    }
}
