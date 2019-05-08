package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.util.format.IAsciiable;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.HashMap;
import java.util.Map;

public class StateDataProvider implements IPersistenceProvider, IAsciiable {
    private Map<String, String> map = new HashMap();

    public StateDataProvider() {
        this(null);
    }

    public StateDataProvider(String persistedData) {
        if (persistedData == null) {
            return;
        }
        this.map = Strings.decodeMap(persistedData);
    }

    public String encode() {
        return Strings.encodeMap(this.map);
    }

    public void save(String objectName, String encodedState) {
        this.map.put(objectName, encodedState);
    }

    public String load(String objectName) {
        return (String) this.map.get(objectName);
    }

    public void clear() {
        this.map.clear();
    }
}


