/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.tree.INode;
/*    */ import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.util.List;
/*    */ import org.eclipse.jface.viewers.Viewer;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ class ContentProvider
        /*    */ implements IFilteredTreeContentProvider
        /*    */ {
    /* 27 */   private static final ILogger logger = GlobalLog.getLogger(ContentProvider.class);
    /*    */
    /*    */
    /*    */
    /*    */ ITreeDocument input;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public void dispose() {
    }

    /*    */
    /*    */
    /*    */
    /*    */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    /*    */ {
        /* 42 */
        this.input = ((ITreeDocument) newInput);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public Object[] getChildren(Object e)
    /*    */ {
        /* 48 */
        if ((e instanceof ITreeDocument)) {
            /* 49 */
            logger.i("getChildren() of root", new Object[0]);
            /* 50 */
            return ((ITreeDocument) e).getRoots().toArray();
            /*    */
        }
        /* 52 */
        if ((e instanceof INode)) {
            /* 53 */
            List<? extends INode> l = ((INode) e).getChildren();
            /* 54 */
            logger.i("getChildren(%s)", new Object[]{((INode) e).getLabel()});
            /* 55 */
            if (l != null) {
                /* 56 */
                return l.toArray();
                /*    */
            }
            /*    */
        }
        /* 59 */
        return new Object[0];
        /*    */
    }

    /*    */
    /*    */
    public Object[] getElements(Object e)
    /*    */ {
        /* 64 */
        return getChildren(e);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Object getParent(Object e)
    /*    */ {
        /* 73 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public boolean hasChildren(Object e)
    /*    */ {
        /* 78 */
        return getChildren(e).length > 0;
        /*    */
    }

    /*    */
    /*    */
    public Object[] getRowElements(Object row)
    /*    */ {
        /* 83 */
        if ((row instanceof INode)) {
            /* 84 */
            INode n = (INode) row;
            /* 85 */
            String[] additionalLabels = n.getAdditionalLabels();
            /* 86 */
            int rowSize = 1 + (additionalLabels == null ? 0 : additionalLabels.length);
            /* 87 */
            Object[] rowElements = new Object[rowSize];
            /* 88 */
            rowElements[0] = Strings.replaceNewLines(n.getLabel(), " / ");
            /* 89 */
            if (additionalLabels != null) {
                /* 90 */
                for (int i = 0; i < additionalLabels.length; i++) {
                    /* 91 */
                    rowElements[(i + 1)] = Strings.replaceNewLines(additionalLabels[i], " / ");
                    /*    */
                }
                /*    */
            }
            /* 94 */
            return rowElements;
            /*    */
        }
        /* 96 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\ContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */