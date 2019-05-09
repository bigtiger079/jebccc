package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.iviewers.table.TableUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class FilteredTableView extends AbstractFilteredView<Table> {
    private List<ITableEventListener> listeners = new ArrayList<>();

    public FilteredTableView(Composite parent, int style, String[] columnNames) {
        this(parent, style, columnNames, null, false);
    }

    public FilteredTableView(Composite parent, int style, String[] columnNames, int[] columnWidths, boolean displayIndex) {
        super(parent, style, columnNames, columnWidths, displayIndex);
        getTable().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (!(e.item instanceof TableItem)) {
                    return;
                }
                Object object = e.item.getData();
                boolean selected = FilteredTableView.this.getTable().getSelectionCount() > 0;
                boolean checked = ((TableItem) e.item).getChecked();
                for (ITableEventListener listener : FilteredTableView.this.listeners) {
                    listener.onTableEvent(object, selected, checked);
                }
            }
        });
    }

    protected Table buildElement(Composite parent, int style) {
        Table table = new Table(parent, 0x10000 | style | 0x2);
        table.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        return table;
    }

    protected void buildColumn(Table parent, String name, int initialWidth) {
        TableColumn tc = new TableColumn(parent, 16384);
        tc.setText(name);
        tc.setResizable(true);
        tc.setMoveable(true);
        if (initialWidth > 0) {
            tc.setWidth(initialWidth);
        }
    }

    public Table getTable() {
        return (Table) getElement();
    }

    public int getSelectionIndex() {
        return ((Table) getElement()).getSelectionIndex();
    }

    public void setSelection(int index) {
        ((Table) getElement()).setSelection(index);
        ((Table) getElement()).showSelection();
    }

    public int getItemCount() {
        return ((Table) getElement()).getItemCount();
    }

    public void addTableEventListener(ITableEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeTableEventListener(ITableEventListener listener) {
        this.listeners.remove(listener);
    }

    public void addSelectionListener(SelectionListener listener) {
        ((Table) getElement()).addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        ((Table) getElement()).removeSelectionListener(listener);
    }

    public void pack(boolean changed) {
        super.pack(changed);
        int cnt = ((Table) getElement()).getColumns().length;
        int cx = ((Table) getElement()).getSize().x;
        if (cnt <= 1) {
            if (cnt == 1) {
                ((Table) getElement()).getColumns()[0].setWidth(cx);
            }
            return;
        }
        int maxWidthColumn = cx / cnt;
        for (TableColumn tc : ((Table) getElement()).getColumns()) {
            tc.pack();
            if (tc.getWidth() > maxWidthColumn) {
                tc.setWidth(maxWidthColumn);
            }
        }
    }

    public String exportToString() {
        return TableUtil.buildCsv(getTable());
    }
}


