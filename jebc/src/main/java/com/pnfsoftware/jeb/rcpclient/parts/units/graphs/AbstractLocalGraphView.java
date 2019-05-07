/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.AllHandlers;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.Graph;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphNode;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphController;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphNodeContents;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextFactory;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.NodeContentsInteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.IFindTextImpl;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextFindResult;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.layout.FillLayout;
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
/*     */
/*     */
/*     */
/*     */ public abstract class AbstractLocalGraphView<T extends IUnit>
        /*     */ extends AbstractUnitFragment<T>
        /*     */ implements IGraphController
        /*     */ {
    /*  56 */   private static final ILogger logger = GlobalLog.getLogger(AbstractLocalGraphView.class);
    /*     */   protected GraphPlaceholder<Graph> gp;
    /*     */   private boolean inUse;
    /*     */   private GraphicalTextFinder<InteractiveTextFindResult> finder;
    /*     */   protected NodeContentsInteractiveTextFactory contentsFactory;

    /*     */
    /*     */
    public AbstractLocalGraphView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context)
    /*     */ {
        /*  64 */
        super(parent, style, unit, unitView, context);
        /*  65 */
        setLayout(new FillLayout());
        /*     */
        /*  67 */
        setBackground(UIAssetManager.getInstance().getColor(16777215));
        /*     */
        /*  69 */
        newGraphWithControls();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    protected final void newGraphWithControls()
    /*     */ {
        /*  76 */
        this.gp = new GraphPlaceholder(this, 0, true, false)
                /*     */ {
            /*     */
            protected Graph createGraph(GraphPlaceholder<Graph> gp, Graph previousGraph) {
                /*  79 */
                boolean keyboardControls = true;
                /*  80 */
                boolean mouseControls = true;
                /*  81 */
                boolean zoomingAllowed = true;
                /*  82 */
                int zoomLevel = 0;
                /*  83 */
                if (previousGraph != null) {
                    /*  84 */
                    keyboardControls = previousGraph.isKeyboardControls();
                    /*  85 */
                    mouseControls = previousGraph.isMouseControls();
                    /*  86 */
                    zoomingAllowed = previousGraph.isZoomingAllowed();
                    /*  87 */
                    zoomLevel = previousGraph.getZoomLevel();
                    /*     */
                }
                /*  89 */
                Graph g = new Graph(gp, 0);
                /*  90 */
                g.setKeyboardControls(keyboardControls);
                /*  91 */
                g.setMouseControls(mouseControls);
                /*  92 */
                g.setZoomingAllowed(zoomingAllowed);
                /*  93 */
                g.setInitialZoomLevel(zoomLevel);
                /*  94 */
                return g;
                /*     */
            }
            /*     */
            /*  97 */
        };
        /*  98 */
        prepareCanvas();
        /*     */
    }

    /*     */
    /*     */
    protected final void prepareCanvas() {
        /* 102 */
        new ContextMenu(this.gp.getGraph()).addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /* 105 */
                if (AbstractLocalGraphView.this.getContext() == null) {
                    /* 106 */
                    return;
                    /*     */
                }
                /* 108 */
                AllHandlers.getInstance().fillManager(menuMgr, 4);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public GraphPlaceholder<Graph> getGraphWithControls() {
        /* 114 */
        return this.gp;
        /*     */
    }

    /*     */
    /*     */
    public Graph getGraph() {
        /* 118 */
        return (Graph) this.gp.getGraph();
        /*     */
    }

    /*     */
    /*     */
    public boolean setFocus()
    /*     */ {
        /* 123 */
        return this.gp.setFocus();
        /*     */
    }

    /*     */
    /*     */
    protected final void reset(boolean markInUse) {
        /* 127 */
        if (this.inUse) {
            /* 128 */
            this.gp.reset();
            /* 129 */
            this.inUse = false;
            /*     */
        }
        /* 131 */
        if ((!this.inUse) && (markInUse)) {
            /* 132 */
            prepareCanvas();
            /* 133 */
            this.inUse = true;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidGraph() {
        /* 138 */
        return (this.inUse) && (getGraph().getNodeCount() >= 1);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    protected void newContentsFactory()
    /*     */ {
        /* 145 */
        this.contentsFactory = new NodeContentsInteractiveTextFactory(getDisplay(), this.context.getFontManager(), this.context.getStyleManager(), this.unit, this.context.getStatusIndicator(), this, this, this.context);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    protected void notifyContentsCreated() {
    }

    /*     */
    /*     */
    /*     */
    protected NodeContentsInteractiveTextView getActiveContents()
    /*     */ {
        /* 155 */
        if (!this.inUse) {
            /* 156 */
            return null;
            /*     */
        }
        /* 158 */
        GraphNode node = ((Graph) this.gp.getGraph()).getActiveNode();
        /* 159 */
        if (node == null) {
            /* 160 */
            return null;
            /*     */
        }
        /* 162 */
        return (NodeContentsInteractiveTextView) node.getContents();
        /*     */
    }

    /*     */
    /*     */
    protected NodeContentsInteractiveTextView getContentsForNode(int index) {
        /* 166 */
        if (!this.inUse) {
            /* 167 */
            return null;
            /*     */
        }
        /* 169 */
        GraphNode node = (GraphNode) ((Graph) this.gp.getGraph()).getNodes().get(index);
        /* 170 */
        if (node == null) {
            /* 171 */
            return null;
            /*     */
        }
        /* 173 */
        return (NodeContentsInteractiveTextView) node.getContents();
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 178 */
        NodeContentsInteractiveTextView contents = getActiveContents();
        /* 179 */
        return contents == null ? null : contents.getActiveAddress();
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 184 */
        NodeContentsInteractiveTextView contents = getActiveContents();
        /* 185 */
        return contents == null ? null : contents.getActiveItem();
        /*     */
    }

    /*     */
    /*     */
    public String getActiveItemAsText()
    /*     */ {
        /* 190 */
        NodeContentsInteractiveTextView contents = getActiveContents();
        /* 191 */
        return contents == null ? null : contents.getActiveItemAsText();
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 196 */
        if (!isValidGraph()) {
            /* 197 */
            return false;
            /*     */
        }
        /*     */
        /* 200 */
        NodeContentsInteractiveTextView contents = getActiveContents();
        /* 201 */
        if ((contents != null) && (contents.verifyOperation(req))) {
            /* 202 */
            return true;
            /*     */
        }
        /*     */
        /* 205 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 207 */
                return true;
            /*     */
            case FIND_NEXT:
                /* 209 */
                return this.finder != null;
            /*     */
            case JUMP_TO:
                /*     */
            case ITEM_FOLLOW:
                /* 212 */
                return true;
            /*     */
            case CENTER:
                /* 214 */
                return true;
            /*     */
            case ZOOM_IN:
                /* 216 */
                return getGraph().isZoomingAllowed();
            /*     */
            case ZOOM_OUT:
                /* 218 */
                return getGraph().isZoomingAllowed();
            /*     */
            case ZOOM_RESET:
                /* 220 */
                return (getGraph().isZoomingAllowed()) && (getGraph().getZoomLevel() != 0);
            /*     */
        }
        /* 222 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 228 */
        if (!isValidGraph()) {
            /* 229 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /* 233 */
        NodeContentsInteractiveTextView contents = getActiveContents();
        /* 234 */
        if (contents != null) {
            /* 235 */
            if (contents.doOperation(req)) {
                /* 236 */
                return true;
                /*     */
            }
            /* 238 */
            if (!req.proceed()) {
                /* 239 */
                return false;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 243 */
        if (contents == null) {
            /* 244 */
            contents = getContentsForNode(0);
            /*     */
            /* 246 */
            if (contents == null) {
                /* 247 */
                return false;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 252 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 254 */
                ITextDocumentViewer iviewer = contents.getViewer();
                /* 255 */
                this.finder = new GraphicalTextFinder(new FindTextInGraphImpl(), this.context);
                /*     */
                /* 257 */
                FindTextDialog dlg = FindTextDialog.getInstance(this);
                /* 258 */
                if (dlg != null) {
                    /* 259 */
                    dlg.setFocus();
                    /* 260 */
                    return true;
                    /*     */
                }
                /* 262 */
                FindTextOptions opt = iviewer.getFindTextOptions(true);
                /* 263 */
                if (Strings.isBlank(opt.getSearchString()))
                    /*     */ {
                    /* 265 */
                    String selection = iviewer.getTextWidget().getSelectionText();
                    /* 266 */
                    if (selection != null) {
                        /* 267 */
                        int endline = selection.indexOf("\n");
                        /* 268 */
                        if (endline == 0) {
                            /* 269 */
                            endline = selection.indexOf("\n", 1);
                            /* 270 */
                            if (endline >= 0) {
                                /* 271 */
                                selection = selection.substring(1, endline);
                                /*     */
                            }
                            /*     */
                            else {
                                /* 274 */
                                selection = selection.substring(1);
                                /*     */
                            }
                            /*     */
                        }
                        /* 277 */
                        else if (endline > 0) {
                            /* 278 */
                            selection = selection.substring(0, endline);
                            /*     */
                        }
                        /* 280 */
                        opt.setSearchString(selection);
                        /*     */
                    }
                    /*     */
                }
                /* 283 */
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                /* 284 */
                dlg = new FindTextDialog(getShell(), this.finder, history, false, this, getUnit().getName());
                /* 285 */
                dlg.open(this);
                /* 286 */
                return true;
            /*     */
            /*     */
            case FIND_NEXT:
                /* 289 */
                this.finder.search(null);
                /* 290 */
                return true;
            /*     */
            /*     */
            case JUMP_TO:
                /* 293 */
                return doJumpTo();
            /*     */
            case ITEM_FOLLOW:
                /* 295 */
                return doItemFollow();
            /*     */
            case CENTER:
                /* 297 */
                getGraph().centerGraph(0);
                /* 298 */
                return true;
            /*     */
            case ZOOM_IN:
                /* 300 */
                getGraph().applyZoom(1, false);
                /* 301 */
                return true;
            /*     */
            case ZOOM_OUT:
                /* 303 */
                getGraph().applyZoom(-1, false);
                /* 304 */
                return true;
            /*     */
            case ZOOM_RESET:
                /* 306 */
                getGraph().applyZoom(0, false);
                /* 307 */
                return true;
            /*     */
        }
        /* 309 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public abstract boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);

    /*     */
    /*     */
    /*     */
    protected boolean doJumpTo()
    /*     */ {
        /* 318 */
        JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
        /* 319 */
        String address = dlg.open();
        /* 320 */
        if (address == null) {
            /* 321 */
            return false;
            /*     */
        }
        /* 323 */
        return setActiveAddress(address);
        /*     */
    }

    /*     */
    /*     */
    protected abstract boolean doItemFollow();

    /*     */
    /*     */
    public boolean canDisplayAtAddress(String address) {
        /* 329 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void onNodeBreakoutAttempt(IGraphNodeContents nodeContents, int direction) {
    }

    /*     */
    /*     */
    /*     */   private class FindTextInGraphImpl
            /*     */ implements IFindTextImpl<InteractiveTextFindResult>
            /*     */ {
        /*     */     final int startIndex;
        /*     */
        /*     */ int index;
        /*     */
        /*     */ GraphNode currentNode;
        /*     */
        /*     */ NodeContentsInteractiveTextView currentNodeContents;
        /*     */
        /*     */ ITextDocumentViewer currentNodeViewer;
        /*     */
        /*     */ FindTextOptions findOptions;
        /*     */ boolean end;

        /*     */
        /*     */
        public FindTextInGraphImpl()
        /*     */ {
            /* 354 */
            List<GraphNode> nodes = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getNodes();
            /* 355 */
            GraphNode node = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getActiveNode();
            /* 356 */
            if (node != null) {
                /* 357 */
                this.index = nodes.indexOf(node);
                /*     */
            }
            /*     */
            else {
                /* 360 */
                this.index = 0;
                /* 361 */
                node = (GraphNode) nodes.get(0);
                /*     */
            }
            /* 363 */
            this.startIndex = this.index;
            /*     */
            /* 365 */
            this.currentNode = node;
            /* 366 */
            this.currentNodeContents = ((NodeContentsInteractiveTextView) node.getContents());
            /* 367 */
            this.currentNodeViewer = this.currentNodeContents.getViewer();
            /*     */
        }

        /*     */
        /*     */
        private boolean nextNode() {
            /* 371 */
            int nodecnt = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getNodeCount();
            /* 372 */
            this.index = ((this.index + 1) % nodecnt);
            /* 373 */
            this.currentNode = ((Graph) AbstractLocalGraphView.this.gp.getGraph()).getNode(this.index);
            /* 374 */
            this.currentNodeContents = ((NodeContentsInteractiveTextView) this.currentNode.getContents());
            /* 375 */
            this.currentNodeViewer = this.currentNodeContents.getViewer();
            /* 376 */
            this.currentNodeViewer.resetFindTextOptions();
            /* 377 */
            return this.index != this.startIndex;
            /*     */
        }

        /*     */
        /*     */
        public boolean supportReverseSearch()
        /*     */ {
            /* 382 */
            return false;
            /*     */
        }

        /*     */
        /*     */
        public void resetFindTextOptions()
        /*     */ {
            /* 387 */
            this.currentNodeViewer.resetFindTextOptions();
            /*     */
        }

        /*     */
        /*     */
        public void setFindTextOptions(FindTextOptions options)
        /*     */ {
            /* 392 */
            this.findOptions = options;
            /*     */
        }

        /*     */
        /*     */
        public FindTextOptions getFindTextOptions(boolean update)
        /*     */ {
            /* 397 */
            if (this.findOptions == null) {
                /* 398 */
                this.findOptions = this.currentNodeViewer.getFindTextOptions(update);
                /*     */
            }
            /*     */
            else {
                /* 401 */
                this.currentNodeViewer.setFindTextOptions(this.findOptions);
                /*     */
            }
            /* 403 */
            this.currentNodeViewer.getFindTextOptions(update);
            /* 404 */
            return this.findOptions;
            /*     */
        }

        /*     */
        /*     */
        public InteractiveTextFindResult findText(FindTextOptions opt)
        /*     */ {
            /* 409 */
            FindTextOptions options = opt != null ? opt : this.findOptions;
            /* 410 */
            if (options == null) {
                /* 411 */
                return null;
                /*     */
            }
            /*     */
            /* 414 */
            if (this.end) {
                /* 415 */
                if (!options.isWrapAround()) {
                    /* 416 */
                    return null;
                    /*     */
                }
                /* 418 */
                this.end = false;
                /*     */
            }
            /*     */
            /*     */
            for (; ; )
                /*     */ {
                /* 423 */
                FindTextOptions inNodeOptions = options.clone();
                /* 424 */
                inNodeOptions.setWrapAround(false);
                /* 425 */
                InteractiveTextFindResult r = (InteractiveTextFindResult) this.currentNodeViewer.findText(inNodeOptions);
                /*     */
                /* 427 */
                if (r == null) {
                    /* 428 */
                    return null;
                    /*     */
                }
                /*     */
                /* 431 */
                if (!r.isEndOfSearch()) {
                    /* 432 */
                    return r;
                    /*     */
                }
                /*     */
                /* 435 */
                if (!nextNode())
                    /*     */ {
                    /* 437 */
                    this.end = true;
                    /* 438 */
                    return r;
                    /*     */
                }
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public void processFindResult(InteractiveTextFindResult r)
        /*     */ {
            /* 445 */
            AbstractLocalGraphView.this.getGraph().showNode(this.currentNode, true);
            /* 446 */
            this.currentNode.setFocus();
            /* 447 */
            this.currentNodeViewer.processFindResult(r);
            /*     */
        }

        /*     */
        /*     */
        public void clearFindResult()
        /*     */ {
            /* 452 */
            this.currentNodeViewer.clearFindResult();
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\AbstractLocalGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */