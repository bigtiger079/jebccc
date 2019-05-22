package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.core.output.code.coordinates.ICodeCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.InstructionCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.MethodCoordinates;
import com.pnfsoftware.jeb.core.units.code.ICodeClass;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
import com.pnfsoftware.jeb.util.collect.MultiMap;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.collect.WeakValueMap;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DalvikCallgraphBuilder implements ICallgraphBuilder {
    private ICodeUnit unit;
    private Digraph model;
    private WeakValueMap<Integer, ICodeMethod> vertexIdToMethodObject;
    private WeakIdentityHashMap<ICodeMethod, Integer> methodObjectToVertexId;
    private String rawfilter;
    private int fltCount;

    public DalvikCallgraphBuilder(ICodeUnit unit) {
        this.unit = unit;
    }

    public Digraph buildModel() {
        MultiMap<Integer, Integer> typeToInternalMethods = new MultiMap();
        Map<Integer, Integer> methodToType = new HashMap();
        Map<Integer, Set<Integer>> methodToMethods = new HashMap();
        int edgecnt = 0;
        Map<Integer, ICodeMethod> internal_methods = new TreeMap();
        for (ICodeMethod method : this.unit.getMethods()) {
            if ((method.isInternal()) && (filter(method))) {
                internal_methods.put(method.getIndex(), method);
            }
        }
        int typeIndex;
        int methodIndex;
        for (ICodeClass classObject : this.unit.getClasses()) {
            if (((classObject.getGenericFlags() & 0x100000) == 0) && (filter(classObject))) {
                typeIndex = classObject.getClassType().getIndex();
                if (classObject.getMethods() != null) {
                    for (ICodeMethod methodObject : classObject.getMethods()) {
                        methodIndex = methodObject.getIndex();
                        typeToInternalMethods.put(typeIndex, methodIndex);
                        methodToType.put(methodIndex, typeIndex);
                        List<? extends IInstruction> instructions = methodObject.getInstructions();
                        if (instructions != null) {
                            for (IInstruction insn : instructions) {
                                String s = insn.format(null);
                                int refMethodIndex = extractMethodIndex(s);
                                if ((refMethodIndex >= 0) && (internal_methods.containsKey(refMethodIndex))) {
                                    Set<Integer> set = methodToMethods.get(methodIndex);
                                    if (set == null) {
                                        set = new TreeSet();
                                        methodToMethods.put(methodIndex, set);
                                    }
                                    if (set.add(refMethodIndex)) {
                                        edgecnt++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        String name;
        this.model = new Digraph();
        this.vertexIdToMethodObject = new WeakValueMap();
        this.methodObjectToVertexId = new WeakIdentityHashMap();
        for (ICodeMethod m : internal_methods.values()) {
            name = m.getClassType().getName(true) + "." + m.getName(true);
            int insncount = m.getInstructions() == null ? 0 : m.getInstructions().size();
            this.model.v(m.getIndex(), (double) insncount, name);
            this.vertexIdToMethodObject.put(m.getIndex(), m);
            this.methodObjectToVertexId.put(m, m.getIndex());
        }
        Iterator<Integer> iterator = methodToMethods.keySet().iterator();
        int i0;
        while (iterator.hasNext()) {
            i0 = iterator.next();
            for (Object o : ((Set) methodToMethods.get(i0))) {
                int i1 = (Integer) o;
                this.model.e(i0, i1);
            }
        }
        return this.model;
    }

    private static int extractMethodIndex(String s) {
        if (s.startsWith("invoke")) {
            int i = s.indexOf("method@");
            if (i >= 0) {
                i += 7;
                int j = s.indexOf(",", i);
                if (j < 0) {
                    j = s.length();
                }
                return Integer.parseInt(s.substring(i, j));
            }
        }
        return -1;
    }

    public String getAddressForVertexId(int vertexId) {
        ICodeMethod m = this.vertexIdToMethodObject == null ? null : this.vertexIdToMethodObject.get(vertexId);
        if (m == null) {
            return null;
        }
        return m.getAddress();
    }

    public Integer getVertexIdForAddress(String address) {
        ICodeCoordinates cc = this.unit.getCodeCoordinatesFromAddress(address);
        Integer index = null;
        if ((cc instanceof InstructionCoordinates)) {
            index = ((InstructionCoordinates) cc).getMethodId();
        } else if ((cc instanceof MethodCoordinates)) {
            index = ((MethodCoordinates) cc).getMethodId();
        } else {
            return null;
        }
        ICodeMethod m = this.unit.getMethods().get(index);
        if (m == null) {
            return null;
        }
        return this.methodObjectToVertexId.get(m);
    }

    private Set<String> wlFltFull = new HashSet();
    private List<String> wlFltStart = new ArrayList<>();
    private Set<String> blFltFull = new HashSet();
    private List<String> blFltStart = new ArrayList<>();

    public String getFilter() {
        return this.rawfilter;
    }

    public void setFilter(String filter) {
        this.rawfilter = filter;
        this.wlFltStart.clear();
        this.blFltStart.clear();
        if (this.rawfilter == null) {
            return;
        }
        this.fltCount = 0;
        for (String line : Strings.splitLines(this.rawfilter)) {
            line = Strings.trim(line);
            if ((!line.isEmpty()) && (!line.startsWith("#"))) {
                boolean blacklisted = false;
                if (line.startsWith("-")) {
                    line = line.substring(1);
                    if (!line.isEmpty()) {
                        blacklisted = true;
                    }
                } else {
                    if (line.endsWith("*")) {
                        line = line.substring(0, line.length() - 1);
                        if (blacklisted) {
                            this.blFltStart.add(line);
                        } else {
                            this.wlFltStart.add(line);
                        }
                    } else if (blacklisted) {
                        this.blFltFull.add(line);
                    } else {
                        this.wlFltFull.add(line);
                    }
                    this.fltCount += 1;
                }
            }
        }
    }

    private boolean filter(ICodeItem item) {
        if (this.fltCount == 0) {
            return true;
        }
        String sig = item.getSignature(true);
        if (sig == null) {
            return false;
        }
        int pos = sig.indexOf(";");
        if (pos < 2) {
            return false;
        }
        String pname = sig.substring(1, pos).replace('/', '.');
        boolean proceed;
        if (((!this.wlFltFull.isEmpty()) || (!this.wlFltStart.isEmpty())) && (!this.wlFltFull.contains(pname))) {
            proceed = false;
            for (String f : this.wlFltStart) {
                if (pname.startsWith(f)) {
                    proceed = true;
                    break;
                }
            }
            if (!proceed) {
                return false;
            }
        }
        if (this.blFltFull.contains(pname)) {
            return false;
        }
        for (String f : this.blFltStart) {
            if (pname.startsWith(f)) {
                return false;
            }
        }
        return true;
    }
}


