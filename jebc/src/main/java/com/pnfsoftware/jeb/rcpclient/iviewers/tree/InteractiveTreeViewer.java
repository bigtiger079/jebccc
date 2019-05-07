/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.output.tree.INode;
/*     */ import com.pnfsoftware.jeb.core.output.tree.INodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
/*     */ import com.pnfsoftware.jeb.core.output.tree.impl.NodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.ITreeSelection;
/*     */ import org.eclipse.jface.viewers.StructuredSelection;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;
/*     */ import org.eclipse.swt.widgets.TreeItem;

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
/*     */ public class InteractiveTreeViewer
        /*     */ implements IOperable, IContextMenu
        /*     */ {
    /*  55 */   private static final ILogger logger = GlobalLog.getLogger(InteractiveTreeViewer.class);
    /*     */
    /*     */   private static final int columnMaxWidth = 250;
    /*     */
    /*     */   private ITreeDocument idoc;
    /*     */   private IEventListener idocListener;
    /*     */   private PatternTreeView pt;
    /*     */   private FilteredTreeViewer viewer;
    /*     */   private IStyleProvider styleAdapter;

    /*     */
    /*     */
    public InteractiveTreeViewer(Composite parent, int style, ITreeDocument idoc, IPropertyManager propertyManager)
    /*     */ {
        /*  67 */
        final Composite container = new Composite(parent, 0);
        /*  68 */
        container.setLayout(new FillLayout());
        /*     */
        /*  70 */
        this.idoc = idoc;
        /*     */
        /*     */
        /*  73 */
        List<String> columnLabels = idoc.getColumnLabels();
        /*  74 */
        if (columnLabels == null) {
            /*  75 */
            columnLabels = new ArrayList();
            /*     */
        }
        /*  77 */
        String[] columnNames = (String[]) columnLabels.toArray(new String[columnLabels.size()]);
        /*     */
        /*  79 */
        LabelProvider labelProvider = new LabelProvider(this);
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*  86 */
        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);
        /*  87 */
        boolean expandAfterFilter = propertyManager.getBoolean(".ui.ExpandTreeNodesOnFiltering");
        /*  88 */
        this.pt = new PatternTreeView(container, 65664, columnNames, null, patternMatcher, expandAfterFilter);
        /*     */
        /*  90 */
        this.viewer = this.pt.getTreeViewer();
        /*     */
        /*     */
        /*     */
        /*  94 */
        ContextMenuFilter.addContextMenu(this.viewer.getViewer(), this.pt.getFilterText(), labelProvider, columnNames, null, this);
        /*     */
        /*  96 */
        this.pt.getTree().setHeaderVisible(true);
        /*  97 */
        this.pt.getTree().setLinesVisible(true);
        /*     */
        /*  99 */
        if ((style & 0x10000000) == 0) {
            /* 100 */
            this.viewer.setContentProvider(new ContentProvider());
            /*     */
        }
        /*     */
        else {
            /* 103 */
            throw new RuntimeException("Unsupported");
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 108 */
        ((TreeViewer) this.viewer.getViewer()).setUseHashlookup(true);
        /*     */
        /* 110 */
        this.viewer.setLabelProvider(labelProvider);
        /*     */
        /*     */
        /* 113 */
        idoc.addListener(this. = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e)
            /*     */ {
                /* 117 */
                UIExecutor.async(container, new UIRunnable()
                        /*     */ {
                    /*     */
                    public void runi() {
                        /* 120 */
                        InteractiveTreeViewer.this.viewer.refresh();
                        /*     */
                    }
                    /*     */
                });
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public void initialize() {
        /* 128 */
        this.viewer.setInput(this.idoc);
        /*     */
        /*     */
        /* 131 */
        int level = this.idoc.getInitialExpansionLevel();
        /* 132 */
        if (level < 0) {
            /* 133 */
            this.viewer.expandAll();
            /*     */
        }
        /* 135 */
        else if (level > 0) {
            /* 136 */
            this.viewer.expandToLevel(level);
            /*     */
        }
        /*     */
        /*     */
        /* 140 */
        for (TreeColumn tc : this.pt.getTree().getColumns()) {
            /* 141 */
            tc.setMoveable(true);
            /* 142 */
            tc.pack();
            /*     */
        }
        /* 144 */
        for (TreeColumn tc : this.pt.getTree().getColumns()) {
            /* 145 */
            if (tc.getWidth() > 250) {
                /* 146 */
                tc.setWidth(250);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void dispose() {
        /* 152 */
        this.idoc.removeListener(this.idocListener);
        /*     */
    }

    /*     */
    /*     */
    public void setStyleAdapter(IStyleProvider styleAdapter) {
        /* 156 */
        this.styleAdapter = styleAdapter;
        /*     */
    }

    /*     */
    /*     */
    public IStyleProvider getStyleAdapter() {
        /* 160 */
        return this.styleAdapter;
        /*     */
    }

    /*     */
    /*     */
    public FilteredTreeViewer getFilteredTreeViewer() {
        /* 164 */
        return this.viewer;
        /*     */
    }

    /*     */
    /*     */
    public TreeViewer getViewer() {
        /* 168 */
        return (TreeViewer) this.viewer.getViewer();
        /*     */
    }

    /*     */
    /*     */
    public Tree getTreeWidget() {
        /* 172 */
        return this.pt.getTree();
        /*     */
    }

    /*     */
    /*     */
    public ITreeDocument getInfiniDocument() {
        /* 176 */
        return this.idoc;
        /*     */
    }

    /*     */
    /*     */
    public INode getSelectedNode() {
        /* 180 */
        ITreeSelection selection = (ITreeSelection) this.viewer.getSelection();
        /* 181 */
        if (selection == null) {
            /* 182 */
            return null;
            /*     */
        }
        /*     */
        /* 185 */
        Object elt = selection.getFirstElement();
        /* 186 */
        if (!(elt instanceof INode)) {
            /* 187 */
            return null;
            /*     */
        }
        /*     */
        /* 190 */
        return (INode) elt;
        /*     */
    }

    /*     */
    /*     */
    public boolean setPosition(INodeCoordinates coord, boolean record) {
        /* 194 */
        List<Integer> seq = coord.getPath();
        /* 195 */
        if ((seq == null) || (seq.isEmpty())) {
            /* 196 */
            return false;
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
        /* 208 */
        List<? extends INode> list = this.idoc.getRoots();
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
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 263 */
        INode node = null;
        /* 264 */
        for (Iterator localIterator = seq.iterator(); localIterator.hasNext(); ) {
            int i = ((Integer) localIterator.next()).intValue();
            /* 265 */
            if (list == null) {
                /* 266 */
                node = null;
                /* 267 */
                break;
                /*     */
            }
            /*     */
            /* 270 */
            this.viewer.expandToLevel(node, 1);
            /*     */
            /* 272 */
            if ((i < 0) || (i >= list.size())) {
                /* 273 */
                return false;
                /*     */
            }
            /*     */
            /* 276 */
            node = (INode) list.get(i);
            /*     */
            /*     */
            /* 279 */
            list = node.getChildren();
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 286 */
        this.viewer.setSelection(new StructuredSelection(node), true);
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 292 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public NodeCoordinates getPosition() {
        /* 296 */
        INode node = getSelectedNode();
        /* 297 */
        if (node == null) {
            /* 298 */
            return null;
            /*     */
        }
        /*     */
        /* 301 */
        List<Integer> seq = new ArrayList();
        /*     */
        /* 303 */
        TreeItem item = this.pt.getTree().getSelection()[0];
        /* 304 */
        while (item != null) {
            /* 305 */
            TreeItem parentItem = item.getParentItem();
            /* 306 */
            TreeItem[] items = null;
            /* 307 */
            int i = 0;
            /*     */
            /* 309 */
            if (parentItem == null) {
                /* 310 */
                items = this.pt.getTree().getItems();
                /*     */
            }
            /*     */
            else {
                /* 313 */
                items = parentItem.getItems();
                /*     */
            }
            /*     */
            /* 316 */
            while ((i < items.length) &&
                    /* 317 */         (items[i] != item))
                /*     */ {
                /*     */
                /* 320 */
                i++;
                /*     */
            }
            /* 322 */
            if (i >= items.length) {
                /* 323 */
                return null;
                /*     */
            }
            /*     */
            /* 326 */
            seq.add(0, Integer.valueOf(i));
            /* 327 */
            item = parentItem;
            /*     */
        }
        /*     */
        /* 330 */
        return new NodeCoordinates(seq);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 335 */
        menuMgr.add(new OperationCopy(this));
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 340 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 342 */
                return true;
            /*     */
            /*     */
            case COPY:
                /* 345 */
                return getSelectedNode() != null;
            /*     */
        }
        /*     */
        /* 348 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 354 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 356 */
                this.pt.setFilterVisibility(true, true);
                /* 357 */
                return true;
            /*     */
            /*     */
            case COPY:
                /* 360 */
                INode node = getSelectedNode();
                /* 361 */
                if (node == null) {
                    /* 362 */
                    return false;
                    /*     */
                }
                /*     */
                /* 365 */
                String s = Strings.safe(node.getLabel()).replace("\\", "\\\\").replace("|", "\\|");
                /* 366 */
                StringBuilder sb = new StringBuilder(s);
                /*     */
                /* 368 */
                String[] additionalLabels = node.getAdditionalLabels();
                /* 369 */
                if (additionalLabels != null) {
                    /* 370 */
                    for (String additionalLabel : additionalLabels) {
                        /* 371 */
                        sb.append('|');
                        /* 372 */
                        sb.append(Strings.safe(additionalLabel).replace("\\", "\\\\").replace("|", "\\|"));
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 376 */
                UIUtil.copyTextToClipboard(sb.toString());
                /* 377 */
                return true;
            /*     */
        }
        /*     */
        /* 380 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public String exportToString()
    /*     */ {
        /* 385 */
        return TreeUtil.buildXml(getTreeWidget(), 2);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\InteractiveTreeViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */