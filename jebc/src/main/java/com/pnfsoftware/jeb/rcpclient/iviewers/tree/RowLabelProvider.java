/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.tree.INode;
/*    */ import org.eclipse.jface.viewers.ILabelProviderListener;
/*    */ import org.eclipse.jface.viewers.ITableLabelProvider;
/*    */ import org.eclipse.swt.graphics.Image;

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
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class RowLabelProvider
        /*    */ implements ITableLabelProvider
        /*    */ {
    /*    */
    public void addListener(ILabelProviderListener listener) {
    }

    /*    */
    /*    */
    public void removeListener(ILabelProviderListener listener) {
    }

    /*    */
    /*    */
    public void dispose() {
    }

    /*    */
    /*    */
    public boolean isLabelProperty(Object element, String property)
    /*    */ {
        /* 37 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public Image getColumnImage(Object element, int columnIndex)
    /*    */ {
        /* 42 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public String getColumnText(Object element, int columnIndex)
    /*    */ {
        /* 47 */
        if (!(element instanceof INode)) {
            /* 48 */
            return "";
            /*    */
        }
        /*    */
        /* 51 */
        INode n = (INode) element;
        /* 52 */
        if (columnIndex == 0) {
            /* 53 */
            return n.getLabel();
            /*    */
        }
        /*    */
        /* 56 */
        int i = columnIndex - 1;
        /* 57 */
        String[] additionalLabels = n.getAdditionalLabels();
        /* 58 */
        if ((additionalLabels == null) || (additionalLabels.length <= i)) {
            /* 59 */
            return "";
            /*    */
        }
        /*    */
        /* 62 */
        return additionalLabels[i];
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\RowLabelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */