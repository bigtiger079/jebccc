package com.pnfsoftware.jeb.rcpclient.iviewers.table;

import com.pnfsoftware.jeb.core.output.table.ICell;
import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.ITableRow;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;

class ContentProvider implements IFilteredTableContentProvider {
    private static final ILogger logger = GlobalLog.getLogger(ContentProvider.class);
    ITableDocumentPart input;

    public void dispose() {
        logger.i("dispose", new Object[0]);
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        logger.i("intputChanged: new=%s", new Object[]{newInput});
        this.input = ((ITableDocumentPart) newInput);
    }

    public Object[] getElements(Object inputElement) {
        logger.i("getElements(): input=%s", new Object[]{inputElement});
        ITableDocumentPart part = (ITableDocumentPart) inputElement;
        return part.getRows().toArray();
    }

    public Object[] getRowElements(Object row) {
        List<? extends ICell> cells = ((ITableRow) row).getCells();
        Object[] rowElements = new Object[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            rowElements[i] = ((ICell) cells.get(i)).getLabel();
        }
        return rowElements;
    }

    public boolean isChecked(Object row) {
        return false;
    }
}


