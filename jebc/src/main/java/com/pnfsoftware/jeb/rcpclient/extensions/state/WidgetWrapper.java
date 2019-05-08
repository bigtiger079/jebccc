
package com.pnfsoftware.jeb.rcpclient.extensions.state;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.util.IPersistenceProvider;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class WidgetWrapper {
    private static final ILogger logger = GlobalLog.getLogger(WidgetWrapper.class);
    private IPersistenceProvider pp;

    public WidgetWrapper(IPersistenceProvider pp) {
        if (pp == null) {
            throw new IllegalArgumentException("The persistence provider is null");
        }
        this.pp = pp;
    }

    public boolean wrap(Control ctl) {
        int id = UIUtil.getWidgetId(ctl);
        if (id == 0) {
            logger.debug("The widget '%s' has a null id and cannot be wrapped", new Object[]{ctl});
            return false;
        }
        wrapInternal("" + id, ctl);
        return true;
    }

    private void wrapInternal(String fqn, Control ctl) {
        if ((ctl instanceof Table)) {
            monitorTable(fqn, (Table) ctl);
        }
        if ((ctl instanceof Tree)) {
            monitorTree(fqn, (Tree) ctl);
        }
        if ((ctl instanceof Composite)) {
            int i = 0;
            for (Control ctl1 : ((Composite) ctl).getChildren()) {
                wrapInternal(fqn + "/@" + i, ctl1);
                i++;
            }
        }
    }

    void monitorTable(final String fqn, Table table) {
        final IColumnWidgetManager t = new TableColumnManager(table);
        restore(fqn, t);
        table.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                WidgetWrapper.this.record(fqn, t);
            }
        });
    }

    void monitorTree(final String fqn, Tree tree) {
        final IColumnWidgetManager t = new TreeColumnManager(tree);
        restore(fqn, t);
        tree.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                WidgetWrapper.this.record(fqn, t);
            }
        });
    }

    void restore(String fqn, IColumnWidgetManager t) {
        logger.i("Restoring state for widget %s", new Object[]{fqn});
        String encodedData = this.pp.load(fqn);
        if (encodedData != null) {
            try {
                String[] elts = encodedData.split(",");
                if (elts.length == t.getCount()) {
                    int[] order = new int[elts.length];
                    int[] widths = new int[elts.length];
                    for (int i = 0; i < elts.length; i++) {
                        String[] v = elts[i].split(":");
                        if (v.length != 2) {
                            break;
                        }
                        order[i] = Integer.parseInt(v[0]);
                        widths[i] = Integer.parseInt(v[1]);
                    }
                    t.setOrder(order);
                    for (int i = 0; i < widths.length; i++) {
                        t.setWidth(i, widths[i]);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.trace("Invalid widget persistence data: %s", new Object[]{e});
            }
        }
    }

    void record(String fqn, IColumnWidgetManager t) {
        logger.i("Recording state for widget %s", new Object[]{fqn});
        int count = t.getCount();
        if (count >= 1) {
            StringBuilder sb = new StringBuilder();
            int[] order = t.getOrder();
            for (int i = 0; i < count; i++) {
                if (i >= 1) {
                    sb.append(',');
                }
                int index = order[i];
                int w = t.getWidth(index);
                sb.append(String.format("%d:%d", new Object[]{Integer.valueOf(index), Integer.valueOf(w)}));
            }
            String encodedState = sb.toString();
            this.pp.save(fqn, encodedState);
        }
    }

    static abstract interface IColumnWidgetManager {
        public abstract int getCount();

        public abstract int[] getOrder();

        public abstract void setOrder(int[] paramArrayOfInt);

        public abstract int getWidth(int paramInt);

        public abstract void setWidth(int paramInt1, int paramInt2);
    }

    static class TableColumnManager implements WidgetWrapper.IColumnWidgetManager {
        private Table t;

        public TableColumnManager(Table t) {
            this.t = t;
        }

        public int getCount() {
            return this.t.getColumnCount();
        }

        public int[] getOrder() {
            return this.t.getColumnOrder();
        }

        public void setOrder(int[] order) {
            this.t.setColumnOrder(order);
        }

        public int getWidth(int index) {
            return this.t.getColumn(index).getWidth();
        }

        public void setWidth(int index, int width) {
            this.t.getColumn(index).setWidth(width);
        }
    }

    static class TreeColumnManager implements WidgetWrapper.IColumnWidgetManager {
        private Tree t;

        public TreeColumnManager(Tree t) {
            this.t = t;
        }

        public int getCount() {
            return this.t.getColumnCount();
        }

        public int[] getOrder() {
            return this.t.getColumnOrder();
        }

        public void setOrder(int[] order) {
            this.t.setColumnOrder(order);
        }

        public int getWidth(int index) {
            return this.t.getColumn(index).getWidth();
        }

        public void setWidth(int index, int width) {
            this.t.getColumn(index).setWidth(width);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\state\WidgetWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */