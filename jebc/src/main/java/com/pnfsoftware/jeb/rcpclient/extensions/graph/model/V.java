
package com.pnfsoftware.jeb.rcpclient.extensions.graph.model;

import com.pnfsoftware.jeb.util.format.Strings;

public class V {
    int index;
    final int id;
    public Double weight;
    public String label;
    public Double score;
    public Double vcscore;

    V(int index, int id, Double weight, String label) {
        this.index = index;
        this.id = id;
        this.weight = weight;
        this.label = label;
    }

    V(int index, int id) {
        this(index, id, null, null);
    }

    V(int index) {
        this(index, index, null, null);
    }

    public int getId() {
        return this.id;
    }

    public Double getWeight() {
        return this.weight;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public V clone() {
        V v = new V(this.index, this.id, this.weight, this.label);
        v.score = this.score;
        v.vcscore = this.vcscore;
        return v;
    }

    public String toString() {
        if (Strings.isBlank(this.label)) {
            return Integer.toString(this.id);
        }
        return this.label;
    }
}


