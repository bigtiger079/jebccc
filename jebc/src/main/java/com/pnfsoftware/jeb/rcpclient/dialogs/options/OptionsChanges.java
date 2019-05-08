package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class OptionsChanges {
    private Map<String, Changes> changes = new HashMap();

    public static class Changes {
        IPropertyManager pm;
        Map<String, Object> changeList = new HashMap();
        List<Listener> listeners = new ArrayList();

        public Changes(IPropertyManager pm) {
            this.pm = pm;
        }

        public boolean hasChanges() {
            return !this.changeList.isEmpty();
        }

        public void addChange(String property, Object value) {
            if (property.startsWith(".")) {
                property = property.substring(1);
            }
            if (Objects.equals(this.pm.getValue(property), value)) {
                removeChange(property, value);
            } else {
                this.changeList.put(property, value);
                if (!this.listeners.isEmpty()) {
                    for (Listener l : this.listeners) {
                        Event e = new Event();
                        e.data = new Object[]{property, value};
                        l.handleEvent(e);
                    }
                }
            }
        }

        public void removeChange(String property, Object value) {
            if (property.startsWith(".")) {
                property = property.substring(1);
            }
            this.changeList.remove(property);
            if (!this.listeners.isEmpty()) {
                for (Listener l : this.listeners) {
                    Event e = new Event();
                    e.data = new Object[]{property, value};
                    l.handleEvent(e);
                }
            }
        }

        public void applyChanges() {
            for (Map.Entry<String, Object> e : this.changeList.entrySet()) {
                this.pm.setValue((String) e.getKey(), e.getValue(), false);
            }
        }

        public Boolean getBoolean(String name) {
            return Boolean.valueOf(this.pm.getBoolean(name));
        }

        public String getString(String name) {
            return this.pm.getString(name);
        }

        public Object getValue(String name) {
            return this.pm.getValue(name);
        }

        public Object getChange(Object key) {
            return this.changeList.get(key);
        }

        public Map<String, Object> getChanges() {
            return this.changeList;
        }

        public IPropertyManager getPropertyManager() {
            return this.pm;
        }
    }

    public void addPropertyManager(String name, IPropertyManager pm) {
        this.changes.put(name, new Changes(pm));
    }

    public void addChange(String propertyManagerName, String key, Object value) {
        Changes c = (Changes) this.changes.get(propertyManagerName);
        if (c != null) {
            c.addChange(key, value);
        }
    }

    public void removeChange(String propertyManagerKey, String key, Object value) {
        Changes c = (Changes) this.changes.get(propertyManagerKey);
        if (c != null) {
            c.removeChange(key, value);
        }
    }

    public void saveAllChanges(ITelemetryDatabase tele) {
        for (Changes c : this.changes.values()) {
            if (c.hasChanges()) {
                c.applyChanges();
                if (tele != null) {
                    for (Map.Entry<String, Object> e : c.getChanges().entrySet()) {
                        String key = (String) e.getKey();
                        Object value = e.getValue();
                        if ((key != null) && (value != null)) {
                            tele.record("optionChange", "name", key, "value", Strings.toString(value));
                        }
                    }
                }
            }
        }
    }

    public Changes get(String key) {
        return (Changes) this.changes.get(key);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\options\OptionsChanges.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */