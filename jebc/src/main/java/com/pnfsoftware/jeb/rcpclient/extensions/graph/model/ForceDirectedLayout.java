
package com.pnfsoftware.jeb.rcpclient.extensions.graph.model;

import com.pnfsoftware.jeb.core.exceptions.InterruptionException;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.base.Couple;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ForceDirectedLayout {
    private static final ILogger logger = GlobalLog.getLogger(ForceDirectedLayout.class);
    private static final double C = 1.0D;
    public static final double DEFAULT_WIDTH = 1.0D;
    public static final double DEFAULT_HEIGHT = 1.0D;
    public static final int DEFAULT_ITERCOUNT = 100;
    private double width;
    private double height;
    private double wh;
    private double hh;
    private double area;
    private double t;
    private double dt;
    private double k;
    private int currentiter;
    private int itercount;
    private int vertexcnt;
    private List<E> uedges;
    private P[] coords;
    private P[] disps;

    public ForceDirectedLayout(Digraph g) {
        this(g, 100, 1.0D, 1.0D, null);
    }

    public ForceDirectedLayout(Digraph g, int itercount, double width, double height, P[] coords) {
        this.vertexcnt = g.getVertexCount();
        Set<Couple<Integer, Integer>> set = new HashSet();
        this.uedges = new ArrayList();
        for (E e : g.getEdges()) {
            int a = e.src.index;
            int b = e.dst.index;
            Couple<Integer, Integer> c;
            if (a < b) {
                c = new Couple(Integer.valueOf(a), Integer.valueOf(b));
            } else {
                c = new Couple(Integer.valueOf(b), Integer.valueOf(a));
            }
            if (set.add(c)) {
                this.uedges.add(e);
            }
        }
        this.width = width;
        this.height = height;
        this.wh = (this.width / 2.0D);
        this.hh = (this.height / 2.0D);
        this.t = (width / 10.0D);
        this.dt = (this.t / (itercount + 1));
        this.area = (width * height);
        this.k = (1.0D * Math.sqrt(this.area / this.vertexcnt));
        if (coords == null) {
            Random prng = new Random(0L);
            coords = new P[this.vertexcnt];
            for (int i = 0; i < this.vertexcnt; i++) {
                coords[i] = new P(Integer.valueOf(g.getVertexByIndex(i).id), prng.nextDouble() * width, prng.nextDouble() * height);
            }
        }
        for (int i = 0; i < this.vertexcnt; i++) {
            P p = coords[i];
            V v = g.getVertexByIndex(i);
            Assert.a(p.getId() == v.id, "Vertex/Point ID mismatch: expected " + v.id + " got " + p.getId());
        }
        this.coords = coords;
        this.disps = new P[this.vertexcnt];
        for (int i = 0; i < this.vertexcnt; i++) {
            this.disps[i] = new P(Integer.valueOf(g.getVertexByIndex(i).id), 0.0D, 0.0D);
        }
        this.itercount = itercount;
        this.currentiter = 0;
    }

    public P[] getPoints() {
        return this.coords;
    }

    public P[] layout() {
        while (this.currentiter < this.itercount) {
            layoutiter();
        }
        return this.coords;
    }

    public P[] layoutiter() {
        for (int i = 0; i < this.vertexcnt; i++) {
            P c0 = this.coords[i];
            P d0 = this.disps[i];
            d0.reset();
            for (int j = 0; j < this.vertexcnt; j++) {
                if (i != j) {
                    P c1 = this.coords[j];
                    P d = new P(c0.getX() - c1.getX(), c0.getY() - c1.getY());
                    double delta = d.dist();
                    if (delta != 0.0D) {
                        double f = f_r(delta, this.k) / delta;
                        d.scale(f);
                        d0.add(d);
                    }
                }
            }
            if (Thread.interrupted()) {
                throw new InterruptionException();
            }
        }
        for (E e : this.uedges) {
            P c0 = this.coords[e.src.index];
            P c1 = this.coords[e.dst.index];
            P d = new P(c0.getX() - c1.getX(), c0.getY() - c1.getY());
            double delta = d.dist();
            if (delta != 0.0D) {
                double f = f_a(delta, this.k) / delta;
                d.scale(f);
                P d0 = this.disps[e.src.index];
                P d1 = this.disps[e.dst.index];
                d0.sub(d);
                d1.add(d);
            }
        }
        for (int i = 0; i < this.vertexcnt; i++) {
            P d0 = this.disps[i];
            double disp = d0.dist();
            if (disp != 0.0D) {
                P c0 = this.coords[i];
                double d = Math.min(disp, this.t) / disp;
                double x = c0.getX() + d0.getX() * d;
                double y = c0.getY() + d0.getY() * d;
                c0.set(x, y);
            }
        }
        this.t *= 0.95D;
        this.currentiter += 1;
        return this.coords;
    }

    private double f_a(double d, double k) {
        return d * d / k;
    }

    private double f_r(double d, double k) {
        return k * k / d;
    }
}


