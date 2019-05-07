/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IUnitFragment;
/*     */ import com.pnfsoftware.jeb.core.actions.ActionContext;
/*     */ import com.pnfsoftware.jeb.core.actions.ActionXrefsData;
/*     */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
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
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
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
/*     */
/*     */ public class ReferencedMethodsView
        /*     */ extends AbstractFilteredTableView<INativeCodeUnit<?>, INativeMethodItem>
        /*     */ implements ILazyView
        /*     */ {
    /*  54 */   private static final ILogger logger = GlobalLog.getLogger(ReferencedMethodsView.class);

    /*     */
    /*     */
    public ReferencedMethodsView(Composite parent, int flags, RcpClientContext context, INativeCodeUnit<?> unit, IRcpUnitView unitView) {
        /*  57 */
        super(parent, flags, unit, unitView, context, new ContentProvider());
        /*  58 */
        setLayout(new FillLayout());
        /*     */
    }

    /*     */
    /*     */
    public void lazyInitialization()
    /*     */ {
        /*  63 */
        Composite container = new Composite(this, 0);
        /*  64 */
        container.setLayout(new FillLayout());
        /*     */
        /*  66 */
        String[] columnNames = {"Name", "Signature"};
        /*     */
        /*  68 */
        FilteredTableViewer viewer = buildFilteredViewer(container, columnNames);
        /*     */
        /*     */
        /*  71 */
        addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  74 */
                menuMgr.add(new Separator());
                /*  75 */
                menuMgr.add(new OperationCopy(ReferencedMethodsView.this));
                /*  76 */
                menuMgr.add(new Action("Jump to first reference")
                        /*     */ {
                    /*     */
                    public void run() {
                        /*  79 */
                        ReferencedMethodsView.this.jumpToFirstMethodReference();
                        /*     */
                    }
                    /*  81 */
                });
                /*  82 */
                menuMgr.add(new Action("Cross References")
                        /*     */ {
                    /*     */
                    public void run() {
                        /*  85 */
                        ReferencedMethodsView.this.openXref();
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*     */
                });
                /*     */
            }
            /*     */
            /*  92 */
        });
        /*  93 */
        viewer.addDoubleClickListener(new IDoubleClickListener()
                /*     */ {
            /*     */
            public void doubleClick(DoubleClickEvent event) {
                /*  96 */
                ReferencedMethodsView.this.jumpToFirstMethodReference();
                /*     */
            }
            /*     */
            /*     */
            /* 100 */
        });
        /* 101 */
        layout();
        /*     */
    }

    /*     */
    /*     */
    protected boolean isCorrectRow(Object obj)
    /*     */ {
        /* 106 */
        return obj instanceof INativeMethodItem;
        /*     */
    }

    /*     */
    /*     */
    public INativeMethodItem getSelectedRow()
    /*     */ {
        /* 111 */
        Object row = getSelectedRawRow();
        /* 112 */
        if (!(row instanceof INativeMethodItem)) {
            /* 113 */
            return null;
            /*     */
        }
        /*     */
        /* 116 */
        return (INativeMethodItem) row;
        /*     */
    }

    /*     */
    /*     */
    private void jumpToFirstMethodReference() {
        /* 120 */
        INativeMethodItem m = getSelectedRow();
        /* 121 */
        if (m != null) {
            /* 122 */
            String address = m.getAddress();
            /* 123 */
            jumpToFirstMethodReference(m, address);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private boolean jumpToFirstMethodReference(INativeMethodItem m, String originAddress) {
        /* 128 */
        if (this.unitView == null) {
            /* 129 */
            return false;
            /*     */
        }
        /* 131 */
        String address = originAddress;
        /* 132 */
        long id;
        if (address == null) {
            /* 133 */
            id = m.getItemId();
            /* 134 */
            if (id != 0L) {
                /* 135 */
                ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
                /* 136 */
                ActionXrefsData data = new ActionXrefsData();
                /* 137 */
                if (((INativeCodeUnit) this.unit).prepareExecution(actionContext, data)) {
                    /* 138 */
                    List<String> addresses = data.getAddresses();
                    /* 139 */
                    if ((addresses != null) && (!addresses.isEmpty()))
                        /*     */ {
                        /* 141 */
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
        /* 147 */
        if (address != null)
            /*     */ {
            /*     */
            /* 150 */
            for (IUnitFragment fragment : this.unitView.getFragments()) {
                /* 151 */
                if ((fragment instanceof InteractiveTextView)) {
                    /* 152 */
                    logger.i("Jumping to address: %s", new Object[]{address});
                    /* 153 */
                    if (((InteractiveTextView) fragment).isValidActiveAddress(address, null)) {
                        /* 154 */
                        this.unitView.setActiveFragment(fragment);
                        /* 155 */
                        boolean found = this.unitView.setActiveAddress(address, null, false);
                        /* 156 */
                        if (found) {
                            /* 157 */
                            return true;
                            /*     */
                        }
                        /*     */
                        /*     */
                        /*     */
                        /*     */
                        /* 163 */
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
        /*     */
        /*     */
        /* 171 */
        if (originAddress != null)
            /*     */ {
            /* 173 */
            jumpToFirstMethodReference(m, null);
            /*     */
        }
        /* 175 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private void openXref() {
        /* 179 */
        INativeMethodItem m = getSelectedRow();
        /* 180 */
        if (m == null) {
            /* 181 */
            return;
            /*     */
        }
        /* 183 */
        if (this.unitView == null) {
            /* 184 */
            return;
            /*     */
        }
        /*     */
        /* 187 */
        long id = m.getItemId();
        /* 188 */
        if (id != 0L) {
            /* 189 */
            IUnitFragment textFragment = null;
            /* 190 */
            for (IUnitFragment fragment : this.unitView.getFragments()) {
                /* 191 */
                if ((fragment instanceof InteractiveTextView)) {
                    /* 192 */
                    textFragment = fragment;
                    /*     */
                }
                /*     */
            }
            /* 195 */
            if (textFragment == null) {
                /* 196 */
                return;
                /*     */
            }
            /* 198 */
            ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
            /* 199 */
            ActionUIContext uictx = new ActionUIContext(actionContext, textFragment);
            /* 200 */
            new GraphicalActionExecutor(getShell(), getContext()).execute(uictx);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   static class ContentProvider implements IFilteredTableContentProvider
            /*     */ {
        /*     */ INativeCodeUnit<?> codeunit;
        /*     */ IEventListener listener;
        /* 208 */     private ViewerRefresher refresher = null;

        /*     */
        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        /*     */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 216 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 217 */
                ((INativeCodeUnit) oldInput).removeListener(this.listener);
                /* 218 */
                this.listener = null;
                /*     */
            }
            /* 220 */
            this.codeunit = ((INativeCodeUnit) newInput);
            /*     */
            /* 222 */
            this.codeunit = ((INativeCodeUnit) newInput);
            /* 223 */
            if (this.codeunit == null) {
                /* 224 */
                return;
                /*     */
            }
            /*     */
            /* 227 */
            if (this.refresher == null) {
                /* 228 */
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer)
                        /*     */ {
                    /*     */
                    protected void performRefresh() {
                        /* 231 */
                        if (ReferencedMethodsView.ContentProvider.this.codeunit != null) {
                            /* 232 */
                            super.performRefresh();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                };
                /*     */
            }
            /* 237 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e) {
                    /* 240 */
                    ReferencedMethodsView.logger.i("Event: %s", new Object[]{e});
                    /* 241 */
                    if ((ReferencedMethodsView.ContentProvider.this.codeunit != null) && (e.getSource() == ReferencedMethodsView.ContentProvider.this.codeunit)) {
                        /* 242 */
                        ReferencedMethodsView.ContentProvider.this.refresher.request();
                        /*     */
                    }
                    /*     */
                }
                /* 245 */
            };
            /* 246 */
            this.codeunit.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /* 251 */
            List<INativeMethodItem> list = new ArrayList();
            /* 252 */
            for (INativeMethodItem m : this.codeunit.getMethods()) {
                /* 253 */
                if (m.getData() == null) {
                    /* 254 */
                    list.add(m);
                    /*     */
                }
                /*     */
            }
            /* 257 */
            return list.toArray();
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 262 */
            INativeMethodItem m = (INativeMethodItem) row;
            /* 263 */
            String name = m.getName(true);
            /* 264 */
            String signature = m.getSignature(true);
            /* 265 */
            return new Object[]{name, signature};
            /*     */
        }

        /*     */
        /*     */
        public boolean isChecked(Object row)
        /*     */ {
            /* 270 */
            return false;
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\ReferencedMethodsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */