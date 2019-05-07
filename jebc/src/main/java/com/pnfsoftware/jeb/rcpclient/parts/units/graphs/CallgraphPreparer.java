/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.L;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.E;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.ForceDirectedLayout;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.V;
/*     */ import com.pnfsoftware.jeb.util.base.Assert;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ class CallgraphPreparer
        /*     */ implements Runnable
        /*     */ {
    /*     */   private ICallgraphBuilder callgraphBuilder;
    /*     */ Digraph model;
    /*     */ List<P> points;
    /*     */ List<L> lines;

    /*     */
    /*     */   CallgraphPreparer(ICallgraphBuilder callgraphBuilder)
    /*     */ {
        /*  30 */
        this.callgraphBuilder = callgraphBuilder;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void run()
    /*     */ {
        /*  36 */
        this.model = this.callgraphBuilder.buildModel();
        /*     */
        /*     */
        /*  39 */
        determineVerticesWeights(this.model);
        /*     */
        /*     */
        /*     */
        /*  43 */
        P[] initial_vertex_points = layoutVerticesBasedOnCentrality(this.model, 1.0D, 1.0D);
        /*  44 */
        ForceDirectedLayout l = new ForceDirectedLayout(this.model, 100, 1.0D, 1.0D, initial_vertex_points);
        /*     */
        /*  46 */
        l.layout();
        /*  47 */
        this.points = Arrays.asList(l.getPoints());
        /*  48 */
        this.lines = generateLinesForEdges(this.model);
        /*     */
    }

    /*     */
    /*     */
    private List<L> generateLinesForEdges(Digraph g) {
        /*  52 */
        List<L> lines = new ArrayList();
        /*  53 */
        for (E e : g.getEdges()) {
            /*  54 */
            lines.add(new L(e.getSrc().getId(), e.getDst().getId()));
            /*     */
        }
        /*  56 */
        return lines;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private void determineVerticesWeights(Digraph g)
    /*     */ {
        /*  65 */
        double minweight = Double.MAX_VALUE;
        /*  66 */
        double maxweight = -1.7976931348623157E308D;
        /*  67 */
        for (V v : g.getVertices()) {
            /*  68 */
            if (v.weight != null) {
                /*  69 */
                if (v.weight.doubleValue() < minweight) {
                    /*  70 */
                    minweight = v.weight.doubleValue();
                    /*     */
                }
                /*  72 */
                if (v.weight.doubleValue() > maxweight) {
                    /*  73 */
                    maxweight = v.weight.doubleValue();
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*  77 */
        for (V v : g.getVertices()) {
            /*  78 */
            if (v.weight != null) {
                /*  79 */
                v.weight = Double.valueOf((v.weight.doubleValue() - minweight) / (maxweight - minweight));
                /*     */
            }
            /*     */
            else {
                /*  82 */
                v.weight = Double.valueOf(0.0D);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        Object localObject;
        /*  87 */
        if (g.getEdgeCount() <= 5000)
            /*     */ {
            /*  89 */
            this.model.computeEdgeBetweenness();
            /*     */
            /*     */
            /*  92 */
            double max_vcscore = -1.7976931348623157E308D;
            /*  93 */
            for (V v : g.getVertices()) {
                /*  94 */
                if (v.vcscore.doubleValue() > max_vcscore) {
                    /*  95 */
                    max_vcscore = v.vcscore.doubleValue();
                    /*     */
                }
                /*     */
            }
            /*  98 */
            for (??? =g.getVertices().iterator(); ???.hasNext();
            /*  99 */
            (((V) localObject).vcscore = Double.valueOf(((V) localObject).vcscore.doubleValue() / max_vcscore)))
            /*     */
            {
                /*  98 */
                V v = (V) ???.next();
                /*  99 */
                localObject = v;
                /*     */
            }
            /*     */
            /*     */
            /* 103 */
            double R = 0.3D;
            /* 104 */
            for (localObject = g.getVertices().iterator(); ((Iterator) localObject).hasNext(); ) {
                V v = (V) ((Iterator) localObject).next();
                /* 105 */
                v.weight = Double.valueOf(0.3D * v.weight.doubleValue() + 0.7D * v.vcscore.doubleValue());
                /*     */
            }
            /*     */
        }
        /*     */
        else {
            /* 109 */
            this.model.resetEdgeBetweennessScores();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private P[] layoutVerticesBasedOnCentrality(Digraph g, double w, double h)
    /*     */ {
        /* 115 */
        int cnt = g.getVertexCount();
        /* 116 */
        P[] initial_points = new P[cnt];
        /*     */
        /* 118 */
        List<Integer> indices = g.getVertexIndexesByDescendingCentrality();
        /* 119 */
        Assert.a(indices.size() == cnt);
        /*     */
        /* 121 */
        double d = Math.sqrt(w * h / cnt);
        /*     */
        /*     */
        /*     */
        /* 125 */
        double x0 = w / 2.0D;
        /* 126 */
        double y0 = h / 2.0D;
        /*     */
        /* 128 */
        int dir = 0;
        /* 129 */
        int dir_change_count = 0;
        /* 130 */
        int requested_inline = 1;
        /* 131 */
        int inline = 0;
        /*     */
        /* 133 */
        for (int i = 0; i < cnt; i++) {
            /* 134 */
            int vertex_index = ((Integer) indices.get(i)).intValue();
            /* 135 */
            initial_points[vertex_index] = new P(Integer.valueOf(g.getVertexByIndex(vertex_index).getId()), x0, y0);
            /* 136 */
            switch (dir) {
                /*     */
                case 0:
                    /* 138 */
                    x0 += d;
                    /* 139 */
                    break;
                /*     */
                case 1:
                    /* 141 */
                    y0 += d;
                    /* 142 */
                    break;
                /*     */
                case 2:
                    /* 144 */
                    x0 -= d;
                    /* 145 */
                    break;
                /*     */
                default:
                    /* 147 */
                    y0 -= d;
                    /*     */
            }
            /*     */
            /* 150 */
            inline++;
            /* 151 */
            if (inline == requested_inline) {
                /* 152 */
                dir = (dir + 1) % 4;
                /* 153 */
                dir_change_count++;
                /* 154 */
                if (dir_change_count % 2 == 0) {
                    /* 155 */
                    requested_inline++;
                    /*     */
                }
                /* 157 */
                inline = 0;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 161 */
        return initial_points;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\CallgraphPreparer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */