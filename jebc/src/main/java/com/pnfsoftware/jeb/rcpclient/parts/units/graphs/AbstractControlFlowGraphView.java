/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphEdgeSquare;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphEdgeSquareManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNodeListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNodeListenerAdapter;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphNodeContents;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextFactory;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.CFGLayoutManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Cell;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.ICFGLayout;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Spreadsheet;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.ColorsGradient;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Color;
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
/*     */ public abstract class AbstractControlFlowGraphView<T extends IUnit>
        /*     */ extends AbstractLocalGraphView<T>
        /*     */ {
    /*  52 */   private static final ILogger logger = GlobalLog.getLogger(AbstractControlFlowGraphView.class);
    /*     */
    /*  54 */   protected static Color cGreen = UIAssetManager.getInstance().getColor(ColorsGradient.get("green"));
    /*  55 */   protected static Color cRed = UIAssetManager.getInstance().getColor(ColorsGradient.get("red 3"));
    /*  56 */   protected static Color cOrange = UIAssetManager.getInstance().getColor(ColorsGradient.get("darkorange"));
    /*     */
    /*     */   private Map<BasicBlock<IInstruction>, GraphNode> blockToNodes;
    /*     */
    /*     */   private GraphNodeListener graphNodeListener;
    /*     */
    /*     */   private GraphNode nodeFocusGained;
    /*     */   private GraphNode nodeFocusLost;

    /*     */
    /*     */
    public AbstractControlFlowGraphView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context)
    /*     */ {
        /*  67 */
        super(parent, style, unit, unitView, context);
        /*     */
    }

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
    protected GraphNode findNodeByInstructionAddress(long address)
    /*     */ {
        /*  81 */
        for (GraphNode node : getGraph().getNodes()) {
            /*  82 */
            BasicBlock<IInstruction> b = (BasicBlock) node.getData("bb");
            /*  83 */
            if ((address >= b.getFirstAddress()) && (address < b.getEndAddress())) {
                /*  84 */
                return node;
                /*     */
            }
            /*     */
        }
        /*  87 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    protected abstract ITextDocument getTextForBlock(BasicBlock<IInstruction> paramBasicBlock);

    /*     */
    /*     */
    protected final void generateGraphForCFG(CFG<IInstruction> cfg)
    /*     */ {
        /*  94 */
        newContentsFactory();
        /*     */
        /*     */
        /*  97 */
        ICFGLayout<IInstruction> layout = CFGLayoutManager.createDefault();
        /*  98 */
        Spreadsheet<BasicBlock<IInstruction>> grid0 = layout.build(cfg);
        /*     */
        /* 100 */
        final Graph g = getGraph();
        /*     */
        /*     */
        /* 103 */
        Spreadsheet<GraphNode> grid1 = new Spreadsheet();
        /*     */
        /*     */
        /* 106 */
        this.blockToNodes = new IdentityHashMap();
        /* 107 */
        for (Iterator localIterator1 = grid0.getRealCells().iterator(); localIterator1.hasNext(); ) {
            cell = (Cell) localIterator1.next();
            /* 108 */
            BasicBlock<IInstruction> b = (BasicBlock) cell.getObject();
            /*     */
            /*     */
            /* 111 */
            GraphNode node = new GraphNode(getGraph(), 17);
            /* 112 */
            node.setData("bb", b);
            /* 113 */
            this.blockToNodes.put(b, node);
            /*     */
            /*     */
            /* 116 */
            ITextDocument fragment = getTextForBlock(b);
            /* 117 */
            this.contentsFactory.create(node, fragment);
            /* 118 */
            node.acknowledgeContents(false);
            /*     */
            /*     */
            /*     */
            /* 122 */
            Cell localCell = grid1.writeCell(cell.getRow(), cell.getColumn(), cell.getHorizontalSpan(), cell
/* 123 */.getVerticalSpan(), node);
        }
        /*     */
        Cell<BasicBlock<IInstruction>> cell;
        /* 125 */
        g.registerNodesGrid(grid1);
        /*     */
        /* 127 */
        notifyContentsCreated();
        /*     */
        /*     */
        /* 130 */
        GraphEdgeSquareManager edgeman = new GraphEdgeSquareManager(g);
        /* 131 */
        g.setEdgeManager(edgeman);
        /* 132 */
        for (BasicBlock<IInstruction> src : cfg) {
            /* 133 */
            n1 = (GraphNode) this.blockToNodes.get(src);
            /*     */
            /* 135 */
            List<BasicBlock<IInstruction>> dstlist = src.getOutputBlocks();
            /* 136 */
            int iedge = 0;
            /* 137 */
            for (BasicBlock<IInstruction> dst : dstlist) {
                /* 138 */
                GraphNode n2 = (GraphNode) this.blockToNodes.get(dst);
                /* 139 */
                GraphEdgeSquare edge = edgeman.create(n1, n2);
                /* 140 */
                if (dstlist.size() >= 2) {
                    /* 141 */
                    edge.setColor(0, iedge == 0 ? cRed : cGreen);
                    /*     */
                }
                /* 143 */
                iedge++;
                /*     */
            }
            /*     */
            /* 146 */
            for (BasicBlock<IInstruction> dst : src.getIrregularOutputBlocks()) {
                /* 147 */
                GraphNode n2 = (GraphNode) this.blockToNodes.get(dst);
                /* 148 */
                GraphEdgeSquare edge = edgeman.create(n1, n2);
                /* 149 */
                edge.setColor(0, cOrange);
                /* 150 */
                edge.setStyle(2);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        GraphNode n1;
        /* 155 */
        g.addGraphNodeListener(this. = new GraphNodeListenerAdapter()
                /*     */ {
            /*     */
            public void onNodeFocusGained(GraphNode node) {
                /* 158 */
                if (g.isDisposed()) {
                    /* 159 */
                    return;
                    /*     */
                }
                /* 161 */
                AbstractControlFlowGraphView.this.nodeFocusGained = node;
                /* 162 */
                if ((AbstractControlFlowGraphView.this.nodeFocusLost != null) && (AbstractControlFlowGraphView.this.nodeFocusGained != AbstractControlFlowGraphView.this.nodeFocusLost)) {
                    /* 163 */
                    NodeContentsInteractiveTextView contents = (NodeContentsInteractiveTextView) AbstractControlFlowGraphView.this.nodeFocusLost.getContents();
                    /* 164 */
                    contents.getViewer().resetSelection();
                    /* 165 */
                    AbstractControlFlowGraphView.this.nodeFocusLost = null;
                    /*     */
                }
                /*     */
            }

            /*     */
            /*     */
            public void onNodeFocusLost(GraphNode node)
            /*     */ {
                /* 171 */
                AbstractControlFlowGraphView.this.nodeFocusLost = node;
                /*     */
            }
            /*     */
            /* 174 */
        });
        /* 175 */
        g.addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /* 178 */
                g.removeGraphNodeListener(AbstractControlFlowGraphView.this.graphNodeListener);
                /* 179 */
                AbstractControlFlowGraphView.this.nodeFocusGained = null;
                /* 180 */
                AbstractControlFlowGraphView.this.nodeFocusLost = null;
                /*     */
            }
            /*     */
            /* 183 */
        });
        /* 184 */
        this.gp.layout();
        /*     */
        /* 186 */
        g.centerGraph(0);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void onNodeBreakoutAttempt(IGraphNodeContents nodeContents, int direction)
    /*     */ {
        /* 192 */
        GraphNode node = this.contentsFactory.getNodeForContents((NodeContentsInteractiveTextView) nodeContents);
        /*     */
        /* 194 */
        BasicBlock<IInstruction> b = (BasicBlock) node.getData("bb");
        /*     */
        /* 196 */
        BasicBlock<IInstruction> b1 = null;
        /* 197 */
        GraphNode node1 = null;
        /* 198 */
        String address1 = null;
        /*     */
        /* 200 */
        if ((direction == 1) && (b.outsize() >= 1)) {
            /* 201 */
            b1 = b.getOutputBlock(0);
            /* 202 */
            address1 = buildAddress(b1.getFirstAddress());
            /*     */
        }
        /* 204 */
        else if ((direction == -1) && (b.insize() >= 1)) {
            /* 205 */
            b1 = b.getInputBlock(0);
            /* 206 */
            address1 = buildAddress(b1.getLastAddress());
            /*     */
        }
        /*     */
        else {
            /* 209 */
            return;
            /*     */
        }
        /*     */
        /* 212 */
        node1 = (GraphNode) this.blockToNodes.get(b1);
        /*     */
        /* 214 */
        this.contentsFactory.getContentsForNode(node1).setFocus();
        /* 215 */
        this.contentsFactory.getContentsForNode(node1).setActiveAddress(address1);
        /*     */
        /* 217 */
        getGraph().showNode(node1, true);
        /*     */
    }

    /*     */
    /*     */
    protected String buildAddress(long offset) {
        /* 221 */
        return String.format("%Xh", new Object[]{Long.valueOf(offset)});
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\AbstractControlFlowGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */