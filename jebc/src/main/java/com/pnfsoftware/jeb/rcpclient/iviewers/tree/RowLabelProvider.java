
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;

import com.pnfsoftware.jeb.core.output.tree.INode;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class RowLabelProvider
        implements ITableLabelProvider {
    public void addListener(ILabelProviderListener listener) {
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof INode)) {
            return "";
        }
        INode n = (INode) element;
        if (columnIndex == 0) {
            return n.getLabel();
        }
        int i = columnIndex - 1;
        String[] additionalLabels = n.getAdditionalLabels();
        if ((additionalLabels == null) || (additionalLabels.length <= i)) {
            return "";
        }
        return additionalLabels[i];
    }
}


