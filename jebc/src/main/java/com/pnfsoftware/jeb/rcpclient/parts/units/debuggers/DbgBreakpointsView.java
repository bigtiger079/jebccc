/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.ITableEventListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractFilteredTableView;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.ICheckStateProvider;
/*     */ import org.eclipse.jface.viewers.TableViewer;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;

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
/*     */ public class DbgBreakpointsView
        /*     */ extends AbstractFilteredTableView<IDebuggerUnit, IDebuggerBreakpoint>
        /*     */ implements IContextMenu
        /*     */ {
    /*  47 */   private static final ILogger logger = GlobalLog.getLogger(DbgBreakpointsView.class);

    /*     */
    /*     */
    public DbgBreakpointsView(Composite parent, int style, RcpClientContext context, IDebuggerUnit unit) {
        /*  50 */
        super(parent, style, unit, null, context, new BreakpointProvider());
        /*  51 */
        setLayout(new FillLayout());
        /*     */
        /*  53 */
        if (unit == null) {
            /*  54 */
            throw new RuntimeException();
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*  65 */
        FilteredTableViewer viewer = buildSimple(this, 36, new String[]{"Address"});
        /*     */
        /*  67 */
        viewer.setCheckStateProvider(new ICheckStateProvider()
                /*     */ {
            /*     */
            public boolean isGrayed(Object element) {
                /*  70 */
                return false;
                /*     */
            }

            /*     */
            /*     */
            public boolean isChecked(Object element)
            /*     */ {
                /*  75 */
                return DbgBreakpointsView.this.getProvider().isChecked(element);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    protected void initFilteredView(FilteredTableView view)
    /*     */ {
        /*  82 */
        view.setFilterVisibility(false, false);
        /*     */
        /*  84 */
        view.addTableEventListener(new ITableEventListener()
                /*     */ {
            /*     */
            public void onTableEvent(Object row, boolean isSelected, boolean isChecked) {
                /*  87 */
                DbgBreakpointsView.logger.i("row=%s selected=%b checked=%b", new Object[]{row, Boolean.valueOf(isSelected), Boolean.valueOf(isChecked)});
                /*  88 */
                IDebuggerUnit dbg = (IDebuggerUnit) DbgBreakpointsView.this.getUnit();
                /*  89 */
                IDebuggerBreakpoint bp = (IDebuggerBreakpoint) row;
                /*  90 */
                if (dbg.isAttached()) {
                    /*  91 */
                    bp.setEnabled(isChecked);
                    /*     */
                }
                /*     */
                /*     */
            }
            /*     */
            /*  96 */
        });
        /*  97 */
        new ContextMenu(view.getTable()).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 102 */
        Object elt = getSelectedRow();
        /* 103 */
        if ((elt instanceof IDebuggerBreakpoint)) {
            /* 104 */
            final IDebuggerBreakpoint bp = (IDebuggerBreakpoint) elt;
            /* 105 */
            menuMgr.add(new Action("Remove")
                    /*     */ {
                /*     */
                public void run() {
                    /* 108 */
                    ((IDebuggerUnit) DbgBreakpointsView.this.getUnit()).clearBreakpoint(bp);
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
        /* 113 */
        addOperationsToContextMenu(menuMgr);
        /*     */
    }

    /*     */
    /*     */
    public TableViewer getJfaceViewer() {
        /* 117 */
        return (TableViewer) getViewer().getViewer();
        /*     */
    }

    /*     */
    /*     */
    /*     */   static class BreakpointProvider
            /*     */ implements IFilteredTableContentProvider
            /*     */ {
        /*     */ IEventListener listener;
        /*     */ IDebuggerUnit dbg;

        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public void inputChanged(final Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 131 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 132 */
                ((IDebuggerUnit) oldInput).removeListener(this.listener);
                /* 133 */
                this.listener = null;
                /*     */
            }
            /*     */
            /* 136 */
            this.dbg = ((IDebuggerUnit) newInput);
            /* 137 */
            if (this.dbg == null) {
                /* 138 */
                return;
                /*     */
            }
            /*     */
            /* 141 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e)
                /*     */ {
                    /* 145 */
                    if ((DbgBreakpointsView.BreakpointProvider.this.dbg != null) && (e.getSource() == DbgBreakpointsView.BreakpointProvider.this.dbg)) {
                        /* 146 */
                        UIExecutor.async(viewer.getControl(), new UIRunnable()
                                /*     */ {
                            /*     */
                            public void runi() {
                                /* 149 */
                                if (DbgBreakpointsView.BreakpointProvider.this.dbg != null)
                                    /*     */ {
                                    /*     */
                                    /*     */
                                    /*     */
                                    /* 154 */
                                    if (!DbgBreakpointsView.BreakpointProvider .1.
                                    this.val$viewer.getControl().isDisposed()){
                                        /* 155 */
                                        DbgBreakpointsView.BreakpointProvider .1. this.val$viewer.refresh();
                                        /*     */
                                    }
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                        });
                        /*     */
                    }
                    /*     */
                }
                /* 162 */
            };
            /* 163 */
            this.dbg.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /* 168 */
            IDebuggerUnit unit = (IDebuggerUnit) inputElement;
            /* 169 */
            if (unit.isAttached()) {
                /* 170 */
                List<? extends IDebuggerBreakpoint> breakpoints = null;
                /* 171 */
                if (unit.isAttached()) {
                    /* 172 */
                    breakpoints = unit.getBreakpoints();
                    /* 173 */
                    if (breakpoints != null) {
                        /* 174 */
                        return breakpoints.toArray();
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /* 178 */
            return ArrayUtil.NO_OBJECT;
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 183 */
            IDebuggerBreakpoint bp = (IDebuggerBreakpoint) row;
            /* 184 */
            return new Object[]{bp.getAddress()};
            /*     */
        }

        /*     */
        /*     */
        public boolean isChecked(Object row)
        /*     */ {
            /* 189 */
            IDebuggerBreakpoint bp = (IDebuggerBreakpoint) row;
            /* 190 */
            return bp.isEnabled();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 196 */
        Object o = getSelectedRow();
        /* 197 */
        if (!(o instanceof IDebuggerBreakpoint)) {
            /* 198 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        /* 202 */
        String address = ((IDebuggerBreakpoint) o).getAddress();
        /* 203 */
        return address;
        /*     */
    }

    /*     */
    /*     */
    protected boolean isCorrectRow(Object obj)
    /*     */ {
        /* 208 */
        return obj instanceof IDebuggerBreakpoint;
        /*     */
    }

    /*     */
    /*     */
    public IDebuggerBreakpoint getSelectedRow()
    /*     */ {
        /* 213 */
        Object row = getSelectedRawRow();
        /* 214 */
        if (!(row instanceof IDebuggerBreakpoint)) {
            /* 215 */
            return null;
            /*     */
        }
        /*     */
        /* 218 */
        return (IDebuggerBreakpoint) row;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgBreakpointsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */