/*      */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*      */
/*      */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*      */ import com.pnfsoftware.jeb.core.actions.ActionContext;
/*      */ import com.pnfsoftware.jeb.core.events.J;
/*      */ import com.pnfsoftware.jeb.core.exceptions.JebRuntimeException;
/*      */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*      */ import com.pnfsoftware.jeb.core.output.IItem;
/*      */ import com.pnfsoftware.jeb.core.output.tree.CodeNodeUtil;
/*      */ import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
/*      */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*      */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*      */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeClass;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeField;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeHierarchy;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodePackage;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeType;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*      */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*      */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
/*      */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
/*      */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*      */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*      */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*      */ import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
/*      */ import com.pnfsoftware.jeb.rcpclient.AllHandlers;
/*      */ import com.pnfsoftware.jeb.rcpclient.AssetManagerOverlay;
/*      */ import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*      */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*      */ import com.pnfsoftware.jeb.rcpclient.RcpErrorHandler;
/*      */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*      */ import com.pnfsoftware.jeb.rcpclient.actions.ActionUIContext;
/*      */ import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.filter.AbstractFilteredFilter;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractArrayGroupFilteredTreeContentProvider;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredViewerComparator;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IDndProvider;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.ArrayLogicalGroup;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.IArrayGroup;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.tree.TreeUtil;
/*      */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*      */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.impl.SimpleCodeNode;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.impl.SimpleCodePackage;
/*      */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*      */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*      */ import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
/*      */ import com.pnfsoftware.jeb.util.events.IEvent;
/*      */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*      */ import com.pnfsoftware.jeb.util.format.Strings;
/*      */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*      */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.ConcurrentModificationException;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.eclipse.jface.action.IMenuManager;
/*      */ import org.eclipse.jface.viewers.ISelection;
/*      */ import org.eclipse.jface.viewers.ISelectionChangedListener;
/*      */ import org.eclipse.jface.viewers.SelectionChangedEvent;
/*      */ import org.eclipse.jface.viewers.StructuredSelection;
/*      */ import org.eclipse.jface.viewers.StyledCellLabelProvider;
/*      */ import org.eclipse.jface.viewers.TreePath;
/*      */ import org.eclipse.jface.viewers.TreeSelection;
/*      */ import org.eclipse.jface.viewers.TreeViewer;
/*      */ import org.eclipse.jface.viewers.Viewer;
/*      */ import org.eclipse.jface.viewers.ViewerCell;
/*      */ import org.eclipse.jface.viewers.ViewerFilter;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.layout.FillLayout;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Tree;
/*      */ import org.eclipse.swt.widgets.TreeColumn;
/*      */ import org.eclipse.swt.widgets.TreeItem;

/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */ public class CodeHierarchyView
        /*      */ extends AbstractUnitFragment<ICodeUnit>
        /*      */ {
    /*  100 */   private static final ILogger logger = GlobalLog.getLogger(CodeHierarchyView.class);
    /*      */
    /*      */   private boolean extraDetails;
    /*      */
    /*      */   private Tree tree;
    /*      */
    /*      */   private TreeViewer viewer;
    /*      */   private ViewerRefresher refresher;
    /*      */   private PatternTreeView pt;
    /*      */   private ContentProvider contentProvider;
    /*      */   private LabelProvider labelProvider;
    /*      */   private boolean autoRefreshDisabled;

    /*      */
    /*      */
    public CodeHierarchyView(Composite parent, int flags, RcpClientContext context, ICodeUnit unit, ICodeNode baseNode, int includedFlags, int excludedFlags, boolean disableInitialExpansion)
    /*      */ {
        /*  115 */
        super(parent, flags, unit, null, context);
        /*  116 */
        setLayout(new FillLayout());
        /*      */
        /*  118 */
        if (baseNode == null) {
            /*  119 */
            ICodeHierarchy hier = unit.getHierarchy();
            /*  120 */
            if (hier != null) {
                /*  121 */
                baseNode = hier.getRoot();
                /*      */
            }
            /*      */
        }
        /*      */
        /*  125 */
        this.extraDetails = (unit instanceof INativeCodeUnit);
        /*      */
        /*  127 */
        int style = 0;
        /*  128 */
        String[] columnLabels = null;
        /*  129 */
        int[] columnWidths = null;
        /*  130 */
        ProcessorType procType = null;
        /*  131 */
        if (this.extraDetails) {
            /*  132 */
            style = 65536;
            /*  133 */
            procType = ((INativeCodeUnit) unit).getProcessor().getType();
            /*  134 */
            if (procType == ProcessorType.ARM) {
                /*  135 */
                columnLabels = new String[]{"Name", "Address", "Size", "Mode"};
                /*  136 */
                columnWidths = new int[]{150, 100, 60, 60};
                /*      */
            }
            /*      */
            else {
                /*  139 */
                columnLabels = new String[]{"Name", "Address", "Size"};
                /*  140 */
                columnWidths = new int[]{150, 100, 60};
                /*      */
            }
            /*      */
        }
        /*      */
        /*  144 */
        this.contentProvider = new ContentProvider(unit);
        /*  145 */
        this.contentProvider.setFlags(includedFlags, excludedFlags);
        /*      */
        /*      */
        /*  148 */
        this.labelProvider = new LabelProvider(this.contentProvider, procType);
        /*  149 */
        IPatternMatcher patternMatcher = new SimplePatternMatcher(this.labelProvider);
        /*  150 */
        this.pt = new PatternTreeView(this, style, columnLabels, columnWidths, patternMatcher, true);
        /*  151 */
        FilteredTreeViewer ftv = this.pt.getTreeViewer();
        /*  152 */
        ftv.addFilteredTextListener(new Listener()
                /*      */ {
            /*      */
            public void handleEvent(Event event) {
                /*  155 */
                if (((event.data instanceof Integer)) &&
                        /*  156 */           (((Integer) event.data).intValue() == -1))
                    /*      */ {
                    /*  158 */
                    String msg = "Too many results: The tree was not fully expanded.";
                    /*  159 */
                    CodeHierarchyView.logger.warn(msg, new Object[0]);
                    /*  160 */
                    UI.infoOptional(CodeHierarchyView.this.getViewer().getTree().getShell(), null, msg, "dlgCodeHierFilterTooManyResults");
                    /*      */
                }
                /*      */
                /*      */
            }
            /*      */
            /*      */
            /*  166 */
        });
        /*  167 */
        this.tree = this.pt.getTree();
        /*  168 */
        if (this.extraDetails) {
            /*  169 */
            this.tree.setHeaderVisible(true);
            /*  170 */
            this.tree.setLinesVisible(true);
            /*      */
        }
        /*  172 */
        setPrimaryWidget(this.tree);
        /*      */
        /*  174 */
        this.viewer = ((TreeViewer) ftv.getViewer());
        /*      */
        /*      */
        /*  177 */
        if (context != null) {
            /*  178 */
            this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
                    /*      */ {
                /*      */
                public void selectionChanged(SelectionChangedEvent event)
                /*      */ {
                    /*  182 */
                    String text = String.format("%s", new Object[]{CodeHierarchyView.this.getActiveAddress()});
                    /*  183 */
                    CodeHierarchyView.this.context.getStatusIndicator().setText(text);
                    /*      */
                }
                /*      */
            });
            /*      */
        }
        /*      */
        /*  188 */
        ftv.addDragnDropSupport(new DndProvider());
        /*  189 */
        ftv.setContentProvider(this.contentProvider);
        /*  190 */
        ftv.setLabelProvider(this.labelProvider);
        /*      */
        /*  192 */
        if (baseNode == null) {
            /*  193 */
            return;
            /*      */
        }
        /*      */
        /*  196 */
        this.refresher = new ViewerRefresher(parent.getDisplay(), this.viewer);
        /*      */
        /*  198 */
        ftv.setInput(new ICodeNode[]{baseNode});
        /*      */
        /*      */
        /*  201 */
        this.viewer.setExpandedState(baseNode, true);
        /*  202 */
        if (!disableInitialExpansion) {
            /*  203 */
            int expandedCount = 0;
            /*  204 */
            List<ICodeNode> currentNodes = new ArrayList(baseNode.getChildren());
            /*      */
            for (; ; ) {
                /*  206 */
                List<ICodeNode> nextNodes = new ArrayList();
                /*  207 */
                for (ICodeNode node : currentNodes) {
                    /*  208 */
                    int expansion = node.getInitialExpansion();
                    /*  209 */
                    if (expansion >= 1) {
                        /*  210 */
                        this.viewer.setExpandedState(node, true);
                        /*  211 */
                        if (this.viewer.getExpandedState(node)) {
                            /*  212 */
                            nextNodes.addAll(node.getChildren());
                            /*  213 */
                            if (expandedCount++ >= 100) {
                                /*  214 */
                                nextNodes.clear();
                                /*  215 */
                                break;
                                /*      */
                            }
                            /*      */
                        }
                        /*      */
                    }
                    /*      */
                }
                /*  220 */
                if (nextNodes.isEmpty()) {
                    /*      */
                    break;
                    /*      */
                }
                /*  223 */
                currentNodes = nextNodes;
                /*      */
            }
            /*      */
            /*  226 */
            this.viewer.refresh();
            /*      */
        }
        /*      */
        /*      */
        /*  230 */
        new ContextMenu(this.viewer.getControl()).addContextMenu(new IContextMenu()
                /*      */ {
            /*      */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  233 */
                AllHandlers.getInstance().fillManager(menuMgr, 5);
                /*      */
            }
            /*      */
        });
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void setAutoRefreshDisabled(boolean autoRefreshDisabled)
    /*      */ {
        /*  244 */
        this.autoRefreshDisabled = autoRefreshDisabled;
        /*      */
    }

    /*      */
    /*      */
    public boolean isAutoRefreshDisabled() {
        /*  248 */
        return this.autoRefreshDisabled;
        /*      */
    }

    /*      */
    /*      */
    public void setHideTypes(boolean hideTypes) {
        /*  252 */
        if (hideTypes) {
            /*  253 */
            this.contentProvider.setFlags(-1, 65536);
            /*      */
        }
        /*      */
        else {
            /*  256 */
            this.contentProvider.setFlags(-1, 0);
            /*      */
        }
        /*  258 */
        this.viewer.refresh();
        /*      */
    }

    /*      */
    /*      */
    public boolean getHideTypes() {
        /*  262 */
        int[] flags = this.contentProvider.getFlags();
        /*  263 */
        return (flags[1] & 0x10000) != 0;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    public void setShowAll(boolean showAll)
    /*      */ {
        /*  270 */
        if (showAll) {
            /*  271 */
            this.contentProvider.setFlags(0, 0);
            /*      */
        }
        /*      */
        else {
            /*  274 */
            this.contentProvider.setFlags(256, 65536);
            /*      */
        }
        /*  276 */
        this.viewer.refresh();
        /*      */
    }

    /*      */
    /*      */
    public boolean getShowAll() {
        /*  280 */
        int[] flags = this.contentProvider.getFlags();
        /*  281 */
        return (flags[0] == 0) && (flags[1] == 0);
        /*      */
    }

    /*      */
    /*      */
    public TreeViewer getViewer() {
        /*  285 */
        return this.viewer;
        /*      */
    }

    /*      */
    /*      */
    private int getBucketLimit() {
        /*  289 */
        return
                /*  290 */       this.context.getPropertyManager().getInteger(this.extraDetails ? ".ui.tree.BucketFlatThreshold" : ".ui.tree.BucketTreeThreshold");
        /*      */
    }

    /*      */
    /*      */
    private int getBucketMaxElements() {
        /*  294 */
        return
                /*  295 */       this.context.getPropertyManager().getInteger(this.extraDetails ? ".ui.tree.BucketFlatMaxElements" : ".ui.tree.BucketTreeMaxElements");
        /*      */
    }

    /*      */
    /*      */
    private boolean usesExplicitDefaultPackage() {
        /*  299 */
        return this.context.getPropertyManager().getBoolean(".ui.tree.UseExplicitDefaultPackage");
        /*      */
    }

    /*      */
    /*      */
    /*      */   class ContentProvider
            /*      */ extends AbstractArrayGroupFilteredTreeContentProvider
            /*      */ {
        /*      */     private ICodeUnit codeunit;
        /*      */
        /*      */     private ICodeNode root;
        /*      */
        /*      */     private int includedFlags;
        /*      */     private int excludedFlags;
        /*      */     private IEventListener eventListener;
        /*      */     private SimpleCodeNode defaultPackage;

        /*      */
        /*      */
        public ContentProvider(ICodeUnit unit)
        /*      */ {
            /*  317 */
            super(CodeHierarchyView.this.getBucketMaxElements(), 20);
            /*      */
            /*  319 */
            this.codeunit = unit;
            /*      */
        }

        /*      */
        /*      */
        /*      */
        public void inputChanged(Viewer v, Object oldInput, Object newInput)
        /*      */ {
            /*  325 */
            if (this.eventListener != null) {
                /*  326 */
                this.codeunit.removeListener(this.eventListener);
                /*  327 */
                this.eventListener = null;
                /*      */
            }
            /*      */
            /*  330 */
            if (newInput == null) {
                /*  331 */
                this.root = null;
                /*  332 */
                return;
                /*      */
            }
            /*      */
            /*  335 */
            this.root = ((ICodeNode[]) (ICodeNode[]) newInput)[0];
            /*  336 */
            this.eventListener = new IEventListener()
                    /*      */ {
                /*      */
                public void onEvent(IEvent e)
                /*      */ {
                    /*  340 */
                    if ((J.isUnitEvent(e)) &&
                            /*  341 */             (!CodeHierarchyView.this.autoRefreshDisabled)) {
                        /*  342 */
                        CodeHierarchyView.this.refresher.request();
                        /*      */
                    }
                    /*      */
                    /*      */
                }
                /*  346 */
            };
            /*  347 */
            ((ICodeUnit) CodeHierarchyView.this.unit).addListener(this.eventListener);
            /*      */
        }

        /*      */
        /*      */
        public void dispose()
        /*      */ {
            /*  352 */
            if (this.eventListener != null) {
                /*  353 */
                this.codeunit.removeListener(this.eventListener);
                /*  354 */
                this.eventListener = null;
                /*      */
            }
            /*      */
        }

        /*      */
        /*      */
        public ICodeUnit getUnit() {
            /*  359 */
            return this.codeunit;
            /*      */
        }

        /*      */
        /*      */
        public ICodeNode getRoot()
        /*      */ {
            /*  364 */
            return this.root;
            /*      */
        }

        /*      */
        /*      */
        public void setFlags(int includedFlags, int excludedFlags) {
            /*  368 */
            if (includedFlags >= 0) {
                /*  369 */
                this.includedFlags = includedFlags;
                /*      */
            }
            /*  371 */
            if (excludedFlags >= 0) {
                /*  372 */
                this.excludedFlags = excludedFlags;
                /*      */
            }
            /*      */
        }

        /*      */
        /*      */
        public int[] getFlags() {
            /*  377 */
            return new int[]{this.includedFlags, this.excludedFlags};
            /*      */
        }

        /*      */
        /*      */
        /*      */
        /*      */
        public Object[] getElements(Object input)
        /*      */ {
            /*  384 */
            if (this.root == null) {
                /*  385 */
                return new ICodeNode[0];
                /*      */
            }
            /*  387 */
            if (this.root.getLabel() != null) {
                /*  388 */
                return new ICodeNode[]{this.root};
                /*      */
            }
            /*      */
            /*  391 */
            return getChildren(this.root);
            /*      */
        }

        /*      */
        /*      */
        /*      */
        public List<?> getChildren2(Object element)
        /*      */ {
            /*  397 */
            List<? extends ICodeNode> li = new ArrayList();
            /*  398 */
            if ((element instanceof ICodeNode)) {
                /*  399 */
                if ((CodeHierarchyView.this.usesExplicitDefaultPackage()) && (!CodeHierarchyView.this.extraDetails) && (element == this.root))
                    /*      */ {
                    /*  401 */
                    li = CodeNodeUtil.getChildren((ICodeNode) element, 32768, this.excludedFlags);
                    /*  402 */
                    List<ICodeNode> li2 = new ArrayList(li);
                    /*  403 */
                    if (this.defaultPackage == null)
                        /*      */ {
                        /*  405 */
                        this.defaultPackage = new SimpleCodeNode(new SimpleCodePackage("(default package)", ""), this.root, CodeNodeUtil.getChildren((ICodeNode) element, this.includedFlags, 32768), true);
                        /*      */
                    }
                    /*      */
                    /*  408 */
                    li2.add(this.defaultPackage);
                    /*  409 */
                    li = li2;
                    /*      */
                }
                /*      */
                else {
                    /*  412 */
                    li = CodeNodeUtil.getChildren((ICodeNode) element, this.includedFlags, this.excludedFlags);
                    /*      */
                }
                /*      */
            }
            /*      */
            /*  416 */
            li = filterOutEmptyPackages(li);
            /*      */
            /*  418 */
            List<CodeNodeWrapper> liw = CodeNodeWrapper.wrapNodes(li);
            /*  419 */
            Collections.sort(liw);
            /*  420 */
            li = CodeNodeWrapper.unwrapNodes(liw);
            /*      */
            /*  422 */
            if (li.size() > getLimit()) {
                /*  423 */
                AbstractFilteredFilter filter = CodeHierarchyView.this.getAbstractFilter();
                /*  424 */
                if ((filter != null) && (filter.isFiltered()))
                    /*      */ {
                    /*      */
                    /*  427 */
                    List<ICodeNode> li2 = new ArrayList();
                    /*  428 */
                    for (ICodeNode o : li) {
                        /*  429 */
                        if ((filter.isElementMatch(o)) || (isChildMatch(filter, o))) {
                            /*  430 */
                            li2.add(o);
                            /*      */
                        }
                        /*      */
                    }
                    /*  433 */
                    return li2;
                    /*      */
                }
                /*      */
            }
            /*  436 */
            return li;
            /*      */
        }

        /*      */
        /*      */
        private boolean isChildMatch(AbstractFilteredFilter filter, ICodeNode o) {
            /*  440 */
            List<? extends ICodeNode> children = o.getChildren();
            /*  441 */
            if ((children == null) || (children.size() == 0)) {
                /*  442 */
                return false;
                /*      */
            }
            /*  444 */
            for (ICodeNode c : children) {
                /*  445 */
                if (filter.isElementMatch(c)) {
                    /*  446 */
                    return true;
                    /*      */
                }
                /*  448 */
                boolean match = isChildMatch(filter, c);
                /*  449 */
                if (match) {
                    /*  450 */
                    return true;
                    /*      */
                }
                /*      */
            }
            /*  453 */
            return false;
            /*      */
        }

        /*      */
        /*      */
        public Object getParent(Object element)
        /*      */ {
            /*  458 */
            if ((element instanceof ICodeNode)) {
                /*  459 */
                return ((ICodeNode) element).getParent();
                /*      */
            }
            /*  461 */
            return null;
            /*      */
        }

        /*      */
        /*      */
        public boolean hasChildren2(Object element)
        /*      */ {
            /*  466 */
            if ((element instanceof ICodeNode)) {
                /*  467 */
                return !CodeNodeUtil.getChildren((ICodeNode) element, this.includedFlags, this.excludedFlags).isEmpty();
                /*      */
            }
            /*  469 */
            return false;
            /*      */
        }

        /*      */
        /*      */
        public void sort(Object[] elements)
        /*      */ {
            /*  474 */
            CodeHierarchyView.this.pt.getTreeViewer().getComparator().sort(CodeHierarchyView.this.viewer, elements);
            /*      */
        }

        /*      */
        /*      */     List<ICodeNode> filterOutEmptyPackages(List<? extends ICodeNode> list) {
            /*  478 */
            List<ICodeNode> list2 = new ArrayList();
            /*  479 */
            for (ICodeNode node : list) {
                /*  480 */
                if ((!(node.getObject() instanceof ICodePackage)) || (isNonEmptyPackageNode(node)) ||
                        /*  481 */           (!CodeNodeUtil.cannotBe(node, 16)))
                    /*      */ {
                    /*      */
                    /*      */
                    /*  485 */
                    list2.add(node);
                }
                /*      */
            }
            /*  487 */
            return list2;
            /*      */
        }

        /*      */
        /*      */     boolean isNonEmptyPackageNode(ICodeNode parent) {
            /*  491 */
            for (ICodeNode node : CodeNodeUtil.getChildren(parent, this.includedFlags, this.excludedFlags)) {
                /*  492 */
                if (!(node.getObject() instanceof ICodePackage)) {
                    /*  493 */
                    return true;
                    /*      */
                }
                /*  495 */
                if (isNonEmptyPackageNode(node)) {
                    /*  496 */
                    return true;
                    /*      */
                }
                /*      */
            }
            /*  499 */
            return false;
            /*      */
        }

        /*      */
        /*      */
        public Object[] getRowElements(Object row)
        /*      */ {
            /*  504 */
            if ((row instanceof ICodeNode)) {
                /*  505 */
                return getRowElementsInner((ICodeNode) row);
                /*      */
            }
            /*  507 */
            if ((row instanceof IArrayGroup)) {
                /*  508 */
                Object first = ((IArrayGroup) row).getFirstElement();
                /*  509 */
                Object last = ((IArrayGroup) row).getLastElement();
                /*  510 */
                Object[] firstNode = null;
                /*  511 */
                Object[] lastNode = null;
                /*  512 */
                if ((first instanceof ICodeNode)) {
                    /*  513 */
                    firstNode = getRowElementsInner((ICodeNode) first);
                    /*      */
                }
                /*  515 */
                if ((last instanceof ICodeNode)) {
                    /*  516 */
                    lastNode = getRowElementsInner((ICodeNode) last);
                    /*      */
                }
                /*      */
                /*  519 */
                if (firstNode == null) {
                    /*  520 */
                    return new Object[0];
                    /*      */
                }
                /*  522 */
                if (lastNode == null) {
                    /*  523 */
                    return append(firstNode, new Object[0]);
                    /*      */
                }
                /*      */
                /*  526 */
                return append(firstNode, lastNode);
                /*      */
            }
            /*      */
            /*      */
            /*  530 */
            return new Object[0];
            /*      */
        }

        /*      */
        /*      */
        private Object[] append(Object[] firstNode, Object... lastNode) {
            /*  534 */
            for (int i = 0; i < firstNode.length; i++) {
                /*  535 */
                Object last = null;
                /*  536 */
                if ((lastNode != null) && (i < lastNode.length)) {
                    /*  537 */
                    last = lastNode[i];
                    /*      */
                }
                /*  539 */
                firstNode[i] = {firstNode[i], last};
                /*      */
            }
            /*  541 */
            return firstNode;
            /*      */
        }

        /*      */
        /*      */
        private Object[] getRowElementsInner(ICodeNode node) {
            /*  545 */
            String label = node.getLabel();
            /*  546 */
            if (!CodeHierarchyView.this.extraDetails) {
                /*  547 */
                return new Object[]{label};
                /*      */
            }
            /*      */
            /*      */
            /*  551 */
            Long address = null;
            /*  552 */
            Integer size = null;
            /*  553 */
            Integer mode = null;
            /*  554 */
            ICodeItem item = node.getObject();
            /*  555 */
            if ((item instanceof INativeMethodItem)) {
                /*  556 */
                INativeMethodDataItem data = ((INativeMethodItem) item).getData();
                /*  557 */
                if (data != null) {
                    /*  558 */
                    address = Long.valueOf(data.getMemoryAddress());
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /*  564 */
                    CFG<?> cfg = data.getCFG();
                    /*      */
                    try {
                        /*  566 */
                        size = Integer.valueOf(cfg.getEffectiveSize());
                        /*  567 */
                        mode = Integer.valueOf(cfg.getEntryBlock().get(0).getProcessorMode());
                        /*      */
                    }
                    /*      */ catch (ConcurrentModificationException | IndexOutOfBoundsException e)
                        /*      */ {
                        /*  571 */
                        size = Integer.valueOf(0);
                        /*  572 */
                        mode = Integer.valueOf(0);
                        /*      */
                    }
                    /*      */
                }
                /*      */
            }
            /*  576 */
            return new Object[]{label, address, size, mode};
            /*      */
        }

        /*      */
        /*  579 */     private final SeparatorRule underscore = new SeparatorRule("_", "_*", false);
        /*  580 */     private final SeparatorRule cpp_separator = new SeparatorRule("::", "", true, new String[]{"("}, new String[]{"<"});

        /*      */
        /*      */     class SeparatorRule
                /*      */ {
            /*      */ String separator;
            /*      */ String suffix;
            /*  586 */ boolean packageSeparator = false;
            /*  587 */ String[] blacklistChars = null;
            /*  588 */ String[] endChars = null;

            /*      */
            /*      */
            public SeparatorRule(String separator, String suffix, boolean packageSeparator) {
                /*  591 */
                this(separator, suffix, packageSeparator, new String[0], new String[0]);
                /*      */
            }

            /*      */
            /*      */
            public SeparatorRule(String separator, String suffix, boolean packageSeparator, String[] blacklistChars, String[] endChars)
            /*      */ {
                /*  596 */
                this.separator = separator;
                /*  597 */
                this.suffix = suffix;
                /*  598 */
                this.packageSeparator = packageSeparator;
                /*  599 */
                this.blacklistChars = blacklistChars;
                /*  600 */
                this.endChars = endChars;
                /*      */
            }

            /*      */
            /*      */
            public String format(String startExpression) {
                /*  604 */
                String str = startExpression;
                /*  605 */
                if (startExpression.endsWith(this.separator)) {
                    /*  606 */
                    str = startExpression.substring(0, startExpression.length() - this.separator.length());
                    /*      */
                }
                /*      */
                else {
                    /*  609 */
                    for (String end : this.endChars) {
                        /*  610 */
                        if (startExpression.endsWith(end)) {
                            /*  611 */
                            str = startExpression.substring(0, startExpression.length() - end.length());
                            /*  612 */
                            break;
                            /*      */
                        }
                        /*      */
                    }
                    /*      */
                }
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*  623 */
                return str + this.suffix;
                /*      */
            }

            /*      */
            /*      */
            public String getStartExpression(String label) {
                /*  627 */
                return getStartExpression(label, 1);
                /*      */
            }

            /*      */
            /*      */
            public String getStartExpression(String label, int from) {
                /*  631 */
                int idx = label.indexOf(this.separator, from);
                /*  632 */
                if (idx > 0) {
                    /*  633 */
                    String candidate = label.substring(0, idx + this.separator.length());
                    /*  634 */
                    if (Strings.contains(candidate, this.blacklistChars)) {
                        /*  635 */
                        return null;
                        /*      */
                    }
                    /*  637 */
                    for (String end : this.endChars) {
                        /*  638 */
                        idx = candidate.indexOf(end);
                        /*  639 */
                        if (idx + end.length() == from) {
                            /*  640 */
                            return null;
                            /*      */
                        }
                        /*  642 */
                        if (idx > 0) {
                            /*  643 */
                            candidate = candidate.substring(0, idx + end.length());
                            /*      */
                        }
                        /*      */
                    }
                    /*  646 */
                    return candidate;
                    /*      */
                }
                /*  648 */
                return null;
                /*      */
            }

            /*      */
            /*      */
            /*  652 */
            public String getStartExpression(String label, String fromStartExpression) {
                return getStartExpression(label, fromStartExpression.length());
            }
        }

        /*      */
        /*      */     class LabelRule {
            private static final String prefix = "group_";

            /*      */
            /*      */       LabelRule() {
            }

            /*      */
            /*  658 */       private int groupNumber = 1;
            /*  659 */ Map<String, List<ArrayLogicalGroup>> groupByName = new HashMap();

            /*      */
            /*      */
            public String generateGroupName() {
                /*  662 */
                String groupName = "group_" + this.groupNumber;
                /*  663 */
                this.groupNumber += 1;
                /*  664 */
                return groupName;
                /*      */
            }

            /*      */
            /*      */
            public void setOriginalLabels() {
                /*  668 */
                for (Map.Entry<String, List<ArrayLogicalGroup>> e : this.groupByName.entrySet()) {
                    /*  669 */
                    if (((List) e.getValue()).size() == 1) {
                        /*  670 */
                        ((ArrayLogicalGroup) ((List) e.getValue()).get(0)).setGroupName(withNumberItemsSuffix((String) e.getKey(), ((ArrayLogicalGroup) ((List) e.getValue()).get(0)).size()));
                        /*      */
                    }
                    /*      */
                }
                /*      */
            }

            /*      */
            /*      */
            private void saveValidLabel(ArrayLogicalGroup logicalGroup, String label) {
                /*  676 */
                List<ArrayLogicalGroup> gr = (List) this.groupByName.get(label);
                /*  677 */
                if (gr == null) {
                    /*  678 */
                    gr = new ArrayList();
                    /*  679 */
                    this.groupByName.put(label, gr);
                    /*      */
                }
                /*  681 */
                gr.add(logicalGroup);
                /*      */
            }

            /*      */
            /*      */
            private String withNumberItemsSuffix(String groupName, int items) {
                /*  685 */
                StringBuilder stb = new StringBuilder(groupName);
                /*  686 */
                stb.append(" (").append(items).append(" items)");
                /*  687 */
                return stb.toString();
                /*      */
            }

            /*      */
            /*      */       String withBoundSuffix(String groupName, int from, ICodeNode firstNode, ICodeNode lastNode) {
                /*  691 */
                StringBuilder label = new StringBuilder(groupName);
                /*  692 */
                label.append(" (").append(firstNode.getLabel().substring(from)).append(" .. ");
                /*  693 */
                label.append(lastNode.getLabel().substring(from)).append(")");
                /*  694 */
                return label.toString();
                /*      */
            }
            /*      */
        }

        /*      */
        /*      */
        public Map<Integer, ArrayLogicalGroup> getLogicalGroups(List<?> r, Object parentElement)
        /*      */ {
            /*  700 */
            Map<Integer, ArrayLogicalGroup> map = super.getLogicalGroups(r, parentElement);
            /*  701 */
            if (CodeHierarchyView.this.extraDetails)
                /*      */ {
                /*      */
                /*  704 */
                boolean packageSeparatorGroup = (CodeHierarchyView.this.viewer.getTree().getSortColumn() != null) && (CodeHierarchyView.this.viewer.getTree().getSortColumn().getText().equals("Name"));
                /*  705 */
                if (!packageSeparatorGroup) {
                    /*  706 */
                    return map;
                    /*      */
                }
                /*      */
            }
            /*  709 */
            LabelRule labelRule = new LabelRule();
            /*      */
            /*  711 */
            List<SeparatorRule> separators = CodeHierarchyView.this.extraDetails ? Arrays.asList(new SeparatorRule[]{this.cpp_separator, this.underscore}) : Arrays.asList(new SeparatorRule[]{this.underscore});
            /*  712 */
            String startExpression = null;
            /*  713 */
            SeparatorRule rule = null;
            /*  714 */
            int size = 0;
            /*  715 */
            for (int i = 0; i < r.size(); i++) {
                /*  716 */
                Object o = r.get(i);
                /*  717 */
                String label;
                if ((o instanceof ICodeNode)) {
                    /*  718 */
                    label = ((ICodeNode) o).getLabel();
                    /*  719 */
                    if (startExpression != null) {
                        /*  720 */
                        if (label.startsWith(startExpression)) {
                            /*  721 */
                            for (SeparatorRule separator : separators) {
                                /*  722 */
                                if (separator == rule) {
                                    /*      */
                                    break;
                                    /*      */
                                }
                                /*      */
                                /*  726 */
                                String testStartExpression = separator.getStartExpression(label);
                                /*  727 */
                                if (testStartExpression != null)
                                    /*      */ {
                                    /*  729 */
                                    addLogicalGroup(map, r, parentElement, i - size, size, startExpression, rule, labelRule);
                                    /*      */
                                    /*      */
                                    /*  732 */
                                    startExpression = testStartExpression;
                                    /*  733 */
                                    rule = separator;
                                    /*  734 */
                                    size = 0;
                                    /*  735 */
                                    break;
                                    /*      */
                                }
                                /*      */
                            }
                            /*  738 */
                            size++;
                            /*      */
                        }
                        /*      */
                        else
                            /*      */ {
                            /*  742 */
                            addLogicalGroup(map, r, parentElement, i - size, size, startExpression, rule, labelRule);
                            /*  743 */
                            startExpression = null;
                            /*      */
                        }
                        /*      */
                    }
                    /*      */
                    else {
                        /*  747 */
                        for (SeparatorRule separator : separators) {
                            /*  748 */
                            startExpression = separator.getStartExpression(label);
                            /*  749 */
                            if (startExpression != null) {
                                /*  750 */
                                rule = separator;
                                /*  751 */
                                size = 1;
                                /*  752 */
                                break;
                                /*      */
                            }
                            /*      */
                        }
                        /*      */
                    }
                    /*      */
                }
                /*  757 */
                else if (startExpression != null) {
                    /*  758 */
                    addLogicalGroup(map, r, parentElement, i - size, size, startExpression, rule, labelRule);
                    /*  759 */
                    startExpression = null;
                    /*      */
                }
                /*      */
            }
            /*      */
            /*  763 */
            if (startExpression != null) {
                /*  764 */
                addLogicalGroup(map, r, parentElement, r.size() - size, size, startExpression, rule, labelRule);
                /*      */
            }
            /*  766 */
            labelRule.setOriginalLabels();
            /*  767 */
            return map;
            /*      */
        }

        /*      */
        /*      */
        private void addLogicalGroup(Map<Integer, ArrayLogicalGroup> map, List<?> r, Object parentElement, int index, int size, String startExpression, SeparatorRule rule, LabelRule labelRule)
        /*      */ {
            /*  772 */
            if (size >= 10) {
                /*  773 */
                if (rule.packageSeparator) {
                    /*  774 */
                    ArrayLogicalGroup group = addPackagedGroup(r, parentElement, index, size, startExpression, rule, labelRule, 1);
                    /*      */
                    /*  776 */
                    map.put(Integer.valueOf(index), group);
                    /*  777 */
                    return;
                    /*      */
                }
                /*      */
                /*      */
                /*  781 */
                if (size > getGroupLimit())
                    /*      */ {
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /*  788 */
                    int i = 1;
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /*  793 */
                    while (size > 0) {
                        /*  794 */
                        int realSize = Math.min(size, getGroupLimit());
                        /*      */
                        /*      */
                        /*      */
                        /*      */
                        /*  799 */
                        String groupName = rule.format(startExpression) + " group_" + i;
                        /*  800 */
                        String label = labelRule.withNumberItemsSuffix(groupName, realSize);
                        /*  801 */
                        ArrayLogicalGroup group = buildLogicalGroup(r, parentElement, index, realSize, label);
                        /*  802 */
                        map.put(Integer.valueOf(index), group);
                        /*  803 */
                        index += realSize;
                        /*  804 */
                        size -= realSize;
                        /*  805 */
                        i++;
                        /*      */
                    }
                    /*      */
                }
                /*      */
                else {
                    /*  809 */
                    ArrayLogicalGroup logicalGroup = buildLogicalGroup(r, parentElement, index, size, labelRule
/*  810 */.generateGroupName());
                    /*  811 */
                    map.put(Integer.valueOf(index), logicalGroup);
                    /*  812 */
                    labelRule.saveValidLabel(logicalGroup, rule.format(startExpression));
                    /*      */
                }
                /*      */
            }
            /*      */
        }

        /*      */
        /*      */
        private ArrayLogicalGroup buildLogicalGroup(List<?> r, Object parentElement, int firstIndex, int size, String groupName)
        /*      */ {
            /*  819 */
            ArrayLogicalGroup group = getArrayLogicalGroup(parentElement, firstIndex, groupName, false, 0);
            /*  820 */
            for (int i = 0; i < size; i++) {
                /*  821 */
                group.add(r.get(firstIndex + i));
                /*      */
            }
            /*  823 */
            return group;
            /*      */
        }

        /*      */
        /*      */
        /*      */
        private ArrayLogicalGroup addPackagedGroup(List<?> r, Object parentElement, int index, int size, String startExpression, SeparatorRule rule, LabelRule labelRule, int toplevel)
        /*      */ {
            /*  829 */
            ArrayLogicalGroup group = getArrayLogicalGroup(parentElement, index, rule.format(startExpression), true, toplevel);
            /*      */
            /*  831 */
            for (int i = 0; i < size; i++) {
                /*  832 */
                Object o = r.get(index + i);
                /*  833 */
                String label = ((ICodeNode) o).getLabel();
                /*  834 */
                if (!label.startsWith(startExpression)) break;
                /*  835 */
                String newStartExpression = rule.getStartExpression(label, startExpression);
                /*  836 */
                if (newStartExpression != null) {
                    /*  837 */
                    ArrayLogicalGroup subgroup = addPackagedGroup(r, parentElement, index + i, size - i, newStartExpression, rule, labelRule, toplevel + 1);
                    /*      */
                    /*  839 */
                    if (subgroup.size() < 10)
                        /*      */ {
                        /*  841 */
                        i += subgroup.size() - 1;
                        /*  842 */
                        for (Object sub : subgroup.getChildren()) {
                            /*  843 */
                            group.add(sub);
                            /*      */
                        }
                        /*      */
                    }
                    /*      */
                    else
                        /*      */ {
                        /*  848 */
                        i += subgroup.size() - 1;
                        /*  849 */
                        group.add(subgroup);
                        /*      */
                    }
                    /*      */
                }
                /*      */
                else
                    /*      */ {
                    /*  854 */
                    group.add(getVirtualElement(o, index + i, label.substring(startExpression.length())));
                    /*      */
                }
                /*      */
            }
            /*      */
            /*      */
            /*      */
            /*      */
            /*  861 */
            group.setGroupName(labelRule.withNumberItemsSuffix(group.getGroupName(), group.size()));
            /*  862 */
            return group;
            /*      */
        }

        /*      */
        /*      */
        public void onFirstOptimization(List<?> r)
        /*      */ {
            /*  867 */
            super.onFirstOptimization(r);
            /*  868 */
            String msg = "Your artifact has too many chidren. They were divided into group nodes.";
            /*  869 */
            CodeHierarchyView.logger.warn(msg, new Object[0]);
            /*  870 */
            UI.infoOptional(CodeHierarchyView.this.getViewer().getTree().getShell(), null, msg, "dlgCodeHierFirstGroup");
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */   class DndProvider implements IDndProvider {
        /*      */     DndProvider() {
        }

        /*      */
        /*  877 */
        public boolean canDrag(Object data) {
            if (((data instanceof IArrayGroup)) &&
                    /*  878 */         (((IArrayGroup) data).isSingle())) {
                /*  879 */
                data = ((IArrayGroup) data).getFirstElement();
                /*      */
            }
            /*      */
            /*  882 */
            if ((data instanceof ICodeNode)) {
                /*  883 */
                Object item = ((ICodeNode) data).getObject();
                /*  884 */
                if ((item instanceof ICodePackage)) {
                    /*  885 */
                    return true;
                    /*      */
                }
                /*  887 */
                if ((item instanceof ICodeType)) {
                    /*  888 */
                    return true;
                    /*      */
                }
                /*  890 */
                if ((item instanceof ICodeClass)) {
                    /*  891 */
                    return true;
                    /*      */
                }
                /*  893 */
                if ((item instanceof ICodeMethod)) {
                    /*  894 */
                    return CodeHierarchyView.this.extraDetails;
                    /*      */
                }
                /*  896 */
                if ((item instanceof ICodeField)) {
                    /*  897 */
                    return false;
                    /*      */
                }
                /*  899 */
                CodeHierarchyView.logger.i("data %s", new Object[]{item});
                /*      */
            }
            /*      */
            /*      */
            /*      */
            /*  904 */
            return false;
            /*      */
        }

        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        public boolean canDrop(String source, Object target, int location)
        /*      */ {
            /*  912 */
            if (source.startsWith("p;")) {
                /*  913 */
                return ((target instanceof ICodeNode)) && ((((ICodeNode) target).getObject() instanceof ICodePackage));
                /*      */
            }
            /*  915 */
            return getPackageFor(target) != null;
            /*      */
        }

        /*      */
        /*      */
        private ICodePackage getPackageFor(Object target) {
            /*  919 */
            if ((target instanceof ICodeNode)) {
                /*  920 */
                Object item = ((ICodeNode) target).getObject();
                /*  921 */
                if (!(item instanceof ICodePackage))
                    /*      */ {
                    /*  923 */
                    if ((item instanceof ICodeType)) {
                        /*  924 */
                        item = ((ICodeNode) target).getParent().getObject();
                        /*      */
                    }
                    /*  926 */
                    else if ((item instanceof ICodeClass)) {
                        /*  927 */
                        item = ((ICodeNode) target).getParent().getObject();
                        /*      */
                    }
                    /*  929 */
                    else if ((CodeHierarchyView.this.extraDetails) && ((item instanceof ICodeMethod))) {
                        /*  930 */
                        item = ((ICodeNode) target).getParent().getObject();
                        /*      */
                    }
                    /*  932 */
                    if (!(item instanceof ICodePackage)) {
                        /*  933 */
                        return null;
                        /*      */
                    }
                    /*      */
                }
                /*  936 */
                return (ICodePackage) item;
                /*      */
            }
            /*  938 */
            return null;
            /*      */
        }

        /*      */
        /*      */
        public boolean performDrop(String source, Object target, int location)
        /*      */ {
            /*  943 */
            if ((target instanceof ICodeNode)) {
                /*  944 */
                long id = Long.parseLong(source.substring(2));
                /*  945 */
                ICodePackage item = getPackageFor(target);
                /*  946 */
                if (item == null) {
                    /*  947 */
                    return false;
                    /*      */
                }
                /*  949 */
                String dest = item.getAddress();
                /*  950 */
                if (dest == null) {
                    /*  951 */
                    dest = "";
                    /*      */
                }
                /*  953 */
                CodeHierarchyView.logger.i("Perform drop from %d to %s", new Object[]{Long.valueOf(id), dest});
                /*  954 */
                GraphicalActionExecutor exec = new GraphicalActionExecutor(CodeHierarchyView.this.getShell(), CodeHierarchyView.this.getContext());
                /*  955 */
                ActionContext actionContext = new ActionContext((IInteractiveUnit) CodeHierarchyView.this.unit, 11, id, dest);
                /*  956 */
                ActionUIContext uictx = new ActionUIContext(actionContext, CodeHierarchyView.this);
                /*  957 */
                if (!exec.execute(uictx, dest)) {
                    /*  958 */
                    CodeHierarchyView.logger.error("Can not move to package %s", new Object[]{dest});
                    /*  959 */
                    return false;
                    /*      */
                }
                /*  961 */
                return true;
                /*      */
            }
            /*  963 */
            return false;
            /*      */
        }

        /*      */
        /*      */
        public Object getSelectedElements()
        /*      */ {
            /*  968 */
            return CodeHierarchyView.this.getSelectedElement();
            /*      */
        }

        /*      */
        /*      */
        public String getDragData()
        /*      */ {
            /*  973 */
            Object elt = CodeHierarchyView.this.getSelectedElement();
            /*  974 */
            if (((elt instanceof IArrayGroup)) &&
                    /*  975 */         (((IArrayGroup) elt).isSingle())) {
                /*  976 */
                elt = ((IArrayGroup) elt).getFirstElement();
                /*      */
            }
            /*      */
            /*  979 */
            if ((elt instanceof ICodeNode)) {
                /*  980 */
                Object item = ((ICodeNode) elt).getObject();
                /*  981 */
                String prefix = "g;";
                /*  982 */
                if ((item instanceof ICodePackage)) {
                    /*  983 */
                    prefix = "p;";
                    /*      */
                }
                /*  985 */
                else if ((item instanceof ICodeType)) {
                    /*  986 */
                    prefix = "t;";
                    /*      */
                }
                /*  988 */
                else if ((item instanceof ICodeClass)) {
                    /*  989 */
                    prefix = "c;";
                    /*      */
                }
                /*  991 */
                else if ((item instanceof ICodeMethod)) {
                    /*  992 */
                    prefix = "m;";
                    /*      */
                }
                /*  994 */
                else if ((item instanceof ICodeField)) {
                    /*  995 */
                    prefix = "f;";
                    /*      */
                }
                /*  997 */
                return prefix + Long.toString(((ICodeNode) elt).getItemId());
                /*      */
            }
            /*  999 */
            return null;
            /*      */
        }

        /*      */
        /*      */
        /*      */
        public boolean shouldExpand(String source, Object target)
        /*      */ {
            /* 1005 */
            return ((target instanceof ICodeNode)) && ((((ICodeNode) target).getObject() instanceof ICodePackage));
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */   class LabelProvider extends StyledCellLabelProvider implements IValueProvider {
        /*      */ CodeHierarchyView.ContentProvider cp;
        /*      */ ProcessorType procType;

        /*      */
        /*      */     LabelProvider(CodeHierarchyView.ContentProvider cp, ProcessorType procType) {
            /* 1014 */
            this.cp = cp;
            /* 1015 */
            this.procType = procType;
            /*      */
        }

        /*      */
        /*      */
        /*      */
        public void update(ViewerCell cell)
        /*      */ {
            /* 1021 */
            int index = cell.getColumnIndex();
            /*      */
            /* 1023 */
            String text = "Hierarchy";
            /* 1024 */
            ICodeItem item = null;
            /* 1025 */
            String iconRelPath = null;
            /* 1026 */
            Image img = null;
            /* 1027 */
            AssetManagerOverlay overlay = null;
            /*      */
            /* 1029 */
            Object o = cell.getElement();
            /*      */
            /*      */
            /*      */
            /* 1033 */
            if (((o instanceof IArrayGroup)) &&
                    /* 1034 */         (((IArrayGroup) o).isSingle()))
                /*      */ {
                /*      */
                /*      */
                /*      */
                /* 1039 */
                o = ((IArrayGroup) o).getFirstElement();
                /*      */
            }
            /*      */
            /* 1042 */
            if ((o instanceof ICodeNode)) {
                /* 1043 */
                item = ((ICodeNode) o).getObject();
                /* 1044 */
                text = getCodeNodeStringAt(o, index);
                /*      */
            }
            /* 1046 */
            else if ((o instanceof IArrayGroup)) {
                /* 1047 */
                text = getArrayGroupStringAt((IArrayGroup) o, index);
                /*      */
            }
            /*      */
            /* 1050 */
            if ((index == 0) && (item != null)) {
                /* 1051 */
                int flags = item.getGenericFlags();
                /*      */
                /* 1053 */
                String visi = "default";
                /* 1054 */
                if ((flags & 0x1) != 0) {
                    /* 1055 */
                    visi = "public";
                    /*      */
                }
                /* 1057 */
                else if ((flags & 0x2) != 0) {
                    /* 1058 */
                    visi = "private";
                    /*      */
                }
                /* 1060 */
                else if ((flags & 0x4) != 0) {
                    /* 1061 */
                    visi = "protected";
                    /*      */
                }
                /*      */
                /*      */
                /* 1065 */
                if ((item instanceof ICodePackage)) {
                    /* 1066 */
                    iconRelPath = "eclipse/package_obj.png";
                    /*      */
                }
                /* 1068 */
                else if ((item instanceof ICodeType)) {
                    /* 1069 */
                    iconRelPath = "eclipse/types.png";
                    /*      */
                }
                /* 1071 */
                else if ((item instanceof ICodeClass)) {
                    /* 1072 */
                    if ((flags & 0x200) != 0) {
                        /* 1073 */
                        iconRelPath = "eclipse/int_obj.png";
                        /*      */
                    }
                    /* 1075 */
                    else if ((flags & 0x4000) != 0) {
                        /* 1076 */
                        iconRelPath = "eclipse/enum_obj.png";
                        /*      */
                    }
                    /*      */
                    else {
                        /* 1079 */
                        iconRelPath = "eclipse/class_obj.png";
                        /*      */
                    }
                    /*      */
                }
                /* 1082 */
                else if ((item instanceof ICodeField)) {
                    /* 1083 */
                    iconRelPath = "eclipse/field_" + visi + "_obj.png";
                    /*      */
                }
                /* 1085 */
                else if ((item instanceof ICodeMethod)) {
                    /* 1086 */
                    iconRelPath = "eclipse/method_" + visi + "_obj.png";
                    /*      */
                }
                /*      */
                /*      */
                /* 1090 */
                if ((iconRelPath != null) && (((item instanceof ICodeField)) || ((item instanceof ICodeMethod)))) {
                    /* 1091 */
                    if ((flags & 0x8) != 0) {
                        /* 1092 */
                        if (overlay == null) {
                            /* 1093 */
                            overlay = new AssetManagerOverlay();
                            /*      */
                        }
                        /* 1095 */
                        overlay.addLayer("eclipse/static_co.png", new Point(0, 0));
                        /*      */
                    }
                    /* 1097 */
                    if ((flags & 0x10) != 0) {
                        /* 1098 */
                        if (overlay == null) {
                            /* 1099 */
                            overlay = new AssetManagerOverlay();
                            /*      */
                        }
                        /* 1101 */
                        overlay.addLayer("eclipse/final_co.png", new Point(9, 0));
                        /*      */
                    }
                    /* 1103 */
                    if ((flags & 0x10000) != 0) {
                        /* 1104 */
                        if (overlay == null) {
                            /* 1105 */
                            overlay = new AssetManagerOverlay();
                            /*      */
                        }
                        /* 1107 */
                        overlay.addLayer("eclipse/constr_ovr.png", new Point(0, 0));
                        /*      */
                    }
                    /* 1109 */
                    if ((!(CodeHierarchyView.this.unit instanceof INativeCodeUnit)) && ((flags & 0x100) != 0))
                        /*      */ {
                        /* 1111 */
                        if (overlay == null) {
                            /* 1112 */
                            overlay = new AssetManagerOverlay();
                            /*      */
                        }
                        /* 1114 */
                        overlay.addLayer("eclipse/native_co.png", new Point(0, 0));
                        /*      */
                    }
                    /* 1116 */
                    if ((flags & 0x20) != 0) {
                        /* 1117 */
                        if (overlay == null) {
                            /* 1118 */
                            overlay = new AssetManagerOverlay();
                            /*      */
                        }
                        /* 1120 */
                        overlay.addLayer("eclipse/synch_co.png", new Point(9, 0));
                        /*      */
                    }
                    /* 1122 */
                    if ((flags & 0x400) != 0) {
                        /* 1123 */
                        if (overlay == null) {
                            /* 1124 */
                            overlay = new AssetManagerOverlay();
                            /*      */
                        }
                        /* 1126 */
                        overlay.addLayer("eclipse/abstract_co.png", new Point(0, 0));
                        /*      */
                    }
                    /*      */
                }
                /*      */
            }
            /*      */
            /* 1131 */
            if (text != null) {
                /* 1132 */
                cell.setText(text);
                /*      */
            }
            /*      */
            /* 1135 */
            if ((img == null) && (iconRelPath != null)) {
                /* 1136 */
                img = UIAssetManager.getInstance().getImage(iconRelPath, overlay);
                /*      */
            }
            /* 1138 */
            if (img != null) {
                /* 1139 */
                cell.setImage(img);
                /*      */
            }
            /*      */
            /* 1142 */
            super.update(cell);
            /*      */
        }

        /*      */
        /*      */
        public String getStringAt(Object element, int key)
        /*      */ {
            /* 1147 */
            if ((element instanceof IArrayGroup)) {
                /* 1148 */
                return getArrayGroupStringAt((IArrayGroup) element, key);
                /*      */
            }
            /* 1150 */
            return getCodeNodeStringAt(element, key);
            /*      */
        }

        /*      */
        /*      */
        public String getCodeNodeStringAt(Object element, int key)
        /*      */ {
            /* 1155 */
            Object[] r = this.cp.getRowElements(element);
            /* 1156 */
            if ((r != null) && (key < r.length)) {
                /* 1157 */
                Object o = r[key];
                /* 1158 */
                if (key == 0) {
                    /* 1159 */
                    return (String) o;
                    /*      */
                }
                /* 1161 */
                if (key == 1) {
                    /* 1162 */
                    return formatLongHex(new StringBuilder(), (Long) o).toString();
                    /*      */
                }
                /* 1164 */
                if (key == 2) {
                    /* 1165 */
                    return Integer.toHexString(((Integer) o).intValue()).toUpperCase() + "h";
                    /*      */
                }
                /* 1167 */
                if (key == 3) {
                    /* 1168 */
                    if (o == null) {
                        /* 1169 */
                        return "";
                        /*      */
                    }
                    /* 1171 */
                    if (this.procType == ProcessorType.ARM) {
                        /* 1172 */
                        return ((Integer) o).intValue() == 16 ? "T32" : "A32";
                        /*      */
                    }
                    /* 1174 */
                    return o.toString();
                    /*      */
                }
                /*      */
            }
            /* 1177 */
            return "";
            /*      */
        }

        /*      */
        /*      */
        public String getArrayGroupStringAt(IArrayGroup element, int key) {
            /* 1181 */
            if (((element instanceof ArrayLogicalGroup)) &&
                    /* 1182 */         (key == 0)) {
                /* 1183 */
                return ((ArrayLogicalGroup) element).getGroupName();
                /*      */
            }
            /*      */
            /* 1186 */
            if (element.isSingle()) {
                /* 1187 */
                return getStringAt(element.getFirstElement(), key);
                /*      */
            }
            /*      */
            /*      */
            /*      */
            /* 1192 */
            Object[] r = this.cp.getRowElements(element);
            /* 1193 */
            if ((r != null) && (key < r.length)) {
                /* 1194 */
                Object o = r[key];
                /* 1195 */
                if (key == 0) {
                    /* 1196 */
                    if ((o instanceof Object[])) {
                        /* 1197 */
                        return ((Object[]) (Object[]) o)[0] + " .. " + Strings.safe(((Object[]) (Object[]) o)[1]);
                        /*      */
                    }
                    /*      */
                }
                /* 1200 */
                else if ((key == 1) &&
                        /* 1201 */           ((o instanceof Object[])) && ((CodeHierarchyView.this.viewer.getTree().getSortColumn() == null) ||
                        /* 1202 */           (CodeHierarchyView.this.viewer.getTree().getSortColumn().getText().equals("Address")))) {
                    /* 1203 */
                    StringBuilder stb = new StringBuilder();
                    /* 1204 */
                    formatLongHex(stb, (Long) ((Object[]) (Object[]) o)[0]);
                    /* 1205 */
                    stb.append(" .. ");
                    /* 1206 */
                    formatLongHex(stb, (Long) ((Object[]) (Object[]) o)[1]);
                    /* 1207 */
                    return stb.toString();
                    /*      */
                }
                /*      */
            }
            /*      */
            /* 1211 */
            return "";
            /*      */
        }

        /*      */
        /*      */
        private StringBuilder formatLongHex(StringBuilder stb, Long o) {
            /* 1215 */
            if (o == null) {
                /* 1216 */
                return stb;
                /*      */
            }
            /* 1218 */
            return stb.append(Long.toHexString(o.longValue()).toUpperCase()).append("h");
            /*      */
        }

        /*      */
        /*      */
        public String getString(Object element)
        /*      */ {
            /* 1223 */
            return getStringAt(element, 0);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    private AbstractFilteredFilter getAbstractFilter() {
        /* 1228 */
        ViewerFilter[] filters = this.viewer.getFilters();
        /* 1229 */
        if ((filters != null) && (filters.length > 0) &&
                /* 1230 */       ((filters[0] instanceof AbstractFilteredFilter))) {
            /* 1231 */
            return (AbstractFilteredFilter) filters[0];
            /*      */
        }
        /*      */
        /* 1234 */
        return null;
        /*      */
    }

    /*      */
    /*      */
    public boolean verifyOperation(OperationRequest req)
    /*      */ {
        /* 1239 */
        switch (req.getOperation()) {
            /*      */
            case REFRESH:
                /*      */
            case FIND:
                /* 1242 */
                return true;
            /*      */
        }
        /*      */
        /* 1245 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    public boolean doOperation(OperationRequest req)
    /*      */ {
        /* 1251 */
        switch (req.getOperation()) {
            /*      */
            case REFRESH:
                /* 1253 */
                this.refresher.request();
                /* 1254 */
                return true;
            /*      */
            /*      */
            case FIND:
                /* 1257 */
                this.pt.setFilterVisibility(true);
                /* 1258 */
                return true;
            /*      */
        }
        /*      */
        /* 1261 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    private Object getSelectedElement()
    /*      */ {
        /* 1266 */
        ISelection selection = this.viewer.getSelection();
        /* 1267 */
        if (!(selection instanceof TreeSelection)) {
            /* 1268 */
            return null;
            /*      */
        }
        /* 1270 */
        Object elt = ((TreeSelection) selection).getFirstElement();
        /*      */
        /* 1272 */
        if (((elt instanceof IArrayGroup)) &&
                /* 1273 */       (((IArrayGroup) elt).isSingle())) {
            /* 1274 */
            elt = ((IArrayGroup) elt).getFirstElement();
            /*      */
        }
        /*      */
        /* 1277 */
        return elt;
        /*      */
    }

    /*      */
    /*      */
    public ICodeNode getSelectedNode() {
        /* 1281 */
        Object elt = getSelectedElement();
        /* 1282 */
        if (!(elt instanceof ICodeNode)) {
            /* 1283 */
            return null;
            /*      */
        }
        /*      */
        /* 1286 */
        return (ICodeNode) elt;
        /*      */
    }

    /*      */
    /*      */
    public boolean isActiveItem(IItem item)
    /*      */ {
        /* 1291 */
        return (item != null) && (getActiveItem() == item);
        /*      */
    }

    /*      */
    /*      */
    public IItem getActiveItem()
    /*      */ {
        /* 1296 */
        Object elt = getSelectedElement();
        /* 1297 */
        if (!(elt instanceof IItem)) {
            /* 1298 */
            return null;
            /*      */
        }
        /*      */
        /* 1301 */
        return (IItem) elt;
        /*      */
    }

    /*      */
    /*      */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*      */ {
        /* 1306 */
        ICodeNode node = getSelectedNode();
        /* 1307 */
        if (node == null) {
            /* 1308 */
            return null;
            /*      */
        }
        /*      */
        /* 1311 */
        ICodeItem item = node.getObject();
        /* 1312 */
        if (item == null) {
            /* 1313 */
            return null;
            /*      */
        }
        /*      */
        /* 1316 */
        return item.getAddress();
        /*      */
    }

    /*      */
    /*      */
    public void dispose()
    /*      */ {
        /* 1321 */
        super.dispose();
        /* 1322 */
        if (this.contentProvider != null) {
            /* 1323 */
            this.contentProvider.dispose();
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public byte[] export()
    /*      */ {
        /* 1329 */
        return Strings.encodeUTF8(TreeUtil.buildXml(this.tree, 2));
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*      */ {
        /* 1378 */
        return AbstractUnitFragment.FragmentType.TREE;
        /*      */
    }

    /*      */
    /*      */
    public void focusOnAddress(String address) {
        /* 1382 */
        ICodeNode node = ((ICodeUnit) this.unit).getHierarchy().findNode(address, true);
        /* 1383 */
        if (node != null) {
            /* 1384 */
            focusOnNode(node);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public void focusOnNode(ICodeNode node) {
        /* 1389 */
        ISelection selection = new StructuredSelection(node);
        /*      */
        /* 1391 */
        List<Object> path = new ArrayList();
        /* 1392 */
        path.add(((ICodeUnit) this.unit).getHierarchy().getRoot());
        /* 1393 */
        path.add(node);
        /*      */
        /* 1395 */
        selection = new TreeSelection(new TreePath(path.toArray()));
        /*      */
        /* 1397 */
        this.viewer.reveal(node);
        /* 1398 */
        this.viewer.setSelection(selection);
        /* 1399 */
        if (getSelectedElement() == null) {
            /* 1400 */
            path = new ArrayList();
            /* 1401 */
            boolean autoExpand = true;
            /* 1402 */
            if (autoExpand) {
                /* 1403 */
                List<ICodeNode> pathNode = new ArrayList();
                /*      */
                /* 1405 */
                getPathNode(node, this.contentProvider.getRoot(), pathNode, true);
                /* 1406 */
                if ((usesExplicitDefaultPackage()) && (!this.extraDetails) &&
                        /* 1407 */           (!(((ICodeNode) pathNode.get(0)).getObject() instanceof ICodePackage)) &&
                        /* 1408 */           (this.contentProvider.defaultPackage != null))
                    /*      */ {
                    /* 1410 */
                    pathNode.add(0, this.contentProvider.defaultPackage);
                    /*      */
                }
                /*      */
                /*      */
                try
                    /*      */ {
                    /* 1415 */
                    this.viewer.getTree().setRedraw(false);
                    /* 1416 */
                    TreeItem[] items = this.viewer.getTree().getItems();
                    /* 1417 */
                    for (ICodeNode o : pathNode)
                        /*      */ {
                        /*      */
                        /*      */
                        /*      */
                        /* 1422 */
                        List<IArrayGroup> arrayPath = new ArrayList();
                        /* 1423 */
                        TreeItem item = getIntermediateNodes(o, items, arrayPath);
                        /*      */
                        /* 1425 */
                        for (IArrayGroup a : arrayPath) {
                            /* 1426 */
                            this.viewer.setExpandedState(a, true);
                            /*      */
                        }
                        /* 1428 */
                        this.viewer.setExpandedState(o, true);
                        /*      */
                        /*      */
                        /* 1431 */
                        item = updateTreeItem(item, arrayPath, 0);
                        /* 1432 */
                        path.addAll(arrayPath);
                        /* 1433 */
                        if ((arrayPath.isEmpty()) || (!((IArrayGroup) arrayPath.get(arrayPath.size() - 1)).isSingle())) {
                            /* 1434 */
                            path.add(o);
                            /* 1435 */
                            item = updateTreeItem(item, o);
                            /*      */
                        }
                        /* 1437 */
                        if (item == null) break;
                        /* 1438 */
                        items = item.getItems();
                        /*      */
                    }
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /* 1444 */
                    logger.debug("Path to item: %s", new Object[]{Strings.joinList(path)});
                    /* 1445 */
                    selection = new TreeSelection(new TreePath(path.toArray()));
                    /* 1446 */
                    this.viewer.setSelection(selection);
                    /* 1447 */
                    if (getSelectedElement() == null) {
                        /* 1448 */
                        AbstractFilteredFilter filter = getAbstractFilter();
                        /* 1449 */
                        String msg = null;
                        /* 1450 */
                        if ((filter != null) && (filter.isFiltered())) {
                            /* 1451 */
                            msg = Strings.f("The item %s can not be focused.\nMaybe it is hidden by current filter?", new Object[]{node
                                    /* 1452 */.getLabel()});
                            /*      */
                        }
                        /*      */
                        else
                            /*      */ {
                            /* 1456 */
                            msg = Strings.f("The item  %s can not be focused.", new Object[]{node.getLabel()});
                            /* 1457 */
                            this.context.getErrorHandler().processThrowableSilent(new JebRuntimeException(msg));
                            /*      */
                        }
                        /* 1459 */
                        UI.error(msg);
                        /*      */
                    }
                    /*      */
                }
                /*      */ finally {
                    /* 1463 */
                    this.viewer.getTree().setRedraw(true);
                    /*      */
                }
                /* 1465 */
                return;
                /*      */
            }
            /*      */
            /* 1468 */
            getPath(node, this.viewer.getTree().getItems(), path);
            /*      */
            /*      */
            /* 1471 */
            path.add(0, ((ICodeUnit) this.unit).getHierarchy().getRoot());
            /*      */
            /*      */
            /* 1474 */
            int pathSize = path.size();
            /* 1475 */
            while ((getSelectedElement() == null) && (!path.isEmpty())) {
                /* 1476 */
                selection = new TreeSelection(new TreePath(path.toArray()));
                /* 1477 */
                this.viewer.setSelection(selection);
                /* 1478 */
                path.remove(path.size() - 1);
                /*      */
            }
            /* 1480 */
            boolean focused = (getSelectedElement() != null) && (path.size() + 1 == pathSize);
            /* 1481 */
            if (!focused) {
                /* 1482 */
                String msg = "For performance reason, the node can not be automatically expanded.";
                /* 1483 */
                logger.warn(msg, new Object[0]);
                /* 1484 */
                UI.infoOptional(getViewer().getTree().getShell(), null, msg, "dlgCodeHierNoExpand");
                /*      */
            }
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    private TreeItem updateTreeItem(TreeItem item, ICodeNode match) {
        /* 1490 */
        if (item == null) {
            /* 1491 */
            return null;
            /*      */
        }
        /* 1493 */
        if (item.getData() == match) {
            /* 1494 */
            return item;
            /*      */
        }
        /* 1496 */
        for (TreeItem t : item.getItems()) {
            /* 1497 */
            if (t.getData() == match) {
                /* 1498 */
                return t;
                /*      */
            }
            /*      */
        }
        /* 1501 */
        return null;
        /*      */
    }

    /*      */
    /*      */
    private TreeItem updateTreeItem(TreeItem item, List<IArrayGroup> arrayPath, int i) {
        /* 1505 */
        if (arrayPath.isEmpty()) {
            /* 1506 */
            return item;
            /*      */
        }
        /* 1508 */
        IArrayGroup match = (IArrayGroup) arrayPath.get(i);
        /* 1509 */
        if ((arrayPath.size() == 1) &&
                /* 1510 */       (item.getData() == match)) {
            /* 1511 */
            return item;
            /*      */
        }
        /*      */
        /* 1514 */
        for (TreeItem t : item.getItems()) {
            /* 1515 */
            if (t.getData() == match) {
                /* 1516 */
                if (i >= arrayPath.size() - 1) {
                    /* 1517 */
                    return t;
                    /*      */
                }
                /* 1519 */
                return updateTreeItem(t, arrayPath, i + 1);
                /*      */
            }
            /*      */
        }
        /* 1522 */
        return null;
        /*      */
    }

    /*      */
    /*      */
    private TreeItem getIntermediateNodes(ICodeNode node, TreeItem[] items, List<IArrayGroup> arrayPath)
    /*      */ {
        /* 1527 */
        if (items != null) {
            /* 1528 */
            for (TreeItem t : items) {
                /* 1529 */
                if (t.getData() == node) {
                    /* 1530 */
                    return t;
                    /*      */
                }
                /* 1532 */
                if (((t.getData() instanceof IArrayGroup)) &&
                        /* 1533 */           (getIntermediatePathArrayGroup(node, (IArrayGroup) t.getData(), arrayPath))) {
                    /* 1534 */
                    arrayPath.add(0, (IArrayGroup) t.getData());
                    /* 1535 */
                    return t;
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*      */
        /* 1540 */
        return null;
        /*      */
    }

    /*      */
    /*      */
    private boolean getIntermediatePathArrayGroup(ICodeNode node, IArrayGroup ag, List<IArrayGroup> arrayPath) {
        /* 1544 */
        if (ag.isSingle()) {
            /* 1545 */
            if (ag.getFirstElement() == node) {
                /* 1546 */
                return true;
                /*      */
            }
            /*      */
        }
        /*      */
        else {
            /* 1550 */
            for (Object o : ag.getChildren()) {
                /* 1551 */
                if (o == node) {
                    /* 1552 */
                    return true;
                    /*      */
                }
                /* 1554 */
                if (((o instanceof IArrayGroup)) &&
                        /* 1555 */           (getIntermediatePathArrayGroup(node, (IArrayGroup) o, arrayPath))) {
                    /* 1556 */
                    arrayPath.add(0, (IArrayGroup) o);
                    /* 1557 */
                    return true;
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*      */
        /* 1562 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private boolean getPath(ICodeNode node, TreeItem[] items, List<Object> path)
    /*      */ {
        /* 1572 */
        if (items != null) {
            /* 1573 */
            for (TreeItem t : items) {
                /* 1574 */
                if (getPath(node, t.getData(), path)) {
                    /* 1575 */
                    return true;
                    /*      */
                }
                /*      */
                /* 1578 */
                if (getPath(node, t.getItems(), path)) {
                    /* 1579 */
                    path.add(0, t.getData());
                    /* 1580 */
                    return true;
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /* 1584 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    private boolean getPath(ICodeNode node, Object o, List<Object> path) {
        /* 1588 */
        if ((o instanceof ICodeNode)) {
            /* 1589 */
            if (getPathNodeObj(node, (ICodeNode) o, path)) {
                /* 1590 */
                path.add(0, o);
                /* 1591 */
                return true;
                /*      */
            }
            /*      */
        }
        /* 1594 */
        else if (((o instanceof IArrayGroup)) &&
                /* 1595 */       (getPathArrayGroup(node, (IArrayGroup) o, path))) {
            /* 1596 */
            path.add(0, o);
            /* 1597 */
            return true;
            /*      */
        }
        /*      */
        /* 1600 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    private boolean getPathArrayGroup(ICodeNode node, IArrayGroup ag, List<Object> path) {
        /* 1604 */
        if (ag.isSingle()) {
            /* 1605 */
            if (ag.getFirstElement() == node)
                /*      */ {
                /* 1607 */
                return true;
                /*      */
            }
            /*      */
        }
        /*      */
        else {
            /* 1611 */
            for (Object o : ag.getChildren()) {
                /* 1612 */
                if (getPath(node, o, path)) {
                    /* 1613 */
                    return true;
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /* 1617 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    private boolean getPathNodeObj(ICodeNode node, ICodeNode search, List<Object> path) {
        /* 1621 */
        if (search == node) {
            /* 1622 */
            return true;
            /*      */
        }
        /* 1624 */
        for (ICodeNode o : search.getChildren()) {
            /* 1625 */
            if (getPathNodeObj(node, o, path)) {
                /* 1626 */
                path.add(0, o);
                /* 1627 */
                return true;
                /*      */
            }
            /*      */
        }
        /* 1630 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    private boolean getPathNode(ICodeNode node, ICodeNode search, List<ICodeNode> path, boolean first) {
        /* 1634 */
        if (search == node) {
            /* 1635 */
            if (first) {
                /* 1636 */
                path.add(search);
                /*      */
            }
            /* 1638 */
            return true;
            /*      */
        }
        /*      */
        /*      */
        /* 1642 */
        for (ICodeNode o : search.getChildren()) {
            /* 1643 */
            if (getPathNode(node, o, path, false)) {
                /* 1644 */
                path.add(0, o);
                /* 1645 */
                return true;
                /*      */
            }
            /*      */
        }
        /* 1648 */
        return false;
        /*      */
    }
    /*      */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\CodeHierarchyView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */