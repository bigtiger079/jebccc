package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.actions.ActionXrefsData;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.table.impl.Cell;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeString;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
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
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

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

public class StringsView extends AbstractFilteredTableView<ICodeUnit, ICodeString> implements ILazyView {
    private static final ILogger logger = GlobalLog.getLogger(StringsView.class);

    public StringsView(Composite parent, int flags, RcpClientContext context, ICodeUnit unit, IRcpUnitView unitView) {
        super(parent, flags, unit, unitView, context, new ContentProvider());
        setLayout(new FillLayout());
    }

    public void lazyInitialization() {
        Composite container = new Composite(this, 0);
        container.setLayout(new FillLayout());
        String[] columnNames = {"Address", "Name", "Value", "Comment"};
        FilteredTableViewer viewer = buildFilteredViewer(container, columnNames);
        addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                menuMgr.add(new Separator());
                menuMgr.add(new OperationCopy(StringsView.this));
                menuMgr.add(new Action("Jump to first reference") {
                    public void run() {
                        StringsView.this.jumpToFirstStringReference();
                    }
                });
                menuMgr.add(new Action("Cross References") {
                    public void run() {
                        StringsView.this.openXref();
                    }
                });
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                StringsView.this.jumpToFirstStringReference();
            }
        });
        layout();
    }

    public IItem getActiveItem() {
        ICodeString str = getSelectedRow();
        if (str == null) {
            return null;
        }
        Cell pseudoItem = new Cell(null);
        pseudoItem.setItemId(str.getIdentifier());
        return pseudoItem;
    }

    protected boolean isCorrectRow(Object obj) {
        return obj instanceof ICodeString;
    }

    public ICodeString getSelectedRow() {
        Object row = getSelectedRawRow();
        if (!(row instanceof ICodeString)) {
            return null;
        }
        return (ICodeString) row;
    }

    private void jumpToFirstStringReference() {
        ICodeString str = getSelectedRow();
        if (str != null) {
            jumpToFirstStringReference(str);
        }
    }

    private boolean jumpToFirstStringReference(ICodeString str) {
        if (this.unitView == null) {
            return false;
        }
        String address = str.getAddress();
        long id;
        if (address == null) {
            id = str.getIdentifier();
            if (id != 0L) {
                ActionContext actionContext = new ActionContext((IInteractiveUnit) this.unit, 4, id, null);
                ActionXrefsData data = new ActionXrefsData();
                if (((ICodeUnit) this.unit).prepareExecution(actionContext, data)) {
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
                    logger.info("Jumping to address: %s", new Object[]{address});
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
        return false;
    }

    private void openXref() {
        ICodeString str = getSelectedRow();
        if (str == null) {
            return;
        }
        if (this.unitView == null) {
            return;
        }
        long id = str.getIdentifier();
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
        ICodeUnit codeunit;
        IEventListener listener;
        ViewerRefresher refresher;

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if ((oldInput != null) && (this.listener != null)) {
                ((ICodeUnit) oldInput).removeListener(this.listener);
                this.listener = null;
            }
            this.codeunit = ((ICodeUnit) newInput);
            this.codeunit = ((ICodeUnit) newInput);
            if (this.codeunit == null) {
                return;
            }
            if (this.refresher == null) {
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer) {
                    protected void performRefresh() {
                        if ((StringsView.ContentProvider.this.codeunit instanceof INativeCodeUnit)) {
                            StringsView.logger.info("Refreshing strings...", new Object[0]);
                            super.performRefresh();
                        }
                    }
                };
            }
            this.listener = new IEventListener() {
                public void onEvent(IEvent e) {
                    StringsView.logger.i("Event: %s", new Object[]{e});
                    if ((StringsView.ContentProvider.this.codeunit != null) && (e.getSource() == StringsView.ContentProvider.this.codeunit)) {
                        StringsView.ContentProvider.this.refresher.request();
                    }
                }
            };
            this.codeunit.addListener(this.listener);
        }

        public Object[] getElements(Object inputElement) {
            List<? extends ICodeString> strings = this.codeunit.getStrings();
            return strings == null ? ArrayUtil.NO_OBJECT : strings.toArray();
        }

        public Object[] getRowElements(Object row) {
            ICodeString e = (ICodeString) row;
            String address = e.getAddress();
            String name = e.getName(true);
            String value = e.getValue();
            String comment = address == null ? null : this.codeunit.getComment(address);
            return new Object[]{address, name, value, comment};
        }

        public boolean isChecked(Object row) {
            return false;
        }
    }
}


