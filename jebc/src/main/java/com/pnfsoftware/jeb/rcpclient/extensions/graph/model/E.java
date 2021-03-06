package com.pnfsoftware.jeb.rcpclient.extensions.graph.model;

public class E {
    V src;
    V dst;
    public Double weight;
    public Double score;
    public Double ebscore;

    E(V src, V dst, Double weight) {
        this.src = src;
        this.dst = dst;
        this.weight = weight;
    }

    E(V src, V dst) {
        this(src, dst, null);
    }

    public V getSrc() {
        return this.src;
    }

    public V getDst() {
        return this.dst;
    }

    public Double getWeight() {
        return this.weight;
    }

    public E clone() {
        E e = new E(this.src, this.dst, this.weight);
        e.score = this.score;
        e.ebscore = this.ebscore;
        return e;
    }

    public String toString() {
        if (this.weight == null) {
            return String.format("%d>%d", this.src.id, this.dst.id);
        }
        return String.format("%d>%d(%f)", this.src.id, this.dst.id, this.weight);
    }
}


