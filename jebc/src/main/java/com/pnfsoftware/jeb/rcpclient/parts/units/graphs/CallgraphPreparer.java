
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;


import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.L;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.E;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.ForceDirectedLayout;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.V;
import com.pnfsoftware.jeb.util.base.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


class CallgraphPreparer
        implements Runnable {
    private ICallgraphBuilder callgraphBuilder;
    Digraph model;
    List<P> points;
    List<L> lines;


    CallgraphPreparer(ICallgraphBuilder callgraphBuilder) {

        this.callgraphBuilder = callgraphBuilder;

    }


    public void run() {

        this.model = this.callgraphBuilder.buildModel();


        determineVerticesWeights(this.model);


        P[] initial_vertex_points = layoutVerticesBasedOnCentrality(this.model, 1.0D, 1.0D);

        ForceDirectedLayout l = new ForceDirectedLayout(this.model, 100, 1.0D, 1.0D, initial_vertex_points);


        l.layout();

        this.points = Arrays.asList(l.getPoints());

        this.lines = generateLinesForEdges(this.model);

    }


    private List<L> generateLinesForEdges(Digraph g) {

        List<L> lines = new ArrayList();

        for (E e : g.getEdges()) {

            lines.add(new L(e.getSrc().getId(), e.getDst().getId()));

        }

        return lines;

    }


    private void determineVerticesWeights(Digraph g) {
        double minweight = Double.MAX_VALUE;
        double maxweight = -1.7976931348623157E308d;
        for (V v : g.getVertices()) {
            if (v.weight != null) {
                if (v.weight < minweight) {
                    minweight = v.weight;
                }
                if (v.weight > maxweight) {
                    maxweight = v.weight;
                }
            }
        }
        for (V v2 : g.getVertices()) {
            if (v2.weight != null) {
                v2.weight = (v2.weight - minweight) / (maxweight - minweight);
            } else {
                v2.weight = 0.0d;
            }
        }
        if (g.getEdgeCount() <= 5000) {
            this.model.computeEdgeBetweenness();
            double max_vcscore = -1.7976931348623157E308d;
            for (V v22 : g.getVertices()) {
                if (v22.vcscore > max_vcscore) {
                    max_vcscore = v22.vcscore;
                }
            }
            for (V v222 : g.getVertices()) {
                v222.vcscore = v222.vcscore / max_vcscore;
            }
            for (V v2222 : g.getVertices()) {
                v2222.weight = (0.3d * v2222.weight) + (0.7d * v2222.vcscore);
            }
            return;
        }
        this.model.resetEdgeBetweennessScores();


    }


    private P[] layoutVerticesBasedOnCentrality(Digraph g, double w, double h) {

        int cnt = g.getVertexCount();

        P[] initial_points = new P[cnt];


        List<Integer> indices = g.getVertexIndexesByDescendingCentrality();

        Assert.a(indices.size() == cnt);


        double d = Math.sqrt(w * h / cnt);


        double x0 = w / 2.0D;

        double y0 = h / 2.0D;


        int dir = 0;

        int dir_change_count = 0;

        int requested_inline = 1;

        int inline = 0;


        for (int i = 0; i < cnt; i++) {

            int vertex_index = ((Integer) indices.get(i)).intValue();

            initial_points[vertex_index] = new P(Integer.valueOf(g.getVertexByIndex(vertex_index).getId()), x0, y0);

            switch (dir) {

                case 0:

                    x0 += d;

                    break;

                case 1:

                    y0 += d;

                    break;

                case 2:

                    x0 -= d;

                    break;

                default:

                    y0 -= d;

            }


            inline++;

            if (inline == requested_inline) {

                dir = (dir + 1) % 4;

                dir_change_count++;

                if (dir_change_count % 2 == 0) {

                    requested_inline++;

                }

                inline = 0;

            }

        }


        return initial_points;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\CallgraphPreparer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */