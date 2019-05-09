package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.actions.ActionXrefsData;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.actions.ActionUIContext;
import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
import com.pnfsoftware.jeb.rcpclient.parts.ILazyView;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractFilteredTableView;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class ReferencedMethodsView extends AbstractFilteredTableView<INativeCodeUnit<?>, INativeMethodItem> implements ILazyView {
    private static final ILogger logger = GlobalLog.getLogger(ReferencedMethodsView.class);

    public ReferencedMethodsView(Composite parent, int flags, RcpClientContext context, INativeCodeUnit<?> unit, IRcpUnitView unitView) {
        super(parent, flags, unit, unitView, context, new ContentProvider());
        setLayout(new FillLayout());
    }

    public void lazyInitialization() {
        Composite container = new Composite(this, 0);
        container.setLayout(new FillLayout());
        String[] columnNames = {"Name", "Signature"};
        FilteredTableViewer viewer = buildFilteredViewer(container, columnNames);
        addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                menuMgr.add(new Separator());
                menuMgr.add(new OperationCopy(ReferencedMethodsView.this));
                menuMgr.add(new Action("Jump to first reference") {
                    public void run() {
                        ReferencedMethodsView.this.jumpToFirstMethodReference();
                    }
                });
                menuMgr.add(new Action("Cross References") {
                    public void run() {
                        ReferencedMethodsView.this.openXref();
                    }
                });
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                ReferencedMethodsView.this.jumpToFirstMethodReference();
            }
        });
        layout();
    }

    protected boolean isCorrectRow(Object obj) {
        return obj instanceof INativeMethodItem;
    }

    public INativeMethodItem getSelectedRow() {
        Object row = getSelectedRawRow();
        if (!(row instanceof INativeMethodItem)) {
            return null;
        }
        return (INativeMethodItem) row;
    }

    private void jumpToFirstMethodReference() {
        INativeMethodItem m = getSelectedRow();
        if (m != null) {
            String address = m.getAddress();
            jumpToFirstMethodReference(m, address);
        }
    }

    private boolean jumpToFirstMethodReference(INativeMethodItem m, String originAddress) {
        if (this.unitView == null) {
            return false;
        }
        String address = originAddress;
        long id;
        if (address == null) {
            id = m.getItemId();
            if (id != 0L) {
                ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
                ActionXrefsData data = new ActionXrefsData();
                if (((INativeCodeUnit) this.unit).prepareExecution(actionContext, data)) {
                    List<String> addresses = data.getAddresses();
                    if ((addresses != null) && (!addresses.isEmpty())) {
                        address = (String) addresses.get(0);
                    }
                }
            }
        }
        if (address != null) {
            for (IUnitFragment fragment : this.unitView.getFragments()) {
                if ((fragment instanceof InteractiveTextView)) {
                    logger.i("Jumping to address: %s", new Object[]{address});
                    if (((InteractiveTextView) fragment).isValidActiveAddress(address, null)) {
                        this.unitView.setActiveFragment(fragment);
                        boolean found = this.unitView.setActiveAddress(address, null, false);
                        if (found) {
                            return true;
                        }
                        this.unitView.setActiveFragment(this);
                    }
                }
            }
        }
        if (originAddress != null) {
            jumpToFirstMethodReference(m, null);
        }
        return false;
    }

    private void openXref() {
        INativeMethodItem m = getSelectedRow();
        if (m == null) {
            return;
        }
        if (this.unitView == null) {
            return;
        }
        long id = m.getItemId();
        if (id != 0L) {
            IUnitFragment textFragment = null;
            for (IUnitFragment fragment : this.unitView.getFragments()) {
                if ((fragment instanceof InteractiveTextView)) {
                    textFragment = fragment;
                }
            }
            if (textFragment == null) {
                return;
            }
            ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
            ActionUIContext uictx = new ActionUIContext(actionContext, textFragment);
            new GraphicalActionExecutor(getShell(), getContext()).execute(uictx);
        }
    }

    static class ContentProvider implements IFilteredTableContentProvider {
        INativeCodeUnit<?> codeunit;
        IEventListener listener;
        private ViewerRefresher refresher = null;

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if ((oldInput != null) && (this.listener != null)) {
                ((INativeCodeUnit) oldInput).removeListener(this.listener);
                this.listener = null;
            }
            this.codeunit = ((INativeCodeUnit) newInput);
            this.codeunit = ((INativeCodeUnit) newInput);
            if (this.codeunit == null) {
                return;
            }
            if (this.refresher == null) {
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer) {
                    protected void performRefresh() {
                        if (ReferencedMethodsView.ContentProvider.this.codeunit != null) {
                            super.performRefresh();
                        }
                    }
                };
            }
            this.listener = new IEventListener() {
                public void onEvent(IEvent e) {
                    ReferencedMethodsView.logger.i("Event: %s", new Object[]{e});
                    if ((ReferencedMethodsView.ContentProvider.this.codeunit != null) && (e.getSource() == ReferencedMethodsView.ContentProvider.this.codeunit)) {
                        ReferencedMethodsView.ContentProvider.this.refresher.request();
                    }
                }
            };
            this.codeunit.addListener(this.listener);
        }

        public Object[] getElements(Object inputElement) {
            List<INativeMethodItem> list = new ArrayList<>();
            for (INativeMethodItem m : this.codeunit.getMethods()) {
                if (m.getData() == null) {
                    list.add(m);
                }
            }
            return list.toArray();
        }

        public Object[] getRowElements(Object row) {
            INativeMethodItem m = (INativeMethodItem) row;
            String name = m.getName(true);
            String signature = m.getSignature(true);
            return new Object[]{name, signature};
        }

        public boolean isChecked(Object row) {
            return false;
        }
    }
}


