
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.export.IExportableData;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.TablePatternMatcher;
import com.pnfsoftware.jeb.rcpclient.iviewers.table.TableUtil;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame.Row;
import com.pnfsoftware.jeb.rcpclient.util.regex.ILabelValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.PatternFilter;
import com.pnfsoftware.jeb.util.base.Assert;

import java.util.List;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class DataFrameView
        extends FilteredTableView {
    private boolean displayIndex;
    private DataFrame df;
    private FilteredTableViewer filteredViewer;
    private ColumnViewer viewer;
    private ContextMenu ctxMenu;
    private IFilteredTableContentProvider provider;
    private DataFrameLabelProvider labelProvider;

    public DataFrameView(Composite parent, DataFrame df, boolean displayIndex) {

        super(parent, 898,
                (String[]) df.getColumnLabels().toArray(new String[df.getColumnLabels().size()]), null, displayIndex);

        this.df = df;

        this.displayIndex = displayIndex;


        String[] titleColumns = (String[]) df.getColumnLabels().toArray(new String[df.getColumnLabels().size()]);

        this.provider = new DataFrameContentProvider();

        this.labelProvider = new DataFrameLabelProvider(this.provider);

        IPatternMatcher patternMatcher = new TablePatternMatcher(this.provider, this.labelProvider);


        this.filteredViewer = new FilteredTableViewer(this);

        this.filteredViewer.setContentProvider(this.provider);

        this.filteredViewer.setLabelProvider(this.labelProvider);

        String[] titleColumnsWithIndex;
        if (displayIndex) {
            titleColumnsWithIndex = new String[titleColumns.length + 1];

            System.arraycopy(titleColumns, 0, titleColumnsWithIndex, 1, titleColumns.length);
            titleColumns = titleColumnsWithIndex;
        }

        this.filteredViewer.setFilterPatternFactory(new PatternFilter(patternMatcher, "", titleColumns));

        this.viewer = this.filteredViewer.getViewer();

        this.ctxMenu = ContextMenuFilter.addContextMenu(this.viewer, getFilterText(), this.labelProvider, titleColumns, null);

        this.filteredViewer.setInput(df, true);

        for (TableColumn tc : getTable().getColumns()) {
            tc.pack();
        }
    }


    public void refresh() {
        this.viewer.refresh();
    }

    public FilteredTableViewer getViewer() {
        return this.filteredViewer;
    }

    public ColumnViewer getTableViewer() {
        return this.viewer;
    }


    public int getSelectedRow() {
        ISelection selection = this.viewer.getSelection();
        if ((selection instanceof IStructuredSelection)) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            Object element = sel.getFirstElement();
            if ((element instanceof DataFrame.Row)) {
                DataFrame.Row row = (DataFrame.Row) element;
                return row.index;
            }
        }

        return -1;
    }


    public ContextMenu getContextMenu() {
        return this.ctxMenu;
    }


    public void forceFilter(String filter) {
        getFilterText().setText(filter);
        this.filteredViewer.applyFilterText();
    }


    class DataFrameLabelProvider
            extends DefaultCellLabelProvider
            implements IExportableData {
        DataFrameLabelProvider(IFilteredTableContentProvider contentProvider) {
            super(contentProvider);
        }


        public String getStringAt(Object element, int key) {
            int index = key;
            if (DataFrameView.this.displayIndex) {
                index--;
            }

            String label = null;

            if (index >= 0) {
                label = DataFrameView.this.df.getLabelFor((DataFrame.Row) element, index);
            }

            if (label == null) {
                label = super.getStringAt(element, key);
            }

            return label;
        }

        public String exportElementToString(Object obj) {
            if ((obj instanceof DataFrame.Row)) {
                Object[] row = ((DataFrame.Row) obj).elements.toArray();

                return ExportUtil.buildCsvLine(this, obj, DataFrameView.this.displayIndex ? row.length + 1 : row.length);

            }
            return null;
        }

    }


    class DataFrameContentProvider implements IFilteredTableContentProvider {
        DataFrameContentProvider() {
        }

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            if (newInput != null) {
                Assert.a(newInput == DataFrameView.this.df);
            }
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            return DataFrameView.this.df.getRows().toArray();
        }


        public Object[] getRowElements(Object row) {

            DataFrame.Row r = (DataFrame.Row) row;

            Object[] rowElements = r.elements.toArray();

            if (DataFrameView.this.displayIndex) {

                Object[] rowElementsWithIndex = new Object[rowElements.length + 1];

                rowElementsWithIndex[0] = Integer.valueOf(r.index);

                System.arraycopy(rowElements, 0, rowElementsWithIndex, 1, rowElements.length);

                return rowElementsWithIndex;
            }

            return rowElements;
        }


        public boolean isChecked(Object row) {
            return false;
        }
    }


    public IFilteredTableContentProvider getProvider() {
        return this.provider;
    }


    public ILabelValueProvider getLabelProvider() {
        return this.labelProvider;

    }


    public String exportToString() {
        return TableUtil.buildCsv(getTable());
    }


    public void addExtraEntriesToContextMenu() {
        ContextMenuFilter.addCopyEntry(this.ctxMenu, getTable(), new CopyAction());
    }


    private class CopyAction implements IOperable {
        public CopyAction() {
        }
        public boolean verifyOperation(OperationRequest req) {
            switch (req.getOperation().ordinal()){
                case 1:
                    return DataFrameView.this.getSelectedRow() >= 0;
            }
            return false;
        }

        public boolean doOperation(OperationRequest req) {
            switch (req.getOperation().ordinal()){
                case 1:
                    IStructuredSelection selection = (IStructuredSelection) DataFrameView.this.viewer.getSelection();
                    if ((DataFrameView.this.viewer.getLabelProvider() instanceof IExportableData)) {
                        ExportUtil.copyLinesToClipboard((IExportableData) DataFrameView.this.viewer.getLabelProvider(), selection.toList());
                        return true;
                    }
                    return false;
            }
            return false;
        }
    }

}
