
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.tree.INode;
import com.pnfsoftware.jeb.core.output.tree.INodeCoordinates;
import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
import com.pnfsoftware.jeb.core.output.tree.impl.NodeCoordinates;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class InteractiveTreeViewer
        implements IOperable, IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(InteractiveTreeViewer.class);
    private static final int columnMaxWidth = 250;
    private ITreeDocument idoc;
    private IEventListener idocListener;
    private PatternTreeView pt;
    private FilteredTreeViewer viewer;
    private IStyleProvider styleAdapter;

    public InteractiveTreeViewer(Composite parent, int style, ITreeDocument idoc, IPropertyManager propertyManager) {
        final Composite container = new Composite(parent, 0);
        container.setLayout(new FillLayout());
        this.idoc = idoc;
        List<String> columnLabels = idoc.getColumnLabels();
        if (columnLabels == null) {
            columnLabels = new ArrayList();
        }
        String[] columnNames = (String[]) columnLabels.toArray(new String[columnLabels.size()]);
        LabelProvider labelProvider = new LabelProvider(this);
        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);
        boolean expandAfterFilter = propertyManager.getBoolean(".ui.ExpandTreeNodesOnFiltering");
        this.pt = new PatternTreeView(container, 65664, columnNames, null, patternMatcher, expandAfterFilter);
        this.viewer = this.pt.getTreeViewer();
        ContextMenuFilter.addContextMenu(this.viewer.getViewer(), this.pt.getFilterText(), labelProvider, columnNames, null, this);
        this.pt.getTree().setHeaderVisible(true);
        this.pt.getTree().setLinesVisible(true);
        if ((style & 0x10000000) == 0) {
            this.viewer.setContentProvider(new ContentProvider());
        } else {
            throw new RuntimeException("Unsupported");
        }
        ((TreeViewer) this.viewer.getViewer()).setUseHashlookup(true);
        this.viewer.setLabelProvider(labelProvider);
        idoc.addListener(this.idocListener = new IEventListener() {
            public void onEvent(IEvent e) {
                UIExecutor.async(container, new UIRunnable() {
                    public void runi() {
                        InteractiveTreeViewer.this.viewer.refresh();
                    }
                });
            }
        });
    }

    public void initialize() {
        this.viewer.setInput(this.idoc);
        int level = this.idoc.getInitialExpansionLevel();
        if (level < 0) {
            this.viewer.expandAll();
        } else if (level > 0) {
            this.viewer.expandToLevel(level);
        }
        for (TreeColumn tc : this.pt.getTree().getColumns()) {
            tc.setMoveable(true);
            tc.pack();
        }
        for (TreeColumn tc : this.pt.getTree().getColumns()) {
            if (tc.getWidth() > 250) {
                tc.setWidth(250);
            }
        }
    }

    public void dispose() {
        this.idoc.removeListener(this.idocListener);
    }

    public void setStyleAdapter(IStyleProvider styleAdapter) {
        this.styleAdapter = styleAdapter;
    }

    public IStyleProvider getStyleAdapter() {
        return this.styleAdapter;
    }

    public FilteredTreeViewer getFilteredTreeViewer() {
        return this.viewer;
    }

    public TreeViewer getViewer() {
        return (TreeViewer) this.viewer.getViewer();
    }

    public Tree getTreeWidget() {
        return this.pt.getTree();
    }

    public ITreeDocument getInfiniDocument() {
        return this.idoc;
    }

    public INode getSelectedNode() {
        ITreeSelection selection = (ITreeSelection) this.viewer.getSelection();
        if (selection == null) {
            return null;
        }
        Object elt = selection.getFirstElement();
        if (!(elt instanceof INode)) {
            return null;
        }
        return (INode) elt;
    }

    public boolean setPosition(INodeCoordinates coord, boolean record) {
        List<Integer> seq = coord.getPath();
        if ((seq == null) || (seq.isEmpty())) {
            return false;
        }
        List<? extends INode> list = this.idoc.getRoots();
        INode node = null;
        for (Iterator localIterator = seq.iterator(); localIterator.hasNext(); ) {
            int i = ((Integer) localIterator.next()).intValue();
            if (list == null) {
                node = null;
                break;
            }
            this.viewer.expandToLevel(node, 1);
            if ((i < 0) || (i >= list.size())) {
                return false;
            }
            node = (INode) list.get(i);
            list = node.getChildren();
        }
        this.viewer.setSelection(new StructuredSelection(node), true);
        return true;
    }

    public NodeCoordinates getPosition() {
        INode node = getSelectedNode();
        if (node == null) {
            return null;
        }
        List<Integer> seq = new ArrayList();
        TreeItem item = this.pt.getTree().getSelection()[0];
        while (item != null) {
            TreeItem parentItem = item.getParentItem();
            TreeItem[] items = null;
            int i = 0;
            if (parentItem == null) {
                items = this.pt.getTree().getItems();
            } else {
                items = parentItem.getItems();
            }
            while ((i < items.length) &&
                    (items[i] != item)) {
                i++;
            }
            if (i >= items.length) {
                return null;
            }
            seq.add(0, Integer.valueOf(i));
            item = parentItem;
        }
        return new NodeCoordinates(seq);
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        menuMgr.add(new OperationCopy(this));
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case FIND:
                return true;
            case COPY:
                return getSelectedNode() != null;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case FIND:
                this.pt.setFilterVisibility(true, true);
                return true;
            case COPY:
                INode node = getSelectedNode();
                if (node == null) {
                    return false;
                }
                String s = Strings.safe(node.getLabel()).replace("\\", "\\\\").replace("|", "\\|");
                StringBuilder sb = new StringBuilder(s);
                String[] additionalLabels = node.getAdditionalLabels();
                if (additionalLabels != null) {
                    for (String additionalLabel : additionalLabels) {
                        sb.append('|');
                        sb.append(Strings.safe(additionalLabel).replace("\\", "\\\\").replace("|", "\\|"));
                    }
                }
                UIUtil.copyTextToClipboard(sb.toString());
                return true;
        }
        return false;
    }

    public String exportToString() {
        return TreeUtil.buildXml(getTreeWidget(), 2);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\InteractiveTreeViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */