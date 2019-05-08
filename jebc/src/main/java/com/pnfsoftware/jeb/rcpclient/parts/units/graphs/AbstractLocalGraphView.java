
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.AllHandlers;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphController;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphNodeContents;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextFactory;
import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
import com.pnfsoftware.jeb.rcpclient.extensions.search.IFindTextImpl;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextFindResult;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractLocalGraphView<T extends IUnit>
        extends AbstractUnitFragment<T>
        implements IGraphController {
    private static final ILogger logger = GlobalLog.getLogger(AbstractLocalGraphView.class);
    protected GraphPlaceholder<Graph> gp;
    private boolean inUse;
    private GraphicalTextFinder<InteractiveTextFindResult> finder;
    protected NodeContentsInteractiveTextFactory contentsFactory;

    public AbstractLocalGraphView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context) {
        super(parent, style, unit, unitView, context);
        setLayout(new FillLayout());
        setBackground(UIAssetManager.getInstance().getColor(16777215));
        newGraphWithControls();
    }

    protected final void newGraphWithControls() {
        this.gp = new GraphPlaceholder<Graph>(this, 0, true, false) {
            protected Graph createGraph(GraphPlaceholder<Graph> gp, Graph previousGraph) {
                boolean keyboardControls = true;
                boolean mouseControls = true;
                boolean zoomingAllowed = true;
                int zoomLevel = 0;
                if (previousGraph != null) {
                    keyboardControls = previousGraph.isKeyboardControls();
                    mouseControls = previousGraph.isMouseControls();
                    zoomingAllowed = previousGraph.isZoomingAllowed();
                    zoomLevel = previousGraph.getZoomLevel();
                }
                Graph g = new Graph(gp, 0);
                g.setKeyboardControls(keyboardControls);
                g.setMouseControls(mouseControls);
                g.setZoomingAllowed(zoomingAllowed);
                g.setInitialZoomLevel(zoomLevel);
                return g;
            }
        };
        prepareCanvas();
    }

    protected final void prepareCanvas() {
        new ContextMenu(this.gp.getGraph()).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                if (AbstractLocalGraphView.this.getContext() == null) {
                    return;
                }
                AllHandlers.getInstance().fillManager(menuMgr, 4);
            }
        });
    }

    public GraphPlaceholder<Graph> getGraphWithControls() {
        return this.gp;
    }

    public Graph getGraph() {
        return (Graph) this.gp.getGraph();
    }

    public boolean setFocus() {
        return this.gp.setFocus();
    }

    protected final void reset(boolean markInUse) {
        if (this.inUse) {
            this.gp.reset();
            this.inUse = false;
        }
        if ((!this.inUse) && (markInUse)) {
            prepareCanvas();
            this.inUse = true;
        }
    }

    public boolean isValidGraph() {
        return (this.inUse) && (getGraph().getNodeCount() >= 1);
    }

    protected void newContentsFactory() {
        this.contentsFactory = new NodeContentsInteractiveTextFactory(getDisplay(), this.context.getFontManager(), this.context.getStyleManager(), this.unit, this.context.getStatusIndicator(), this, this, this.context);
    }

    protected void notifyContentsCreated() {
    }

    protected NodeContentsInteractiveTextView getActiveContents() {
        if (!this.inUse) {
            return null;
        }
        GraphNode node = ((Graph) this.gp.getGraph()).getActiveNode();
        if (node == null) {
            return null;
        }
        return (NodeContentsInteractiveTextView) node.getContents();
    }

    protected NodeContentsInteractiveTextView getContentsForNode(int index) {
        if (!this.inUse) {
            return null;
        }
        GraphNode node = (GraphNode) ((Graph) this.gp.getGraph()).getNodes().get(index);
        if (node == null) {
            return null;
        }
        return (NodeContentsInteractiveTextView) node.getContents();
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        NodeContentsInteractiveTextView contents = getActiveContents();
        return contents == null ? null : contents.getActiveAddress();
    }

    public IItem getActiveItem() {
        NodeContentsInteractiveTextView contents = getActiveContents();
        return contents == null ? null : contents.getActiveItem();
    }

    public String getActiveItemAsText() {
        NodeContentsInteractiveTextView contents = getActiveContents();
        return contents == null ? null : contents.getActiveItemAsText();
    }

    public boolean verifyOperation(OperationRequest req) {
        if (!isValidGraph()) {
            return false;
        }
        NodeContentsInteractiveTextView contents = getActiveContents();
        if ((contents != null) && (contents.verifyOperation(req))) {
            return true;
        }
        switch (req.getOperation()) {
            case FIND:
                return true;
            case FIND_NEXT:
                return this.finder != null;
            case JUMP_TO:
            case ITEM_FOLLOW:
                return true;
            case CENTER:
                return true;
            case ZOOM_IN:
                return getGraph().isZoomingAllowed();
            case ZOOM_OUT:
                return getGraph().isZoomingAllowed();
            case ZOOM_RESET:
                return (getGraph().isZoomingAllowed()) && (getGraph().getZoomLevel() != 0);
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        if (!isValidGraph()) {
            return false;
        }
        NodeContentsInteractiveTextView contents = getActiveContents();
        if (contents != null) {
            if (contents.doOperation(req)) {
                return true;
            }
            if (!req.proceed()) {
                return false;
            }
        }
        if (contents == null) {
            contents = getContentsForNode(0);
            if (contents == null) {
                return false;
            }
        }
        switch (req.getOperation()) {
            case FIND:
                ITextDocumentViewer iviewer = contents.getViewer();
                this.finder = new GraphicalTextFinder(new FindTextInGraphImpl(), this.context);
                FindTextDialog dlg = FindTextDialog.getInstance(this);
                if (dlg != null) {
                    dlg.setFocus();
                    return true;
                }
                FindTextOptions opt = iviewer.getFindTextOptions(true);
                if (Strings.isBlank(opt.getSearchString())) {
                    String selection = iviewer.getTextWidget().getSelectionText();
                    if (selection != null) {
                        int endline = selection.indexOf("\n");
                        if (endline == 0) {
                            endline = selection.indexOf("\n", 1);
                            if (endline >= 0) {
                                selection = selection.substring(1, endline);
                            } else {
                                selection = selection.substring(1);
                            }
                        } else if (endline > 0) {
                            selection = selection.substring(0, endline);
                        }
                        opt.setSearchString(selection);
                    }
                }
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                dlg = new FindTextDialog(getShell(), this.finder, history, false, this, getUnit().getName());
                dlg.open(this);
                return true;
            case FIND_NEXT:
                this.finder.search(null);
                return true;
            case JUMP_TO:
                return doJumpTo();
            case ITEM_FOLLOW:
                return doItemFollow();
            case CENTER:
                getGraph().centerGraph(0);
                return true;
            case ZOOM_IN:
                getGraph().applyZoom(1, false);
                return true;
            case ZOOM_OUT:
                getGraph().applyZoom(-1, false);
                return true;
            case ZOOM_RESET:
                getGraph().applyZoom(0, false);
                return true;
        }
        return false;
    }

    public abstract boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);

    protected boolean doJumpTo() {
        JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
        String address = dlg.open();
        if (address == null) {
            return false;
        }
        return setActiveAddress(address);
    }

    protected abstract boolean doItemFollow();

    public boolean canDisplayAtAddress(String address) {
        return true;
    }

    public void onNodeBreakoutAttempt(IGraphNodeContents nodeContents, int direction) {
    }

    private class FindTextInGraphImpl
            implements IFindTextImpl<InteractiveTextFindResult> {
        final int startIndex;
        int index;
        GraphNode currentNode;
        NodeContentsInteractiveTextView currentNodeContents;
        ITextDocumentViewer currentNodeViewer;
        FindTextOptions findOptions;
        boolean end;

        public FindTextInGraphImpl() {
            List<GraphNode> nodes = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getNodes();
            GraphNode node = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getActiveNode();
            if (node != null) {
                this.index = nodes.indexOf(node);
            } else {
                this.index = 0;
                node = (GraphNode) nodes.get(0);
            }
            this.startIndex = this.index;
            this.currentNode = node;
            this.currentNodeContents = ((NodeContentsInteractiveTextView) node.getContents());
            this.currentNodeViewer = this.currentNodeContents.getViewer();
        }

        private boolean nextNode() {
            int nodecnt = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getNodeCount();
            this.index = ((this.index + 1) % nodecnt);
            this.currentNode = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getNode(this.index);
            this.currentNodeContents = ((NodeContentsInteractiveTextView) this.currentNode.getContents());
            this.currentNodeViewer = this.currentNodeContents.getViewer();
            this.currentNodeViewer.resetFindTextOptions();
            return this.index != this.startIndex;
        }

        public boolean supportReverseSearch() {
            return false;
        }

        public void resetFindTextOptions() {
            this.currentNodeViewer.resetFindTextOptions();
        }

        public void setFindTextOptions(FindTextOptions options) {
            this.findOptions = options;
        }

        public FindTextOptions getFindTextOptions(boolean update) {
            if (this.findOptions == null) {
                this.findOptions = this.currentNodeViewer.getFindTextOptions(update);
            } else {
                this.currentNodeViewer.setFindTextOptions(this.findOptions);
            }
            this.currentNodeViewer.getFindTextOptions(update);
            return this.findOptions;
        }

        public InteractiveTextFindResult findText(FindTextOptions opt) {
            FindTextOptions options = opt != null ? opt : this.findOptions;
            if (options == null) {
                return null;
            }
            if (this.end) {
                if (!options.isWrapAround()) {
                    return null;
                }
                this.end = false;
            }
            for (; ; ) {
                FindTextOptions inNodeOptions = options.clone();
                inNodeOptions.setWrapAround(false);
                InteractiveTextFindResult r = (InteractiveTextFindResult) this.currentNodeViewer.findText(inNodeOptions);
                if (r == null) {
                    return null;
                }
                if (!r.isEndOfSearch()) {
                    return r;
                }
                if (!nextNode()) {
                    this.end = true;
                    return r;
                }
            }
        }

        public void processFindResult(InteractiveTextFindResult r) {
            AbstractLocalGraphView.this.getGraph().showNode(this.currentNode, true);
            this.currentNode.setFocus();
            this.currentNodeViewer.processFindResult(r);
        }

        public void clearFindResult() {
            this.currentNodeViewer.clearFindResult();
        }
    }
}


