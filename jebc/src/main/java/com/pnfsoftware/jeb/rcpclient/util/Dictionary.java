
package com.pnfsoftware.jeb.rcpclient.util;


import java.util.HashMap;
import java.util.Map;


public class Dictionary {
    private Map<Object, Object> map = new HashMap();


    public Dictionary() {
    }


    public Dictionary(Object key, Object value) {

        set(key, value);

    }


    public Dictionary(Map<Object, Object> items) {

        this.map.putAll(items);

    }


    public Object set(Object key, Object value) {

        return this.map.put(key, value);

    }


    public Object get(Object key) {

        return this.map.get(key);

    }


    public <T> T get(Object key, Class<T> c) {

        return (T) this.map.get(key);

    }

}


