/*     */
package com.pnfsoftware.jeb.rcpclient.util;
/*     */
/*     */

import com.pnfsoftware.jeb.util.format.IAsciiable;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class TextHistory
        /*     */ implements IAsciiable, Serializable
        /*     */ {
    /*     */   private static final long serialVersionUID = 1L;
    /*     */   private static final int defaultMaxEntries = 100;
    /*     */   private int maxcnt;
    /*     */   private ArrayList<String> history;
    /*     */   private int ptr;
    /*     */   private boolean allowDuplicates;

    /*     */
    /*     */
    public TextHistory()
    /*     */ {
        /*  40 */
        this(100, null);
        /*     */
    }

    /*     */
    /*     */
    public TextHistory(int maxcnt) {
        /*  44 */
        this(maxcnt, null);
        /*     */
    }

    /*     */
    /*     */
    public TextHistory(int maxcnt, List<String> init) {
        /*  48 */
        if (maxcnt <= 0) {
            /*  49 */
            throw new IllegalArgumentException();
            /*     */
        }
        /*     */
        /*  52 */
        this.maxcnt = maxcnt;
        /*  53 */
        this.history = new ArrayList(maxcnt);
        /*  54 */
        this.ptr = 0;
        /*     */
        /*  56 */
        if (init == null) {
            /*  57 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /*  61 */
        if (init.size() > maxcnt) {
            /*  62 */
            for (int i = init.size() - maxcnt; i < init.size(); i++) {
                /*  63 */
                this.history.add(init.get(i));
                /*     */
            }
            /*     */
            /*     */
        } else {
            /*  67 */
            this.history = new ArrayList(init);
            /*     */
        }
        /*  69 */
        this.ptr = (this.history.size() % maxcnt);
        /*     */
    }

    /*     */
    /*     */
    public void clear() {
        /*  73 */
        this.history.clear();
        /*  74 */
        this.ptr = 0;
        /*     */
    }

    /*     */
    /*     */
    public int getMaxCount() {
        /*  78 */
        return this.maxcnt;
        /*     */
    }

    /*     */
    /*     */
    public int size() {
        /*  82 */
        return this.history.size();
        /*     */
    }

    /*     */
    /*     */
    public boolean isEmpty() {
        /*  86 */
        return this.history.isEmpty();
        /*     */
    }

    /*     */
    /*     */
    public boolean getAllowDuplicates() {
        /*  90 */
        return this.allowDuplicates;
        /*     */
    }

    /*     */
    /*     */
    public void setAllowDuplicates(boolean enabled) {
        /*  94 */
        this.allowDuplicates = enabled;
        /*     */
    }

    /*     */
    /*     */
    public void record(String text) {
        /*  98 */
        if (text == null) {
            /*  99 */
            return;
            /*     */
        }
        /*     */
        /* 102 */
        if (!this.allowDuplicates) {
            /* 103 */
            int index = this.history.indexOf(text);
            /* 104 */
            if (index >= 0) {
                /* 105 */
                int nextIndex = increment(index);
                /* 106 */
                while (nextIndex != this.ptr) {
                    /* 107 */
                    this.history.set(index, this.history.get(nextIndex));
                    /* 108 */
                    index = nextIndex;
                    /* 109 */
                    nextIndex = increment(index);
                    /*     */
                }
                /* 111 */
                this.history.set(index, text);
                /* 112 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 116 */
        if (this.history.size() >= this.maxcnt) {
            /* 117 */
            if (this.history.size() > this.maxcnt) {
                /* 118 */
                throw new RuntimeException();
                /*     */
            }
            /* 120 */
            this.history.set(this.ptr, text);
            /*     */
        }
        /*     */
        else {
            /* 123 */
            this.history.add(text);
            /*     */
        }
        /* 125 */
        this.ptr = increment(this.ptr);
        /*     */
    }

    /*     */
    /*     */
    private int increment(int index) {
        /* 129 */
        return (index + 1) % this.maxcnt;
        /*     */
    }

    /*     */
    /*     */
    private int decrement(int index) {
        /* 133 */
        return (index - 1 + this.maxcnt) % this.maxcnt;
        /*     */
    }

    /*     */
    /*     */
    public List<String> getAll()
    /*     */ {
        /* 138 */
        if (this.history.size() < this.maxcnt) {
            /* 139 */
            return new ArrayList(this.history);
            /*     */
        }
        /*     */
        /*     */
        /* 143 */
        if (this.history.size() != this.maxcnt) {
            /* 144 */
            throw new RuntimeException();
            /*     */
        }
        /* 146 */
        List<String> r = new ArrayList();
        /* 147 */
        for (int i = this.ptr; i < this.maxcnt; i++) {
            /* 148 */
            r.add(this.history.get(i));
            /*     */
        }
        /* 150 */
        for (int i = 0; i < this.ptr; i++) {
            /* 151 */
            r.add(this.history.get(i));
            /*     */
        }
        /* 153 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public String getLast() {
        /* 157 */
        if (this.history.isEmpty()) {
            /* 158 */
            throw new ArrayIndexOutOfBoundsException();
            /*     */
        }
        /*     */
        /* 161 */
        return (String) this.history.get(decrement(this.ptr));
        /*     */
    }

    /*     */
    /*     */
    public List<String> getLast(int cnt) {
        /* 165 */
        if (this.history.size() < cnt) {
            /* 166 */
            throw new ArrayIndexOutOfBoundsException();
            /*     */
        }
        /*     */
        /* 169 */
        List<String> r = new ArrayList();
        /* 170 */
        for (int i = decrement(this.ptr); cnt > 0; cnt--) {
            /* 171 */
            r.add(0, this.history.get(i));
            /* 172 */
            i = decrement(i);
            /*     */
        }
        /* 174 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public String encode()
    /*     */ {
        /* 179 */
        StringBuilder sb = new StringBuilder();
        /* 180 */
        sb.append(String.format("maxcount=%d", new Object[]{Integer.valueOf(this.maxcnt)}));
        /* 181 */
        sb.append("&strings=");
        /* 182 */
        sb.append(Strings.encodeArray(getAll().toArray()));
        /* 183 */
        return sb.toString();
        /*     */
    }

    /*     */
    /*     */
    public static TextHistory decode(String s) {
        /*     */
        try {
            /* 188 */
            String[] parts = Strings.parseUrlParameters(s, new String[]{"maxcount", "strings"});
            /* 189 */
            int maxcount = Integer.parseInt(parts[0]);
            /* 190 */
            String[] array = Strings.decodeArray(parts[1]);
            /* 191 */
            return new TextHistory(maxcount, Arrays.asList(array));
            /*     */
        }
        /*     */ catch (Exception e) {
        }
        /* 194 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\TextHistory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */