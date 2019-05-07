/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.table;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.table.ICell;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableRow;
/*     */ import com.pnfsoftware.jeb.util.base.CharSequenceList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.text.StringEscapeUtils;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;

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
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class TableUtil
        /*     */ {
    /*     */
    public static String buildCsv(ITableDocumentPart part, Table table)
    /*     */ {
        /*  37 */
        List<CharSequence> lines = new ArrayList();
        /*     */
        /*  39 */
        buildHeader(lines, table);
        /*     */
        /*  41 */
        for (ITableRow line : part.getRows()) {
            /*  42 */
            StringBuilder csvLine = new StringBuilder();
            /*  43 */
            List<? extends ICell> cells = line.getCells();
            /*  44 */
            for (int i = 0; i < cells.size(); i++) {
                /*  45 */
                csvLine.append(StringEscapeUtils.escapeCsv(((ICell) cells.get(i)).getLabel()));
                /*  46 */
                if (i != cells.size() - 1) {
                    /*  47 */
                    appendSeparator(csvLine);
                    /*     */
                }
                /*     */
            }
            /*  50 */
            lines.add(csvLine);
            /*     */
        }
        /*  52 */
        return new CharSequenceList(lines).toString();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static String buildCsv(Table table)
    /*     */ {
        /*  62 */
        List<CharSequence> lines = new ArrayList();
        /*     */
        /*  64 */
        buildHeader(lines, table);
        /*     */
        /*     */
        /*  67 */
        StringBuilder csvLine = new StringBuilder();
        /*  68 */
        TableColumn[] columns = table.getColumns();
        /*  69 */
        TableItem[] items = table.getItems();
        /*  70 */
        for (int i = 0; i < items.length; i++) {
            /*  71 */
            csvLine = new StringBuilder();
            /*  72 */
            for (int j = 0; j < columns.length; j++) {
                /*  73 */
                csvLine.append(StringEscapeUtils.escapeCsv(items[i].getText(j)));
                /*  74 */
                if (j != columns.length - 1) {
                    /*  75 */
                    appendSeparator(csvLine);
                    /*     */
                }
                /*     */
            }
            /*  78 */
            lines.add(csvLine);
            /*     */
        }
        /*     */
        /*  81 */
        return new CharSequenceList(lines).toString();
        /*     */
    }

    /*     */
    /*     */
    private static void buildHeader(List<CharSequence> lines, Table table) {
        /*  85 */
        if (table == null) {
            /*  86 */
            return;
            /*     */
        }
        /*  88 */
        StringBuilder csvLine = new StringBuilder();
        /*  89 */
        TableColumn[] columns = table.getColumns();
        /*     */
        /*  91 */
        for (int i = 0; i < columns.length; i++) {
            /*  92 */
            csvLine.append(StringEscapeUtils.escapeCsv(columns[i].getText()));
            /*  93 */
            if (i != columns.length - 1) {
                /*  94 */
                appendSeparator(csvLine);
                /*     */
            }
            /*     */
        }
        /*  97 */
        lines.add(csvLine);
        /*     */
    }

    /*     */
    /*     */
    private static StringBuilder appendSeparator(StringBuilder stb) {
        /* 101 */
        return stb.append(",");
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\table\TableUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */