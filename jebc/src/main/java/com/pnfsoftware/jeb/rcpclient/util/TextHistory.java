package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.util.format.IAsciiable;
import com.pnfsoftware.jeb.util.format.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextHistory implements IAsciiable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int defaultMaxEntries = 100;
    private int maxcnt;
    private ArrayList<String> history;
    private int ptr;
    private boolean allowDuplicates;

    public TextHistory() {
        this(100, null);
    }

    public TextHistory(int maxcnt) {
        this(maxcnt, null);
    }

    public TextHistory(int maxcnt, List<String> init) {
        if (maxcnt <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxcnt = maxcnt;
        this.history = new ArrayList<>(maxcnt);
        this.ptr = 0;
        if (init == null) {
            return;
        }
        if (init.size() > maxcnt) {
            for (int i = init.size() - maxcnt; i < init.size(); i++) {
                this.history.add(init.get(i));
            }
        } else {
            this.history = new ArrayList<>(init);
        }
        this.ptr = (this.history.size() % maxcnt);
    }

    public void clear() {
        this.history.clear();
        this.ptr = 0;
    }

    public int getMaxCount() {
        return this.maxcnt;
    }

    public int size() {
        return this.history.size();
    }

    public boolean isEmpty() {
        return this.history.isEmpty();
    }

    public boolean getAllowDuplicates() {
        return this.allowDuplicates;
    }

    public void setAllowDuplicates(boolean enabled) {
        this.allowDuplicates = enabled;
    }

    public void record(String text) {
        if (text == null) {
            return;
        }
        if (!this.allowDuplicates) {
            int index = this.history.indexOf(text);
            if (index >= 0) {
                int nextIndex = increment(index);
                while (nextIndex != this.ptr) {
                    this.history.set(index, this.history.get(nextIndex));
                    index = nextIndex;
                    nextIndex = increment(index);
                }
                this.history.set(index, text);
                return;
            }
        }
        if (this.history.size() >= this.maxcnt) {
            if (this.history.size() > this.maxcnt) {
                throw new RuntimeException();
            }
            this.history.set(this.ptr, text);
        } else {
            this.history.add(text);
        }
        this.ptr = increment(this.ptr);
    }

    private int increment(int index) {
        return (index + 1) % this.maxcnt;
    }

    private int decrement(int index) {
        return (index - 1 + this.maxcnt) % this.maxcnt;
    }

    public List<String> getAll() {
        if (this.history.size() < this.maxcnt) {
            return new ArrayList<>(this.history);
        }
        if (this.history.size() != this.maxcnt) {
            throw new RuntimeException();
        }
        List<String> r = new ArrayList<>();
        for (int i = this.ptr; i < this.maxcnt; i++) {
            r.add(this.history.get(i));
        }
        for (int i = 0; i < this.ptr; i++) {
            r.add(this.history.get(i));
        }
        return r;
    }

    public String getLast() {
        if (this.history.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return (String) this.history.get(decrement(this.ptr));
    }

    public List<String> getLast(int cnt) {
        if (this.history.size() < cnt) {
            throw new ArrayIndexOutOfBoundsException();
        }
        List<String> r = new ArrayList<>();
        for (int i = decrement(this.ptr); cnt > 0; cnt--) {
            r.add(0, this.history.get(i));
            i = decrement(i);
        }
        return r;
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("maxcount=%d", new Object[]{Integer.valueOf(this.maxcnt)}));
        sb.append("&strings=");
        sb.append(Strings.encodeArray(getAll().toArray()));
        return sb.toString();
    }

    public static TextHistory decode(String s) {
        try {
            String[] parts = Strings.parseUrlParameters(s, new String[]{"maxcount", "strings"});
            int maxcount = Integer.parseInt(parts[0]);
            String[] array = Strings.decodeArray(parts[1]);
            return new TextHistory(maxcount, Arrays.asList(array));
        } catch (Exception e) {
        }
        return null;
    }
}


