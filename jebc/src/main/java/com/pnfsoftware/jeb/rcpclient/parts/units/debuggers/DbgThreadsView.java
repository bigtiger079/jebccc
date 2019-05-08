package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.exceptions.DebuggerException;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThreadStackFrame;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class DbgThreadsView extends AbstractUnitFragment<IDebuggerUnit> implements IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(DbgThreadsView.class);
    private PatternTreeView ftv;
    private FilteredTreeViewer viewer;
    private LabelProvider labelProvider;
    private Object lastSelectedNode;
    private String lastAddress;

    public DbgThreadsView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit) {
        super(parent, flags, unit, null, context);
        setLayout(new FillLayout());
        this.labelProvider = new LabelProvider();
        IPatternMatcher patternMatcher = new SimplePatternMatcher(this.labelProvider);
        boolean expandAfterFilter = context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");
        String[] titleColumns = {"Thread ID", "State", "Name"};
        this.ftv = new PatternTreeView(this, 65536, titleColumns, null, patternMatcher, expandAfterFilter);
        this.viewer = this.ftv.getTreeViewer();
        Tree tree = this.ftv.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.addListener(17, new Listener() {
            public void handleEvent(Event event) {
                DbgThreadsView.this.expandTree((TreeItem) event.item);
            }
        });
        tree.addListener(18, new Listener() {
            public void handleEvent(Event event) {
                DbgThreadsView.this.collapseTree((TreeItem) event.item);
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                DbgThreadsView.this.handleDoubleClick();
            }
        });
        this.viewer.setContentProvider(new TreeContentProvider());
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.setInput(unit);
        TreeColumn[] cols = tree.getColumns();
        for (TreeColumn col : cols) {
            col.pack();
        }
        new ContextMenu(tree).addContextMenu(this);
    }

    private void expandTree(TreeItem root) {
        IDebuggerThread t = (IDebuggerThread) root.getData();
        Object[] frames = getData(t);
        TreeItem[] items = root.getItems();
        if ((items != null) && (items.length > 1)) {
            collapseTree(root);
        }
        int i = 0;
        for (Object frame : frames) {
            TreeItem item = new TreeItem(root, 0, i);
            item.setData(frame);
            item.setText(this.labelProvider.getStringArray(frame));
            i++;
        }
        items = root.getItems();
        if ((items != null) && (items.length > 1)) {
            items[(items.length - 1)].dispose();
        }
    }

    private void collapseTree(TreeItem root) {
        TreeItem[] items = root.getItems();
        for (int i = 0; i < items.length - 1; i++) {
            items[i].dispose();
        }
    }

    private void handleDoubleClick() {
        Object node = getSelectedNode();
        if ((node instanceof IDebuggerThread)) {
            IDebuggerThread thread = (IDebuggerThread) node;
            IDebuggerThread currentThread = ((IDebuggerUnit) getUnit()).getDefaultThread();
            if ((currentThread == null) || (currentThread.getId() != thread.getId())) {
                ((IDebuggerUnit) getUnit()).setDefaultThread(thread.getId());
            }
        }
    }

    public Object getSelectedNode() {
        ITreeSelection treesel = (ITreeSelection) this.viewer.getSelection();
        if (treesel.isEmpty()) {
            return null;
        }
        return treesel.getFirstElement();
    }

    public TreeViewer getJfaceViewer() {
        return (TreeViewer) this.viewer.getViewer();
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        Object elt = getSelectedNode();
        if ((elt instanceof IDebuggerThread)) {
            final IDebuggerThread t = (IDebuggerThread) elt;
            menuMgr.add(new Action("Set as default thread") {
                public void run() {
                    ((IDebuggerUnit) DbgThreadsView.this.getUnit()).setDefaultThread(t.getId());
                }

                public boolean isEnabled() {
                    return ((IDebuggerUnit) DbgThreadsView.this.getUnit()).getDefaultThread() != t;
                }
            });
            menuMgr.add(new Action("Resume") {
                public void run() {
                    t.resume();
                }

                public boolean isEnabled() {
                    return t.getStatus() == DebuggerThreadStatus.PAUSED;
                }
            });
            menuMgr.add(new Action("Suspend") {
                public void run() {
                    t.suspend();
                }

                public boolean isEnabled() {
                    return t.getStatus() != DebuggerThreadStatus.PAUSED;
                }
            });
        }
        addOperationsToContextMenu(menuMgr);
    }

    public class TreeContentProvider implements IFilteredTreeContentProvider {
        IDebuggerUnit dbg;
        IEventListener listener;

        public TreeContentProvider() {
        }

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
                    DbgThreadsView.logger.i("Event: %s", new Object[]{e});
                    if ((DbgThreadsView.TreeContentProvider.this.dbg != null) && (e.getSource() == DbgThreadsView.TreeContentProvider.this.dbg)) {
                        UIExecutor.async(viewer.getControl(), new UIRunnable() {
                            public void runi() {
                                if ((DbgThreadsView.TreeContentProvider.this.dbg != null) && (!viewer.getControl().isDisposed())) {
                                    DbgThreadsView.logger.i("Refreshing threads list...", new Object[0]);
                                    List<Integer> expanded = DbgThreadsView.TreeContentProvider.this.getExpandedTreeItems();
                                    viewer.refresh();
                                    DbgThreadsView.TreeContentProvider.this.expandTreeItems(expanded);
                                }
                            }
                        });
                    }
                }
            };
            this.dbg.addListener(this.listener);
        }

        private List<Integer> getExpandedTreeItems() {
            TreeItem[] treeItems = DbgThreadsView.this.ftv.getTree().getItems();
            List<Integer> expanded = new ArrayList();
            if (treeItems != null) {
                for (int i = 0; i < treeItems.length; i++) {
                    TreeItem item = treeItems[i];
                    if (item.getExpanded()) {
                        expanded.add(Integer.valueOf(i));
                    }
                }
            }
            return expanded;
        }

        private void expandTreeItems(List<Integer> expanded) {
            for (Iterator localIterator = expanded.iterator(); localIterator.hasNext(); ) {
                int i = ((Integer) localIterator.next()).intValue();
                TreeItem[] treeItems = DbgThreadsView.this.ftv.getTree().getItems();
                if (i < treeItems.length) {
                    treeItems[i].setExpanded(true);
                    DbgThreadsView.this.expandTree(treeItems[i]);
                }
            }
        }

        public Object[] getElements(Object inputElement) {
            IDebuggerUnit unit = (IDebuggerUnit) inputElement;
            try {
                if (unit.isAttached()) {
                    List<? extends IDebuggerThread> threads = unit.getThreads();
                    if (threads != null) {
                        return threads.toArray();
                    }
                }
            } catch (DebuggerException localDebuggerException) {
            }
            return ArrayUtil.NO_OBJECT;
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element) != null;
        }

        public Object[] getChildren(Object parentElement) {
            if ((parentElement instanceof IDebuggerThread)) {
                return new Object[]{new Object()};
            }
            return null;
        }

        public String getString(Object element) {
            if ((element instanceof IDebuggerThread)) {
                IDebuggerThread t = (IDebuggerThread) element;
                return Strings.join(",", Arrays.asList(new Serializable[]{t.getName(), Long.valueOf(t.getId())}));
            }
            return null;
        }

        public Object[] getRowElements(Object row) {
            return new Object[]{getString(row)};
        }
    }

    private static Object[] getData(IDebuggerThread t) {
        if (t.getStatus() == DebuggerThreadStatus.PAUSED) {
            try {
                List<? extends IDebuggerThreadStackFrame> frames = t.getFrames();
                if (frames != null) {
                    return frames.toArray();
                }
            } catch (DebuggerException localDebuggerException) {
            }
        }
        return ArrayUtil.NO_OBJECT;
    }

    class LabelProvider extends StyledCellLabelProvider implements IValueProvider {
        LabelProvider() {
        }

        public void update(ViewerCell cell) {
            Object elt = cell.getElement();
            int index = cell.getColumnIndex();
            String text = getStringAt(elt, index);
            if ((elt instanceof IDebuggerThread)) {
                long tid = ((IDebuggerThread) elt).getId();
                if ((((IDebuggerUnit) DbgThreadsView.this.getUnit()).getDefaultThread() != null) && (((IDebuggerUnit) DbgThreadsView.this.getUnit()).getDefaultThread().getId() == tid)) {
                    cell.setForeground(UIAssetManager.getInstance().getColor(255, 0, 0));
                } else {
                    cell.setForeground(null);
                }
            }
            cell.setText(text);
            super.update(cell);
        }

        public String getStringAt(Object element, int key) {
            if ((element instanceof IDebuggerThread)) {
                IDebuggerThread e = (IDebuggerThread) element;
                if (key == 0) {
                    return Long.toString(e.getId());
                }
                if (key == 1) {
                    return "" + e.getStatus();
                }
                if (key == 2) {
                    return e.getName();
                }
            } else if ((element instanceof IDebuggerThreadStackFrame)) {
                IDebuggerThreadStackFrame e = (IDebuggerThreadStackFrame) element;
                if (key == 0) {
                    return Long.toString(e.getId());
                }
                if (key == 1) {
                    return "";
                }
                if (key == 2) {
                    return e.getAddress();
                }
            }
            return null;
        }

        private String[] getStringArray(Object element) {
            return new String[]{getStringAt(element, 0), getStringAt(element, 1), getStringAt(element, 2)};
        }

        public String getString(Object element) {
            return Strings.join(",", Arrays.asList(getStringArray(element)));
        }
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case FIND:
                return true;
            case COPY:
                return getSelectedNode() != null;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case FIND:
                this.ftv.setFilterVisibility(true, true);
                return true;
            case COPY:
                return copyObjectToClipboard(getSelectedNode());
        }
        return false;
    }

    private boolean copyObjectToClipboard(Object obj) {
        if (obj != null) {
            String s = obj.toString();
            if (s != null) {
                UIUtil.copyTextToClipboard(s);
                return true;
            }
        }
        return false;
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        Object selectedNode = getSelectedNode();
        if ((this.lastSelectedNode == selectedNode) && (this.lastAddress != null)) {
            return this.lastAddress;
        }
        String address = null;
        if ((selectedNode instanceof IDebuggerThread)) {
            address = ((IDebuggerThread) selectedNode).getLocation();
        }
        if ((selectedNode instanceof IDebuggerThreadStackFrame)) {
            address = ((IDebuggerThreadStackFrame) selectedNode).getAddress();
        }
        this.lastSelectedNode = selectedNode;
        this.lastAddress = address;
        return address;
    }

    public byte[] export() {
        return Strings.encodeUTF8(this.viewer.exportToString());
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TREE;
    }
}


