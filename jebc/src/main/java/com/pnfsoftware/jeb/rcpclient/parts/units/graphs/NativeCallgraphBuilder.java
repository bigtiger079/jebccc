
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.core.output.code.coordinates.ICodeCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.InstructionCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.MethodCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.NativeCoordinates;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.EntryPointDescription;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.CallGraphVertex;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.ICallGraph;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeModel;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
import com.pnfsoftware.jeb.util.collect.MultiMap;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.collect.WeakValueMap;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NativeCallgraphBuilder implements ICallgraphBuilder {
    private INativeCodeUnit<IInstruction> unit;
    private Digraph model;
    private WeakValueMap<Integer, INativeMethodItem> vertexIdToMethodObject;
    private WeakIdentityHashMap<INativeMethodItem, Integer> methodObjectToVertexId;

    public NativeCallgraphBuilder(INativeCodeUnit<IInstruction> unit) {
        this.unit = unit;
    }

    public Digraph buildModel() {
        int i0;
        List<? extends INativeMethodItem> methods = this.unit.getInternalMethods();
        ICallGraph cg = this.unit.getCodeModel().getCallGraphManager().getGlobalCallGraph();
        MultiMap<Integer, Integer> mm = new MultiMap();
        for (INativeMethodItem src : methods) {
            for (CallGraphVertex target : cg.getCallees(src, false)) {
                if (target.isInternal()) {
                    i0 = methods.indexOf(src);
                    INativeMethodItem dst = this.unit.getInternalMethod(target.getInternalAddress().getAddress(), true);
                    if (dst != null) {
                        int i1 = methods.indexOf(dst);
                        if (!mm.getSafe(Integer.valueOf(i0)).contains(Integer.valueOf(i1))) {
                            mm.put(Integer.valueOf(i0), Integer.valueOf(i1));
                        }
                    }
                }
            }
        }
        this.model = new Digraph();
        this.vertexIdToMethodObject = new WeakValueMap();
        this.methodObjectToVertexId = new WeakIdentityHashMap();
        int i = 0;
        for (INativeMethodItem m : methods) {
            int insncnt = m.getData().getCFG().getInstructionCount();
            this.model.v(i, Double.valueOf((double) insncnt), m.getName(true));
            this.vertexIdToMethodObject.put(Integer.valueOf(i), m);
            this.methodObjectToVertexId.put(m, Integer.valueOf(i));
            i++;
        }
        for (Integer intValue : mm.keySet()) {
            i0 = intValue.intValue();
            for (Integer intValue2 : mm.getSafe(Integer.valueOf(i0))) {
                this.model.e(i0, intValue2.intValue());
            }
        }
        return this.model;
    }

    public String getAddressForVertexId(int vertexId) {
        INativeMethodItem m = this.vertexIdToMethodObject == null ? null : (INativeMethodItem) this.vertexIdToMethodObject.get(Integer.valueOf(vertexId));
        if (m == null) {
            return null;
        }
        return m.getAddress();
    }

    public Integer getVertexIdForAddress(String address) {
        ICodeCoordinates cc = this.unit.getCodeCoordinatesFromAddress(address);
        INativeMethodItem m = null;
        if ((cc instanceof MethodCoordinates)) {
            int index = ((MethodCoordinates) cc).getMethodId();
            m = this.unit.getMethodByIndex(index);
        } else if ((cc instanceof InstructionCoordinates)) {
            int index = ((InstructionCoordinates) cc).getMethodId();
            m = this.unit.getMethodByIndex(index);
        } else if ((cc instanceof NativeCoordinates)) {
            long a = ((NativeCoordinates) cc).getAddress();
            m = this.unit.getInternalMethod(a, false);
        } else {
            return null;
        }
        if (m == null) {
            return null;
        }
        return (Integer) this.methodObjectToVertexId.get(m);
    }
}


