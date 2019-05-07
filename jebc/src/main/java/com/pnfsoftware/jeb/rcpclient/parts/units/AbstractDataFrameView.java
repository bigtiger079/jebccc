/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.IItem;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame.Row;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import org.eclipse.jface.viewers.ColumnViewer;
/*    */ import org.eclipse.jface.viewers.IStructuredSelection;
/*    */ import org.eclipse.swt.widgets.Composite;

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
/*    */ public abstract class AbstractDataFrameView<T extends IUnit>
        /*    */ extends AbstractInteractiveTableView<T, DataFrame.Row>
        /*    */ {
    /*    */   private DataFrame df;
    /*    */   private DataFrameView dfv;

    /*    */
    /*    */
    public AbstractDataFrameView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context)
    /*    */ {
        /* 35 */
        super(parent, style, unit, unitView, context);
        /*    */
    }

    /*    */
    /*    */
    public DataFrameView buildSimple(Composite parent, String... labels) {
        /* 39 */
        this.df = new DataFrame(labels);
        /* 40 */
        initDataFrame(this.df);
        /*    */
        /* 42 */
        this.dfv = new DataFrameView(parent, this.df, false);
        /*    */
        /* 44 */
        return this.dfv;
        /*    */
    }

    /*    */
    /*    */
    protected abstract void initDataFrame(DataFrame paramDataFrame);

    /*    */
    /*    */
    public DataFrame.Row getSelectedRow()
    /*    */ {
        /* 51 */
        IStructuredSelection sel = (IStructuredSelection) this.dfv.getTableViewer().getSelection();
        /* 52 */
        if (sel.isEmpty()) {
            /* 53 */
            return null;
            /*    */
        }
        /*    */
        /* 56 */
        Object o = sel.getFirstElement();
        /* 57 */
        if ((o instanceof DataFrame.Row)) {
            /* 58 */
            return (DataFrame.Row) o;
            /*    */
        }
        /* 60 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public IStructuredSelection getSelection()
    /*    */ {
        /* 65 */
        return (IStructuredSelection) this.dfv.getTableViewer().getSelection();
        /*    */
    }

    /*    */
    /*    */
    public String exportElementToString(Object obj)
    /*    */ {
        /* 70 */
        if ((obj instanceof DataFrame.Row)) {
            /* 71 */
            return exportRowToString((DataFrame.Row) obj);
            /*    */
        }
        /* 73 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    protected String exportRowToString(DataFrame.Row obj) {
        /* 77 */
        Object[] row = this.dfv.getProvider().getRowElements(obj);
        /* 78 */
        return ExportUtil.buildCsvLine(this.dfv.getLabelProvider(), obj, row.length);
        /*    */
    }

    /*    */
    /*    */
    public boolean isActiveItem(IItem item)
    /*    */ {
        /* 83 */
        return (item != null) && (getActiveItem() == item);
        /*    */
    }

    /*    */
    /*    */
    public byte[] export()
    /*    */ {
        /* 88 */
        return Strings.encodeUTF8(this.dfv.exportToString());
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\AbstractDataFrameView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */