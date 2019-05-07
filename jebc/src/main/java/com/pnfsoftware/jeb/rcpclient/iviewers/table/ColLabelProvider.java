/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.table;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*     */ import com.pnfsoftware.jeb.core.output.table.ICell;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableRow;
/*     */ import com.pnfsoftware.jeb.core.output.table.IVisualCell;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.CellLabelProvider;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.graphics.Color;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ class ColLabelProvider
        /*     */ extends CellLabelProvider
        /*     */ implements IValueProvider
        /*     */ {
    /*     */   private InteractiveTableViewer viewer;

    /*     */
    /*     */
    public ColLabelProvider(InteractiveTableViewer viewer)
    /*     */ {
        /*  37 */
        this.viewer = viewer;
        /*     */
    }

    /*     */
    /*     */
    public void update(ViewerCell cell)
    /*     */ {
        /*  42 */
        Object element = cell.getElement();
        /*  43 */
        int index = cell.getColumnIndex();
        /*  44 */
        cell.setText(getText(element, index));
        /*  45 */
        cell.setBackground(getBackground(element, index));
        /*  46 */
        cell.setForeground(getForeground(element, index));
        /*     */
    }

    /*     */
    /*     */
    public String getText(Object element, int index) {
        /*  50 */
        ICell cell = getCell(element, index);
        /*  51 */
        if (cell == null) {
            /*  52 */
            return null;
            /*     */
        }
        /*     */
        /*  55 */
        return Strings.replaceNewLines(cell.getLabel(), " / ");
        /*     */
    }

    /*     */
    /*     */
    public Color getForeground(Object element, int index) {
        /*  59 */
        Style style = getStyle(element, index);
        /*  60 */
        return style == null ? null : style.getColor();
        /*     */
    }

    /*     */
    /*     */
    public Color getBackground(Object element, int index) {
        /*  64 */
        Style style = getStyle(element, index);
        /*  65 */
        return style == null ? null : style.getBackgroungColor();
        /*     */
    }

    /*     */
    /*     */
    private static ICell getCell(Object element, int colIndex) {
        /*  69 */
        if (element == null) {
            /*  70 */
            return null;
            /*     */
        }
        /*  72 */
        List<? extends ICell> cells = ((ITableRow) element).getCells();
        /*  73 */
        if (cells == null) {
            /*  74 */
            return null;
            /*     */
        }
        /*  76 */
        if (colIndex >= cells.size()) {
            /*  77 */
            return null;
            /*     */
        }
        /*  79 */
        return (ICell) cells.get(colIndex);
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(Object element, int colIndex) {
        /*  83 */
        ICell cell = getCell(element, colIndex);
        /*  84 */
        if (!(cell instanceof IVisualCell)) {
            /*  85 */
            return null;
            /*     */
        }
        /*     */
        /*  88 */
        ItemClassIdentifiers classId = ((IVisualCell) cell).getClassId();
        /*  89 */
        if (classId == null) {
            /*  90 */
            return null;
            /*     */
        }
        /*     */
        /*  93 */
        IStyleProvider styleAdapter = this.viewer.getStyleAdapter();
        /*  94 */
        if (styleAdapter == null) {
            /*  95 */
            return null;
            /*     */
        }
        /*     */
        /*  98 */
        return styleAdapter.getStyle(cell);
        /*     */
    }

    /*     */
    /*     */
    public String getString(Object element)
    /*     */ {
        /* 103 */
        return getStringAt(element, 0);
        /*     */
    }

    /*     */
    /*     */
    public String getStringAt(Object element, int key)
    /*     */ {
        /* 108 */
        ICell cell = getCell(element, key);
        /* 109 */
        if (cell == null) {
            /* 110 */
            return null;
            /*     */
        }
        /* 112 */
        return cell.getLabel();
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\table\ColLabelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */