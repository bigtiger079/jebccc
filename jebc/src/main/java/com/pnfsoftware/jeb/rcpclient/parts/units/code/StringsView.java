/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IUnitFragment;
/*     */ import com.pnfsoftware.jeb.core.actions.ActionContext;
/*     */ import com.pnfsoftware.jeb.core.actions.ActionXrefsData;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.table.impl.Cell;
/*     */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeString;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.actions.ActionUIContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.ILazyView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractFilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.action.Separator;
/*     */ import org.eclipse.jface.viewers.DoubleClickEvent;
/*     */ import org.eclipse.jface.viewers.IDoubleClickListener;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;

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
/*     */ public class StringsView
        /*     */ extends AbstractFilteredTableView<ICodeUnit, ICodeString>
        /*     */ implements ILazyView
        /*     */ {
    /*  56 */   private static final ILogger logger = GlobalLog.getLogger(StringsView.class);

    /*     */
    /*     */
    public StringsView(Composite parent, int flags, RcpClientContext context, ICodeUnit unit, IRcpUnitView unitView) {
        /*  59 */
        super(parent, flags, unit, unitView, context, new ContentProvider());
        /*  60 */
        setLayout(new FillLayout());
        /*     */
    }

    /*     */
    /*     */
    public void lazyInitialization()
    /*     */ {
        /*  65 */
        Composite container = new Composite(this, 0);
        /*  66 */
        container.setLayout(new FillLayout());
        /*     */
        /*  68 */
        String[] columnNames = {"Address", "Name", "Value", "Comment"};
        /*     */
        /*  70 */
        FilteredTableViewer viewer = buildFilteredViewer(container, columnNames);
        /*     */
        /*     */
        /*  73 */
        addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  76 */
                menuMgr.add(new Separator());
                /*  77 */
                menuMgr.add(new OperationCopy(StringsView.this));
                /*  78 */
                menuMgr.add(new Action("Jump to first reference")
                        /*     */ {
                    /*     */
                    public void run() {
                        /*  81 */
                        StringsView.this.jumpToFirstStringReference();
                        /*     */
                    }
                    /*  83 */
                });
                /*  84 */
                menuMgr.add(new Action("Cross References")
                        /*     */ {
                    /*     */
                    public void run() {
                        /*  87 */
                        StringsView.this.openXref();
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*     */
                });
                /*     */
            }
            /*  93 */
        });
        /*  94 */
        viewer.addDoubleClickListener(new IDoubleClickListener()
                /*     */ {
            /*     */
            public void doubleClick(DoubleClickEvent event) {
                /*  97 */
                StringsView.this.jumpToFirstStringReference();
                /*     */
            }
            /*     */
            /*     */
            /* 101 */
        });
        /* 102 */
        layout();
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 107 */
        ICodeString str = getSelectedRow();
        /* 108 */
        if (str == null) {
            /* 109 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        /* 113 */
        Cell pseudoItem = new Cell(null);
        /* 114 */
        pseudoItem.setItemId(str.getIdentifier());
        /* 115 */
        return pseudoItem;
        /*     */
    }

    /*     */
    /*     */
    protected boolean isCorrectRow(Object obj)
    /*     */ {
        /* 120 */
        return obj instanceof ICodeString;
        /*     */
    }

    /*     */
    /*     */
    public ICodeString getSelectedRow()
    /*     */ {
        /* 125 */
        Object row = getSelectedRawRow();
        /* 126 */
        if (!(row instanceof ICodeString)) {
            /* 127 */
            return null;
            /*     */
        }
        /*     */
        /* 130 */
        return (ICodeString) row;
        /*     */
    }

    /*     */
    /*     */
    private void jumpToFirstStringReference() {
        /* 134 */
        ICodeString str = getSelectedRow();
        /* 135 */
        if (str != null) {
            /* 136 */
            jumpToFirstStringReference(str);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private boolean jumpToFirstStringReference(ICodeString str) {
        /* 141 */
        if (this.unitView == null) {
            /* 142 */
            return false;
            /*     */
        }
        /* 144 */
        String address = str.getAddress();
        /* 145 */
        long id;
        if (address == null) {
            /* 146 */
            id = str.getIdentifier();
            /* 147 */
            if (id != 0L) {
                /* 148 */
                ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
                /* 149 */
                ActionXrefsData data = new ActionXrefsData();
                /* 150 */
                if (((ICodeUnit) this.unit).prepareExecution(actionContext, data)) {
                    /* 151 */
                    List<String> addresses = data.getAddresses();
                    /* 152 */
                    if ((addresses != null) && (!addresses.isEmpty()))
                        /*     */ {
                        /* 154 */
                        address = (String) addresses.get(0);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 160 */
        if (address != null)
            /*     */ {
            /*     */
            /* 163 */
            for (IUnitFragment fragment : this.unitView.getFragments()) {
                /* 164 */
                if ((fragment instanceof InteractiveTextView)) {
                    /* 165 */
                    logger.i("Jumping to address: %s", new Object[]{address});
                    /* 166 */
                    if (((InteractiveTextView) fragment).isValidActiveAddress(address, null)) {
                        /* 167 */
                        this.unitView.setActiveFragment(fragment);
                        /* 168 */
                        boolean found = this.unitView.setActiveAddress(address, null, false);
                        /* 169 */
                        if (found) {
                            /* 170 */
                            return true;
                            /*     */
                        }
                        /*     */
                        /*     */
                        /*     */
                        /*     */
                        /* 176 */
                        this.unitView.setActiveFragment(this);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 182 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private void openXref() {
        /* 186 */
        ICodeString str = getSelectedRow();
        /* 187 */
        if (str == null) {
            /* 188 */
            return;
            /*     */
        }
        /* 190 */
        if (this.unitView == null) {
            /* 191 */
            return;
            /*     */
        }
        /*     */
        /* 194 */
        long id = str.getIdentifier();
        /* 195 */
        if (id != 0L) {
            /* 196 */
            IUnitFragment textFragment = null;
            /* 197 */
            for (IUnitFragment fragment : this.unitView.getFragments()) {
                /* 198 */
                if ((fragment instanceof InteractiveTextView)) {
                    /* 199 */
                    textFragment = fragment;
                    /*     */
                }
                /*     */
            }
            /* 202 */
            if (textFragment == null) {
                /* 203 */
                return;
                /*     */
            }
            /* 205 */
            ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
            /* 206 */
            ActionUIContext uictx = new ActionUIContext(actionContext, textFragment);
            /* 207 */
            new GraphicalActionExecutor(getShell(), getContext()).execute(uictx);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   static class ContentProvider
            /*     */ implements IFilteredTableContentProvider
            /*     */ {
        /*     */ ICodeUnit codeunit;
        /*     */ IEventListener listener;
        /*     */ ViewerRefresher refresher;

        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 222 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 223 */
                ((ICodeUnit) oldInput).removeListener(this.listener);
                /* 224 */
                this.listener = null;
                /*     */
            }
            /* 226 */
            this.codeunit = ((ICodeUnit) newInput);
            /*     */
            /* 228 */
            this.codeunit = ((ICodeUnit) newInput);
            /* 229 */
            if (this.codeunit == null) {
                /* 230 */
                return;
                /*     */
            }
            /*     */
            /* 233 */
            if (this.refresher == null) {
                /* 234 */
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer)
                        /*     */ {
                    /*     */
                    protected void performRefresh()
                    /*     */ {
                        /* 238 */
                        if ((StringsView.ContentProvider.this.codeunit instanceof INativeCodeUnit)) {
                            /* 239 */
                            StringsView.logger.i("Refreshing strings...", new Object[0]);
                            /* 240 */
                            super.performRefresh();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                };
                /*     */
            }
            /*     */
            /* 246 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e) {
                    /* 249 */
                    StringsView.logger.i("Event: %s", new Object[]{e});
                    /* 250 */
                    if ((StringsView.ContentProvider.this.codeunit != null) && (e.getSource() == StringsView.ContentProvider.this.codeunit)) {
                        /* 251 */
                        StringsView.ContentProvider.this.refresher.request();
                        /*     */
                    }
                    /*     */
                }
                /* 254 */
            };
            /* 255 */
            this.codeunit.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /* 260 */
            List<? extends ICodeString> strings = this.codeunit.getStrings();
            /* 261 */
            return strings == null ? ArrayUtil.NO_OBJECT : strings.toArray();
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 266 */
            ICodeString e = (ICodeString) row;
            /* 267 */
            String address = e.getAddress();
            /* 268 */
            String name = e.getName(true);
            /* 269 */
            String value = e.getValue();
            /* 270 */
            String comment = address == null ? null : this.codeunit.getComment(address);
            /* 271 */
            return new Object[]{address, name, value, comment};
            /*     */
        }

        /*     */
        /*     */
        public boolean isChecked(Object row)
        /*     */ {
            /* 276 */
            return false;
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StringsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */