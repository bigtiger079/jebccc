/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.tree.INode;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import org.eclipse.jface.viewers.StyledCellLabelProvider;
/*    */ import org.eclipse.jface.viewers.ViewerCell;

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
/*    */ public class LabelProvider
        /*    */ extends StyledCellLabelProvider
        /*    */ implements IValueProvider
        /*    */ {
    /*    */   private InteractiveTreeViewer viewer;

    /*    */
    /*    */
    public LabelProvider(InteractiveTreeViewer viewer)
    /*    */ {
        /* 28 */
        this.viewer = viewer;
        /*    */
    }

    /*    */
    /*    */
    public void update(ViewerCell cell)
    /*    */ {
        /* 33 */
        Object e = cell.getElement();
        /* 34 */
        if ((e instanceof INode)) {
            /* 35 */
            INode n = (INode) e;
            /* 36 */
            int index = cell.getColumnIndex();
            /*    */
            /* 38 */
            cell.setText(Strings.safe(getStringAt(e, index)));
            /*    */
            /* 40 */
            if (index == 0)
                /*    */ {
                /* 42 */
                IStyleProvider styleAdapter = this.viewer.getStyleAdapter();
                /* 43 */
                if (styleAdapter != null) {
                    /* 44 */
                    Style style = this.viewer.getStyleAdapter().getStyle(n);
                    /* 45 */
                    if (style != null) {
                        /* 46 */
                        cell.setForeground(style.getColor());
                        /* 47 */
                        cell.setBackground(style.getBackgroungColor());
                        /*    */
                    }
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /* 52 */
        super.update(cell);
        /*    */
    }

    /*    */
    /*    */
    public String getString(Object element)
    /*    */ {
        /* 57 */
        return getStringAt(element, 0);
        /*    */
    }

    /*    */
    /*    */
    public String getStringAt(Object element, int index)
    /*    */ {
        /* 62 */
        if ((element instanceof INode)) {
            /* 63 */
            INode n = (INode) element;
            /* 64 */
            if (index >= 1) {
                /* 65 */
                int i = index - 1;
                /* 66 */
                String[] additionalLabels = n.getAdditionalLabels();
                /* 67 */
                if ((additionalLabels != null) && (i < additionalLabels.length))
                    /*    */ {
                    /* 69 */
                    return Strings.replaceNewLines(additionalLabels[i], " / ");
                    /*    */
                }
                /*    */
            }
            /* 72 */
            else if (index == 0) {
                /* 73 */
                return Strings.replaceNewLines(n.getLabel(), " / ");
                /*    */
            }
            /*    */
        }
        /*    */
        /* 77 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\LabelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */