/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.table;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.output.table.ICellCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableDocument;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableRow;
/*     */ import com.pnfsoftware.jeb.core.output.table.impl.CellCoordinates;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.TablePatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.PatternFilter;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.ColumnViewer;
/*     */ import org.eclipse.jface.viewers.IStructuredSelection;
/*     */ import org.eclipse.jface.viewers.StructuredSelection;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;

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
/*     */ public class InteractiveTableViewer
        /*     */ implements IOperable, IContextMenu
        /*     */ {
    /*     */   private static final int columnMaxWidth = 250;
    /*     */   private ITableDocument idoc;
    /*     */   private IEventListener idocListener;
    /*     */   private FilteredTableViewer viewer;
    /*     */   private FilteredTableView filteredView;
    /*     */   private Table table;
    /*     */   private boolean outdated;
    /*     */   private boolean refreshOnChange;
    /*     */   private IStyleProvider styleAdapter;
    /*     */   private ContentProvider provider;
    /*     */   private ColLabelProvider labelProvider;

    /*     */
    /*     */
    public InteractiveTableViewer(Composite parent, int style, ITableDocument idoc, RcpClientContext context)
    /*     */ {
        /*  65 */
        final Composite container = new Composite(parent, 0);
        /*  66 */
        container.setLayout(new FillLayout());
        /*     */
        /*  68 */
        this.idoc = idoc;
        /*     */
        /*  70 */
        List<String> columnLabels = idoc.getColumnLabels();
        /*  71 */
        if (columnLabels.isEmpty()) {
            /*  72 */
            throw new RuntimeException("The table contains 0 column");
            /*     */
        }
        /*  74 */
        String[] columnNames = (String[]) columnLabels.toArray(new String[columnLabels.size()]);
        /*  75 */
        this.provider = new ContentProvider();
        /*  76 */
        this.labelProvider = new ColLabelProvider(this);
        /*  77 */
        IPatternMatcher patternMatcher = new TablePatternMatcher(this.provider, this.labelProvider);
        /*     */
        /*  79 */
        this.filteredView = new FilteredTableView(container, 896, columnNames);
        /*  80 */
        this.viewer = new FilteredTableViewer(this.filteredView);
        /*  81 */
        this.viewer.setFilterPatternFactory(new PatternFilter(patternMatcher, "", columnNames));
        /*     */
        /*  83 */
        this.table = this.filteredView.getTable();
        /*     */
        /*     */
        /*  86 */
        ContextMenuFilter.addContextMenu(this.viewer.getViewer(), this.filteredView.getFilterText(), this.labelProvider, columnNames, null, this);
        /*     */
        /*     */
        /*  89 */
        this.viewer.setContentProvider(this.provider);
        /*  90 */
        this.viewer.setLabelProvider(this.labelProvider);
        /*     */
        /*  92 */
        this.outdated = true;
        /*  93 */
        this.refreshOnChange = true;
        /*     */
        /*     */
        /*  96 */
        idoc.addListener(this. = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e)
            /*     */ {
                /* 100 */
                UIExecutor.async(container, new UIRunnable()
                        /*     */ {
                    /*     */
                    public void runi()
                    /*     */ {
                        /* 104 */
                        InteractiveTableViewer.this.outdated = true;
                        /* 105 */
                        if (InteractiveTableViewer.this.refreshOnChange) {
                            /* 106 */
                            InteractiveTableViewer.this.refresh();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                });
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public void initialize()
    /*     */ {
        /* 116 */
        refresh();
        /*     */
        /*     */
        /* 119 */
        for (TableColumn tc : this.table.getColumns()) {
            /* 120 */
            tc.pack();
            /*     */
        }
        /* 122 */
        for (TableColumn tc : this.table.getColumns()) {
            /* 123 */
            if (tc.getWidth() > 250) {
                /* 124 */
                tc.setWidth(250);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void dispose() {
        /* 130 */
        this.idoc.removeListener(this.idocListener);
        /*     */
    }

    /*     */
    /*     */
    public void setStyleAdapter(IStyleProvider styleAdapter) {
        /* 134 */
        this.styleAdapter = styleAdapter;
        /*     */
    }

    /*     */
    /*     */
    public IStyleProvider getStyleAdapter() {
        /* 138 */
        return this.styleAdapter;
        /*     */
    }

    /*     */
    /*     */
    public boolean isDirty() {
        /* 142 */
        return this.outdated;
        /*     */
    }

    /*     */
    /*     */
    public boolean getRefreshOnChange() {
        /* 146 */
        return this.refreshOnChange;
        /*     */
    }

    /*     */
    /*     */
    public void setRefreshOnChange(boolean enabled) {
        /* 150 */
        this.refreshOnChange = enabled;
        /*     */
    }

    /*     */
    /*     */
    public void refresh() {
        /* 154 */
        refresh(false);
        /*     */
    }

    /*     */
    /*     */
    public void refresh(boolean force) {
        /* 158 */
        if ((this.outdated) || (force))
            /*     */ {
            /* 160 */
            ITableDocumentPart docPart = this.idoc.getTable();
            /* 161 */
            this.viewer.setInput(docPart, false);
            /* 162 */
            this.outdated = false;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public FilteredTableViewer getFilteredTableViewer() {
        /* 167 */
        return this.viewer;
        /*     */
    }

    /*     */
    /*     */
    public ColumnViewer getViewer() {
        /* 171 */
        return this.viewer.getViewer();
        /*     */
    }

    /*     */
    /*     */
    public Table getTableWidget() {
        /* 175 */
        return this.table;
        /*     */
    }

    /*     */
    /*     */
    public ITableDocument getInfiniDocument() {
        /* 179 */
        return this.idoc;
        /*     */
    }

    /*     */
    /*     */
    public IStructuredSelection getSelection() {
        /* 183 */
        return (IStructuredSelection) this.viewer.getSelection();
        /*     */
    }

    /*     */
    /*     */
    public ITableRow getSelectedRow() {
        /* 187 */
        IStructuredSelection selection = getSelection();
        /* 188 */
        if (selection == null) {
            /* 189 */
            return null;
            /*     */
        }
        /*     */
        /* 192 */
        Object elt = selection.getFirstElement();
        /* 193 */
        if (!(elt instanceof ITableRow)) {
            /* 194 */
            return null;
            /*     */
        }
        /*     */
        /* 197 */
        return (ITableRow) elt;
        /*     */
    }

    /*     */
    /*     */
    public boolean setPosition(ICellCoordinates coord, boolean record) {
        /* 201 */
        int rowIndex = coord.getRowIndex();
        /* 202 */
        if ((rowIndex < 0) || (rowIndex >= this.idoc.getRowCount())) {
            /* 203 */
            return false;
            /*     */
        }
        /*     */
        /* 206 */
        ITableRow row = (ITableRow) this.idoc.getTable().getRows().get(rowIndex);
        /*     */
        /*     */
        /* 209 */
        this.viewer.setSelection(new StructuredSelection(row), true);
        /* 210 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public CellCoordinates getPosition()
    /*     */ {
        /* 219 */
        int selectedIndex = this.table.getSelectionIndex();
        /* 220 */
        if (selectedIndex < 0) {
            /* 221 */
            return null;
            /*     */
        }
        /*     */
        /* 224 */
        return new CellCoordinates(selectedIndex, 0);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 229 */
        menuMgr.add(new OperationCopy(this));
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 234 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 236 */
                return true;
            /*     */
        }
        /*     */
        /* 239 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 245 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 247 */
                this.filteredView.setFilterVisibility(true, true);
                /* 248 */
                return true;
            /*     */
        }
        /*     */
        /* 251 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public IFilteredTableContentProvider getProvider()
    /*     */ {
        /* 256 */
        return this.provider;
        /*     */
    }

    /*     */
    /*     */
    public IValueProvider getLabelProvider() {
        /* 260 */
        return this.labelProvider;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\table\InteractiveTableViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */