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
/*    */ public class TextHistoryCollection
        /*    */ implements IAsciiable
        /*    */ {
    /* 22 */ Map<String, TextHistory> map = new HashMap();

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public boolean add(String name, TextHistory history)
    /*    */ {
        /* 29 */
        if (this.map.containsKey(name)) {
            /* 30 */
            return false;
            /*    */
        }
        /* 32 */
        this.map.put(name, history);
        /* 33 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public TextHistory get(String name) {
        /* 37 */
        TextHistory r = (TextHistory) this.map.get(name);
        /* 38 */
        if (r == null) {
            /* 39 */
            r = new TextHistory();
            /* 40 */
            this.map.put(name, r);
            /*    */
        }
        /* 42 */
        return r;
        /*    */
    }

    /*    */
    /*    */
    public boolean remove(String name) {
        /* 46 */
        return this.map.remove(name) != null;
        /*    */
    }

    /*    */
    /*    */
    public String encode()
    /*    */ {
        /* 51 */
        StringBuilder sb = new StringBuilder();
        /* 52 */
        sb.append("map=");
        /* 53 */
        for (String name : this.map.keySet()) {
            /* 54 */
            TextHistory history = (TextHistory) this.map.get(name);
            /* 55 */
            if (history != null) {
                /* 56 */
                sb.append(Strings.urlencodeUTF8(name) + ":" + Strings.urlencodeUTF8(history.encode()) + ",");
                /*    */
            }
            /*    */
        }
        /* 59 */
        return sb.toString();
        /*    */
    }

    /*    */
    /*    */
    public static TextHistoryCollection decode(String s) {
        /*    */
        try {
            /* 64 */
            TextHistoryCollection r = new TextHistoryCollection();
            /* 65 */
            String map = Strings.parseUrlParameter(s, "map");
            /*    */
            /* 67 */
            String[] couples = map.split(",");
            /* 68 */
            for (String couple : couples) {
                /* 69 */
                String[] name_data = couple.split(":");
                /* 70 */
                String name = Strings.urldecodeUTF8(name_data[0]);
                /* 71 */
                String data = Strings.urldecodeUTF8(name_data[1]);
                /* 72 */
                r.map.put(name, TextHistory.decode(data));
                /*    */
            }
            /* 74 */
            return r;
            /*    */
        }
        /*    */ catch (Exception e) {
        }
        /* 77 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\TextHistoryCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */