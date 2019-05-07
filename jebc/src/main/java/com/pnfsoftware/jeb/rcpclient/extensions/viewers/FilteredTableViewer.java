
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;


import com.pnfsoftware.jeb.rcpclient.extensions.controls.AbstractFilteredView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.filter.AbstractFilteredFilter;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class FilteredTableViewer
        extends AbstractFilteredViewer<Table, TableViewer> {

    public FilteredTableViewer(FilteredTableView widget) {

        super(widget);


        this.comparator = new FilteredViewerComparator(Strings.getDefaultComparator(), this);

        ((TableViewer) getViewer()).setComparator(this.comparator);


        int colIndex = 0;

        for (TableColumn col : widget.getTable().getColumns()) {

            col.addSelectionListener(new ColumnSelectionListener(colIndex, col));

            colIndex++;

        }

    }


    protected TableViewer buildViewer(AbstractFilteredView<Table> widget) {

        if ((((Table) widget.getElement()).getStyle() & 0x20) == 0) {

            return new TableViewer((Table) widget.getElement());

        }


        return new CheckboxTableViewer((Table) widget.getElement());

    }


    protected AbstractFilteredFilter buildFilter(TableViewer viewer) {

        return new Filter(viewer);

    }


    class ColumnSelectionListener extends SelectionAdapter {
        int columnIndex;
        TableColumn column;


        public ColumnSelectionListener(int columnIndex, TableColumn column) {

            this.columnIndex = columnIndex;

            this.column = column;

        }


        public void widgetSelected(SelectionEvent e) {

            if (FilteredTableViewer.this.comparator != null) {

                FilteredTableViewer.this.comparator.setColumn(this.columnIndex);

                int dir = FilteredTableViewer.this.comparator.getDirection();

                ((Table) FilteredTableViewer.this.getWidget().getElement()).setSortDirection(dir);

                ((Table) FilteredTableViewer.this.getWidget().getElement()).setSortColumn(this.column);

                FilteredTableViewer.this.refresh();

            }

        }

    }


    public void setInput(Object input, boolean pack) {

        super.setInput(input);

        if (pack) {

            getWidget().pack(false);

        }

    }


    public void setContentProvider(IFilteredTableContentProvider provider) {

        super.setContentProvider(provider);

    }


    public IFilteredTableContentProvider getProvider() {

        return (IFilteredTableContentProvider) this.provider;

    }


    public void setCheckStateProvider(ICheckStateProvider provider) {

        if (!(getViewer() instanceof CheckboxTableViewer)) {

            throw new RuntimeException();

        }

        ((CheckboxTableViewer) getViewer()).setCheckStateProvider(provider);

    }


    class Filter
            extends AbstractFilteredFilter {

        public Filter(StructuredViewer viewer) {

            super(viewer);

        }


        public boolean select(Viewer viewer, Object parent, Object element) {

            return isElementMatch(element);

        }


        public IFilteredContentProvider getProvider() {

            return FilteredTableViewer.this.getProvider();

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\FilteredTableViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */