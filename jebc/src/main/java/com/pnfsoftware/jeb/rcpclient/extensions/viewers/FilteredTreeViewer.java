
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.rcpclient.extensions.controls.AbstractFilteredView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.filter.AbstractFilteredFilter;
import com.pnfsoftware.jeb.rcpclient.iviewers.tree.TreeUtil;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class FilteredTreeViewer
        extends AbstractFilteredViewer<Tree, TreeViewer> {
    Object[] expandedElements;
    private boolean expandAfterFilter;
    private static final int EXPAND_LIMIT = 2000;
    public static final int EXPAND_ERROR = -1;

    public FilteredTreeViewer(FilteredTreeView widget, boolean expandAfterFilter) {
        super(widget);
        this.expandAfterFilter = expandAfterFilter;
        this.comparator = new FilteredViewerComparator(Strings.getDefaultComparator(), this);
        ((TreeViewer) getViewer()).setComparator(this.comparator);
        int colIndex = 0;
        for (TreeColumn col : widget.getTree().getColumns()) {
            col.addSelectionListener(new ColumnSelectionListener(colIndex, col));
            colIndex++;
        }
    }

    protected TreeViewer buildViewer(AbstractFilteredView<Tree> widget) {
        return new TreeViewer((Tree) widget.getElement());
    }

    protected AbstractFilteredFilter buildFilter(TreeViewer viewer) {
        return new RegexViewerFilter(viewer);
    }

    class ColumnSelectionListener extends SelectionAdapter {
        int columnIndex;
        TreeColumn column;

        public ColumnSelectionListener(int columnIndex, TreeColumn column) {
            this.columnIndex = columnIndex;
            this.column = column;
        }

        public void widgetSelected(SelectionEvent e) {
            if (FilteredTreeViewer.this.comparator != null) {
                FilteredTreeViewer.this.comparator.setColumn(this.columnIndex);
                int dir = FilteredTreeViewer.this.comparator.getDirection();
                ((Tree) FilteredTreeViewer.this.getWidget().getElement()).setSortDirection(dir);
                ((Tree) FilteredTreeViewer.this.getWidget().getElement()).setSortColumn(this.column);
                FilteredTreeViewer.this.refresh();
            }
        }
    }

    protected Object doAfterEmptyFilter() {
        if (this.expandedElements != null) {
            ((TreeViewer) getViewer()).collapseAll();
            ((TreeViewer) getViewer()).setExpandedElements(this.expandedElements);
            this.expandedElements = null;
        }
        return null;
    }

    protected void doBeforeNotNullFilter() {
        if (this.expandedElements == null) {
            this.expandedElements = ((TreeViewer) getViewer()).getExpandedElements();
        }
    }

    protected Object doAfterNotNullFilter() {
        if (this.expandAfterFilter) {
            try {
                ((TreeViewer) getViewer()).getTree().setRedraw(false);
                TreeItem[] items = ((TreeViewer) getViewer()).getTree().getItems();
                return Integer.valueOf(expandFiltered(items, items.length));
            } finally {
                ((TreeViewer) getViewer()).getTree().setRedraw(true);
            }
        }
        return null;
    }

    private int expandFiltered(TreeItem[] items, int opened) {
        if ((items == null) || (items.length == 0)) {
            return opened;
        }
        List<TreeItem> toExpand = new ArrayList();
        for (TreeItem item : items) {
            if (getProvider().hasChildren(item.getData())) {
                boolean childMatch = isOneChildMatchFilter(item.getData());
                if (childMatch) {
                    toExpand.add(item);
                }
            }
        }
        Iterator<TreeItem> iterator = toExpand.iterator();
//        for (??? =toExpand.iterator();
//        ((Iterator) ? ??).hasNext();){
        while (iterator.hasNext()) {
            TreeItem item = iterator.next();
            ((TreeViewer) getViewer()).setExpandedState(item.getData(), true);
            opened += item.getItems().length;
            if (opened > 2000) {
                return -1;
            }
        }
        for (int i = 0; i < toExpand.size(); i++) {
            opened = expandFiltered(((TreeItem) toExpand.get(i)).getItems(), opened);
            if (opened > 2000) {
                return -1;
            }
            if (opened == -1) {
                return -1;
            }
        }
        return opened;
    }

    private boolean isOneChildMatchFilter(Object root) {
        if ((root == null) || (!getProvider().hasChildren(root))) {
            return false;
        }
        Object[] children = getProvider().getChildren(root);
        for (Object child : children) {
            if (this.filter.isElementMatch(child)) {
                return true;
            }
        }
        for (Object child : children) {
            boolean childMatch = isOneChildMatchFilter(child);
            if (childMatch) {
                return true;
            }
        }
        return false;
    }

    public void addDragnDropSupport(IDndProvider dndProvider) {
        Transfer[] types = {TextTransfer.getInstance()};
        int operations = 10;
        DndDragSource dragSource = new DndDragSource(dndProvider);
        ((TreeViewer) getViewer()).addDragSupport(operations, types, dragSource);
        ((TreeViewer) getViewer()).addDropSupport(operations, types, new DndDropTarget(getViewer(), dndProvider, dragSource));
    }

    public void setContentProvider(IFilteredTreeContentProvider provider) {
        super.setContentProvider(provider);
    }

    public IFilteredTreeContentProvider getProvider() {
        return (IFilteredTreeContentProvider) this.provider;
    }

    public void expandAll() {
        ((TreeViewer) getViewer()).expandAll();
    }

    public void expandToLevel(int level) {
        ((TreeViewer) getViewer()).expandToLevel(level);
    }

    public void expandToLevel(Object elementOrTreePath, int level) {
        ((TreeViewer) getViewer()).expandToLevel(elementOrTreePath, level);
    }

    public class RegexViewerFilter
            extends AbstractFilteredFilter {
        private Set<Object> parentMatches = Collections.newSetFromMap(new WeakHashMap());

        public RegexViewerFilter(StructuredViewer viewer) {
            super(viewer);
        }

        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (this.patternChanged) {
                this.patternChanged = false;
                this.parentMatches.clear();
            }
            if (this.parentMatches.contains(element)) {
                return true;
            }
            if (isParentMatches(parentElement)) {
                this.parentMatches.add(element);
                return true;
            }
            boolean elementMatch = isElementMatch(element);
            if (elementMatch) {
                this.parentMatches.add(element);
                return true;
            }
            Object[] children = getProvider().getChildren(element);
            if (children != null) {
                for (Object child : children) {
                    boolean r = select(viewer, element, child);
                    if (r) {
                        return true;
                    }
                }
            }
            return elementMatch;
        }

        public IFilteredTreeContentProvider getProvider() {
            return FilteredTreeViewer.this.getProvider();
        }

        private boolean isParentMatches(Object parentElement) {
            return this.parentMatches.contains(parentElement);
        }

        protected void onRefreshDone() {
        }
    }

    public String exportToString() {
        return TreeUtil.buildXml(((TreeViewer) getViewer()).getTree(), 2);
    }
}


