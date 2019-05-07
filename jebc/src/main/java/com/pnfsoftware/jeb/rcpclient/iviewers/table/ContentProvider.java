/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.table;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.table.ICell;
/*    */ import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
/*    */ import com.pnfsoftware.jeb.core.output.table.ITableRow;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
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
/*    */
/*    */
/*    */ class ContentProvider
        /*    */ implements IFilteredTableContentProvider
        /*    */ {
    /* 29 */   private static final ILogger logger = GlobalLog.getLogger(ContentProvider.class);
    /*    */
    /*    */ ITableDocumentPart input;

    /*    */
    /*    */
    public void dispose()
    /*    */ {
        /* 35 */
        logger.i("dispose", new Object[0]);
        /*    */
    }

    /*    */
    /*    */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    /*    */ {
        /* 40 */
        logger.i("intputChanged: new=%s", new Object[]{newInput});
        /*    */
        /* 42 */
        this.input = ((ITableDocumentPart) newInput);
        /*    */
    }

    /*    */
    /*    */
    public Object[] getElements(Object inputElement)
    /*    */ {
        /* 47 */
        logger.i("getElements(): input=%s", new Object[]{inputElement});
        /*    */
        /* 49 */
        ITableDocumentPart part = (ITableDocumentPart) inputElement;
        /* 50 */
        return part.getRows().toArray();
        /*    */
    }

    /*    */
    /*    */
    public Object[] getRowElements(Object row)
    /*    */ {
        /* 55 */
        List<? extends ICell> cells = ((ITableRow) row).getCells();
        /* 56 */
        Object[] rowElements = new Object[cells.size()];
        /* 57 */
        for (int i = 0; i < cells.size(); i++) {
            /* 58 */
            rowElements[i] = ((ICell) cells.get(i)).getLabel();
            /*    */
        }
        /* 60 */
        return rowElements;
        /*    */
    }

    /*    */
    /*    */
    public boolean isChecked(Object row)
    /*    */ {
        /* 65 */
        return false;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\table\ContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */