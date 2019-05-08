package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame.Row;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractDataFrameView<T extends IUnit> extends AbstractInteractiveTableView<T, DataFrame.Row> {
    private DataFrame df;
    private DataFrameView dfv;

    public AbstractDataFrameView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context) {
        super(parent, style, unit, unitView, context);
    }

    public DataFrameView buildSimple(Composite parent, String... labels) {
        this.df = new DataFrame(labels);
        initDataFrame(this.df);
        this.dfv = new DataFrameView(parent, this.df, false);
        return this.dfv;
    }

    protected abstract void initDataFrame(DataFrame paramDataFrame);

    public DataFrame.Row getSelectedRow() {
        IStructuredSelection sel = (IStructuredSelection) this.dfv.getTableViewer().getSelection();
        if (sel.isEmpty()) {
            return null;
        }
        Object o = sel.getFirstElement();
        if ((o instanceof DataFrame.Row)) {
            return (DataFrame.Row) o;
        }
        return null;
    }

    public IStructuredSelection getSelection() {
        return (IStructuredSelection) this.dfv.getTableViewer().getSelection();
    }

    public String exportElementToString(Object obj) {
        if ((obj instanceof DataFrame.Row)) {
            return exportRowToString((DataFrame.Row) obj);
        }
        return null;
    }

    protected String exportRowToString(DataFrame.Row obj) {
        Object[] row = this.dfv.getProvider().getRowElements(obj);
        return ExportUtil.buildCsvLine(this.dfv.getLabelProvider(), obj, row.length);
    }

    public boolean isActiveItem(IItem item) {
        return (item != null) && (getActiveItem() == item);
    }

    public byte[] export() {
        return Strings.encodeUTF8(this.dfv.exportToString());
    }
}


