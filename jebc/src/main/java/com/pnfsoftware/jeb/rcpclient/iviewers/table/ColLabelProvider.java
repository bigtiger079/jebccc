
package com.pnfsoftware.jeb.rcpclient.iviewers.table;


import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.table.ICell;
import com.pnfsoftware.jeb.core.output.table.ITableRow;
import com.pnfsoftware.jeb.core.output.table.IVisualCell;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;


class ColLabelProvider
        extends CellLabelProvider
        implements IValueProvider {
    private InteractiveTableViewer viewer;


    public ColLabelProvider(InteractiveTableViewer viewer) {

        this.viewer = viewer;

    }


    public void update(ViewerCell cell) {

        Object element = cell.getElement();

        int index = cell.getColumnIndex();

        cell.setText(getText(element, index));

        cell.setBackground(getBackground(element, index));

        cell.setForeground(getForeground(element, index));

    }


    public String getText(Object element, int index) {

        ICell cell = getCell(element, index);

        if (cell == null) {

            return null;

        }


        return Strings.replaceNewLines(cell.getLabel(), " / ");

    }


    public Color getForeground(Object element, int index) {

        Style style = getStyle(element, index);

        return style == null ? null : style.getColor();

    }


    public Color getBackground(Object element, int index) {

        Style style = getStyle(element, index);

        return style == null ? null : style.getBackgroungColor();

    }


    private static ICell getCell(Object element, int colIndex) {

        if (element == null) {

            return null;

        }

        List<? extends ICell> cells = ((ITableRow) element).getCells();

        if (cells == null) {

            return null;

        }

        if (colIndex >= cells.size()) {

            return null;

        }

        return (ICell) cells.get(colIndex);

    }


    public Style getStyle(Object element, int colIndex) {

        ICell cell = getCell(element, colIndex);

        if (!(cell instanceof IVisualCell)) {

            return null;

        }


        ItemClassIdentifiers classId = ((IVisualCell) cell).getClassId();

        if (classId == null) {

            return null;

        }


        IStyleProvider styleAdapter = this.viewer.getStyleAdapter();

        if (styleAdapter == null) {

            return null;

        }


        return styleAdapter.getStyle(cell);

    }


    public String getString(Object element) {

        return getStringAt(element, 0);

    }


    public String getStringAt(Object element, int key) {

        ICell cell = getCell(element, key);

        if (cell == null) {

            return null;

        }

        return cell.getLabel();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\table\ColLabelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */