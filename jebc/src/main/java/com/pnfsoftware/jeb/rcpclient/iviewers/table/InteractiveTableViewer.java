package com.pnfsoftware.jeb.rcpclient.iviewers.table;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.table.ICellCoordinates;
import com.pnfsoftware.jeb.core.output.table.ITableDocument;
import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.ITableRow;
import com.pnfsoftware.jeb.core.output.table.impl.CellCoordinates;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.TablePatternMatcher;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.PatternFilter;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class InteractiveTableViewer implements IOperable, IContextMenu {
    private static final int columnMaxWidth = 250;
    private ITableDocument idoc;
    private IEventListener idocListener;
    private FilteredTableViewer viewer;
    private FilteredTableView filteredView;
    private Table table;
    private boolean outdated;
    private boolean refreshOnChange;
    private IStyleProvider styleAdapter;
    private ContentProvider provider;
    private ColLabelProvider labelProvider;

    public InteractiveTableViewer(Composite parent, int style, ITableDocument idoc, RcpClientContext context) {
        final Composite container = new Composite(parent, 0);
        container.setLayout(new FillLayout());
        this.idoc = idoc;
        List<String> columnLabels = idoc.getColumnLabels();
        if (columnLabels.isEmpty()) {
            throw new RuntimeException("The table contains 0 column");
        }
        String[] columnNames = (String[]) columnLabels.toArray(new String[columnLabels.size()]);
        this.provider = new ContentProvider();
        this.labelProvider = new ColLabelProvider(this);
        IPatternMatcher patternMatcher = new TablePatternMatcher(this.provider, this.labelProvider);
        this.filteredView = new FilteredTableView(container, 896, columnNames);
        this.viewer = new FilteredTableViewer(this.filteredView);
        this.viewer.setFilterPatternFactory(new PatternFilter(patternMatcher, "", columnNames));
        this.table = this.filteredView.getTable();
        ContextMenuFilter.addContextMenu(this.viewer.getViewer(), this.filteredView.getFilterText(), this.labelProvider, columnNames, null, this);
        this.viewer.setContentProvider(this.provider);
        this.viewer.setLabelProvider(this.labelProvider);
        this.outdated = true;
        this.refreshOnChange = true;
        idoc.addListener(this.idocListener = new IEventListener() {
            public void onEvent(IEvent e) {
                UIExecutor.async(container, new UIRunnable() {
                    public void runi() {
                        InteractiveTableViewer.this.outdated = true;
                        if (InteractiveTableViewer.this.refreshOnChange) {
                            InteractiveTableViewer.this.refresh();
                        }
                    }
                });
            }
        });
    }

    public void initialize() {
        refresh();
        for (TableColumn tc : this.table.getColumns()) {
            tc.pack();
        }
        for (TableColumn tc : this.table.getColumns()) {
            if (tc.getWidth() > 250) {
                tc.setWidth(250);
            }
        }
    }

    public void dispose() {
        this.idoc.removeListener(this.idocListener);
    }

    public void setStyleAdapter(IStyleProvider styleAdapter) {
        this.styleAdapter = styleAdapter;
    }

    public IStyleProvider getStyleAdapter() {
        return this.styleAdapter;
    }

    public boolean isDirty() {
        return this.outdated;
    }

    public boolean getRefreshOnChange() {
        return this.refreshOnChange;
    }

    public void setRefreshOnChange(boolean enabled) {
        this.refreshOnChange = enabled;
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean force) {
        if ((this.outdated) || (force)) {
            ITableDocumentPart docPart = this.idoc.getTable();
            this.viewer.setInput(docPart, false);
            this.outdated = false;
        }
    }

    public FilteredTableViewer getFilteredTableViewer() {
        return this.viewer;
    }

    public ColumnViewer getViewer() {
        return this.viewer.getViewer();
    }

    public Table getTableWidget() {
        return this.table;
    }

    public ITableDocument getInfiniDocument() {
        return this.idoc;
    }

    public IStructuredSelection getSelection() {
        return (IStructuredSelection) this.viewer.getSelection();
    }

    public ITableRow getSelectedRow() {
        IStructuredSelection selection = getSelection();
        if (selection == null) {
            return null;
        }
        Object elt = selection.getFirstElement();
        if (!(elt instanceof ITableRow)) {
            return null;
        }
        return (ITableRow) elt;
    }

    public boolean setPosition(ICellCoordinates coord, boolean record) {
        int rowIndex = coord.getRowIndex();
        if ((rowIndex < 0) || (rowIndex >= this.idoc.getRowCount())) {
            return false;
        }
        ITableRow row = (ITableRow) this.idoc.getTable().getRows().get(rowIndex);
        this.viewer.setSelection(new StructuredSelection(row), true);
        return true;
    }

    public CellCoordinates getPosition() {
        int selectedIndex = this.table.getSelectionIndex();
        if (selectedIndex < 0) {
            return null;
        }
        return new CellCoordinates(selectedIndex, 0);
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        menuMgr.add(new OperationCopy(this));
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case FIND:
                return true;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case FIND:
                this.filteredView.setFilterVisibility(true, true);
                return true;
        }
        return false;
    }

    public IFilteredTableContentProvider getProvider() {
        return this.provider;
    }

    public IValueProvider getLabelProvider() {
        return this.labelProvider;
    }
}


