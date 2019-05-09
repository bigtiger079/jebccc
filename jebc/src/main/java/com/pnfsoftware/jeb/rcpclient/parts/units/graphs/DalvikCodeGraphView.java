package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.code.coordinates.ICodeCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.InstructionCoordinates;
import com.pnfsoftware.jeb.core.output.code.coordinates.MethodCoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextPartAsDocumentProxy;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
import com.pnfsoftware.jeb.core.units.code.android.dex.IDalvikInstruction;
import com.pnfsoftware.jeb.core.units.code.android.dex.IDexCodeItem;
import com.pnfsoftware.jeb.core.units.code.android.dex.IDexMethod;
import com.pnfsoftware.jeb.core.units.code.android.dex.IDexMethodData;
import com.pnfsoftware.jeb.core.units.code.android.render.DexDisassemblyProperties;
import com.pnfsoftware.jeb.core.units.code.android.render.IDexDisassemblyDocument;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.util.base.Couple;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

public class DalvikCodeGraphView extends AbstractControlFlowGraphView<IDexUnit> {
    private static final ILogger logger = GlobalLog.getLogger(DalvikCodeGraphView.class);
    private IDexDisassemblyDocument disasDoc;
    private IDexMethod currentMethod;

    public DalvikCodeGraphView(Composite parent, int style, RcpClientContext context, IDexUnit unit, IRcpUnitView unitView) {
        super(parent, style, unit, unitView, context);
    }

    private Couple<IDexMethod, Integer> processAddress(String address) {
        ICodeCoordinates cc = ((IDexUnit) this.unit).getCodeCoordinatesFromAddress(address);
        if (cc == null) {
            return null;
        }
        int offset;
        int methodId;
        if ((cc instanceof MethodCoordinates)) {
            methodId = ((MethodCoordinates) cc).getMethodId();
            offset = 0;
        } else {
            if ((cc instanceof InstructionCoordinates)) {
                methodId = ((InstructionCoordinates) cc).getMethodId();
                offset = ((InstructionCoordinates) cc).getOffset();
            } else {
                return null;
            }
        }
        IDexMethod method = ((IDexUnit) this.unit).getMethod(methodId);
        if (method == null) {
            return null;
        }
        return new Couple(method, Integer.valueOf(offset));
    }

    protected String buildAddress(long offset) {
        return ((IDexUnit) this.unit).getAddressFromCodeCoordinates(new InstructionCoordinates(this.currentMethod.getIndex(), (int) offset));
    }

    public boolean canDisplayAtAddress(String address) {
        return processAddress(address) != null;
    }

    public boolean isValidActiveAddress(String address, Object object) {
        return processAddress(address) != null;
    }

    public boolean setActiveAddress(String address, Object extra, boolean record) {
        Couple<IDexMethod, Integer> details = processAddress(address);
        if (details == null) {
            return false;
        }
        GlobalPosition pos0 = (!record) || (getViewManager() == null) ? null : getViewManager().getCurrentGlobalPosition();
        IDexMethod method = (IDexMethod) details.getFirst();
        int offset = ((Integer) details.getSecond()).intValue();
        if ((method.getData() == null) || (method.getData().getCodeItem() == null)) {
            return false;
        }
        if (method != this.currentMethod) {
            replaceGraph(method);
        }
        if (pos0 != null) {
            getViewManager().recordGlobalPosition(pos0);
        }
        GraphNode node = findNodeByInstructionAddress(offset);
        if (node == null) {
            return false;
        }
        Graph g = getGraph();
        g.showNode(node, true);
        g.setActiveNode(node, false);
        NodeContentsInteractiveTextView contents = (NodeContentsInteractiveTextView) node.getContents();
        return contents.setActiveAddress(address);
    }

    protected boolean doItemFollow() {
        IItem item = getActiveItem();
        if ((item instanceof IActionableItem)) {
            long itemId = ((IActionableItem) item).getItemId();
            String address = ((IDexUnit) this.unit).getAddressOfItem(itemId);
            if (address != null) {
                setActiveAddress(address);
            }
        }
        return false;
    }

    protected ITextDocument getTextForBlock(final BasicBlock<IInstruction> b) {
        return new AbstractTextPartAsDocumentProxy(this.disasDoc) {
            protected ITextDocumentPart getPartAsDocument() {
                return ((IDexDisassemblyDocument) getFullDocument()).getItemDisassembly(new InstructionCoordinates(DalvikCodeGraphView.this.currentMethod.getIndex(), (int) b.getFirstAddress()));
            }
        };
    }

    private void replaceGraph(IDexMethod method) {
        reset(true);
        this.currentMethod = method;
        Map<Long, IDalvikInstruction> insnmap = new HashMap();
        com.pnfsoftware.jeb.core.units.code.android.controlflow.CFG<? extends IDalvikInstruction> cfg0 = method.getData().getCodeItem().getControlFlowGraph();
        if (cfg0 == null) {
            this.currentMethod = null;
            return;
        }
        IDalvikInstruction insn;
        for (Iterator localIterator = cfg0.getInstructions().iterator(); localIterator.hasNext(); ) {
            insn = (IDalvikInstruction) localIterator.next();
            insnmap.put(Long.valueOf(insn.getOffset()), insn);
        }
        Object irrdata = new ArrayList<>();
        for (com.pnfsoftware.jeb.core.units.code.android.controlflow.IrregularFlowData o : cfg0.generateIrregularFlowDataObjects()) {
            ((List) irrdata).add(new com.pnfsoftware.jeb.core.units.code.asm.cfg.IrregularFlowData(o.getFirstAddress(), o.getLastAddress(), o.getTargetAddress()));
        }
        com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG<IInstruction> cfg = new com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG(insnmap, (List) irrdata, null, 0L, 3);
        this.disasDoc = ((IDexUnit) this.unit).getDisassemblyDocument();
        DexDisassemblyProperties propertyOverrides = new DexDisassemblyProperties();
        IPropertyManager pm = this.context.getPropertyManager();
        propertyOverrides.setShowAddresses(Boolean.valueOf(pm.getBoolean(".ui.text.cfg.ShowAddresses")));
        propertyOverrides.setShowBytecode(Boolean.valueOf(pm.getInteger(".ui.text.cfg.ShowBytesCount") > 0));
        this.disasDoc.setPropertyOverrides(propertyOverrides);
        generateGraphForCFG(cfg);
    }
}


