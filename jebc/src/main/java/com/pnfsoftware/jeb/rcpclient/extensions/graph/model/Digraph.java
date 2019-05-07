package com.pnfsoftware.jeb.rcpclient.extensions.graph.model;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.core.exceptions.InterruptionException;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.collect.MultiList;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Digraph {
    private static final ILogger logger = GlobalLog.getLogger(Digraph.class);
    private boolean done;
    private MultiList<E> edgefrommap = new MultiList();
    private List<E> edges = new ArrayList();
    private MultiList<E> edgetomap = new MultiList();
    private List<Set<Integer>> reachabilityIndices;
    private TreeMap<Integer, V> vertexmap = new TreeMap();
    private List<V> vertices = new ArrayList();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.addEdge(int, int, java.lang.Double):com.pnfsoftware.jeb.rcpclient.extensions.graph.model.E, dex:
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:293)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JavaClass.getCode(JavaClass.java:48)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:595)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:79)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 5 more
        */
    private com.pnfsoftware.jeb.rcpclient.extensions.graph.model.E addEdge(int r1, int r2, java.lang.Double r3) {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.addEdge(int, int, java.lang.Double):com.pnfsoftware.jeb.rcpclient.extensions.graph.model.E, dex:
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.addEdge(int, int, java.lang.Double):com.pnfsoftware.jeb.rcpclient.extensions.graph.model.E");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.computeEdgeBetweenness():java.util.List<java.lang.Integer>, dex:
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:293)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JavaClass.getCode(JavaClass.java:48)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:595)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:79)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 5 more
        */
    public java.util.List<java.lang.Integer> computeEdgeBetweenness() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.computeEdgeBetweenness():java.util.List<java.lang.Integer>, dex:
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.computeEdgeBetweenness():java.util.List<java.lang.Integer>");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.getEdgeIndexesByDescendingBetweenness():java.util.List<java.lang.Integer>, dex:
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:293)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JavaClass.getCode(JavaClass.java:48)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:595)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:79)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 5 more
        */
    public java.util.List<java.lang.Integer> getEdgeIndexesByDescendingBetweenness() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.getEdgeIndexesByDescendingBetweenness():java.util.List<java.lang.Integer>, dex:
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.getEdgeIndexesByDescendingBetweenness():java.util.List<java.lang.Integer>");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.getVertexIndexesByDescendingCentrality():java.util.List<java.lang.Integer>, dex:
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:293)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JavaClass.getCode(JavaClass.java:48)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:595)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:79)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 5 more
        */
    public java.util.List<java.lang.Integer> getVertexIndexesByDescendingCentrality() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.getVertexIndexesByDescendingCentrality():java.util.List<java.lang.Integer>, dex:
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.getVertexIndexesByDescendingCentrality():java.util.List<java.lang.Integer>");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.removeVertex(com.pnfsoftware.jeb.rcpclient.extensions.graph.model.V, boolean):void, dex:
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:293)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JavaClass.getCode(JavaClass.java:48)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:595)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:79)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 5 more
        */
    public void removeVertex(com.pnfsoftware.jeb.rcpclient.extensions.graph.model.V r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.removeVertex(com.pnfsoftware.jeb.rcpclient.extensions.graph.model.V, boolean):void, dex:
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph.removeVertex(com.pnfsoftware.jeb.rcpclient.extensions.graph.model.V, boolean):void");
    }

    public static Digraph create() {
        return new Digraph();
    }

    public List<V> copyOfVertices() {
        List<V> r = new ArrayList(getVertexCount());
        for (V v : this.vertices) {
            r.add(v.clone());
        }
        return r;
    }

    public List<E> copyOfEdges() {
        List<E> r = new ArrayList(getEdgeCount());
        for (E e : this.edges) {
            r.add(e.clone());
        }
        return r;
    }

    public int getVertexCount() {
        return this.vertices.size();
    }

    public List<V> getVertices() {
        return this.vertices;
    }

    public V getVertex(int id) {
        return (V) this.vertexmap.get(Integer.valueOf(id));
    }

    public V getVertexByIndex(int index) {
        return (V) this.vertices.get(index);
    }

    public String getVertexLabel(int id) {
        return ((V) this.vertexmap.get(Integer.valueOf(id))).getLabel();
    }

    public void setVertexLabel(int id, String label) {
        ((V) this.vertexmap.get(Integer.valueOf(id))).setLabel(label);
    }

    private static /* synthetic */ boolean lambda$removeVertex$0(V v, E e) {
        return e.dst == v;
    }

    private static /* synthetic */ boolean lambda$removeVertex$1(V v, E e) {
        return e.src == v;
    }

    private void verify() {
        boolean z;
        Assert.a(this.vertices.size() == this.vertexmap.size());
        if (this.edgefrommap.values().size() == this.edges.size()) {
            z = true;
        } else {
            z = false;
        }
        Assert.a(z, this.edgefrommap.values().size() + " != " + this.edges.size());
        if (this.edgetomap.values().size() == this.edges.size()) {
            z = true;
        } else {
            z = false;
        }
        Assert.a(z, this.edgetomap.values().size() + " != " + this.edges.size());
        if (!Licensing.isReleaseBuild()) {
            Set<Integer> idset = new HashSet();
            int index = 0;
            for (V v : this.vertices) {
                if (v.index == index) {
                    z = true;
                } else {
                    z = false;
                }
                Assert.a(z, "Unexpected index");
                index++;
                if (this.vertexmap.get(Integer.valueOf(v.id)) == v) {
                    z = true;
                } else {
                    z = false;
                }
                Assert.a(z, "Vertex is missing");
                Assert.a(idset.add(Integer.valueOf(v.id)), "Duplicate id: " + v.id);
            }
            for (E e : this.edges) {
                Assert.a(this.vertices.contains(e.src));
                Assert.a(this.vertices.contains(e.dst), "Vertex id=" + e.dst.id + " was not found");
                this.edgefrommap.get(e.src.id).contains(e);
                this.edgetomap.get(e.dst.id).contains(e);
            }
        }
    }

    public int getEdgeCount() {
        return this.edges.size();
    }

    public List<E> getEdges() {
        return this.edges;
    }

    public E getEdge(int index) {
        return (E) this.edges.get(index);
    }

    public void removeEdge(E e) {
        Assert.a(this.edges.remove(e));
        Assert.a(this.edgefrommap.removeElement(e.src.index, e));
        Assert.a(this.edgetomap.removeElement(e.dst.index, e));
    }

    public void removeEdge(int index) {
        E e = (E) this.edges.remove(index);
        Assert.a(this.edgefrommap.removeElement(e.src.index, e));
        Assert.a(this.edgetomap.removeElement(e.dst.index, e));
    }

    public E getEdge(int srcId, int dstId) {
        V src = (V) this.vertexmap.get(Integer.valueOf(srcId));
        Assert.a(src != null, "Source vertex " + srcId + " does not exist");
        for (E e : getEdgesFrom(src.index)) {
            if (e.dst.id == dstId) {
                return e;
            }
        }
        return null;
    }

    List<E> getEdgesFrom(int srcIndex) {
        return this.edgefrommap.get(srcIndex);
    }

    List<E> getEdgesTo(int dstIndex) {
        return this.edgetomap.get(dstIndex);
    }

    public Digraph v(int id, Double weight, String label) {
        addVertex(id, weight, label, true);
        return this;
    }

    public Digraph v(int id, Double weight) {
        return v(id, weight, null);
    }

    public Digraph v(int id) {
        return v(id, null, null);
    }

    private V addVertex(int id, Double weight, String label, boolean failOnDup) {
        verifyNotDone();
        V v = (V) this.vertexmap.get(Integer.valueOf(id));
        if (v == null) {
            v = new V(this.vertices.size(), id, weight, label);
            this.vertices.add(v);
            this.vertexmap.put(Integer.valueOf(id), v);
            return v;
        } else if (!failOnDup) {
            return v;
        } else {
            throw new IllegalArgumentException("Vertex id " + id + " is already in use");
        }
    }

    private static /* synthetic */ boolean lambda$addEdge$2(V b, E e) {
        return e.dst == b;
    }

    public Digraph e(int srcId, int dstId, Double weight) {
        addEdge(srcId, dstId, weight);
        return this;
    }

    public Digraph e(int srcId, int dstId) {
        return e(srcId, dstId, null);
    }

    public Digraph done() {
        verify();
        return this;
    }

    private void verifyNotDone() {
        if (this.done) {
            throw new IllegalStateException();
        }
    }

    public void resetEdgeBetweennessScores() {
        for (E e : this.edges) {
            e.ebscore = Double.valueOf(0.0d);
        }
        for (V v : this.vertices) {
            v.vcscore = Double.valueOf(0.0d);
        }
    }

    private /* synthetic */ int lambda$getEdgeIndexesByDescendingBetweenness$5(Integer a, Integer b) {
        return -Double.compare(((E) this.edges.get(a.intValue())).ebscore.doubleValue(), ((E) this.edges.get(b.intValue())).ebscore.doubleValue());
    }

    private /* synthetic */ int lambda$getVertexIndexesByDescendingCentrality$6(Integer a, Integer b) {
        return -Double.compare(((V) this.vertices.get(a.intValue())).vcscore.doubleValue(), ((V) this.vertices.get(b.intValue())).vcscore.doubleValue());
    }

    private void computeEdgeBetweennessFromSingleNode(int startNodeIndex) {
        E e;
        List<E> level;
        for (E e2 : this.edges) {
            e2.score = null;
        }
        for (V v : this.vertices) {
            v.score = Double.valueOf(0.0d);
        }
        int[] vPathCounts = new int[getVertexCount()];
        vPathCounts[startNodeIndex] = 1;
        List<List<E>> levels = new ArrayList();
        Set<Integer> seen = new HashSet();
        List<Integer> startIndexes = new ArrayList();
        startIndexes.add(Integer.valueOf(startNodeIndex));
        Set<Integer> nextStartIndexes = new HashSet();
        while (!startIndexes.isEmpty()) {
            level = new ArrayList();
            for (Integer intValue : startIndexes) {
                int srcIndex = intValue.intValue();
                for (E edge : getEdgesFrom(srcIndex)) {
                    int dstIndex = edge.dst.index;
                    if (!seen.contains(Integer.valueOf(dstIndex))) {
                        level.add(edge);
                        nextStartIndexes.add(Integer.valueOf(dstIndex));
                        vPathCounts[dstIndex] = vPathCounts[dstIndex] + vPathCounts[srcIndex];
                    }
                }
            }
            if (!level.isEmpty()) {
                levels.add(level);
            }
            seen.addAll(nextStartIndexes);
            List<Integer> arrayList = new ArrayList(nextStartIndexes);
            nextStartIndexes.clear();
            if (Thread.interrupted()) {
                throw new InterruptionException();
            }
        }
        for (int i = levels.size() - 1; i >= 0; i--) {
            level = (List) levels.get(i);
            for (int j = 0; j < level.size(); j++) {
                E e0 = (E) level.get(j);
                if (e0.score == null) {
                    List<E> coll = new ArrayList();
                    coll.add(e0);
                    V dst = e0.dst;
                    for (int k = j + 1; k < level.size(); k++) {
                        E e2 = (E) level.get(k);
                        if (e2.dst == dst) {
                            coll.add(e2);
                        }
                    }
                    double total = 0.0d;
                    for (E e22 : coll) {
                        total += (double) vPathCounts[e22.src.index];
                    }
                    double s = 1.0d + ((V) this.vertices.get(dst.index)).score.doubleValue();
                    for (E e222 : coll) {
                        double score = s * (((double) vPathCounts[e222.src.index]) / total);
                        e222.score = Double.valueOf(score);
                        V v2 = (V) this.vertices.get(e222.src.index);
                        v2.score = Double.valueOf(v2.score.doubleValue() + score);
                    }
                }
            }
            if (Thread.interrupted()) {
                throw new InterruptionException();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (E e2222 : this.edges) {
            if (e2222.score != null) {
                sb.append(String.format("%s>%s:%.1f,", new Object[]{(e2222).src, (e2222).dst, (e2222).score}));
            }
        }
    }

    public boolean isWeaklyConnected() {
        Set<Integer> startIndexes = new HashSet();
        startIndexes.add(Integer.valueOf(0));
        Set<Integer> seen = new HashSet(startIndexes);
        while (!startIndexes.isEmpty()) {
            Set<Integer> nextStartIndexes = new HashSet();
            for (Integer intValue : startIndexes) {
                int index;
                int startIndex = intValue.intValue();
                for (E edge : getEdgesFrom(startIndex)) {
                    index = edge.dst.index;
                    if (!seen.contains(Integer.valueOf(index))) {
                        nextStartIndexes.add(Integer.valueOf(index));
                        seen.add(Integer.valueOf(index));
                    }
                }
                for (E edge2 : getEdgesTo(startIndex)) {
                    index = edge2.src.index;
                    if (!seen.contains(Integer.valueOf(index))) {
                        nextStartIndexes.add(Integer.valueOf(index));
                        seen.add(Integer.valueOf(index));
                    }
                }
            }
            startIndexes = nextStartIndexes;
        }
        return seen.size() == getVertexCount();
    }

    public List<Digraph> getWeaklyConnectedComponents() {
        List<Digraph> gcomponents = new ArrayList();
        Set<Integer> left = new HashSet(getVertexCount());
        for (int i = 0; i < getVertexCount(); i++) {
            left.add(Integer.valueOf(i));
        }
        int round = 0;
        while (!left.isEmpty()) {
            int index;
            Set<Integer> startIndexes = new HashSet();
            startIndexes.add(left.iterator().next());
            Set<Integer> seen = new HashSet(startIndexes);
            while (!startIndexes.isEmpty()) {
                Set<Integer> nextStartIndexes = new HashSet();
                for (Integer intValue : startIndexes) {
                    int startIndex = intValue.intValue();
                    for (E edge : getEdgesFrom(startIndex)) {
                        index = edge.dst.index;
                        if (!seen.contains(Integer.valueOf(index))) {
                            nextStartIndexes.add(Integer.valueOf(index));
                            seen.add(Integer.valueOf(index));
                        }
                    }
                    for (E edge2 : getEdgesTo(startIndex)) {
                        index = edge2.src.index;
                        if (!seen.contains(Integer.valueOf(index))) {
                            nextStartIndexes.add(Integer.valueOf(index));
                            seen.add(Integer.valueOf(index));
                        }
                    }
                }
                startIndexes = nextStartIndexes;
            }
            if (round == 0 && left.equals(seen)) {
                gcomponents.add(this);
                break;
            }
            Digraph gs = create();
            for (Integer intValue2 : seen) {
                index = intValue2.intValue();
                V vertex = (V) this.vertices.get(index);
                gs.addVertex(vertex.id, vertex.weight, vertex.label, false);
                for (E e : getEdgesFrom(index)) {
                    gs.e(e.src.id, e.dst.id, e.weight);
                }
            }
            gcomponents.add(gs.done());
            left.removeAll(seen);
            round++;
        }
        return gcomponents;
    }

    public String toString() {
        return String.format("V=%s:E=%s", new Object[]{this.vertices, this.edges});
    }

    private void computeTransitiveClosure() {
        int cnt = getVertexCount();
        this.reachabilityIndices = new ArrayList(cnt);
        for (int i = 0; i < cnt; i++) {
            Set<Integer> seen = new HashSet();
            List<Integer> startIndices = new ArrayList();
            Set<Integer> nextStartIndexes = new HashSet();
            startIndices.add(Integer.valueOf(i));
            while (!startIndices.isEmpty()) {
                for (Integer intValue : startIndices) {
                    for (E edge : getEdgesFrom(intValue.intValue())) {
                        int dstIndex = edge.dst.index;
                        if (!seen.contains(Integer.valueOf(dstIndex))) {
                            nextStartIndexes.add(Integer.valueOf(dstIndex));
                        }
                    }
                }
                seen.addAll(nextStartIndexes);
                startIndices = new ArrayList(nextStartIndexes);
                nextStartIndexes.clear();
            }
            this.reachabilityIndices.add(seen);
        }
    }

    public boolean isAdjacent(V from, V to) {
        for (E e : getEdgesFrom(from.index)) {
            if (e.dst == to) {
                return true;
            }
        }
        return false;
    }

    public boolean canReach(V from, V to) {
        if (isAdjacent(from, to)) {
            return true;
        }
        if (this.reachabilityIndices == null) {
            computeTransitiveClosure();
        }
        if (((Set) this.reachabilityIndices.get(from.index)) == null) {
            return false;
        }
        return ((Set) this.reachabilityIndices.get(from.index)).contains(Integer.valueOf(to.index));
    }

    public Set<Integer> getReachableVertices(int fromId) {
        V src = (V) this.vertexmap.get(Integer.valueOf(fromId));
        Assert.a(src != null, "Vertex id does not exist: " + fromId);
        if (this.reachabilityIndices == null) {
            computeTransitiveClosure();
        }
        Set<Integer> indices = (Set) this.reachabilityIndices.get(src.index);
        Set<Integer> r = new HashSet();
        for (Integer intValue : indices) {
            r.add(Integer.valueOf(((V) this.vertices.get(intValue.intValue())).id));
        }
        return r;
    }
}