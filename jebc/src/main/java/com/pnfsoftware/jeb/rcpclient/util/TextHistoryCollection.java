
package com.pnfsoftware.jeb.rcpclient.util;


import com.pnfsoftware.jeb.util.format.IAsciiable;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.HashMap;
import java.util.Map;


public class TextHistoryCollection
        implements IAsciiable {
    Map<String, TextHistory> map = new HashMap();


    public boolean add(String name, TextHistory history) {

        if (this.map.containsKey(name)) {

            return false;

        }

        this.map.put(name, history);

        return true;

    }


    public TextHistory get(String name) {

        TextHistory r = (TextHistory) this.map.get(name);

        if (r == null) {

            r = new TextHistory();

            this.map.put(name, r);

        }

        return r;

    }


    public boolean remove(String name) {

        return this.map.remove(name) != null;

    }


    public String encode() {

        StringBuilder sb = new StringBuilder();

        sb.append("map=");

        for (String name : this.map.keySet()) {

            TextHistory history = (TextHistory) this.map.get(name);

            if (history != null) {

                sb.append(Strings.urlencodeUTF8(name) + ":" + Strings.urlencodeUTF8(history.encode()) + ",");

            }

        }

        return sb.toString();

    }


    public static TextHistoryCollection decode(String s) {

        try {

            TextHistoryCollection r = new TextHistoryCollection();

            String map = Strings.parseUrlParameter(s, "map");


            String[] couples = map.split(",");

            for (String couple : couples) {

                String[] name_data = couple.split(":");

                String name = Strings.urldecodeUTF8(name_data[0]);

                String data = Strings.urldecodeUTF8(name_data[1]);

                r.map.put(name, TextHistory.decode(data));

            }

            return r;

        } catch (Exception e) {
        }

        return null;

    }

}


