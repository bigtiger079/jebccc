/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import java.util.HashMap;
/*    */ import java.util.Map;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class Dictionary
        /*    */ {
    /* 19 */   private Map<Object, Object> map = new HashMap();

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Dictionary() {
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Dictionary(Object key, Object value)
    /*    */ {
        /* 34 */
        set(key, value);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Dictionary(Map<Object, Object> items)
    /*    */ {
        /* 43 */
        this.map.putAll(items);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Object set(Object key, Object value)
    /*    */ {
        /* 54 */
        return this.map.put(key, value);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Object get(Object key)
    /*    */ {
        /* 64 */
        return this.map.get(key);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public <T> T get(Object key, Class<T> c)
    /*    */ {
        /* 76 */
        return (T) this.map.get(key);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\Dictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */