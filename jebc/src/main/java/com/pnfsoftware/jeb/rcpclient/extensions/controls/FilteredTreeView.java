package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class FilteredTreeView extends AbstractFilteredView<Tree> {
    public FilteredTreeView(Composite parent, int style, String[] columnNames) {
        this(parent, style, columnNames, null);
    }

    public FilteredTreeView(Composite parent, int style, String[] columnNames, int[] columnWidths) {
        super(parent, style, columnNames, columnWidths, false);
    }

    public Tree getTree() {
        return (Tree) getElement();
    }

    protected Tree buildElement(Composite parent, int style) {
        Tree tree = new Tree(parent, style);
        tree.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        return tree;
    }

    protected void buildColumn(Tree parent, String name, int initialWidth) {
        TreeColumn col = new TreeColumn(parent, 16384);
        col.setText(name);
        col.setResizable(true);
        if (initialWidth > 0) {
            col.setWidth(initialWidth);
        }
    }

    public int getSelectionIndex() {
        if (((Tree) getElement()).getSelectionCount() == 0) {
            return -1;
        }
        MutableInt counter = new MutableInt(0);
        boolean found = getIndexOfItem(((Tree) getElement()).getItems(), ((Tree) getElement()).getSelection()[0], counter);
        if (found) {
            return counter.intValue();
        }
        return -1;
    }

    private boolean getIndexOfItem(TreeItem[] fullTree, TreeItem toFind, MutableInt currentIndex) {
        for (TreeItem item : fullTree) {
            if (!isBlankItem(item)) {
                if (item.equals(toFind)) {
                    return true;
                }
                currentIndex.increment();
                boolean found = getIndexOfItem(item.getItems(), toFind, currentIndex);
                if (found) return found;
            }
        }
        return false;
    }

    public void setSelection(int index) {
    }

    public int getItemCount() {
        return getItemCount(((Tree) getElement()).getItems());
    }

    private int getItemCount(TreeItem[] items) {
        int childrenCount = 0;
        if ((items == null) || (items.length == 0)) {
            return childrenCount;
        }
        for (TreeItem item : items) {
            if (!isBlankItem(item)) {
                childrenCount += 1 + getItemCount(item.getItems());
            }
        }
        return childrenCount;
    }

    private static boolean isBlankItem(TreeItem item) {
        return ((item.getItems() == null) || (item.getItems().length == 0)) && (item.getData() == null) && (item.getText().isEmpty());
    }
}


