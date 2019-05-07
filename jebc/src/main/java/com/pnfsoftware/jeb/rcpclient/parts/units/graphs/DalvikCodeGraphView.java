/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.ICodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.InstructionCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.code.coordinates.MethodCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextPartAsDocumentProxy;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.dex.IDalvikInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.dex.IDexCodeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.dex.IDexMethod;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.dex.IDexMethodData;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.render.DexDisassemblyProperties;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.render.IDexDisassemblyDocument;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
/*     */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.util.base.Couple;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.widgets.Composite;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class DalvikCodeGraphView
        /*     */ extends AbstractControlFlowGraphView<IDexUnit>
        /*     */ {
    /*  52 */   private static final ILogger logger = GlobalLog.getLogger(DalvikCodeGraphView.class);
    /*     */
    /*     */   private IDexDisassemblyDocument disasDoc;
    /*     */   private IDexMethod currentMethod;

    /*     */
    /*     */
    public DalvikCodeGraphView(Composite parent, int style, RcpClientContext context, IDexUnit unit, IRcpUnitView unitView)
    /*     */ {
        /*  59 */
        super(parent, style, unit, unitView, context);
        /*     */
    }

    /*     */
    /*     */
    private Couple<IDexMethod, Integer> processAddress(String address) {
        /*  63 */
        ICodeCoordinates cc = ((IDexUnit) this.unit).getCodeCoordinatesFromAddress(address);
        /*  64 */
        if (cc == null) {
            /*  65 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        int offset;
        /*     */
        /*  70 */
        if ((cc instanceof MethodCoordinates)) {
            /*  71 */
            int methodId = ((MethodCoordinates) cc).getMethodId();
            /*  72 */
            offset = 0;
            /*     */
        } else {
            int offset;
            /*  74 */
            if ((cc instanceof InstructionCoordinates)) {
                /*  75 */
                int methodId = ((InstructionCoordinates) cc).getMethodId();
                /*  76 */
                offset = ((InstructionCoordinates) cc).getOffset();
                /*     */
            }
            /*     */
            else {
                /*  79 */
                return null;
            }
        }
        /*     */
        int offset;
        /*     */
        int methodId;
        /*  82 */
        IDexMethod method = ((IDexUnit) this.unit).getMethod(methodId);
        /*  83 */
        if (method == null) {
            /*  84 */
            return null;
            /*     */
        }
        /*     */
        /*  87 */
        return new Couple(method, Integer.valueOf(offset));
        /*     */
    }

    /*     */
    /*     */
    /*     */
    protected String buildAddress(long offset)
    /*     */ {
        /*  93 */
        return ((IDexUnit) this.unit).getAddressFromCodeCoordinates(new InstructionCoordinates(this.currentMethod.getIndex(), (int) offset));
        /*     */
    }

    /*     */
    /*     */
    public boolean canDisplayAtAddress(String address)
    /*     */ {
        /*  98 */
        return processAddress(address) != null;
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /* 103 */
        return processAddress(address) != null;
        /*     */
    }

    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /* 108 */
        Couple<IDexMethod, Integer> details = processAddress(address);
        /* 109 */
        if (details == null) {
            /* 110 */
            return false;
            /*     */
        }
        /*     */
        /* 113 */
        GlobalPosition pos0 = (!record) || (getViewManager() == null) ? null : getViewManager().getCurrentGlobalPosition();
        /*     */
        /* 115 */
        IDexMethod method = (IDexMethod) details.getFirst();
        /* 116 */
        int offset = ((Integer) details.getSecond()).intValue();
        /*     */
        /*     */
        /* 119 */
        if ((method.getData() == null) || (method.getData().getCodeItem() == null)) {
            /* 120 */
            return false;
            /*     */
        }
        /*     */
        /* 123 */
        if (method != this.currentMethod) {
            /* 124 */
            replaceGraph(method);
            /*     */
        }
        /*     */
        /* 127 */
        if (pos0 != null) {
            /* 128 */
            getViewManager().recordGlobalPosition(pos0);
            /*     */
        }
        /*     */
        /* 131 */
        GraphNode node = findNodeByInstructionAddress(offset);
        /* 132 */
        if (node == null) {
            /* 133 */
            return false;
            /*     */
        }
        /*     */
        /* 136 */
        Graph g = getGraph();
        /* 137 */
        g.showNode(node, true);
        /* 138 */
        g.setActiveNode(node, false);
        /*     */
        /* 140 */
        NodeContentsInteractiveTextView contents = (NodeContentsInteractiveTextView) node.getContents();
        /* 141 */
        return contents.setActiveAddress(address);
        /*     */
    }

    /*     */
    /*     */
    protected boolean doItemFollow()
    /*     */ {
        /* 146 */
        IItem item = getActiveItem();
        /* 147 */
        if ((item instanceof IActionableItem)) {
            /* 148 */
            long itemId = ((IActionableItem) item).getItemId();
            /* 149 */
            String address = ((IDexUnit) this.unit).getAddressOfItem(itemId);
            /* 150 */
            if (address != null) {
                /* 151 */
                setActiveAddress(address);
                /*     */
            }
            /*     */
        }
        /* 154 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    protected ITextDocument getTextForBlock(final BasicBlock<IInstruction> b)
    /*     */ {
        /* 159 */
        new AbstractTextPartAsDocumentProxy(this.disasDoc)
                /*     */ {
            /*     */
            protected ITextDocumentPart getPartAsDocument() {
                /* 162 */
                return ((IDexDisassemblyDocument) getFullDocument()).getItemDisassembly(new InstructionCoordinates(
                        /* 163 */           DalvikCodeGraphView.this.currentMethod.getIndex(), (int) b.getFirstAddress()));
                /*     */
            }
            /*     */
        };
        /*     */
    }

    /*     */
    /*     */
    private void replaceGraph(IDexMethod method) {
        /* 169 */
        reset(true);
        /* 170 */
        this.currentMethod = method;
        /*     */
        /*     */
        /* 173 */
        Map<Long, IDalvikInstruction> insnmap = new HashMap();
        /*     */
        /* 175 */
        com.pnfsoftware.jeb.core.units.code.android.controlflow.CFG<? extends IDalvikInstruction> cfg0 = method.getData().getCodeItem().getControlFlowGraph();
        /* 176 */
        if (cfg0 == null) {
            /* 177 */
            this.currentMethod = null;
            /* 178 */
            return;
            /*     */
        }
        /*     */
        /* 181 */
        for (Iterator localIterator = cfg0.getInstructions().iterator(); localIterator.hasNext(); ) {
            insn = (IDalvikInstruction) localIterator.next();
            /* 182 */
            insnmap.put(Long.valueOf(insn.getOffset()), insn);
        }
        /*     */
        IDalvikInstruction insn;
        /* 184 */
        Object irrdata = new ArrayList();
        /* 185 */
        for (com.pnfsoftware.jeb.core.units.code.android.controlflow.IrregularFlowData o : cfg0.generateIrregularFlowDataObjects()) {
            /* 186 */
            ((List) irrdata).add(new com.pnfsoftware.jeb.core.units.code.asm.cfg.IrregularFlowData(o.getFirstAddress(), o.getLastAddress(), o.getTargetAddress()));
            /*     */
        }
        /*     */
        /*     */
        /* 190 */
        com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG<IInstruction> cfg = new com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG(insnmap, (List) irrdata, null, 0L, 3);
        /*     */
        /* 192 */
        this.disasDoc = ((IDexUnit) this.unit).getDisassemblyDocument();
        /* 193 */
        DexDisassemblyProperties propertyOverrides = new DexDisassemblyProperties();
        /* 194 */
        IPropertyManager pm = this.context.getPropertyManager();
        /* 195 */
        propertyOverrides.setShowAddresses(Boolean.valueOf(pm.getBoolean(".ui.text.cfg.ShowAddresses")));
        /* 196 */
        propertyOverrides.setShowBytecode(Boolean.valueOf(pm.getInteger(".ui.text.cfg.ShowBytesCount") > 0));
        /* 197 */
        this.disasDoc.setPropertyOverrides(propertyOverrides);
        /*     */
        /* 199 */
        generateGraphForCFG(cfg);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\DalvikCodeGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */