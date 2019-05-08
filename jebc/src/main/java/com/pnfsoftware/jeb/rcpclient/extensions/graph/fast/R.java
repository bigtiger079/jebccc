package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

public class R {
    public double x;
    public double y;
    public double w;
    public double h;

    public R(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean contains(P p) {
        return (p.x >= this.x) && (p.x < this.x + this.w) && (p.y >= this.y) && (p.y < this.y + this.h);
    }

    public String toString() {
        return String.format("%f,%f,%f,%f", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.w), Double.valueOf(this.h)});
    }
}


