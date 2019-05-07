/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextPartAsDocumentProxy;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.AddressableInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.IFlowInformation;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.render.INativeDisassemblyDocument;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.render.NativeDisassemblyProperties;
/*     */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ReferencesDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class NativeCodeGraphView
        /*     */ extends AbstractControlFlowGraphView<INativeCodeUnit<IInstruction>>
        /*     */ {
    /*  47 */   private static final ILogger logger = GlobalLog.getLogger(NativeCodeGraphView.class);
    /*     */
    /*     */   private INativeDisassemblyDocument disasDoc;
    /*     */   private INativeMethodItem currentMethod;

    /*     */
    /*     */
    public NativeCodeGraphView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<IInstruction> unit, IRcpUnitView unitView)
    /*     */ {
        /*  54 */
        super(parent, style, unit, unitView, context);
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /*  59 */
        return ((INativeCodeUnit) this.unit).getCanonicalMemoryAddress(address) != -1L;
        /*     */
    }

    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /*  64 */
        long a = ((INativeCodeUnit) this.unit).getCanonicalMemoryAddress(address);
        /*  65 */
        if (a == -1L) {
            /*  66 */
            return false;
            /*     */
        }
        /*     */
        /*  69 */
        int index = -1;
        /*  70 */
        List<? extends INativeMethodItem> methods = ((INativeCodeUnit) this.unit).getInternalMethods(a);
        /*  71 */
        if (methods.size() == 1) {
            /*  72 */
            index = 0;
            /*     */
        }
        /*  74 */
        else if (methods.size() >= 2) {
            /*  75 */
            List<String> addresses = new ArrayList();
            /*  76 */
            for (INativeMethodItem method : methods) {
                /*  77 */
                addresses.add(method.getAddress());
                /*     */
            }
            /*  79 */
            ReferencesDialog dlg = new ReferencesDialog(getShell(), "Select a routine", addresses, null, this.unit);
            /*  80 */
            String msg = String.format("There are more than one routine sharing code at address %s.\nSelect the routine you would like to visualize the CFG of:", new Object[]{address});
            /*     */
            /*  82 */
            dlg.setMessage(msg);
            /*  83 */
            index = dlg.open().intValue();
            /*     */
        }
        /*  85 */
        if ((index < 0) || (index >= methods.size())) {
            /*  86 */
            return false;
            /*     */
        }
        /*  88 */
        INativeMethodItem method = (INativeMethodItem) methods.get(index);
        /*     */
        /*  90 */
        GlobalPosition pos0 = (!record) || (getViewManager() == null) ? null : getViewManager().getCurrentGlobalPosition();
        /*     */
        /*  92 */
        if (method != this.currentMethod) {
            /*  93 */
            replaceGraph(method);
            /*     */
        }
        /*     */
        /*  96 */
        if (pos0 != null) {
            /*  97 */
            getViewManager().recordGlobalPosition(pos0);
            /*     */
        }
        /*     */
        /* 100 */
        GraphNode node = findNodeByInstructionAddress(a);
        /* 101 */
        if (node == null) {
            /* 102 */
            return false;
            /*     */
        }
        /*     */
        /* 105 */
        Graph g = getGraph();
        /* 106 */
        g.showNode(node, true);
        /* 107 */
        g.setActiveNode(node, false);
        /*     */
        /* 109 */
        NodeContentsInteractiveTextView contents = (NodeContentsInteractiveTextView) node.getContents();
        /* 110 */
        return contents.setActiveAddress(address, null, false);
        /*     */
    }

    /*     */
    /*     */
    protected boolean doItemFollow()
    /*     */ {
        /* 115 */
        IItem item = getActiveItem();
        /* 116 */
        if ((item instanceof IActionableItem)) {
            /* 117 */
            String address = ((INativeCodeUnit) this.unit).getAddressOfItem(((IActionableItem) item).getItemId());
            /* 118 */
            if (address != null) {
                /* 119 */
                return setActiveAddress(address);
                /*     */
            }
            /*     */
        }
        /* 122 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    protected ITextDocument getTextForBlock(final BasicBlock<IInstruction> b)
    /*     */ {
        /* 127 */
        new AbstractTextPartAsDocumentProxy(this.disasDoc)
                /*     */ {
            /*     */
            protected ITextDocumentPart getPartAsDocument() {
                /* 130 */
                return ((INativeDisassemblyDocument) getFullDocument()).getDisassemblyPart(b.getFirstAddress(), b
/* 131 */.getEndAddress());
                /*     */
            }
            /*     */
        };
        /*     */
    }

    /*     */
    /*     */
    private void replaceGraph(INativeMethodItem method) {
        /* 137 */
        reset(true);
        /* 138 */
        this.currentMethod = method;
        /*     */
        /*     */
        /* 141 */
        CFG<IInstruction> cfg = method.getData().getCFG();
        /*     */
        /*     */
        /* 144 */
        boolean restructureCFG = true;
        /* 145 */
        for (AddressableInstruction<IInstruction> ainsn : cfg.addressableInstructions()) {
            /* 146 */
            IFlowInformation flowinfo = ainsn.getBreakingFlow();
            /* 147 */
            if ((flowinfo.isBroken()) && (flowinfo.getDelaySlotCount() != 0)) {
                /* 148 */
                restructureCFG = false;
                /* 149 */
                break;
                /*     */
            }
            /* 151 */
            flowinfo = ainsn.getRoutineCall();
            /* 152 */
            if ((flowinfo.isBroken()) && (flowinfo.getDelaySlotCount() != 0)) {
                /* 153 */
                restructureCFG = false;
                /* 154 */
                break;
                /*     */
            }
            /*     */
        }
        /* 157 */
        if ((restructureCFG) && ((cfg.getFlags() & 0x1) == 0)) {
            /* 158 */
            int flags = cfg.getFlags() | 0x1 | 0x2;
            /* 159 */
            cfg = new CFG(cfg.getInstructionsMap(), null, null, cfg.getEntryAddress(), flags);
            /*     */
        }
        /*     */
        /* 162 */
        this.disasDoc = ((INativeCodeUnit) this.unit).getDisassemblyDocument();
        /* 163 */
        NativeDisassemblyProperties propertyOverrides = new NativeDisassemblyProperties();
        /* 164 */
        IPropertyManager pm = this.context.getPropertyManager();
        /* 165 */
        propertyOverrides.setLabelAreaLength(Integer.valueOf(0));
        /* 166 */
        propertyOverrides.setShowAddresses(Boolean.valueOf(pm.getBoolean(".ui.text.cfg.ShowAddresses")));
        /* 167 */
        propertyOverrides.setShowBytesCount(Integer.valueOf(pm.getInteger(".ui.text.cfg.ShowBytesCount")));
        /* 168 */
        propertyOverrides.setRoutineSeparatorLength(Integer.valueOf(0));
        /* 169 */
        propertyOverrides.setShowSegmentHeaders(Boolean.valueOf(false));
        /* 170 */
        propertyOverrides.setShowSpaceBetweenBlocks(Boolean.valueOf(false));
        /* 171 */
        propertyOverrides.setInstructionAreaLength(Integer.valueOf(25));
        /* 172 */
        propertyOverrides.setBlockXrefsCount(Integer.valueOf(0));
        /* 173 */
        this.disasDoc.setPropertyOverrides(propertyOverrides);
        /*     */
        /* 175 */
        generateGraphForCFG(cfg);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\NativeCodeGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */