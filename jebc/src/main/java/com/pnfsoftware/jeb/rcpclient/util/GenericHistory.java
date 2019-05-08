package com.pnfsoftware.jeb.rcpclient.util;

import java.util.ArrayList;
import java.util.List;

public class GenericHistory<T> {
    private ArrayList<T> history;
    private int ptr;

    public GenericHistory() {
        this(null);
    }

    public GenericHistory(List<T> init) {
        this.history = new ArrayList();
        this.ptr = 0;
        if (init == null) {
            return;
        }
        this.history = new ArrayList(init);
        this.ptr = this.history.size();
    }

    public void clear() {
        this.history.clear();
        this.ptr = 0;
    }

    public int size() {
        return this.history.size();
    }

    public boolean isEmpty() {
        return this.history.isEmpty();
    }

    public void record(T o) {
        if (o == null) {
            return;
        }
        if (this.ptr == this.history.size()) {
            this.history.add(o);
            this.ptr += 1;
        } else {
            this.history.set(this.ptr, o);
            this.ptr += 1;
            while (this.history.size() > this.ptr) {
                this.history.remove(this.ptr);
            }
        }
    }

    public void replace(T o) {
        this.history.set(this.ptr - 1, o);
    }

    public List<T> getAll() {
        return new ArrayList(this.history);
    }

    public T getLast() {
        if (this.history.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if ((this.ptr <= 0) || (this.ptr > this.history.size())) {
            throw new IllegalStateException("pointer is not coherent with History");
        }
        return (T) this.history.get(this.ptr - 1);
    }

    public boolean hasBackward() {
        return this.ptr > 1;
    }

    public boolean hasForward() {
        return this.ptr < size();
    }

    public T backward() {
        if (this.ptr > 1) {
            this.ptr -= 1;
            return (T) getLast();
        }
        throw new ArrayIndexOutOfBoundsException("There is no backward element");
    }

    public T forward() {
        if (this.ptr < size()) {
            this.ptr += 1;
            return (T) getLast();
        }
        throw new ArrayIndexOutOfBoundsException("There is no forward element");
    }
}


