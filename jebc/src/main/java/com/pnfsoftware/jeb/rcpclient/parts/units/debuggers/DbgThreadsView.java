/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.exceptions.DebuggerException;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThreadStackFrame;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.DoubleClickEvent;
/*     */ import org.eclipse.jface.viewers.IDoubleClickListener;
/*     */ import org.eclipse.jface.viewers.ITreeSelection;
/*     */ import org.eclipse.jface.viewers.StyledCellLabelProvider;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;
/*     */ import org.eclipse.swt.widgets.TreeItem;

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
/*     */ public class DbgThreadsView
        /*     */ extends AbstractUnitFragment<IDebuggerUnit>
        /*     */ implements IContextMenu
        /*     */ {
    /*  68 */   private static final ILogger logger = GlobalLog.getLogger(DbgThreadsView.class);
    /*     */
    /*     */
    /*     */   private PatternTreeView ftv;
    /*     */
    /*     */
    /*     */   private FilteredTreeViewer viewer;
    /*     */
    /*     */   private LabelProvider labelProvider;
    /*     */
    /*     */   private Object lastSelectedNode;
    /*     */
    /*     */   private String lastAddress;

    /*     */
    /*     */
    /*     */
    public DbgThreadsView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit)
    /*     */ {
        /*  85 */
        super(parent, flags, unit, null, context);
        /*  86 */
        setLayout(new FillLayout());
        /*     */
        /*  88 */
        this.labelProvider = new LabelProvider();
        /*     */
        /*  90 */
        IPatternMatcher patternMatcher = new SimplePatternMatcher(this.labelProvider);
        /*  91 */
        boolean expandAfterFilter = context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");
        /*  92 */
        String[] titleColumns = {"Thread ID", "State", "Name"};
        /*  93 */
        this.ftv = new PatternTreeView(this, 65536, titleColumns, null, patternMatcher, expandAfterFilter);
        /*  94 */
        this.viewer = this.ftv.getTreeViewer();
        /*     */
        /*  96 */
        Tree tree = this.ftv.getTree();
        /*  97 */
        tree.setHeaderVisible(true);
        /*  98 */
        tree.setLinesVisible(true);
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
        /* 110 */
        tree.addListener(17, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /* 113 */
                DbgThreadsView.this.expandTree((TreeItem) event.item);
                /*     */
            }
            /* 115 */
        });
        /* 116 */
        tree.addListener(18, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /* 119 */
                DbgThreadsView.this.collapseTree((TreeItem) event.item);
                /*     */
            }
            /*     */
            /* 122 */
        });
        /* 123 */
        this.viewer.addDoubleClickListener(new IDoubleClickListener()
                /*     */ {
            /*     */
            public void doubleClick(DoubleClickEvent event) {
                /* 126 */
                DbgThreadsView.this.handleDoubleClick();
                /*     */
            }
            /*     */
            /* 129 */
        });
        /* 130 */
        this.viewer.setContentProvider(new TreeContentProvider());
        /* 131 */
        this.viewer.setLabelProvider(this.labelProvider);
        /* 132 */
        this.viewer.setInput(unit);
        /*     */
        /*     */
        /*     */
        /* 136 */
        TreeColumn[] cols = tree.getColumns();
        /* 137 */
        for (TreeColumn col : cols) {
            /* 138 */
            col.pack();
            /*     */
        }
        /*     */
        /*     */
        /* 142 */
        new ContextMenu(tree).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    private void expandTree(TreeItem root) {
        /* 146 */
        IDebuggerThread t = (IDebuggerThread) root.getData();
        /* 147 */
        Object[] frames = getData(t);
        /* 148 */
        TreeItem[] items = root.getItems();
        /* 149 */
        if ((items != null) && (items.length > 1))
            /*     */ {
            /*     */
            /* 152 */
            collapseTree(root);
            /*     */
        }
        /* 154 */
        int i = 0;
        /* 155 */
        for (Object frame : frames) {
            /* 156 */
            TreeItem item = new TreeItem(root, 0, i);
            /* 157 */
            item.setData(frame);
            /* 158 */
            item.setText(this.labelProvider.getStringArray(frame));
            /* 159 */
            i++;
            /*     */
        }
        /* 161 */
        items = root.getItems();
        /* 162 */
        if ((items != null) && (items.length > 1))
            /*     */ {
            /* 164 */
            items[(items.length - 1)].dispose();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void collapseTree(TreeItem root) {
        /* 169 */
        TreeItem[] items = root.getItems();
        /*     */
        /* 171 */
        for (int i = 0; i < items.length - 1; i++) {
            /* 172 */
            items[i].dispose();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void handleDoubleClick() {
        /* 177 */
        Object node = getSelectedNode();
        /* 178 */
        if ((node instanceof IDebuggerThread)) {
            /* 179 */
            IDebuggerThread thread = (IDebuggerThread) node;
            /* 180 */
            IDebuggerThread currentThread = ((IDebuggerUnit) getUnit()).getDefaultThread();
            /* 181 */
            if ((currentThread == null) || (currentThread.getId() != thread.getId())) {
                /* 182 */
                ((IDebuggerUnit) getUnit()).setDefaultThread(thread.getId());
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public Object getSelectedNode() {
        /* 188 */
        ITreeSelection treesel = (ITreeSelection) this.viewer.getSelection();
        /* 189 */
        if (treesel.isEmpty()) {
            /* 190 */
            return null;
            /*     */
        }
        /*     */
        /* 193 */
        return treesel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public TreeViewer getJfaceViewer()
    /*     */ {
        /* 199 */
        return (TreeViewer) this.viewer.getViewer();
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 204 */
        Object elt = getSelectedNode();
        /* 205 */
        if ((elt instanceof IDebuggerThread)) {
            /* 206 */
            final IDebuggerThread t = (IDebuggerThread) elt;
            /* 207 */
            menuMgr.add(new Action("Set as default thread")
                    /*     */ {
                /*     */
                public void run() {
                    /* 210 */
                    ((IDebuggerUnit) DbgThreadsView.this.getUnit()).setDefaultThread(t.getId());
                    /*     */
                }

                /*     */
                /*     */
                public boolean isEnabled()
                /*     */ {
                    /* 215 */
                    return ((IDebuggerUnit) DbgThreadsView.this.getUnit()).getDefaultThread() != t;
                    /*     */
                }
                /*     */
                /* 218 */
            });
            /* 219 */
            menuMgr.add(new Action("Resume")
                    /*     */ {
                /*     */
                public void run() {
                    /* 222 */
                    t.resume();
                    /*     */
                }

                /*     */
                /*     */
                public boolean isEnabled()
                /*     */ {
                    /* 227 */
                    return t.getStatus() == DebuggerThreadStatus.PAUSED;
                    /*     */
                }
                /*     */
                /* 230 */
            });
            /* 231 */
            menuMgr.add(new Action("Suspend")
                    /*     */ {
                /*     */
                public void run() {
                    /* 234 */
                    t.suspend();
                    /*     */
                }

                /*     */
                /*     */
                public boolean isEnabled()
                /*     */ {
                    /* 239 */
                    return t.getStatus() != DebuggerThreadStatus.PAUSED;
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
        /* 244 */
        addOperationsToContextMenu(menuMgr);
        /*     */
    }

    /*     */
    /*     */   public class TreeContentProvider implements IFilteredTreeContentProvider
            /*     */ {
        /*     */ IDebuggerUnit dbg;
        /*     */ IEventListener listener;

        /*     */
        /*     */
        public TreeContentProvider() {
        }

        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
            /* 257 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 258 */
                ((IDebuggerUnit) oldInput).removeListener(this.listener);
                /* 259 */
                this.listener = null;
                /*     */
            }
            /*     */
            /* 262 */
            this.dbg = ((IDebuggerUnit) newInput);
            /* 263 */
            if (this.dbg == null) {
                /* 264 */
                return;
                /*     */
            }
            /*     */
            /* 267 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e) {
                    /* 270 */
                    DbgThreadsView.logger.i("Event: %s", new Object[]{e});
                    /* 271 */
                    if ((DbgThreadsView.TreeContentProvider.this.dbg != null) && (e.getSource() == DbgThreadsView.TreeContentProvider.this.dbg)) {
                        /* 272 */
                        UIExecutor.async(viewer.getControl(), new UIRunnable()
                                /*     */ {
                            /*     */
                            public void runi() {
                                /* 275 */
                                if ((DbgThreadsView.TreeContentProvider.this.dbg != null) && (!DbgThreadsView.TreeContentProvider
                                .1. this.val$viewer.getControl().isDisposed())){
                                    /* 276 */
                                    DbgThreadsView.logger.i("Refreshing threads list...", new Object[0]);
                                    /*     */
                                    /* 278 */
                                    List<Integer> expanded = DbgThreadsView.TreeContentProvider.this.getExpandedTreeItems();
                                    /* 279 */
                                    DbgThreadsView.TreeContentProvider .1. this.val$viewer.refresh();
                                    /* 280 */
                                    DbgThreadsView.TreeContentProvider.this.expandTreeItems(expanded);
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
                /* 286 */
            };
            /* 287 */
            this.dbg.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        private List<Integer> getExpandedTreeItems()
        /*     */ {
            /* 294 */
            TreeItem[] treeItems = DbgThreadsView.this.ftv.getTree().getItems();
            /* 295 */
            List<Integer> expanded = new ArrayList();
            /* 296 */
            if (treeItems != null) {
                /* 297 */
                for (int i = 0; i < treeItems.length; i++) {
                    /* 298 */
                    TreeItem item = treeItems[i];
                    /* 299 */
                    if (item.getExpanded()) {
                        /* 300 */
                        expanded.add(Integer.valueOf(i));
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /* 304 */
            return expanded;
            /*     */
        }

        /*     */
        /*     */
        private void expandTreeItems(List<Integer> expanded) {
            /* 308 */
            for (Iterator localIterator = expanded.iterator(); localIterator.hasNext(); ) {
                int i = ((Integer) localIterator.next()).intValue();
                /* 309 */
                TreeItem[] treeItems = DbgThreadsView.this.ftv.getTree().getItems();
                /* 310 */
                if (i < treeItems.length) {
                    /* 311 */
                    treeItems[i].setExpanded(true);
                    /*     */
                    /* 313 */
                    DbgThreadsView.this.expandTree(treeItems[i]);
                    /*     */
                }
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /* 320 */
            IDebuggerUnit unit = (IDebuggerUnit) inputElement;
            /*     */
            try {
                /* 322 */
                if (unit.isAttached()) {
                    /* 323 */
                    List<? extends IDebuggerThread> threads = unit.getThreads();
                    /* 324 */
                    if (threads != null) {
                        /* 325 */
                        return threads.toArray();
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */ catch (DebuggerException localDebuggerException) {
            }
            /*     */
            /*     */
            /* 332 */
            return ArrayUtil.NO_OBJECT;
            /*     */
        }

        /*     */
        /*     */
        public Object getParent(Object element)
        /*     */ {
            /* 337 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public boolean hasChildren(Object element)
        /*     */ {
            /* 342 */
            return getChildren(element) != null;
            /*     */
        }

        /*     */
        /*     */
        public Object[] getChildren(Object parentElement)
        /*     */ {
            /* 347 */
            if ((parentElement instanceof IDebuggerThread))
                /*     */ {
                /* 349 */
                return new Object[]{new Object()};
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 358 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public String getString(Object element) {
            /* 362 */
            if ((element instanceof IDebuggerThread)) {
                /* 363 */
                IDebuggerThread t = (IDebuggerThread) element;
                /* 364 */
                return Strings.join(",", Arrays.asList(new Serializable[]{t.getName(), Long.valueOf(t.getId())}));
                /*     */
            }
            /* 366 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 371 */
            return new Object[]{getString(row)};
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private static Object[] getData(IDebuggerThread t) {
        /* 376 */
        if (t.getStatus() == DebuggerThreadStatus.PAUSED) {
            /*     */
            try
                /*     */ {
                /* 379 */
                List<? extends IDebuggerThreadStackFrame> frames = t.getFrames();
                /*     */
                /*     */
                /* 382 */
                if (frames != null) {
                    /* 383 */
                    return frames.toArray();
                    /*     */
                }
                /*     */
            }
            /*     */ catch (DebuggerException localDebuggerException) {
            }
            /*     */
        }
        /*     */
        /* 389 */
        return ArrayUtil.NO_OBJECT;
        /*     */
    }

    /*     */
    /*     */   class LabelProvider extends StyledCellLabelProvider implements IValueProvider {
        /*     */     LabelProvider() {
        }

        /*     */
        /* 395 */
        public void update(ViewerCell cell) {
            Object elt = cell.getElement();
            /* 396 */
            int index = cell.getColumnIndex();
            /*     */
            /* 398 */
            String text = getStringAt(elt, index);
            /*     */
            /* 400 */
            if ((elt instanceof IDebuggerThread)) {
                /* 401 */
                long tid = ((IDebuggerThread) elt).getId();
                /* 402 */
                if ((((IDebuggerUnit) DbgThreadsView.this.getUnit()).getDefaultThread() != null) && (((IDebuggerUnit) DbgThreadsView.this.getUnit()).getDefaultThread().getId() == tid)) {
                    /* 403 */
                    cell.setForeground(UIAssetManager.getInstance().getColor(255, 0, 0));
                    /*     */
                }
                /*     */
                else {
                    /* 406 */
                    cell.setForeground(null);
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 410 */
            cell.setText(text);
            /* 411 */
            super.update(cell);
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int key)
        /*     */ {
            /* 416 */
            if ((element instanceof IDebuggerThread)) {
                /* 417 */
                IDebuggerThread e = (IDebuggerThread) element;
                /* 418 */
                if (key == 0) {
                    /* 419 */
                    return Long.toString(e.getId());
                    /*     */
                }
                /* 421 */
                if (key == 1) {
                    /* 422 */
                    return "" + e.getStatus();
                    /*     */
                }
                /* 424 */
                if (key == 2) {
                    /* 425 */
                    return e.getName();
                    /*     */
                }
                /*     */
            }
            /* 428 */
            else if ((element instanceof IDebuggerThreadStackFrame)) {
                /* 429 */
                IDebuggerThreadStackFrame e = (IDebuggerThreadStackFrame) element;
                /* 430 */
                if (key == 0) {
                    /* 431 */
                    return Long.toString(e.getId());
                    /*     */
                }
                /* 433 */
                if (key == 1) {
                    /* 434 */
                    return "";
                    /*     */
                }
                /* 436 */
                if (key == 2) {
                    /* 437 */
                    return e.getAddress();
                    /*     */
                }
                /*     */
            }
            /* 440 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        private String[] getStringArray(Object element) {
            /* 444 */
            return new String[]{getStringAt(element, 0), getStringAt(element, 1), getStringAt(element, 2)};
            /*     */
        }

        /*     */
        /*     */
        public String getString(Object element)
        /*     */ {
            /* 449 */
            return Strings.join(",", Arrays.asList(getStringArray(element)));
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 455 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 457 */
                return true;
            /*     */
            /*     */
            case COPY:
                /* 460 */
                return getSelectedNode() != null;
            /*     */
        }
        /*     */
        /* 463 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 469 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 471 */
                this.ftv.setFilterVisibility(true, true);
                /* 472 */
                return true;
            /*     */
            /*     */
            case COPY:
                /* 475 */
                return copyObjectToClipboard(getSelectedNode());
            /*     */
        }
        /*     */
        /* 478 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private boolean copyObjectToClipboard(Object obj)
    /*     */ {
        /* 483 */
        if (obj != null) {
            /* 484 */
            String s = obj.toString();
            /* 485 */
            if (s != null) {
                /* 486 */
                UIUtil.copyTextToClipboard(s);
                /* 487 */
                return true;
                /*     */
            }
            /*     */
        }
        /* 490 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 496 */
        Object selectedNode = getSelectedNode();
        /* 497 */
        if ((this.lastSelectedNode == selectedNode) && (this.lastAddress != null)) {
            /* 498 */
            return this.lastAddress;
            /*     */
        }
        /*     */
        /* 501 */
        String address = null;
        /* 502 */
        if ((selectedNode instanceof IDebuggerThread)) {
            /* 503 */
            address = ((IDebuggerThread) selectedNode).getLocation();
            /*     */
        }
        /* 505 */
        if ((selectedNode instanceof IDebuggerThreadStackFrame)) {
            /* 506 */
            address = ((IDebuggerThreadStackFrame) selectedNode).getAddress();
            /*     */
        }
        /*     */
        /* 509 */
        this.lastSelectedNode = selectedNode;
        /* 510 */
        this.lastAddress = address;
        /* 511 */
        return address;
        /*     */
    }

    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 516 */
        return Strings.encodeUTF8(this.viewer.exportToString());
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 521 */
        return AbstractUnitFragment.FragmentType.TREE;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgThreadsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */