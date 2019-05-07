/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.code.coordinates.ICodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.InstructionCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.MethodCoordinates;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeClass;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeType;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.model.Digraph;
/*     */ import com.pnfsoftware.jeb.util.collect.MultiMap;
/*     */ import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
/*     */ import com.pnfsoftware.jeb.util.collect.WeakValueMap;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;

/*     */
/*     */
/*     */ public class DalvikCallgraphBuilder
        /*     */ implements ICallgraphBuilder
        /*     */ {
    /*     */   private ICodeUnit unit;
    /*     */   private Digraph model;
    /*     */   private WeakValueMap<Integer, ICodeMethod> vertexIdToMethodObject;
    /*     */   private WeakIdentityHashMap<ICodeMethod, Integer> methodObjectToVertexId;
    /*     */   private String rawfilter;
    /*     */   private int fltCount;

    /*     */
    /*     */
    public DalvikCallgraphBuilder(ICodeUnit unit)
    /*     */ {
        /*  40 */
        this.unit = unit;
        /*     */
    }

    /*     */
    /*     */
    public Digraph buildModel()
    /*     */ {
        /*  45 */
        MultiMap<Integer, Integer> typeToInternalMethods = new MultiMap();
        /*  46 */
        Map<Integer, Integer> methodToType = new HashMap();
        /*  47 */
        Map<Integer, Set<Integer>> methodToMethods = new HashMap();
        /*     */
        /*  49 */
        int edgecnt = 0;
        /*     */
        /*  51 */
        Map<Integer, ICodeMethod> internal_methods = new TreeMap();
        /*  52 */
        for (ICodeMethod method : this.unit.getMethods()) {
            /*  53 */
            if ((method.isInternal()) &&
                    /*     */
                    /*     */
                    /*     */
                    /*  57 */         (filter(method)))
                /*     */ {
                /*     */
                /*     */
                /*  61 */
                internal_methods.put(Integer.valueOf(method.getIndex()), method);
                /*     */
            }
            /*     */
        }
        /*  64 */
        for (ICodeClass classObject : this.unit.getClasses()) {
            /*  65 */
            if (((classObject.getGenericFlags() & 0x100000) == 0) &&
                    /*     */
                    /*     */
                    /*     */
                    /*  69 */         (filter(classObject)))
                /*     */ {
                /*     */
                /*     */
                /*  73 */
                typeIndex = classObject.getClassType().getIndex();
                /*     */
                /*  75 */
                if (classObject.getMethods() != null)
                    /*     */ {
                    /*     */
                    /*     */
                    /*  79 */
                    for (ICodeMethod methodObject : classObject.getMethods()) {
                        /*  80 */
                        methodIndex = methodObject.getIndex();
                        /*  81 */
                        typeToInternalMethods.put(Integer.valueOf(typeIndex), Integer.valueOf(methodIndex));
                        /*  82 */
                        methodToType.put(Integer.valueOf(methodIndex), Integer.valueOf(typeIndex));
                        /*     */
                        /*  84 */
                        List<? extends IInstruction> instructions = methodObject.getInstructions();
                        /*  85 */
                        if (instructions != null)
                            /*     */ {
                            /*     */
                            /*     */
                            /*  89 */
                            for (IInstruction insn : instructions) {
                                /*  90 */
                                String s = insn.format(null);
                                /*  91 */
                                int refMethodIndex = extractMethodIndex(s);
                                /*  92 */
                                if ((refMethodIndex >= 0) && (internal_methods.containsKey(Integer.valueOf(refMethodIndex)))) {
                                    /*  93 */
                                    Set<Integer> set = (Set) methodToMethods.get(Integer.valueOf(methodIndex));
                                    /*  94 */
                                    if (set == null) {
                                        /*  95 */
                                        set = new TreeSet();
                                        /*  96 */
                                        methodToMethods.put(Integer.valueOf(methodIndex), set);
                                        /*     */
                                    }
                                    /*  98 */
                                    if (set.add(Integer.valueOf(refMethodIndex))) {
                                        /*  99 */
                                        edgecnt++;
                                        /*     */
                                    }
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        int typeIndex;
        /*     */
        /*     */
        /*     */
        int methodIndex;
        /*     */
        /*     */
        /* 116 */
        this.model = new Digraph();
        /* 117 */
        this.vertexIdToMethodObject = new WeakValueMap();
        /* 118 */
        this.methodObjectToVertexId = new WeakIdentityHashMap();
        /*     */
        /* 120 */
        for (ICodeMethod m : internal_methods.values()) {
            /* 121 */
            name = m.getClassType().getName(true) + "." + m.getName(true);
            /* 122 */
            int insncount = m.getInstructions() == null ? 0 : m.getInstructions().size();
            /* 123 */
            this.model.v(m.getIndex(), Double.valueOf(insncount), name);
            /* 124 */
            this.vertexIdToMethodObject.put(Integer.valueOf(m.getIndex()), m);
            /* 125 */
            this.methodObjectToVertexId.put(m, Integer.valueOf(m.getIndex()));
        }
        /*     */
        String name;
        /* 127 */
        for (??? =methodToMethods.keySet().iterator(); ???.hasNext();){
            i0 = ((Integer) ? ??.next()).intValue();
            /* 128 */
            for (name = ((Set) methodToMethods.get(Integer.valueOf(i0))).iterator(); name.hasNext(); ) {
                int i1 = ((Integer) name.next()).intValue();
                /* 129 */
                this.model.e(i0, i1);
                /*     */
            }
            /*     */
        }
        /*     */
        int i0;
        /* 133 */
        return this.model;
        /*     */
    }

    /*     */
    /*     */
    private static int extractMethodIndex(String s)
    /*     */ {
        /* 138 */
        if (s.startsWith("invoke")) {
            /* 139 */
            int i = s.indexOf("method@");
            /* 140 */
            if (i >= 0) {
                /* 141 */
                i += 7;
                /* 142 */
                int j = s.indexOf(",", i);
                /* 143 */
                if (j < 0) {
                    /* 144 */
                    j = s.length();
                    /*     */
                }
                /* 146 */
                return Integer.parseInt(s.substring(i, j));
                /*     */
            }
            /*     */
        }
        /* 149 */
        return -1;
        /*     */
    }

    /*     */
    /*     */
    public String getAddressForVertexId(int vertexId)
    /*     */ {
        /* 154 */
        ICodeMethod m = this.vertexIdToMethodObject == null ? null : (ICodeMethod) this.vertexIdToMethodObject.get(Integer.valueOf(vertexId));
        /* 155 */
        if (m == null) {
            /* 156 */
            return null;
            /*     */
        }
        /* 158 */
        return m.getAddress();
        /*     */
    }

    /*     */
    /*     */
    public Integer getVertexIdForAddress(String address)
    /*     */ {
        /* 163 */
        ICodeCoordinates cc = this.unit.getCodeCoordinatesFromAddress(address);
        /* 164 */
        Integer index = null;
        /* 165 */
        if ((cc instanceof InstructionCoordinates)) {
            /* 166 */
            index = Integer.valueOf(((InstructionCoordinates) cc).getMethodId());
            /*     */
        }
        /* 168 */
        else if ((cc instanceof MethodCoordinates)) {
            /* 169 */
            index = Integer.valueOf(((MethodCoordinates) cc).getMethodId());
            /*     */
        }
        /*     */
        else {
            /* 172 */
            return null;
            /*     */
        }
        /*     */
        /* 175 */
        ICodeMethod m = (ICodeMethod) this.unit.getMethods().get(index.intValue());
        /* 176 */
        if (m == null) {
            /* 177 */
            return null;
            /*     */
        }
        /*     */
        /* 180 */
        return (Integer) this.methodObjectToVertexId.get(m);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /* 185 */   private Set<String> wlFltFull = new HashSet();
    /* 186 */   private List<String> wlFltStart = new ArrayList();
    /* 187 */   private Set<String> blFltFull = new HashSet();
    /* 188 */   private List<String> blFltStart = new ArrayList();

    /*     */
    /*     */
    public String getFilter() {
        /* 191 */
        return this.rawfilter;
        /*     */
    }

    /*     */
    /*     */
    public void setFilter(String filter)
    /*     */ {
        /* 196 */
        this.rawfilter = filter;
        /* 197 */
        this.wlFltStart.clear();
        /* 198 */
        this.blFltStart.clear();
        /*     */
        /* 200 */
        if (this.rawfilter == null) {
            /* 201 */
            return;
            /*     */
        }
        /*     */
        /* 204 */
        this.fltCount = 0;
        /* 205 */
        for (String line : Strings.splitLines(this.rawfilter)) {
            /* 206 */
            line = Strings.trim(line);
            /* 207 */
            if ((!line.isEmpty()) && (!line.startsWith("#")))
                /*     */ {
                /*     */
                /*     */
                /* 211 */
                boolean blacklisted = false;
                /* 212 */
                if (line.startsWith("-")) {
                    /* 213 */
                    line = line.substring(1);
                    /* 214 */
                    if (!line.isEmpty())
                        /*     */ {
                        /*     */
                        /* 217 */
                        blacklisted = true;
                        /*     */
                    }
                    /*     */
                } else {
                    /* 220 */
                    if (line.endsWith("*")) {
                        /* 221 */
                        line = line.substring(0, line.length() - 1);
                        /* 222 */
                        if (blacklisted) {
                            /* 223 */
                            this.blFltStart.add(line);
                            /*     */
                        }
                        /*     */
                        else {
                            /* 226 */
                            this.wlFltStart.add(line);
                            /*     */
                        }
                        /*     */
                        /*     */
                    }
                    /* 230 */
                    else if (blacklisted) {
                        /* 231 */
                        this.blFltFull.add(line);
                        /*     */
                    }
                    /*     */
                    else {
                        /* 234 */
                        this.wlFltFull.add(line);
                        /*     */
                    }
                    /*     */
                    /* 237 */
                    this.fltCount += 1;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
    }

    /*     */
    /* 242 */
    private boolean filter(ICodeItem item) {
        if (this.fltCount == 0) {
            /* 243 */
            return true;
            /*     */
        }
        /*     */
        /* 246 */
        String sig = item.getSignature(true);
        /* 247 */
        if (sig == null) {
            /* 248 */
            return false;
            /*     */
        }
        /* 250 */
        int pos = sig.indexOf(";");
        /* 251 */
        if (pos < 2) {
            /* 252 */
            return false;
            /*     */
        }
        /* 254 */
        String pname = sig.substring(1, pos).replace('/', '.');
        /*     */
        /*     */
        boolean proceed;
        /* 257 */
        if (((!this.wlFltFull.isEmpty()) || (!this.wlFltStart.isEmpty())) &&
                /* 258 */       (!this.wlFltFull.contains(pname))) {
            /* 259 */
            proceed = false;
            /* 260 */
            for (String f : this.wlFltStart) {
                /* 261 */
                if (pname.startsWith(f)) {
                    /* 262 */
                    proceed = true;
                    /* 263 */
                    break;
                    /*     */
                }
                /*     */
            }
            /* 266 */
            if (!proceed) {
                /* 267 */
                return false;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 273 */
        if (this.blFltFull.contains(pname)) {
            /* 274 */
            return false;
            /*     */
        }
        /* 276 */
        for (String f : this.blFltStart) {
            /* 277 */
            if (pname.startsWith(f)) {
                /* 278 */
                return false;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 283 */
        return true;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\DalvikCallgraphBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */