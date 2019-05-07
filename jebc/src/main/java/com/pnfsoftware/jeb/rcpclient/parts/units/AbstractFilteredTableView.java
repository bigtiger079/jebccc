/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.TablePatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.ILabelValueProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.PatternFilter;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import org.eclipse.jface.viewers.ColumnViewer;
/*     */ import org.eclipse.jface.viewers.IStructuredSelection;
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
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public abstract class AbstractFilteredTableView<T extends IUnit, V>
        /*     */ extends AbstractInteractiveTableView<T, V>
        /*     */ {
    /*     */   private FilteredTableView filteredView;
    /*     */   private FilteredTableViewer filteredViewer;
    /*     */   private ContextMenu menu;
    /*     */   private IFilteredTableContentProvider provider;
    /*     */   private ILabelValueProvider labelProvider;

    /*     */
    /*     */
    public AbstractFilteredTableView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context, IFilteredTableContentProvider provider)
    /*     */ {
        /*  52 */
        super(parent, style, unit, unitView, context);
        /*  53 */
        this.provider = provider;
        /*     */
    }

    /*     */
    /*     */
    public IFilteredTableContentProvider getProvider() {
        /*  57 */
        return this.provider;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected void setLabelProvider(ILabelValueProvider labelProvider)
    /*     */ {
        /*  67 */
        this.labelProvider = labelProvider;
        /*     */
    }

    /*     */
    /*     */
    public ILabelValueProvider getLabelProvider() {
        /*  71 */
        if (this.labelProvider == null)
            /*     */ {
            /*  73 */
            this.labelProvider = new DefaultCellLabelProvider(this.provider);
            /*     */
        }
        /*  75 */
        return this.labelProvider;
        /*     */
    }

    /*     */
    /*     */
    protected FilteredTableViewer buildFilteredViewer(Composite parent, String[] columnNames) {
        /*  79 */
        return buildFilteredViewer(parent, columnNames, true);
        /*     */
    }

    /*     */
    /*     */
    protected FilteredTableViewer buildFilteredViewer(Composite parent, String[] columnNames, boolean defaultMenu) {
        /*  83 */
        IPatternMatcher patternMatcher = new TablePatternMatcher(this.provider, getLabelProvider());
        /*     */
        /*  85 */
        buildSimpleInner(parent, 898, columnNames);
        /*     */
        /*  87 */
        this.filteredViewer.setFilterPatternFactory(new PatternFilter(patternMatcher, "", columnNames));
        /*  88 */
        ColumnViewer viewer = this.filteredViewer.getViewer();
        /*  89 */
        if (defaultMenu) {
            /*  90 */
            this.menu = ContextMenuFilter.addContextMenu(viewer, this.filteredView.getFilterText(), getLabelProvider(), columnNames, null);
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*  95 */
        this.filteredViewer.setInput(this.unit, true);
        /*  96 */
        this.filteredViewer.setDisplayFilteredRowCount(true);
        /*     */
        /*  98 */
        for (TableColumn tc : this.filteredView.getTable().getColumns()) {
            /*  99 */
            tc.pack();
            /*     */
        }
        /*     */
        /* 102 */
        return this.filteredViewer;
        /*     */
    }

    /*     */
    /*     */
    protected FilteredTableViewer buildSimple(Composite parent, int style, String[] columnNames) {
        /* 106 */
        buildSimpleInner(parent, style, columnNames);
        /*     */
        /* 108 */
        this.filteredViewer.setInput(this.unit, true);
        /*     */
        /* 110 */
        return this.filteredViewer;
        /*     */
    }

    /*     */
    /*     */
    private FilteredTableViewer buildSimpleInner(Composite parent, int style, String[] columnNames) {
        /* 114 */
        this.filteredView = new FilteredTableView(parent, style, columnNames);
        /* 115 */
        initFilteredView(this.filteredView);
        /* 116 */
        this.filteredViewer = new FilteredTableViewer(this.filteredView);
        /* 117 */
        this.filteredViewer.setContentProvider(this.provider);
        /* 118 */
        this.filteredViewer.setLabelProvider(getLabelProvider());
        /*     */
        /* 120 */
        return this.filteredViewer;
        /*     */
    }

    /*     */
    /*     */
    protected void addContextMenu(IContextMenu menuItem) {
        /* 124 */
        if (this.menu == null) {
            /* 125 */
            throw new IllegalStateException("No menu exist for this fragment");
            /*     */
        }
        /* 127 */
        this.menu.addContextMenu(menuItem);
        /*     */
    }

    /*     */
    /*     */
    public FilteredTableViewer getViewer() {
        /* 131 */
        return this.filteredViewer;
        /*     */
    }

    /*     */
    /*     */
    public void refresh() {
        /* 135 */
        this.filteredViewer.refresh();
        /*     */
    }

    /*     */
    /*     */
    public IStructuredSelection getSelection()
    /*     */ {
        /* 140 */
        return (IStructuredSelection) this.filteredViewer.getSelection();
        /*     */
    }

    /*     */
    /*     */
    public Object getSelectedRawRow() {
        /* 144 */
        IStructuredSelection sel = getSelection();
        /* 145 */
        if (sel.isEmpty()) {
            /* 146 */
            return null;
            /*     */
        }
        /*     */
        /* 149 */
        return sel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    protected abstract boolean isCorrectRow(Object paramObject);

    /*     */
    /*     */
    public String exportElementToString(Object obj)
    /*     */ {
        /* 157 */
        if (isCorrectRow(obj)) {
            /* 158 */
            return exportRowToStringV(obj);
            /*     */
        }
        /* 160 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    protected String exportRowToStringV(V obj) {
        /* 164 */
        Object[] row = this.provider.getRowElements(obj);
        /* 165 */
        return ExportUtil.buildCsvLine(getLabelProvider(), obj, row.length);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected void initFilteredView(FilteredTableView view) {
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 179 */
        return Strings.encodeUTF8(this.filteredView.exportToString());
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\AbstractFilteredTableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */