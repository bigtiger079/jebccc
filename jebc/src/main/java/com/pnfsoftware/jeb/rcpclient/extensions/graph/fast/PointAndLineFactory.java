package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

import java.util.ArrayList;
import java.util.List;

public class PointAndLineFactory {
    private int id;
    private List<P> points;
    private List<L> lines;

    public PointAndLineFactory() {
        this.points = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public List<P> getPoints() {
        return this.points;
    }

    public List<L> getEdges() {
        return this.lines;
    }

    public P p(double x, double y) {
        P p = new P(this.id++, x, y);
        this.points.add(p);
        return p;
    }

    public L e(int src, int dst) {
        if ((src < 0) || (src >= this.id)) {
            throw new IllegalArgumentException();
        }
        if ((dst < 0) || (dst >= this.id)) {
            throw new IllegalArgumentException();
        }
        L e = new L(src, dst);
        this.lines.add(e);
        return e;
    }

    public String toString() {
        return String.format("points=%s,edges=%s", this.points, this.lines);
    }
}


