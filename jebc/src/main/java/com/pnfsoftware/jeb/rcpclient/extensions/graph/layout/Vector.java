package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

public class Vector {
    boolean[] vect;

    public Vector(int size) {
        this.vect = new boolean[size];
    }

    public Vector on(int index) {
        this.vect[index] = true;
        return this;
    }

    public Vector onRange(int start, int count) {
        for (int i = start; i < start + count; i++) {
            this.vect[i] = true;
        }
        return this;
    }

    public boolean[] get() {
        return this.vect;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean v : this.vect) {
            sb.append(v ? '1' : '0');
        }
        return sb.toString();
    }
}


