
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;

import com.pnfsoftware.jeb.core.output.tree.INode;
import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;

class ContentProvider
        implements IFilteredTreeContentProvider {
    private static final ILogger logger = GlobalLog.getLogger(ContentProvider.class);
    ITreeDocument input;

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.input = ((ITreeDocument) newInput);
    }

    public Object[] getChildren(Object e) {
        if ((e instanceof ITreeDocument)) {
            logger.i("getChildren() of root", new Object[0]);
            return ((ITreeDocument) e).getRoots().toArray();
        }
        if ((e instanceof INode)) {
            List<? extends INode> l = ((INode) e).getChildren();
            logger.i("getChildren(%s)", new Object[]{((INode) e).getLabel()});
            if (l != null) {
                return l.toArray();
            }
        }
        return new Object[0];
    }

    public Object[] getElements(Object e) {
        return getChildren(e);
    }

    public Object getParent(Object e) {
        return null;
    }

    public boolean hasChildren(Object e) {
        return getChildren(e).length > 0;
    }

    public Object[] getRowElements(Object row) {
        if ((row instanceof INode)) {
            INode n = (INode) row;
            String[] additionalLabels = n.getAdditionalLabels();
            int rowSize = 1 + (additionalLabels == null ? 0 : additionalLabels.length);
            Object[] rowElements = new Object[rowSize];
            rowElements[0] = Strings.replaceNewLines(n.getLabel(), " / ");
            if (additionalLabels != null) {
                for (int i = 0; i < additionalLabels.length; i++) {
                    rowElements[(i + 1)] = Strings.replaceNewLines(additionalLabels[i], " / ");
                }
            }
            return rowElements;
        }
        return null;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\ContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */