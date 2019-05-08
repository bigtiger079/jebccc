package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.TablePatternMatcher;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.util.regex.ILabelValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.PatternFilter;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class AbstractFilteredTableView<T extends IUnit, V> extends AbstractInteractiveTableView<T, V> {
    private FilteredTableView filteredView;
    private FilteredTableViewer filteredViewer;
    private ContextMenu menu;
    private IFilteredTableContentProvider provider;
    private ILabelValueProvider labelProvider;

    public AbstractFilteredTableView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context, IFilteredTableContentProvider provider) {
        super(parent, style, unit, unitView, context);
        this.provider = provider;
    }

    public IFilteredTableContentProvider getProvider() {
        return this.provider;
    }

    protected void setLabelProvider(ILabelValueProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public ILabelValueProvider getLabelProvider() {
        if (this.labelProvider == null) {
            this.labelProvider = new DefaultCellLabelProvider(this.provider);
        }
        return this.labelProvider;
    }

    protected FilteredTableViewer buildFilteredViewer(Composite parent, String[] columnNames) {
        return buildFilteredViewer(parent, columnNames, true);
    }

    protected FilteredTableViewer buildFilteredViewer(Composite parent, String[] columnNames, boolean defaultMenu) {
        IPatternMatcher patternMatcher = new TablePatternMatcher(this.provider, getLabelProvider());
        buildSimpleInner(parent, 898, columnNames);
        this.filteredViewer.setFilterPatternFactory(new PatternFilter(patternMatcher, "", columnNames));
        ColumnViewer viewer = this.filteredViewer.getViewer();
        if (defaultMenu) {
            this.menu = ContextMenuFilter.addContextMenu(viewer, this.filteredView.getFilterText(), getLabelProvider(), columnNames, null);
        }
        this.filteredViewer.setInput(this.unit, true);
        this.filteredViewer.setDisplayFilteredRowCount(true);
        for (TableColumn tc : this.filteredView.getTable().getColumns()) {
            tc.pack();
        }
        return this.filteredViewer;
    }

    protected FilteredTableViewer buildSimple(Composite parent, int style, String[] columnNames) {
        buildSimpleInner(parent, style, columnNames);
        this.filteredViewer.setInput(this.unit, true);
        return this.filteredViewer;
    }

    private FilteredTableViewer buildSimpleInner(Composite parent, int style, String[] columnNames) {
        this.filteredView = new FilteredTableView(parent, style, columnNames);
        initFilteredView(this.filteredView);
        this.filteredViewer = new FilteredTableViewer(this.filteredView);
        this.filteredViewer.setContentProvider(this.provider);
        this.filteredViewer.setLabelProvider(getLabelProvider());
        return this.filteredViewer;
    }

    protected void addContextMenu(IContextMenu menuItem) {
        if (this.menu == null) {
            throw new IllegalStateException("No menu exist for this fragment");
        }
        this.menu.addContextMenu(menuItem);
    }

    public FilteredTableViewer getViewer() {
        return this.filteredViewer;
    }

    public void refresh() {
        this.filteredViewer.refresh();
    }

    public IStructuredSelection getSelection() {
        return (IStructuredSelection) this.filteredViewer.getSelection();
    }

    public Object getSelectedRawRow() {
        IStructuredSelection sel = getSelection();
        if (sel.isEmpty()) {
            return null;
        }
        return sel.getFirstElement();
    }

    protected abstract boolean isCorrectRow(Object paramObject);

    public String exportElementToString(Object obj) {
        if (isCorrectRow(obj)) {
            return exportRowToStringV((V) obj);
        }
        return null;
    }

    protected String exportRowToStringV(V obj) {
        Object[] row = this.provider.getRowElements(obj);
        return ExportUtil.buildCsvLine(getLabelProvider(), obj, row.length);
    }

    protected void initFilteredView(FilteredTableView view) {
    }

    public byte[] export() {
        return Strings.encodeUTF8(this.filteredView.exportToString());
    }
}


