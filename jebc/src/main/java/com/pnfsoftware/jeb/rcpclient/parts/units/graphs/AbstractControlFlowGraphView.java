
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;


import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphEdgeSquare;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphEdgeSquareManager;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNodeListener;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNodeListenerAdapter;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphNodeContents;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextFactory;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.CFGLayoutManager;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Cell;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.ICFGLayout;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.layout.Spreadsheet;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.util.ColorsGradient;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;


public abstract class AbstractControlFlowGraphView<T extends IUnit>
        extends AbstractLocalGraphView<T> {
    private static final ILogger logger = GlobalLog.getLogger(AbstractControlFlowGraphView.class);

    protected static Color cGreen = UIAssetManager.getInstance().getColor(ColorsGradient.get("green"));
    protected static Color cRed = UIAssetManager.getInstance().getColor(ColorsGradient.get("red 3"));
    protected static Color cOrange = UIAssetManager.getInstance().getColor(ColorsGradient.get("darkorange"));

    private Map<BasicBlock<IInstruction>, GraphNode> blockToNodes;

    private GraphNodeListener graphNodeListener;

    private GraphNode nodeFocusGained;
    private GraphNode nodeFocusLost;


    public AbstractControlFlowGraphView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context) {

        super(parent, style, unit, unitView, context);

    }


    protected GraphNode findNodeByInstructionAddress(long address) {

        for (GraphNode node : getGraph().getNodes()) {

            BasicBlock<IInstruction> b = (BasicBlock) node.getData("bb");

            if ((address >= b.getFirstAddress()) && (address < b.getEndAddress())) {

                return node;

            }

        }

        return null;

    }


    protected abstract ITextDocument getTextForBlock(BasicBlock<IInstruction> paramBasicBlock);


    protected final void generateGraphForCFG(CFG<IInstruction> cfg) {

        newContentsFactory();


        ICFGLayout<IInstruction> layout = CFGLayoutManager.createDefault();

        Spreadsheet<BasicBlock<IInstruction>> grid0 = layout.build(cfg);


        final Graph g = getGraph();

        Spreadsheet<GraphNode> grid1 = new Spreadsheet();

        this.blockToNodes = new IdentityHashMap();
        Cell<BasicBlock<IInstruction>> cell;
        GraphNode n1;
        for (Iterator localIterator1 = grid0.getRealCells().iterator(); localIterator1.hasNext(); ) {
            cell = (Cell) localIterator1.next();

            BasicBlock<IInstruction> b = (BasicBlock) cell.getObject();


            GraphNode node = new GraphNode(getGraph(), 17);

            node.setData("bb", b);

            this.blockToNodes.put(b, node);


            ITextDocument fragment = getTextForBlock(b);

            this.contentsFactory.create(node, fragment);

            node.acknowledgeContents(false);


            Cell localCell = grid1.writeCell(cell.getRow(), cell.getColumn(), cell.getHorizontalSpan(), cell
                    .getVerticalSpan(), node);
        }

        g.registerNodesGrid(grid1);


        notifyContentsCreated();


        GraphEdgeSquareManager edgeman = new GraphEdgeSquareManager(g);

        g.setEdgeManager(edgeman);

        for (BasicBlock<IInstruction> src : cfg) {

            n1 = (GraphNode) this.blockToNodes.get(src);


            List<BasicBlock<IInstruction>> dstlist = src.getOutputBlocks();

            int iedge = 0;

            for (BasicBlock<IInstruction> dst : dstlist) {

                GraphNode n2 = (GraphNode) this.blockToNodes.get(dst);

                GraphEdgeSquare edge = edgeman.create(n1, n2);

                if (dstlist.size() >= 2) {

                    edge.setColor(0, iedge == 0 ? cRed : cGreen);

                }

                iedge++;

            }


            for (BasicBlock<IInstruction> dst : src.getIrregularOutputBlocks()) {

                GraphNode n2 = (GraphNode) this.blockToNodes.get(dst);

                GraphEdgeSquare edge = edgeman.create(n1, n2);

                edge.setColor(0, cOrange);

                edge.setStyle(2);

            }

        }

        g.addGraphNodeListener(this.graphNodeListener = new GraphNodeListenerAdapter() {

            public void onNodeFocusGained(GraphNode node) {

                if (g.isDisposed()) {

                    return;

                }

                AbstractControlFlowGraphView.this.nodeFocusGained = node;

                if ((AbstractControlFlowGraphView.this.nodeFocusLost != null) && (AbstractControlFlowGraphView.this.nodeFocusGained != AbstractControlFlowGraphView.this.nodeFocusLost)) {

                    NodeContentsInteractiveTextView contents = (NodeContentsInteractiveTextView) AbstractControlFlowGraphView.this.nodeFocusLost.getContents();

                    contents.getViewer().resetSelection();

                    AbstractControlFlowGraphView.this.nodeFocusLost = null;

                }

            }


            public void onNodeFocusLost(GraphNode node) {

                AbstractControlFlowGraphView.this.nodeFocusLost = node;

            }


        });

        g.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {

                g.removeGraphNodeListener(AbstractControlFlowGraphView.this.graphNodeListener);

                AbstractControlFlowGraphView.this.nodeFocusGained = null;

                AbstractControlFlowGraphView.this.nodeFocusLost = null;

            }


        });

        this.gp.layout();


        g.centerGraph(0);

    }


    public void onNodeBreakoutAttempt(IGraphNodeContents nodeContents, int direction) {

        GraphNode node = this.contentsFactory.getNodeForContents((NodeContentsInteractiveTextView) nodeContents);


        BasicBlock<IInstruction> b = (BasicBlock) node.getData("bb");


        BasicBlock<IInstruction> b1 = null;

        GraphNode node1 = null;

        String address1 = null;


        if ((direction == 1) && (b.outsize() >= 1)) {

            b1 = b.getOutputBlock(0);

            address1 = buildAddress(b1.getFirstAddress());

        } else if ((direction == -1) && (b.insize() >= 1)) {

            b1 = b.getInputBlock(0);

            address1 = buildAddress(b1.getLastAddress());

        } else {

            return;

        }


        node1 = (GraphNode) this.blockToNodes.get(b1);


        this.contentsFactory.getContentsForNode(node1).setFocus();

        this.contentsFactory.getContentsForNode(node1).setActiveAddress(address1);


        getGraph().showNode(node1, true);

    }


    protected String buildAddress(long offset) {

        return String.format("%Xh", new Object[]{Long.valueOf(offset)});

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\AbstractControlFlowGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */