/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.code.coordinates.ICodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.InstructionCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.MethodCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.NativeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.EntryPointDescription;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.CallGraphVertex;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.ICallGraph;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeModel;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
/*     */ import com.pnfsoftware.jeb.util.collect.MultiMap;
/*     */ import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
/*     */ import com.pnfsoftware.jeb.util.collect.WeakValueMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;

/*     */
/*     */ public class NativeCallgraphBuilder implements ICallgraphBuilder
        /*     */ {
    /*     */   private INativeCodeUnit<IInstruction> unit;
    /*     */   private Digraph model;
    /*     */   private WeakValueMap<Integer, INativeMethodItem> vertexIdToMethodObject;
    /*     */   private WeakIdentityHashMap<INativeMethodItem, Integer> methodObjectToVertexId;

    /*     */
    /*     */
    public NativeCallgraphBuilder(INativeCodeUnit<IInstruction> unit)
    /*     */ {
        /*  33 */
        this.unit = unit;
        /*     */
    }

    /*     */
    /*     */
    public Digraph buildModel()
    /*     */ {
        /*  38 */
        List<? extends INativeMethodItem> methods = this.unit.getInternalMethods();
        /*  39 */
        ICallGraph cg = this.unit.getCodeModel().getCallGraphManager().getGlobalCallGraph();
        /*     */
        /*  41 */
        MultiMap<Integer, Integer> mm = new MultiMap();
        /*  42 */
        for (Iterator localIterator1 = methods.iterator(); localIterator1.hasNext(); ) {
            src = (INativeMethodItem) localIterator1.next();
            /*  43 */
            List<CallGraphVertex> dstlist = cg.getCallees(src, false);
            /*  44 */
            for (CallGraphVertex target : dstlist) {
                /*  45 */
                if (target.isInternal())
                    /*     */ {
                    /*     */
                    /*  48 */
                    int i0 = methods.indexOf(src);
                    /*  49 */
                    INativeMethodItem dst = this.unit.getInternalMethod(target.getInternalAddress().getAddress(), true);
                    /*  50 */
                    if (dst != null)
                        /*     */ {
                        /*     */
                        /*  53 */
                        int i1 = methods.indexOf(dst);
                        /*  54 */
                        if (!mm.getSafe(Integer.valueOf(i0)).contains(Integer.valueOf(i1)))
                            /*  55 */ mm.put(Integer.valueOf(i0), Integer.valueOf(i1));
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*  60 */
        this.model = new Digraph();
        /*     */
        /*  62 */
        this.vertexIdToMethodObject = new WeakValueMap();
        /*  63 */
        this.methodObjectToVertexId = new WeakIdentityHashMap();
        /*     */
        /*  65 */
        int i = 0;
        /*  66 */
        for (INativeMethodItem m : methods) {
            /*  67 */
            insncnt = m.getData().getCFG().getInstructionCount();
            /*  68 */
            this.model.v(i, Double.valueOf(insncnt), m.getName(true));
            /*  69 */
            this.vertexIdToMethodObject.put(Integer.valueOf(i), m);
            /*  70 */
            this.methodObjectToVertexId.put(m, Integer.valueOf(i));
            /*  71 */
            i++;
        }
        /*     */
        int insncnt;
        /*  73 */
        for (INativeMethodItem src = mm.keySet().iterator(); src.hasNext(); ) {
            i0 = ((Integer) src.next()).intValue();
            /*  74 */
            for (localIterator3 = mm.getSafe(Integer.valueOf(i0)).iterator(); localIterator3.hasNext(); ) {
                int i1 = ((Integer) localIterator3.next()).intValue();
                /*  75 */
                this.model.e(i0, i1);
                /*     */
            }
        }
        /*     */
        int i0;
        /*     */
        Iterator localIterator3;
        /*  79 */
        return this.model;
        /*     */
    }

    /*     */
    /*     */
    public String getAddressForVertexId(int vertexId)
    /*     */ {
        /*  84 */
        INativeMethodItem m = this.vertexIdToMethodObject == null ? null : (INativeMethodItem) this.vertexIdToMethodObject.get(Integer.valueOf(vertexId));
        /*  85 */
        if (m == null) {
            /*  86 */
            return null;
            /*     */
        }
        /*  88 */
        return m.getAddress();
        /*     */
    }

    /*     */
    /*     */
    public Integer getVertexIdForAddress(String address)
    /*     */ {
        /*  93 */
        ICodeCoordinates cc = this.unit.getCodeCoordinatesFromAddress(address);
        /*  94 */
        INativeMethodItem m = null;
        /*  95 */
        if ((cc instanceof MethodCoordinates)) {
            /*  96 */
            int index = ((MethodCoordinates) cc).getMethodId();
            /*  97 */
            m = this.unit.getMethodByIndex(index);
            /*     */
        }
        /*  99 */
        else if ((cc instanceof InstructionCoordinates)) {
            /* 100 */
            int index = ((InstructionCoordinates) cc).getMethodId();
            /* 101 */
            m = this.unit.getMethodByIndex(index);
            /*     */
        }
        /* 103 */
        else if ((cc instanceof NativeCoordinates)) {
            /* 104 */
            long a = ((NativeCoordinates) cc).getAddress();
            /* 105 */
            m = this.unit.getInternalMethod(a, false);
            /*     */
        }
        /*     */
        else {
            /* 108 */
            return null;
            /*     */
        }
        /*     */
        /* 111 */
        if (m == null) {
            /* 112 */
            return null;
            /*     */
        }
        /* 114 */
        return (Integer) this.methodObjectToVertexId.get(m);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\NativeCallgraphBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */