
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.rcpclient.util.regex.ILabelValueProvider;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class DefaultCellLabelProvider
        extends CellLabelProvider
        implements ILabelValueProvider {
    private IFilteredTableContentProvider provider;

    public DefaultCellLabelProvider(IFilteredTableContentProvider provider) {
        this.provider = provider;
    }

    public void update(ViewerCell cell) {
        String s = getStringAt(cell.getElement(), cell.getColumnIndex());
        cell.setText(Strings.safe(s));
    }

    public String getStringAt(Object element, int key) {
        if (element == null) {
            return null;
        }
        Object[] elts = this.provider.getRowElements(element);
        Object o = null;
        if ((elts != null) && (key >= 0) && (key < elts.length)) {
            o = elts[key];
        }
        return o == null ? null : o.toString();
    }

    public String getString(Object element) {
        StringBuilder sb = new StringBuilder();
        int maxSafe = 40;
        int key = 0;
        while (key < 40) {
            if (key >= 1) {
                sb.append(",");
            }
            String value = getStringAt(element, key);
            if (value == null) {
                break;
            }
            sb.append(value);
            key++;
        }
        return sb.toString();
    }
}


