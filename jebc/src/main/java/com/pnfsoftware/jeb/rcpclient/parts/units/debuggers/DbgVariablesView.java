/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.exceptions.DebuggerException;
/*     */ import com.pnfsoftware.jeb.core.exceptions.JebException;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThreadStackFrame;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerVariable;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.ITypedValue;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueComposite;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueNumber;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueDouble;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueFloat;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueObject;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueRaw;
/*     */ import com.pnfsoftware.jeb.rcpclient.AssetManagerOverlay;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractArrayGroupFilteredTreeContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredViewerComparator;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.DbgTypedValueUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.CellEditor;
/*     */ import org.eclipse.jface.viewers.ColumnViewer;
/*     */ import org.eclipse.jface.viewers.EditingSupport;
/*     */ import org.eclipse.jface.viewers.ITreeSelection;
/*     */ import org.eclipse.jface.viewers.StyledCellLabelProvider;
/*     */ import org.eclipse.jface.viewers.TextCellEditor;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.jface.viewers.TreeViewerColumn;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;

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
/*     */ public class DbgVariablesView
        /*     */ extends AbstractUnitFragment<IDebuggerUnit>
        /*     */ implements IContextMenu
        /*     */ {
    /*  79 */   private static final ILogger logger = GlobalLog.getLogger(DbgVariablesView.class);
    /*     */
    /*     */
    /*     */   private FilteredTreeViewer viewer;
    /*     */
    /*     */
    /*     */   private IDebuggerThreadStackFrame targetFrame;

    /*     */
    /*     */
    /*     */
    /*     */
    public DbgVariablesView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit)
    /*     */ {
        /*  91 */
        super(parent, flags, unit, null, context);
        /*  92 */
        setLayout(new FillLayout());
        /*     */
        /*  94 */
        String[] titleColumns = {"Name", "Type", "Value", "Extra"};
        /*  95 */
        LabelProvider labelProvider = new LabelProvider();
        /*  96 */
        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);
        /*  97 */
        boolean expandAfterFilter = context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");
        /*  98 */
        PatternTreeView ftv = new PatternTreeView(this, 65536, titleColumns, null, patternMatcher, expandAfterFilter);
        /*     */
        /* 100 */
        ftv.setFilterVisibility(false, false);
        /* 101 */
        this.viewer = ftv.getTreeViewer();
        /*     */
        /* 103 */
        Tree tree = ftv.getTree();
        /* 104 */
        tree.setHeaderVisible(true);
        /* 105 */
        tree.setLinesVisible(true);
        /*     */
        /* 107 */
        TreeColumn[] cols = tree.getColumns();
        /* 108 */
        TreeViewerColumn tcv1 = new TreeViewerColumn((TreeViewer) this.viewer.getViewer(), cols[1]);
        /* 109 */
        tcv1.setEditingSupport(new ValueEditingSupport(this.viewer.getViewer(), 2));
        /* 110 */
        TreeViewerColumn tcv2 = new TreeViewerColumn((TreeViewer) this.viewer.getViewer(), cols[2]);
        /* 111 */
        tcv2.setEditingSupport(new ValueEditingSupport(this.viewer.getViewer(), 0));
        /* 112 */
        TreeViewerColumn tcv3 = new TreeViewerColumn((TreeViewer) this.viewer.getViewer(), cols[3]);
        /* 113 */
        tcv3.setEditingSupport(new ValueEditingSupport(this.viewer.getViewer(), 1));
        /*     */
        /* 115 */
        this.viewer.setContentProvider(new TreeContentProvider());
        /* 116 */
        this.viewer.setLabelProvider(labelProvider);
        /* 117 */
        this.viewer.setInput(unit);
        /*     */
        /*     */
        /* 120 */
        this.viewer.expandToLevel(2);
        /*     */
        /* 122 */
        for (TreeColumn col : cols) {
            /* 123 */
            col.pack();
            /*     */
        }
        /*     */
        /*     */
        /* 127 */
        new ContextMenu(tree).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 132 */
        Object elt = getSelectedNode();
        /* 133 */
        if ((elt instanceof IDebuggerVariable)) {
            /* 134 */
            final IDebuggerVariable v = (IDebuggerVariable) elt;
            /* 135 */
            menuMgr.add(new Action("View string representation")
                    /*     */ {
                /*     */
                public void run() {
                    /* 138 */
                    if ((v.getTypedValue() instanceof ValueObject)) {
                        /*     */
                        try {
                            /* 140 */
                            IDebuggerThread t = ((IDebuggerUnit) DbgVariablesView.this.getUnit()).getDefaultThread();
                            /* 141 */
                            if (t != null) {
                                /* 142 */
                                long threadId = t.getId();
                                /* 143 */
                                ITypedValue result = ((ValueObject) v.getTypedValue()).invoke("toString", threadId, null);
                                /*     */
                                /* 145 */
                                if (result != null) {
                                    /* 146 */
                                    DbgVariablesView.logger.info(result.toString(), new Object[0]);
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                            else {
                                /* 150 */
                                DbgVariablesView.logger.error("Can not call toString: no default thread", new Object[0]);
                                /*     */
                            }
                            /*     */
                        }
                        /*     */ catch (JebException e) {
                            /* 154 */
                            DbgVariablesView.logger.catching(e);
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }

                /*     */
                /*     */
                public boolean isEnabled()
                /*     */ {
                    /* 161 */
                    return ((v.getTypedValue() instanceof ValueObject)) &&
                            /* 162 */             (((ValueObject) v.getTypedValue()).getObjectId() != 0L);
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
        /* 167 */
        addOperationsToContextMenu(menuMgr);
        /*     */
    }

    /*     */
    /*     */
    public void setTargetFrame(IDebuggerThreadStackFrame frame) {
        /* 171 */
        this.targetFrame = frame;
        /* 172 */
        this.viewer.refresh();
        /*     */
    }

    /*     */
    /*     */
    public IDebuggerThreadStackFrame getTargetFrame() {
        /* 176 */
        if (this.targetFrame != null) {
            /* 177 */
            return this.targetFrame;
            /*     */
        }
        /*     */
        /* 180 */
        IDebuggerThread t = ((IDebuggerUnit) getUnit()).getDefaultThread();
        /* 181 */
        if ((t != null) &&
                /* 182 */       (t.getStatus() == DebuggerThreadStatus.PAUSED)) {
            /* 183 */
            List<? extends IDebuggerThreadStackFrame> frames = t.getFrames();
            /* 184 */
            if ((frames != null) && (!frames.isEmpty())) {
                /* 185 */
                return (IDebuggerThreadStackFrame) frames.get(0);
                /*     */
            }
            /*     */
        }
        /*     */
        /* 189 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private Object getSelectedNode() {
        /* 193 */
        ITreeSelection treesel = (ITreeSelection) this.viewer.getSelection();
        /* 194 */
        if (treesel.isEmpty()) {
            /* 195 */
            return null;
            /*     */
        }
        /*     */
        /* 198 */
        return treesel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */
    private boolean copyValueToClipboard(ITypedValue value) {
        /* 202 */
        String s = formatValue(value);
        /* 203 */
        if (s != null) {
            /* 204 */
            UIUtil.copyTextToClipboard(s);
            /* 205 */
            return true;
            /*     */
        }
        /* 207 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private String formatValue(ITypedValue value) {
        /* 211 */
        String s = null;
        /* 212 */
        if ((value instanceof AbstractValueComposite)) {
            /* 213 */
            s = ((AbstractValueComposite) value).format();
            /*     */
        }
        /*     */
        else {
            /* 216 */
            s = value.toString();
            /*     */
        }
        /* 218 */
        return s;
        /*     */
    }

    /*     */
    /*     */   public class TreeContentProvider extends AbstractArrayGroupFilteredTreeContentProvider {
        /*     */ IDebuggerUnit dbg;
        /*     */ IEventListener listener;

        /*     */
        /*     */
        public TreeContentProvider() {
            /* 226 */
            super(100, 100);
            /*     */
        }

        /*     */
        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        /*     */
        public void inputChanged(final Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 235 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 236 */
                ((IDebuggerUnit) oldInput).removeListener(this.listener);
                /* 237 */
                this.listener = null;
                /*     */
            }
            /*     */
            /* 240 */
            this.dbg = ((IDebuggerUnit) newInput);
            /* 241 */
            if (this.dbg == null) {
                /* 242 */
                return;
                /*     */
            }
            /*     */
            /* 245 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e) {
                    /* 248 */
                    DbgVariablesView.logger.i("Event: %s", new Object[]{e});
                    /* 249 */
                    if ((DbgVariablesView.TreeContentProvider.this.dbg != null) && (e.getSource() == DbgVariablesView.TreeContentProvider.this.dbg)) {
                        /* 250 */
                        UIExecutor.async(viewer.getControl(), new UIRunnable()
                                /*     */ {
                            /*     */
                            public void runi() {
                                /* 253 */
                                if ((DbgVariablesView.TreeContentProvider.this.dbg != null) && (!DbgVariablesView.TreeContentProvider
                                .1. this.val$viewer.getControl().isDisposed())){
                                    /* 254 */
                                    DbgVariablesView.TreeContentProvider .1. this.val$viewer.refresh();
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
                /* 260 */
            };
            /* 261 */
            this.dbg.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /*     */
            try {
                /* 267 */
                if (((IDebuggerUnit) DbgVariablesView.this.unit).isAttached())
                    /*     */ {
                    /*     */
                    /* 270 */
                    IDebuggerThreadStackFrame frame = DbgVariablesView.this.getTargetFrame();
                    /* 271 */
                    if (frame != null) {
                        /* 272 */
                        List<? extends IDebuggerVariable> variables = frame.getVariables();
                        /* 273 */
                        if (variables != null) {
                            /* 274 */
                            return variables.toArray();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */ catch (DebuggerException e) {
                /* 280 */
                DbgVariablesView.logger.catching(e);
                /*     */
            }
            /* 282 */
            return ArrayUtil.NO_OBJECT;
            /*     */
        }

        /*     */
        /*     */
        public Object getParent(Object element)
        /*     */ {
            /* 287 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public boolean hasChildren2(Object element)
        /*     */ {
            /* 292 */
            ITypedValue val = getTypedValue(element);
            /* 293 */
            if ((val instanceof AbstractValueComposite)) {
                /* 294 */
                return ((AbstractValueComposite) val).hasChildren();
                /*     */
            }
            /* 296 */
            return false;
            /*     */
        }

        /*     */
        /*     */
        public List<?> getChildren2(Object parentElement)
        /*     */ {
            /* 301 */
            ITypedValue val = getTypedValue(parentElement);
            /* 302 */
            if ((val instanceof AbstractValueComposite)) {
                /* 303 */
                return ((AbstractValueComposite) val).getValue();
                /*     */
            }
            /* 305 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public void sort(Object[] elements)
        /*     */ {
            /* 310 */
            DbgVariablesView.this.viewer.getComparator().sort(DbgVariablesView.this.viewer.getViewer(), elements);
            /*     */
        }

        /*     */
        /*     */
        private ITypedValue getTypedValue(Object parentElement) {
            /* 314 */
            if ((parentElement instanceof IDebuggerVariable)) {
                /* 315 */
                return ((IDebuggerVariable) parentElement).getTypedValue();
                /*     */
            }
            /* 317 */
            if ((parentElement instanceof ITypedValue)) {
                /* 318 */
                return (ITypedValue) parentElement;
                /*     */
            }
            /* 320 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public String getString(Object element) {
            /* 324 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 329 */
            return new Object[]{getString(row)};
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   class LabelProvider extends StyledCellLabelProvider implements IValueProvider
            /*     */ {
        /*     */     LabelProvider() {
        }

        /*     */
        /*     */
        public void update(ViewerCell cell) {
            /* 338 */
            Object elt = cell.getElement();
            /* 339 */
            int index = cell.getColumnIndex();
            /* 340 */
            cell.setText(Strings.safe(getStringAt(elt, index)));
            /*     */
            /*     */
            /*     */
            /* 344 */
            if (index == 0) {
                /* 345 */
                Image img = null;
                /* 346 */
                if ((elt instanceof IDebuggerVariable)) {
                    /* 347 */
                    int flags = ((IDebuggerVariable) elt).getFlags();
                    /* 348 */
                    if (flags != 0) {
                        /* 349 */
                        String visibilityFlag = null;
                        /* 350 */
                        if ((flags & 0x1) != 0) {
                            /* 351 */
                            visibilityFlag = "eclipse/field_public_obj.png";
                            /*     */
                        }
                        /* 353 */
                        else if ((flags & 0x4) != 0) {
                            /* 354 */
                            visibilityFlag = "eclipse/field_protected_obj.png";
                            /*     */
                        }
                        /* 356 */
                        else if ((flags & 0x2) != 0) {
                            /* 357 */
                            visibilityFlag = "eclipse/field_private_obj.png";
                            /*     */
                        }
                        /*     */
                        else {
                            /* 360 */
                            visibilityFlag = "eclipse/field_default_obj.png";
                            /*     */
                        }
                        /*     */
                        /* 363 */
                        AssetManagerOverlay overlay = null;
                        /* 364 */
                        if ((flags & 0x8) != 0) {
                            /* 365 */
                            if (overlay == null) {
                                /* 366 */
                                overlay = new AssetManagerOverlay();
                                /*     */
                            }
                            /* 368 */
                            overlay.addLayer("eclipse/static_co.png", new Point(0, 0));
                            /*     */
                        }
                        /* 370 */
                        if ((flags & 0x10) != 0) {
                            /* 371 */
                            if (overlay == null) {
                                /* 372 */
                                overlay = new AssetManagerOverlay();
                                /*     */
                            }
                            /* 374 */
                            overlay.addLayer("eclipse/final_co.png", new Point(9, 0));
                            /*     */
                        }
                        /*     */
                        /* 377 */
                        img = UIAssetManager.getInstance().getImage(visibilityFlag, overlay);
                        /*     */
                    }
                    /*     */
                }
                /* 380 */
                if (img != null) {
                    /* 381 */
                    cell.setImage(img);
                    /*     */
                }
                /*     */
            }
            /* 384 */
            else if ((index == 2) &&
                    /* 385 */         ((elt instanceof IDebuggerVariable)) &&
                    /* 386 */         ((((IDebuggerVariable) elt).getTypedValue() instanceof ValueRaw))) {
                /* 387 */
                cell.setFont(DbgVariablesView.this.context.getFontManager().getCodeFont());
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /* 392 */
            super.update(cell);
            /*     */
        }

        /*     */
        /*     */
        public String getString(Object element)
        /*     */ {
            /* 397 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int index)
        /*     */ {
            /* 402 */
            if ((element instanceof IDebuggerVariable)) {
                /* 403 */
                IDebuggerVariable v = (IDebuggerVariable) element;
                /* 404 */
                if (index == 0) {
                    /* 405 */
                    return v.getName();
                    /*     */
                }
                /* 407 */
                if (index == 1) {
                    /* 408 */
                    return v.getTypedValue().getTypeName();
                    /*     */
                }
                /* 410 */
                if ((index >= 2) && (index < 5)) {
                    /* 411 */
                    return DbgVariablesView.this.formatCell(v.getTypedValue(), index - 2);
                    /*     */
                }
                /*     */
            }
            /* 414 */
            if ((element instanceof ITypedValue)) {
                /* 415 */
                ITypedValue v = (ITypedValue) element;
                /* 416 */
                if (index == 0) {
                    /* 417 */
                    return "";
                    /*     */
                }
                /* 419 */
                if (index == 1) {
                    /* 420 */
                    return v.getTypeName();
                    /*     */
                }
                /* 422 */
                if ((index >= 2) && (index < 5)) {
                    /* 423 */
                    return DbgVariablesView.this.formatCell(v, index - 2);
                    /*     */
                }
                /*     */
            }
            /* 426 */
            return AbstractArrayGroupFilteredTreeContentProvider.getStringAt(element, index);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private String formatCell(ITypedValue value, int index) {
        /* 431 */
        if ((index == 0) || (index == 1)) {
            /* 432 */
            return DbgTypedValueUtil.formatValue(value, index, (IDebuggerUnit) this.unit);
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /* 438 */
        return "";
        /*     */
    }

    /*     */
    /*     */   class ValueEditingSupport extends EditingSupport {
        /*     */     private static final int COLUMN_VALUE = 0;
        /*     */     private static final int COLUMN_EXTRA = 1;
        /*     */     private static final int COLUMN_TYPE = 2;
        /*     */ ColumnViewer columnVviewer;
        /*     */ TextCellEditor editor;
        /*     */ int index;

        /*     */
        /*     */
        public ValueEditingSupport(ColumnViewer viewer, int index) {
            /* 450 */
            super();
            /* 451 */
            this.columnVviewer = viewer;
            /*     */
            /* 453 */
            Composite parent = (Composite) viewer.getControl();
            /* 454 */
            this.editor = new TextCellEditor(parent);
            /* 455 */
            this.index = index;
            /*     */
        }

        /*     */
        /*     */
        protected CellEditor getCellEditor(Object element)
        /*     */ {
            /* 460 */
            if (!(element instanceof IDebuggerVariable)) {
                /* 461 */
                return null;
                /*     */
            }
            /* 463 */
            return this.editor;
            /*     */
        }

        /*     */
        /*     */
        protected boolean canEdit(Object element)
        /*     */ {
            /* 468 */
            if (!(element instanceof IDebuggerVariable)) {
                /* 469 */
                return false;
                /*     */
            }
            /*     */
            /* 472 */
            IDebuggerVariable v = (IDebuggerVariable) element;
            /* 473 */
            ITypedValue t = v.getTypedValue();
            /*     */
            /*     */
            /*     */
            /*     */
            /* 478 */
            if (this.index == 0) {
                /* 479 */
                return v.canEditValue();
                /*     */
            }
            /* 481 */
            if (this.index == 1) {
                /* 482 */
                if ((t instanceof AbstractValueNumber)) {
                    /* 483 */
                    return (v.canEditValue()) && (!(t instanceof ValueFloat)) && (!(t instanceof ValueDouble));
                    /*     */
                }
                /*     */
            }
            /* 486 */
            else if (this.index == 2) {
                /* 487 */
                return v.canEditType();
                /*     */
            }
            /*     */
            /* 490 */
            return false;
            /*     */
        }

        /*     */
        /*     */
        protected Object getValue(Object element)
        /*     */ {
            /* 495 */
            IDebuggerVariable v = (IDebuggerVariable) element;
            /* 496 */
            return DbgVariablesView.this.formatCell(v.getTypedValue(), this.index);
            /*     */
        }

        /*     */
        /*     */
        protected void setValue(Object element, Object value)
        /*     */ {
            /* 501 */
            IDebuggerVariable v = (IDebuggerVariable) element;
            /*     */
            /*     */
            /* 504 */
            if ((this.index == 0) || (this.index == 1)) {
                /* 505 */
                ITypedValue newValue = buildValue(v.getTypedValue(), (String) value);
                /* 506 */
                if (newValue != null) {
                    /* 507 */
                    IDebuggerThreadStackFrame frame = DbgVariablesView.this.getTargetFrame();
                    /* 508 */
                    if (frame != null) {
                        /* 509 */
                        boolean success = v.setTypedValue(newValue);
                        /* 510 */
                        if (success)
                            /*     */ {
                            /* 512 */
                            this.columnVviewer.refresh();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /* 517 */
            else if (this.index == 2) {
                /* 518 */
                String visuType = (String) value;
                /* 519 */
                DbgVariablesView.logger.i("visuType= %s", new Object[]{visuType});
                /* 520 */
                if ((!Strings.isBlank(visuType)) &&
                        /* 521 */           (v.setTypeHint(visuType))) {
                    /* 522 */
                    this.columnVviewer.refresh();
                    /*     */
                }
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public ITypedValue buildValue(ITypedValue value, String newValue)
        /*     */ {
            /* 529 */
            if (newValue == null) {
                /* 530 */
                return null;
                /*     */
            }
            /* 532 */
            if (DbgTypedValueUtil.equals(value, newValue, (IDebuggerUnit) DbgVariablesView.this.unit)) {
                /* 533 */
                return null;
                /*     */
            }
            /* 535 */
            ITypedValue typedValue = DbgTypedValueUtil.buildValue(value, newValue);
            /* 536 */
            if ((typedValue != null) && (Objects.equals(typedValue.getValue(), value.getValue())))
                /*     */ {
                /* 538 */
                return null;
                /*     */
            }
            /* 540 */
            if (typedValue == null) {
                /* 541 */
                DbgVariablesView.logger.i("Illegal value for IDebuggerVariable '%s'", new Object[]{newValue});
                /*     */
            }
            /* 543 */
            return typedValue;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 549 */
        switch (req.getOperation()) {
            /*     */
            case COPY:
                /* 551 */
                return getSelectedNode() != null;
            /*     */
        }
        /* 553 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 559 */
        switch (req.getOperation()) {
            /*     */
            case COPY:
                /* 561 */
                Object elt = getSelectedNode();
                /* 562 */
                if ((elt instanceof IDebuggerVariable)) {
                    /* 563 */
                    return copyValueToClipboard(((IDebuggerVariable) elt).getTypedValue());
                    /*     */
                }
                /* 565 */
                if ((elt instanceof ITypedValue)) {
                    /* 566 */
                    return copyValueToClipboard((ITypedValue) elt);
                    /*     */
                }
                /* 568 */
                return false;
            /*     */
        }
        /*     */
        /* 571 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 577 */
        return Strings.encodeUTF8(this.viewer.exportToString());
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 582 */
        return AbstractUnitFragment.FragmentType.TREE;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgVariablesView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */