
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;


import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextPartAsDocumentProxy;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.AddressableInstruction;
import com.pnfsoftware.jeb.core.units.code.IFlowInformation;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.render.INativeDisassemblyDocument;
import com.pnfsoftware.jeb.core.units.code.asm.render.NativeDisassemblyProperties;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.ReferencesDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;


public class NativeCodeGraphView
        extends AbstractControlFlowGraphView<INativeCodeUnit<IInstruction>> {
    private static final ILogger logger = GlobalLog.getLogger(NativeCodeGraphView.class);

    private INativeDisassemblyDocument disasDoc;
    private INativeMethodItem currentMethod;


    public NativeCodeGraphView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<IInstruction> unit, IRcpUnitView unitView) {

        super(parent, style, unit, unitView, context);

    }


    public boolean isValidActiveAddress(String address, Object object) {

        return ((INativeCodeUnit) this.unit).getCanonicalMemoryAddress(address) != -1L;

    }


    public boolean setActiveAddress(String address, Object extra, boolean record) {

        long a = ((INativeCodeUnit) this.unit).getCanonicalMemoryAddress(address);

        if (a == -1L) {

            return false;

        }


        int index = -1;

        List<? extends INativeMethodItem> methods = ((INativeCodeUnit) this.unit).getInternalMethods(a);

        if (methods.size() == 1) {

            index = 0;

        } else if (methods.size() >= 2) {

            List<String> addresses = new ArrayList();

            for (INativeMethodItem method : methods) {

                addresses.add(method.getAddress());

            }

            ReferencesDialog dlg = new ReferencesDialog(getShell(), "Select a routine", addresses, null, this.unit);

            String msg = String.format("There are more than one routine sharing code at address %s.\nSelect the routine you would like to visualize the CFG of:", new Object[]{address});


            dlg.setMessage(msg);

            index = dlg.open().intValue();

        }

        if ((index < 0) || (index >= methods.size())) {

            return false;

        }

        INativeMethodItem method = (INativeMethodItem) methods.get(index);


        GlobalPosition pos0 = (!record) || (getViewManager() == null) ? null : getViewManager().getCurrentGlobalPosition();


        if (method != this.currentMethod) {

            replaceGraph(method);

        }


        if (pos0 != null) {

            getViewManager().recordGlobalPosition(pos0);

        }


        GraphNode node = findNodeByInstructionAddress(a);

        if (node == null) {

            return false;

        }


        Graph g = getGraph();

        g.showNode(node, true);

        g.setActiveNode(node, false);


        NodeContentsInteractiveTextView contents = (NodeContentsInteractiveTextView) node.getContents();

        return contents.setActiveAddress(address, null, false);

    }


    protected boolean doItemFollow() {

        IItem item = getActiveItem();

        if ((item instanceof IActionableItem)) {

            String address = ((INativeCodeUnit) this.unit).getAddressOfItem(((IActionableItem) item).getItemId());

            if (address != null) {

                return setActiveAddress(address);

            }

        }

        return false;

    }


    protected ITextDocument getTextForBlock(final BasicBlock<IInstruction> b) {

        return new AbstractTextPartAsDocumentProxy(this.disasDoc) {

            protected ITextDocumentPart getPartAsDocument() {

                return ((INativeDisassemblyDocument) getFullDocument()).getDisassemblyPart(b.getFirstAddress(), b
                        .getEndAddress());

            }
        };

    }


    private void replaceGraph(INativeMethodItem method) {

        reset(true);

        this.currentMethod = method;


        CFG<IInstruction> cfg = (CFG<IInstruction>) method.getData().getCFG();


        boolean restructureCFG = true;

        for (AddressableInstruction<IInstruction> ainsn : cfg.addressableInstructions()) {

            IFlowInformation flowinfo = ainsn.getBreakingFlow();

            if ((flowinfo.isBroken()) && (flowinfo.getDelaySlotCount() != 0)) {

                restructureCFG = false;

                break;

            }

            flowinfo = ainsn.getRoutineCall();

            if ((flowinfo.isBroken()) && (flowinfo.getDelaySlotCount() != 0)) {

                restructureCFG = false;

                break;

            }

        }

        if ((restructureCFG) && ((cfg.getFlags() & 0x1) == 0)) {

            int flags = cfg.getFlags() | 0x1 | 0x2;

            cfg = new CFG(cfg.getInstructionsMap(), null, null, cfg.getEntryAddress(), flags);

        }


        this.disasDoc = ((INativeCodeUnit) this.unit).getDisassemblyDocument();

        NativeDisassemblyProperties propertyOverrides = new NativeDisassemblyProperties();

        IPropertyManager pm = this.context.getPropertyManager();

        propertyOverrides.setLabelAreaLength(Integer.valueOf(0));

        propertyOverrides.setShowAddresses(Boolean.valueOf(pm.getBoolean(".ui.text.cfg.ShowAddresses")));

        propertyOverrides.setShowBytesCount(Integer.valueOf(pm.getInteger(".ui.text.cfg.ShowBytesCount")));

        propertyOverrides.setRoutineSeparatorLength(Integer.valueOf(0));

        propertyOverrides.setShowSegmentHeaders(Boolean.valueOf(false));

        propertyOverrides.setShowSpaceBetweenBlocks(Boolean.valueOf(false));

        propertyOverrides.setInstructionAreaLength(Integer.valueOf(25));

        propertyOverrides.setBlockXrefsCount(Integer.valueOf(0));

        this.disasDoc.setPropertyOverrides(propertyOverrides);


        generateGraphForCFG(cfg);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\NativeCodeGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */