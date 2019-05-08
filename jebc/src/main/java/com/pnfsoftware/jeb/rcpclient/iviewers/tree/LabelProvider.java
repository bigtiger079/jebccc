
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;

import com.pnfsoftware.jeb.core.output.tree.INode;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class LabelProvider
        extends StyledCellLabelProvider
        implements IValueProvider {
    private InteractiveTreeViewer viewer;

    public LabelProvider(InteractiveTreeViewer viewer) {
        this.viewer = viewer;
    }

    public void update(ViewerCell cell) {
        Object e = cell.getElement();
        if ((e instanceof INode)) {
            INode n = (INode) e;
            int index = cell.getColumnIndex();
            cell.setText(Strings.safe(getStringAt(e, index)));
            if (index == 0) {
                IStyleProvider styleAdapter = this.viewer.getStyleAdapter();
                if (styleAdapter != null) {
                    Style style = this.viewer.getStyleAdapter().getStyle(n);
                    if (style != null) {
                        cell.setForeground(style.getColor());
                        cell.setBackground(style.getBackgroungColor());
                    }
                }
            }
        }
        super.update(cell);
    }

    public String getString(Object element) {
        return getStringAt(element, 0);
    }

    public String getStringAt(Object element, int index) {
        if ((element instanceof INode)) {
            INode n = (INode) element;
            if (index >= 1) {
                int i = index - 1;
                String[] additionalLabels = n.getAdditionalLabels();
                if ((additionalLabels != null) && (i < additionalLabels.length)) {
                    return Strings.replaceNewLines(additionalLabels[i], " / ");
                }
            } else if (index == 0) {
                return Strings.replaceNewLines(n.getLabel(), " / ");
            }
        }
        return null;
    }
}


