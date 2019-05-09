package com.pnfsoftware.jeb.rcpclient.extensions.graph.model;

import com.pnfsoftware.jeb.rcpclient.extensions.graph.fast.P;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;

public class CommunityDetector {
    private static final ILogger logger = GlobalLog.getLogger(CommunityDetector.class);

    public static class Node {
        Digraph originalSubgraphRef;
        List<Node> children;
        Integer vertexId;
        int descCount;

        Node() {
        }

        Node(Digraph g) {
            this.originalSubgraphRef = g;
            this.descCount = this.originalSubgraphRef.getVertexCount();
        }

        public List<Node> getChildren() {
            return this.children;
        }

        public Integer getVertexId() {
            return this.vertexId;
        }

        public String toString() {
            if (this.children != null) {
                return this.descCount + ":" + this.children.toString();
            }
            return Integer.toString(this.vertexId.intValue());
        }
    }

    public Node perform(Digraph g) {
        Node root = new Node();
        performRecurse(g, root);
        return root;
    }

    public Node perform(Digraph g, int subgraphCountThreshold) {
        return performIter(g, subgraphCountThreshold);
    }

    private int performRecurse(Digraph g, Node node) {
        if (g.getVertexCount() == 1) {
            node.vertexId = Integer.valueOf(g.getVertexByIndex(0).id);
            return 1;
        }
        List<Digraph> subgraphs;
        List<Integer> ebOrder;
        double score;
        for (; ; ) {
            subgraphs = g.getWeaklyConnectedComponents();
            if (subgraphs.size() > 1) {
                break;
            }
            ebOrder = g.computeEdgeBetweenness();
            E e = g.getEdge(((Integer) ebOrder.get(0)).intValue());
            double bestScore = e.ebscore.doubleValue();
            List<E> tbr = new ArrayList<>();
            tbr.add(e);
            int cnt;
            for (cnt = 1; cnt < g.getEdgeCount(); cnt++) {
                e = g.getEdge(((Integer) ebOrder.get(cnt)).intValue());
                score = e.ebscore.doubleValue();
                if (!almostEquals(score, bestScore)) {
                    break;
                }
                tbr.add(e);
            }
            if (cnt >= 2) {
                logger.i("%d edges have the same EB score: %f", new Object[]{Integer.valueOf(cnt), Double.valueOf(bestScore)});
            }
            for (E edge : tbr) {
                g.removeEdge(edge);
            }
        }
        logger.i("-> %d sub-graphs", new Object[]{Integer.valueOf(subgraphs.size())});
        node.children = new ArrayList<>(subgraphs.size());
        for (Digraph subgraph : subgraphs) {
            Node subnode = new Node();
            node.children.add(subnode);
            int cnt = performRecurse(subgraph, subnode);
            node.descCount += cnt;
        }
        return node.descCount;
    }

    private Node performIter(Digraph inputGraph, int subgraphCountThreshold) {
        List<Digraph> q = new ArrayList<>();
        q.add(inputGraph);
        IdentityHashMap<Digraph, Node> map = new IdentityHashMap();
        Node inputNode = new Node(inputGraph);
        map.put(inputGraph, inputNode);
        while ((!q.isEmpty()) && ((subgraphCountThreshold <= 0) || (q.size() < subgraphCountThreshold))) {
            Digraph g = (Digraph) q.remove(0);
            Node node = (Node) map.get(g);
            if (g.getVertexCount() == 1) {
                node.vertexId = Integer.valueOf(g.getVertexByIndex(0).id);
            } else {
                List<Digraph> subgraphs;
                List<Integer> ebOrder;
                double score;
                for (; ; ) {
                    subgraphs = g.getWeaklyConnectedComponents();
                    if (subgraphs.size() > 1) {
                        break;
                    }
                    ebOrder = g.computeEdgeBetweenness();
                    E e = g.getEdge(((Integer) ebOrder.get(0)).intValue());
                    double bestScore = e.ebscore.doubleValue();
                    List<E> tbr = new ArrayList<>();
                    tbr.add(e);
                    int cnt;
                    for (cnt = 1; cnt < g.getEdgeCount(); cnt++) {
                        e = g.getEdge(((Integer) ebOrder.get(cnt)).intValue());
                        score = e.ebscore.doubleValue();
                        if (!almostEquals(score, bestScore)) {
                            break;
                        }
                        tbr.add(e);
                    }
                    if (cnt >= 2) {
                        logger.i("%d edges have the same EB score: %f", new Object[]{Integer.valueOf(cnt), Double.valueOf(bestScore)});
                    }
                    for (E edge : tbr) {
                        g.removeEdge(edge);
                    }
                }
                logger.i("-> %d sub-graphs", new Object[]{Integer.valueOf(subgraphs.size())});
                node.children = new ArrayList<>(subgraphs.size());
                for (Digraph subgraph : subgraphs) {
                    Node subnode = new Node(subgraph);
                    node.children.add(subnode);
                    map.put(subgraph, subnode);
                }
                q.addAll(subgraphs);
            }
        }
        return inputNode;
    }

    private static boolean almostEquals(double a, double b) {
        return Math.abs(a - b) <= 1.0E-7D;
    }

    private Random prng = new Random(0L);

    public List<P> layout(Node root) {
        return layout(root, 0.0D, 0.0D, 1.0D, 1.0D);
    }

    public List<P> layout(Node root, double gridX, double gridY, double gridW, double gridH) {
        List<P> points = new ArrayList<>();
        layoutInternal(root, gridX, gridY, gridW, gridH, points);
        return points;
    }

    public void layoutInternal(Node node, double gridX, double gridY, double gridW, double gridH, List<P> points) {
        double H;
        if (node.children != null) {
            boolean splitHorz = gridW > gridH;
            int N = node.descCount;
            double W;
            if (splitHorz) {
                W = gridW / N;
                for (Node child : node.children) {
                    double w = W * Math.max(1, child.descCount);
                    layoutInternal(child, gridX, gridY, w, gridH, points);
                    gridX += w;
                }
            } else {
                H = gridH / N;
                for (Node child : node.children) {
                    double h = H * Math.max(1, child.descCount);
                    layoutInternal(child, gridX, gridY, gridW, h, points);
                    gridY += h;
                }
            }
        } else {
            double x = gridX + gridW / 2.0D;
            double y = gridY + gridH / 2.0D;
            points.add(new P(node.vertexId, x, y));
        }
    }
}


