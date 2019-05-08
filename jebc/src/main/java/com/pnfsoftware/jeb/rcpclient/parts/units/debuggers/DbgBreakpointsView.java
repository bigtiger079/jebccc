
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ITableEventListener;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractFilteredTableView;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DbgBreakpointsView
        extends AbstractFilteredTableView<IDebuggerUnit, IDebuggerBreakpoint>
        implements IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(DbgBreakpointsView.class);

    public DbgBreakpointsView(Composite parent, int style, RcpClientContext context, IDebuggerUnit unit) {
        super(parent, style, unit, null, context, new BreakpointProvider());
        setLayout(new FillLayout());
        if (unit == null) {
            throw new RuntimeException();
        }
        FilteredTableViewer viewer = buildSimple(this, 36, new String[]{"Address"});
        viewer.setCheckStateProvider(new ICheckStateProvider() {
            public boolean isGrayed(Object element) {
                return false;
            }

            public boolean isChecked(Object element) {
                return DbgBreakpointsView.this.getProvider().isChecked(element);
            }
        });
    }

    protected void initFilteredView(FilteredTableView view) {
        view.setFilterVisibility(false, false);
        view.addTableEventListener(new ITableEventListener() {
            public void onTableEvent(Object row, boolean isSelected, boolean isChecked) {
                DbgBreakpointsView.logger.i("row=%s selected=%b checked=%b", new Object[]{row, Boolean.valueOf(isSelected), Boolean.valueOf(isChecked)});
                IDebuggerUnit dbg = (IDebuggerUnit) DbgBreakpointsView.this.getUnit();
                IDebuggerBreakpoint bp = (IDebuggerBreakpoint) row;
                if (dbg.isAttached()) {
                    bp.setEnabled(isChecked);
                }
            }
        });
        new ContextMenu(view.getTable()).addContextMenu(this);
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        Object elt = getSelectedRow();
        if ((elt instanceof IDebuggerBreakpoint)) {
            final IDebuggerBreakpoint bp = (IDebuggerBreakpoint) elt;
            menuMgr.add(new Action("Remove") {
                public void run() {
                    ((IDebuggerUnit) DbgBreakpointsView.this.getUnit()).clearBreakpoint(bp);
                }
            });
        }
        addOperationsToContextMenu(menuMgr);
    }

    public TableViewer getJfaceViewer() {
        return (TableViewer) getViewer().getViewer();
    }

    static class BreakpointProvider
            implements IFilteredTableContentProvider {
        IEventListener listener;
        IDebuggerUnit dbg;

        public void dispose() {
        }

        public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
            if ((oldInput != null) && (this.listener != null)) {
                ((IDebuggerUnit) oldInput).removeListener(this.listener);
                this.listener = null;
            }
            this.dbg = ((IDebuggerUnit) newInput);
            if (this.dbg == null) {
                return;
            }
            this.listener = new IEventListener() {
                public void onEvent(IEvent e) {
                    if ((DbgBreakpointsView.BreakpointProvider.this.dbg != null) && (e.getSource() == DbgBreakpointsView.BreakpointProvider.this.dbg)) {
                        UIExecutor.async(viewer.getControl(), new UIRunnable() {
                            public void runi() {
                                if (DbgBreakpointsView.BreakpointProvider.this.dbg != null) {
                                    if (!viewer.getControl().isDisposed()) {
                                        viewer.refresh();
                                    }
                                }
                            }
                        });
                    }
                }
            };
            this.dbg.addListener(this.listener);
        }

        public Object[] getElements(Object inputElement) {
            IDebuggerUnit unit = (IDebuggerUnit) inputElement;
            if (unit.isAttached()) {
                List<? extends IDebuggerBreakpoint> breakpoints = null;
                if (unit.isAttached()) {
                    breakpoints = unit.getBreakpoints();
                    if (breakpoints != null) {
                        return breakpoints.toArray();
                    }
                }
            }
            return ArrayUtil.NO_OBJECT;
        }

        public Object[] getRowElements(Object row) {
            IDebuggerBreakpoint bp = (IDebuggerBreakpoint) row;
            return new Object[]{bp.getAddress()};
        }

        public boolean isChecked(Object row) {
            IDebuggerBreakpoint bp = (IDebuggerBreakpoint) row;
            return bp.isEnabled();
        }
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        Object o = getSelectedRow();
        if (!(o instanceof IDebuggerBreakpoint)) {
            return null;
        }
        String address = ((IDebuggerBreakpoint) o).getAddress();
        return address;
    }

    protected boolean isCorrectRow(Object obj) {
        return obj instanceof IDebuggerBreakpoint;
    }

    public IDebuggerBreakpoint getSelectedRow() {
        Object row = getSelectedRawRow();
        if (!(row instanceof IDebuggerBreakpoint)) {
            return null;
        }
        return (IDebuggerBreakpoint) row;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgBreakpointsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */