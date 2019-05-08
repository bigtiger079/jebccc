
package com.pnfsoftware.jeb.rcpclient.iviewers.table;

import com.pnfsoftware.jeb.core.output.table.ICell;
import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.ITableRow;
import com.pnfsoftware.jeb.util.base.CharSequenceList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableUtil {
    public static String buildCsv(ITableDocumentPart part, Table table) {
        List<CharSequence> lines = new ArrayList();
        buildHeader(lines, table);
        for (ITableRow line : part.getRows()) {
            StringBuilder csvLine = new StringBuilder();
            List<? extends ICell> cells = line.getCells();
            for (int i = 0; i < cells.size(); i++) {
                csvLine.append(StringEscapeUtils.escapeCsv(((ICell) cells.get(i)).getLabel()));
                if (i != cells.size() - 1) {
                    appendSeparator(csvLine);
                }
            }
            lines.add(csvLine);
        }
        return new CharSequenceList(lines).toString();
    }

    public static String buildCsv(Table table) {
        List<CharSequence> lines = new ArrayList();
        buildHeader(lines, table);
        StringBuilder csvLine = new StringBuilder();
        TableColumn[] columns = table.getColumns();
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            csvLine = new StringBuilder();
            for (int j = 0; j < columns.length; j++) {
                csvLine.append(StringEscapeUtils.escapeCsv(items[i].getText(j)));
                if (j != columns.length - 1) {
                    appendSeparator(csvLine);
                }
            }
            lines.add(csvLine);
        }
        return new CharSequenceList(lines).toString();
    }

    private static void buildHeader(List<CharSequence> lines, Table table) {
        if (table == null) {
            return;
        }
        StringBuilder csvLine = new StringBuilder();
        TableColumn[] columns = table.getColumns();
        for (int i = 0; i < columns.length; i++) {
            csvLine.append(StringEscapeUtils.escapeCsv(columns[i].getText()));
            if (i != columns.length - 1) {
                appendSeparator(csvLine);
            }
        }
        lines.add(csvLine);
    }

    private static StringBuilder appendSeparator(StringBuilder stb) {
        return stb.append(",");
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\table\TableUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */