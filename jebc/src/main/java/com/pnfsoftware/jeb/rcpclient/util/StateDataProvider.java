/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import com.pnfsoftware.jeb.util.format.IAsciiable;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import java.util.HashMap;
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
/*    */ public class StateDataProvider
        /*    */ implements IPersistenceProvider, IAsciiable
        /*    */ {
    /* 22 */   private Map<String, String> map = new HashMap();

    /*    */
    /*    */
    public StateDataProvider() {
        /* 25 */
        this(null);
        /*    */
    }

    /*    */
    /*    */
    public StateDataProvider(String persistedData) {
        /* 29 */
        if (persistedData == null) {
            /* 30 */
            return;
            /*    */
        }
        /*    */
        /* 33 */
        this.map = Strings.decodeMap(persistedData);
        /*    */
    }

    /*    */
    /*    */
    public String encode()
    /*    */ {
        /* 38 */
        return Strings.encodeMap(this.map);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void save(String objectName, String encodedState)
    /*    */ {
        /* 44 */
        this.map.put(objectName, encodedState);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public String load(String objectName)
    /*    */ {
        /* 50 */
        return (String) this.map.get(objectName);
        /*    */
    }

    /*    */
    /*    */
    public void clear() {
        /* 54 */
        this.map.clear();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\StateDataProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */