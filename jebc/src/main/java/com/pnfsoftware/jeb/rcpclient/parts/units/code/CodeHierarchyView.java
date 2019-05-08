package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.exceptions.JebRuntimeException;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.tree.CodeNodeUtil;
import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeClass;
import com.pnfsoftware.jeb.core.units.code.ICodeField;
import com.pnfsoftware.jeb.core.units.code.ICodeHierarchy;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
import com.pnfsoftware.jeb.core.units.code.ICodePackage;
import com.pnfsoftware.jeb.core.units.code.ICodeType;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
import com.pnfsoftware.jeb.rcpclient.AllHandlers;
import com.pnfsoftware.jeb.rcpclient.AssetManagerOverlay;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.RcpErrorHandler;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.actions.ActionUIContext;
import com.pnfsoftware.jeb.rcpclient.actions.GraphicalActionExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.filter.AbstractFilteredFilter;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractArrayGroupFilteredTreeContentProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredViewerComparator;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IDndProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.ArrayLogicalGroup;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.IArrayGroup;
import com.pnfsoftware.jeb.rcpclient.iviewers.tree.TreeUtil;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.impl.SimpleCodeNode;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.impl.SimpleCodePackage;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class CodeHierarchyView extends AbstractUnitFragment<ICodeUnit> {
    private static final ILogger logger = GlobalLog.getLogger(CodeHierarchyView.class);
    private boolean extraDetails;
    private Tree tree;
    private TreeViewer viewer;
    private ViewerRefresher refresher;
    private PatternTreeView pt;
    private ContentProvider contentProvider;
    private LabelProvider labelProvider;
    private boolean autoRefreshDisabled;

    public CodeHierarchyView(Composite parent, int flags, RcpClientContext context, ICodeUnit unit, ICodeNode baseNode, int includedFlags, int excludedFlags, boolean disableInitialExpansion) {
        super(parent, flags, unit, null, context);
        setLayout(new FillLayout());
        if (baseNode == null) {
            ICodeHierarchy hier = unit.getHierarchy();
            if (hier != null) {
                baseNode = hier.getRoot();
            }
        }
        this.extraDetails = (unit instanceof INativeCodeUnit);
        int style = 0;
        String[] columnLabels = null;
        int[] columnWidths = null;
        ProcessorType procType = null;
        if (this.extraDetails) {
            style = 65536;
            procType = ((INativeCodeUnit) unit).getProcessor().getType();
            if (procType == ProcessorType.ARM) {
                columnLabels = new String[]{"Name", "Address", "Size", "Mode"};
                columnWidths = new int[]{150, 100, 60, 60};
            } else {
                columnLabels = new String[]{"Name", "Address", "Size"};
                columnWidths = new int[]{150, 100, 60};
            }
        }
        this.contentProvider = new ContentProvider(unit);
        this.contentProvider.setFlags(includedFlags, excludedFlags);
        this.labelProvider = new LabelProvider(this.contentProvider, procType);
        IPatternMatcher patternMatcher = new SimplePatternMatcher(this.labelProvider);
        this.pt = new PatternTreeView(this, style, columnLabels, columnWidths, patternMatcher, true);
        FilteredTreeViewer ftv = this.pt.getTreeViewer();
        ftv.addFilteredTextListener(new Listener() {
            public void handleEvent(Event event) {
                if (((event.data instanceof Integer)) && (((Integer) event.data).intValue() == -1)) {
                    String msg = "Too many results: The tree was not fully expanded.";
                    CodeHierarchyView.logger.warn(msg, new Object[0]);
                    UI.infoOptional(CodeHierarchyView.this.getViewer().getTree().getShell(), null, msg, "dlgCodeHierFilterTooManyResults");
                }
            }
        });
        this.tree = this.pt.getTree();
        if (this.extraDetails) {
            this.tree.setHeaderVisible(true);
            this.tree.setLinesVisible(true);
        }
        setPrimaryWidget(this.tree);
        this.viewer = ((TreeViewer) ftv.getViewer());
        if (context != null) {
            this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent event) {
                    String text = String.format("%s", new Object[]{CodeHierarchyView.this.getActiveAddress()});
                    CodeHierarchyView.this.context.getStatusIndicator().setText(text);
                }
            });
        }
        ftv.addDragnDropSupport(new DndProvider());
        ftv.setContentProvider(this.contentProvider);
        ftv.setLabelProvider(this.labelProvider);
        if (baseNode == null) {
            return;
        }
        this.refresher = new ViewerRefresher(parent.getDisplay(), this.viewer);
        ftv.setInput(new ICodeNode[]{baseNode});
        this.viewer.setExpandedState(baseNode, true);
        if (!disableInitialExpansion) {
            int expandedCount = 0;
            List<ICodeNode> currentNodes = new ArrayList(baseNode.getChildren());
            for (; ; ) {
                List<ICodeNode> nextNodes = new ArrayList();
                for (ICodeNode node : currentNodes) {
                    int expansion = node.getInitialExpansion();
                    if (expansion >= 1) {
                        this.viewer.setExpandedState(node, true);
                        if (this.viewer.getExpandedState(node)) {
                            nextNodes.addAll(node.getChildren());
                            if (expandedCount++ >= 100) {
                                nextNodes.clear();
                                break;
                            }
                        }
                    }
                }
                if (nextNodes.isEmpty()) {
                    break;
                }
                currentNodes = nextNodes;
            }
            this.viewer.refresh();
        }
        new ContextMenu(this.viewer.getControl()).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                AllHandlers.getInstance().fillManager(menuMgr, 5);
            }
        });
    }

    public void setAutoRefreshDisabled(boolean autoRefreshDisabled) {
        this.autoRefreshDisabled = autoRefreshDisabled;
    }

    public boolean isAutoRefreshDisabled() {
        return this.autoRefreshDisabled;
    }

    public void setHideTypes(boolean hideTypes) {
        if (hideTypes) {
            this.contentProvider.setFlags(-1, 65536);
        } else {
            this.contentProvider.setFlags(-1, 0);
        }
        this.viewer.refresh();
    }

    public boolean getHideTypes() {
        int[] flags = this.contentProvider.getFlags();
        return (flags[1] & 0x10000) != 0;
    }

    public void setShowAll(boolean showAll) {
        if (showAll) {
            this.contentProvider.setFlags(0, 0);
        } else {
            this.contentProvider.setFlags(256, 65536);
        }
        this.viewer.refresh();
    }

    public boolean getShowAll() {
        int[] flags = this.contentProvider.getFlags();
        return (flags[0] == 0) && (flags[1] == 0);
    }

    public TreeViewer getViewer() {
        return this.viewer;
    }

    private int getBucketLimit() {
        return this.context.getPropertyManager().getInteger(this.extraDetails ? ".ui.tree.BucketFlatThreshold" : ".ui.tree.BucketTreeThreshold");
    }

    private int getBucketMaxElements() {
        return this.context.getPropertyManager().getInteger(this.extraDetails ? ".ui.tree.BucketFlatMaxElements" : ".ui.tree.BucketTreeMaxElements");
    }

    private boolean usesExplicitDefaultPackage() {
        return this.context.getPropertyManager().getBoolean(".ui.tree.UseExplicitDefaultPackage");
    }

    class ContentProvider extends AbstractArrayGroupFilteredTreeContentProvider {
        private ICodeUnit codeunit;
        private ICodeNode root;
        private int includedFlags;
        private int excludedFlags;
        private IEventListener eventListener;
        private SimpleCodeNode defaultPackage;

        public ContentProvider(ICodeUnit unit) {
            super(CodeHierarchyView.this.getBucketLimit(), CodeHierarchyView.this.getBucketMaxElements(), 20);
            this.codeunit = unit;
        }

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            if (this.eventListener != null) {
                this.codeunit.removeListener(this.eventListener);
                this.eventListener = null;
            }
            if (newInput == null) {
                this.root = null;
                return;
            }
            this.root = ((ICodeNode[]) (ICodeNode[]) newInput)[0];
            this.eventListener = new IEventListener() {
                public void onEvent(IEvent e) {
                    if ((J.isUnitEvent(e)) && (!CodeHierarchyView.this.autoRefreshDisabled)) {
                        CodeHierarchyView.this.refresher.request();
                    }
                }
            };
            ((ICodeUnit) CodeHierarchyView.this.unit).addListener(this.eventListener);
        }

        public void dispose() {
            if (this.eventListener != null) {
                this.codeunit.removeListener(this.eventListener);
                this.eventListener = null;
            }
        }

        public ICodeUnit getUnit() {
            return this.codeunit;
        }

        public ICodeNode getRoot() {
            return this.root;
        }

        public void setFlags(int includedFlags, int excludedFlags) {
            if (includedFlags >= 0) {
                this.includedFlags = includedFlags;
            }
            if (excludedFlags >= 0) {
                this.excludedFlags = excludedFlags;
            }
        }

        public int[] getFlags() {
            return new int[]{this.includedFlags, this.excludedFlags};
        }

        public Object[] getElements(Object input) {
            if (this.root == null) {
                return new ICodeNode[0];
            }
            if (this.root.getLabel() != null) {
                return new ICodeNode[]{this.root};
            }
            return getChildren(this.root);
        }

        public List<?> getChildren2(Object element) {
            List<? extends ICodeNode> li = new ArrayList();
            if ((element instanceof ICodeNode)) {
                if ((CodeHierarchyView.this.usesExplicitDefaultPackage()) && (!CodeHierarchyView.this.extraDetails) && (element == this.root)) {
                    li = CodeNodeUtil.getChildren((ICodeNode) element, 32768, this.excludedFlags);
                    List<ICodeNode> li2 = new ArrayList(li);
                    if (this.defaultPackage == null) {
                        this.defaultPackage = new SimpleCodeNode(new SimpleCodePackage("(default package)", ""), this.root, CodeNodeUtil.getChildren((ICodeNode) element, this.includedFlags, 32768), true);
                    }
                    li2.add(this.defaultPackage);
                    li = li2;
                } else {
                    li = CodeNodeUtil.getChildren((ICodeNode) element, this.includedFlags, this.excludedFlags);
                }
            }
            li = filterOutEmptyPackages(li);
            List<CodeNodeWrapper> liw = CodeNodeWrapper.wrapNodes(li);
            Collections.sort(liw);
            li = CodeNodeWrapper.unwrapNodes(liw);
            if (li.size() > getLimit()) {
                AbstractFilteredFilter filter = CodeHierarchyView.this.getAbstractFilter();
                if ((filter != null) && (filter.isFiltered())) {
                    List<ICodeNode> li2 = new ArrayList();
                    for (ICodeNode o : li) {
                        if ((filter.isElementMatch(o)) || (isChildMatch(filter, o))) {
                            li2.add(o);
                        }
                    }
                    return li2;
                }
            }
            return li;
        }

        private boolean isChildMatch(AbstractFilteredFilter filter, ICodeNode o) {
            List<? extends ICodeNode> children = o.getChildren();
            if ((children == null) || (children.size() == 0)) {
                return false;
            }
            for (ICodeNode c : children) {
                if (filter.isElementMatch(c)) {
                    return true;
                }
                boolean match = isChildMatch(filter, c);
                if (match) {
                    return true;
                }
            }
            return false;
        }

        public Object getParent(Object element) {
            if ((element instanceof ICodeNode)) {
                return ((ICodeNode) element).getParent();
            }
            return null;
        }

        public boolean hasChildren2(Object element) {
            if ((element instanceof ICodeNode)) {
                return !CodeNodeUtil.getChildren((ICodeNode) element, this.includedFlags, this.excludedFlags).isEmpty();
            }
            return false;
        }

        public void sort(Object[] elements) {
            CodeHierarchyView.this.pt.getTreeViewer().getComparator().sort(CodeHierarchyView.this.viewer, elements);
        }

        List<ICodeNode> filterOutEmptyPackages(List<? extends ICodeNode> list) {
            List<ICodeNode> list2 = new ArrayList();
            for (ICodeNode node : list) {
                if ((!(node.getObject() instanceof ICodePackage)) || (isNonEmptyPackageNode(node)) || (!CodeNodeUtil.cannotBe(node, 16))) {
                    list2.add(node);
                }
            }
            return list2;
        }

        boolean isNonEmptyPackageNode(ICodeNode parent) {
            for (ICodeNode node : CodeNodeUtil.getChildren(parent, this.includedFlags, this.excludedFlags)) {
                if (!(node.getObject() instanceof ICodePackage)) {
                    return true;
                }
                if (isNonEmptyPackageNode(node)) {
                    return true;
                }
            }
            return false;
        }

        public Object[] getRowElements(Object row) {
            if ((row instanceof ICodeNode)) {
                return getRowElementsInner((ICodeNode) row);
            }
            if ((row instanceof IArrayGroup)) {
                Object first = ((IArrayGroup) row).getFirstElement();
                Object last = ((IArrayGroup) row).getLastElement();
                Object[] firstNode = null;
                Object[] lastNode = null;
                if ((first instanceof ICodeNode)) {
                    firstNode = getRowElementsInner((ICodeNode) first);
                }
                if ((last instanceof ICodeNode)) {
                    lastNode = getRowElementsInner((ICodeNode) last);
                }
                if (firstNode == null) {
                    return new Object[0];
                }
                if (lastNode == null) {
                    return append(firstNode, new Object[0]);
                }
                return append(firstNode, lastNode);
            }
            return new Object[0];
        }

        private Object[] append(Object[] firstNode, Object... lastNode) {
            for (int i = 0; i < firstNode.length; i++) {
                Object last = null;
                if ((lastNode != null) && (i < lastNode.length)) {
                    last = lastNode[i];
                }
                firstNode[i] = new Object[]{firstNode[i], last};
            }
            return firstNode;
        }

        private Object[] getRowElementsInner(ICodeNode node) {
            String label = node.getLabel();
            if (!CodeHierarchyView.this.extraDetails) {
                return new Object[]{label};
            }
            Long address = null;
            Integer size = null;
            Integer mode = null;
            ICodeItem item = node.getObject();
            if ((item instanceof INativeMethodItem)) {
                INativeMethodDataItem data = ((INativeMethodItem) item).getData();
                if (data != null) {
                    address = Long.valueOf(data.getMemoryAddress());
                    CFG<?> cfg = data.getCFG();
                    try {
                        size = Integer.valueOf(cfg.getEffectiveSize());
                        mode = Integer.valueOf(cfg.getEntryBlock().get(0).getProcessorMode());
                    } catch (ConcurrentModificationException | IndexOutOfBoundsException e) {
                        size = Integer.valueOf(0);
                        mode = Integer.valueOf(0);
                    }
                }
            }
            return new Object[]{label, address, size, mode};
        }

        private final SeparatorRule underscore = new SeparatorRule("_", "_*", false);
        private final SeparatorRule cpp_separator = new SeparatorRule("::", "", true, new String[]{"("}, new String[]{"<"});

        class SeparatorRule {
            String separator;
            String suffix;
            boolean packageSeparator = false;
            String[] blacklistChars = null;
            String[] endChars = null;

            public SeparatorRule(String separator, String suffix, boolean packageSeparator) {
                this(separator, suffix, packageSeparator, new String[0], new String[0]);
            }

            public SeparatorRule(String separator, String suffix, boolean packageSeparator, String[] blacklistChars, String[] endChars) {
                this.separator = separator;
                this.suffix = suffix;
                this.packageSeparator = packageSeparator;
                this.blacklistChars = blacklistChars;
                this.endChars = endChars;
            }

            public String format(String startExpression) {
                String str = startExpression;
                if (startExpression.endsWith(this.separator)) {
                    str = startExpression.substring(0, startExpression.length() - this.separator.length());
                } else {
                    for (String end : this.endChars) {
                        if (startExpression.endsWith(end)) {
                            str = startExpression.substring(0, startExpression.length() - end.length());
                            break;
                        }
                    }
                }
                return str + this.suffix;
            }

            public String getStartExpression(String label) {
                return getStartExpression(label, 1);
            }

            public String getStartExpression(String label, int from) {
                int idx = label.indexOf(this.separator, from);
                if (idx > 0) {
                    String candidate = label.substring(0, idx + this.separator.length());
                    if (Strings.contains(candidate, this.blacklistChars)) {
                        return null;
                    }
                    for (String end : this.endChars) {
                        idx = candidate.indexOf(end);
                        if (idx + end.length() == from) {
                            return null;
                        }
                        if (idx > 0) {
                            candidate = candidate.substring(0, idx + end.length());
                        }
                    }
                    return candidate;
                }
                return null;
            }

            public String getStartExpression(String label, String fromStartExpression) {
                return getStartExpression(label, fromStartExpression.length());
            }
        }

        class LabelRule {
            private static final String prefix = "group_";

            LabelRule() {
            }

            private int groupNumber = 1;
            Map<String, List<ArrayLogicalGroup>> groupByName = new HashMap();

            public String generateGroupName() {
                String groupName = "group_" + this.groupNumber;
                this.groupNumber += 1;
                return groupName;
            }

            public void setOriginalLabels() {
                for (Map.Entry<String, List<ArrayLogicalGroup>> e : this.groupByName.entrySet()) {
                    if (((List) e.getValue()).size() == 1) {
                        ((ArrayLogicalGroup) ((List) e.getValue()).get(0)).setGroupName(withNumberItemsSuffix((String) e.getKey(), ((ArrayLogicalGroup) ((List) e.getValue()).get(0)).size()));
                    }
                }
            }

            private void saveValidLabel(ArrayLogicalGroup logicalGroup, String label) {
                List<ArrayLogicalGroup> gr = (List) this.groupByName.get(label);
                if (gr == null) {
                    gr = new ArrayList();
                    this.groupByName.put(label, gr);
                }
                gr.add(logicalGroup);
            }

            private String withNumberItemsSuffix(String groupName, int items) {
                StringBuilder stb = new StringBuilder(groupName);
                stb.append(" (").append(items).append(" items)");
                return stb.toString();
            }

            String withBoundSuffix(String groupName, int from, ICodeNode firstNode, ICodeNode lastNode) {
                StringBuilder label = new StringBuilder(groupName);
                label.append(" (").append(firstNode.getLabel().substring(from)).append(" .. ");
                label.append(lastNode.getLabel().substring(from)).append(")");
                return label.toString();
            }
        }

        public Map<Integer, ArrayLogicalGroup> getLogicalGroups(List<?> r, Object parentElement) {
            Map<Integer, ArrayLogicalGroup> map = super.getLogicalGroups(r, parentElement);
            if (CodeHierarchyView.this.extraDetails) {
                boolean packageSeparatorGroup = (CodeHierarchyView.this.viewer.getTree().getSortColumn() != null) && (CodeHierarchyView.this.viewer.getTree().getSortColumn().getText().equals("Name"));
                if (!packageSeparatorGroup) {
                    return map;
                }
            }
            LabelRule labelRule = new LabelRule();
            List<SeparatorRule> separators = CodeHierarchyView.this.extraDetails ? Arrays.asList(new SeparatorRule[]{this.cpp_separator, this.underscore}) : Arrays.asList(new SeparatorRule[]{this.underscore});
            String startExpression = null;
            SeparatorRule rule = null;
            int size = 0;
            for (int i = 0; i < r.size(); i++) {
                Object o = r.get(i);
                String label;
                if ((o instanceof ICodeNode)) {
                    label = ((ICodeNode) o).getLabel();
                    if (startExpression != null) {
                        if (label.startsWith(startExpression)) {
                            for (SeparatorRule separator : separators) {
                                if (separator == rule) {
                                    break;
                                }
                                String testStartExpression = separator.getStartExpression(label);
                                if (testStartExpression != null) {
                                    addLogicalGroup(map, r, parentElement, i - size, size, startExpression, rule, labelRule);
                                    startExpression = testStartExpression;
                                    rule = separator;
                                    size = 0;
                                    break;
                                }
                            }
                            size++;
                        } else {
                            addLogicalGroup(map, r, parentElement, i - size, size, startExpression, rule, labelRule);
                            startExpression = null;
                        }
                    } else {
                        for (SeparatorRule separator : separators) {
                            startExpression = separator.getStartExpression(label);
                            if (startExpression != null) {
                                rule = separator;
                                size = 1;
                                break;
                            }
                        }
                    }
                } else if (startExpression != null) {
                    addLogicalGroup(map, r, parentElement, i - size, size, startExpression, rule, labelRule);
                    startExpression = null;
                }
            }
            if (startExpression != null) {
                addLogicalGroup(map, r, parentElement, r.size() - size, size, startExpression, rule, labelRule);
            }
            labelRule.setOriginalLabels();
            return map;
        }

        private void addLogicalGroup(Map<Integer, ArrayLogicalGroup> map, List<?> r, Object parentElement, int index, int size, String startExpression, SeparatorRule rule, LabelRule labelRule) {
            if (size >= 10) {
                if (rule.packageSeparator) {
                    ArrayLogicalGroup group = addPackagedGroup(r, parentElement, index, size, startExpression, rule, labelRule, 1);
                    map.put(Integer.valueOf(index), group);
                    return;
                }
                if (size > getGroupLimit()) {
                    int i = 1;
                    while (size > 0) {
                        int realSize = Math.min(size, getGroupLimit());
                        String groupName = rule.format(startExpression) + " group_" + i;
                        String label = labelRule.withNumberItemsSuffix(groupName, realSize);
                        ArrayLogicalGroup group = buildLogicalGroup(r, parentElement, index, realSize, label);
                        map.put(Integer.valueOf(index), group);
                        index += realSize;
                        size -= realSize;
                        i++;
                    }
                } else {
                    ArrayLogicalGroup logicalGroup = buildLogicalGroup(r, parentElement, index, size, labelRule.generateGroupName());
                    map.put(Integer.valueOf(index), logicalGroup);
                    labelRule.saveValidLabel(logicalGroup, rule.format(startExpression));
                }
            }
        }

        private ArrayLogicalGroup buildLogicalGroup(List<?> r, Object parentElement, int firstIndex, int size, String groupName) {
            ArrayLogicalGroup group = getArrayLogicalGroup(parentElement, firstIndex, groupName, false, 0);
            for (int i = 0; i < size; i++) {
                group.add(r.get(firstIndex + i));
            }
            return group;
        }

        private ArrayLogicalGroup addPackagedGroup(List<?> r, Object parentElement, int index, int size, String startExpression, SeparatorRule rule, LabelRule labelRule, int toplevel) {
            ArrayLogicalGroup group = getArrayLogicalGroup(parentElement, index, rule.format(startExpression), true, toplevel);
            for (int i = 0; i < size; i++) {
                Object o = r.get(index + i);
                String label = ((ICodeNode) o).getLabel();
                if (!label.startsWith(startExpression)) break;
                String newStartExpression = rule.getStartExpression(label, startExpression);
                if (newStartExpression != null) {
                    ArrayLogicalGroup subgroup = addPackagedGroup(r, parentElement, index + i, size - i, newStartExpression, rule, labelRule, toplevel + 1);
                    if (subgroup.size() < 10) {
                        i += subgroup.size() - 1;
                        for (Object sub : subgroup.getChildren()) {
                            group.add(sub);
                        }
                    } else {
                        i += subgroup.size() - 1;
                        group.add(subgroup);
                    }
                } else {
                    group.add(getVirtualElement(o, index + i, label.substring(startExpression.length())));
                }
            }
            group.setGroupName(labelRule.withNumberItemsSuffix(group.getGroupName(), group.size()));
            return group;
        }

        public void onFirstOptimization(List<?> r) {
            super.onFirstOptimization(r);
            String msg = "Your artifact has too many chidren. They were divided into group nodes.";
            CodeHierarchyView.logger.warn(msg, new Object[0]);
            UI.infoOptional(CodeHierarchyView.this.getViewer().getTree().getShell(), null, msg, "dlgCodeHierFirstGroup");
        }
    }

    class DndProvider implements IDndProvider {
        DndProvider() {
        }

        public boolean canDrag(Object data) {
            if (((data instanceof IArrayGroup)) && (((IArrayGroup) data).isSingle())) {
                data = ((IArrayGroup) data).getFirstElement();
            }
            if ((data instanceof ICodeNode)) {
                Object item = ((ICodeNode) data).getObject();
                if ((item instanceof ICodePackage)) {
                    return true;
                }
                if ((item instanceof ICodeType)) {
                    return true;
                }
                if ((item instanceof ICodeClass)) {
                    return true;
                }
                if ((item instanceof ICodeMethod)) {
                    return CodeHierarchyView.this.extraDetails;
                }
                if ((item instanceof ICodeField)) {
                    return false;
                }
                CodeHierarchyView.logger.i("data %s", new Object[]{item});
            }
            return false;
        }

        public boolean canDrop(String source, Object target, int location) {
            if (source.startsWith("p;")) {
                return ((target instanceof ICodeNode)) && ((((ICodeNode) target).getObject() instanceof ICodePackage));
            }
            return getPackageFor(target) != null;
        }

        private ICodePackage getPackageFor(Object target) {
            if ((target instanceof ICodeNode)) {
                Object item = ((ICodeNode) target).getObject();
                if (!(item instanceof ICodePackage)) {
                    if ((item instanceof ICodeType)) {
                        item = ((ICodeNode) target).getParent().getObject();
                    } else if ((item instanceof ICodeClass)) {
                        item = ((ICodeNode) target).getParent().getObject();
                    } else if ((CodeHierarchyView.this.extraDetails) && ((item instanceof ICodeMethod))) {
                        item = ((ICodeNode) target).getParent().getObject();
                    }
                    if (!(item instanceof ICodePackage)) {
                        return null;
                    }
                }
                return (ICodePackage) item;
            }
            return null;
        }

        public boolean performDrop(String source, Object target, int location) {
            if ((target instanceof ICodeNode)) {
                long id = Long.parseLong(source.substring(2));
                ICodePackage item = getPackageFor(target);
                if (item == null) {
                    return false;
                }
                String dest = item.getAddress();
                if (dest == null) {
                    dest = "";
                }
                CodeHierarchyView.logger.i("Perform drop from %d to %s", new Object[]{Long.valueOf(id), dest});
                GraphicalActionExecutor exec = new GraphicalActionExecutor(CodeHierarchyView.this.getShell(), CodeHierarchyView.this.getContext());
                ActionContext actionContext = new ActionContext((IInteractiveUnit) CodeHierarchyView.this.unit, 11, id, dest);
                ActionUIContext uictx = new ActionUIContext(actionContext, CodeHierarchyView.this);
                if (!exec.execute(uictx, dest)) {
                    CodeHierarchyView.logger.error("Can not move to package %s", new Object[]{dest});
                    return false;
                }
                return true;
            }
            return false;
        }

        public Object getSelectedElements() {
            return CodeHierarchyView.this.getSelectedElement();
        }

        public String getDragData() {
            Object elt = CodeHierarchyView.this.getSelectedElement();
            if (((elt instanceof IArrayGroup)) && (((IArrayGroup) elt).isSingle())) {
                elt = ((IArrayGroup) elt).getFirstElement();
            }
            if ((elt instanceof ICodeNode)) {
                Object item = ((ICodeNode) elt).getObject();
                String prefix = "g;";
                if ((item instanceof ICodePackage)) {
                    prefix = "p;";
                } else if ((item instanceof ICodeType)) {
                    prefix = "t;";
                } else if ((item instanceof ICodeClass)) {
                    prefix = "c;";
                } else if ((item instanceof ICodeMethod)) {
                    prefix = "m;";
                } else if ((item instanceof ICodeField)) {
                    prefix = "f;";
                }
                return prefix + Long.toString(((ICodeNode) elt).getItemId());
            }
            return null;
        }

        public boolean shouldExpand(String source, Object target) {
            return ((target instanceof ICodeNode)) && ((((ICodeNode) target).getObject() instanceof ICodePackage));
        }
    }

    class LabelProvider extends StyledCellLabelProvider implements IValueProvider {
        CodeHierarchyView.ContentProvider cp;
        ProcessorType procType;

        LabelProvider(CodeHierarchyView.ContentProvider cp, ProcessorType procType) {
            this.cp = cp;
            this.procType = procType;
        }

        public void update(ViewerCell cell) {
            int index = cell.getColumnIndex();
            String text = "Hierarchy";
            ICodeItem item = null;
            String iconRelPath = null;
            Image img = null;
            AssetManagerOverlay overlay = null;
            Object o = cell.getElement();
            if (((o instanceof IArrayGroup)) && (((IArrayGroup) o).isSingle())) {
                o = ((IArrayGroup) o).getFirstElement();
            }
            if ((o instanceof ICodeNode)) {
                item = ((ICodeNode) o).getObject();
                text = getCodeNodeStringAt(o, index);
            } else if ((o instanceof IArrayGroup)) {
                text = getArrayGroupStringAt((IArrayGroup) o, index);
            }
            if ((index == 0) && (item != null)) {
                int flags = item.getGenericFlags();
                String visi = "default";
                if ((flags & 0x1) != 0) {
                    visi = "public";
                } else if ((flags & 0x2) != 0) {
                    visi = "private";
                } else if ((flags & 0x4) != 0) {
                    visi = "protected";
                }
                if ((item instanceof ICodePackage)) {
                    iconRelPath = "eclipse/package_obj.png";
                } else if ((item instanceof ICodeType)) {
                    iconRelPath = "eclipse/types.png";
                } else if ((item instanceof ICodeClass)) {
                    if ((flags & 0x200) != 0) {
                        iconRelPath = "eclipse/int_obj.png";
                    } else if ((flags & 0x4000) != 0) {
                        iconRelPath = "eclipse/enum_obj.png";
                    } else {
                        iconRelPath = "eclipse/class_obj.png";
                    }
                } else if ((item instanceof ICodeField)) {
                    iconRelPath = "eclipse/field_" + visi + "_obj.png";
                } else if ((item instanceof ICodeMethod)) {
                    iconRelPath = "eclipse/method_" + visi + "_obj.png";
                }
                if ((iconRelPath != null) && (((item instanceof ICodeField)) || ((item instanceof ICodeMethod)))) {
                    if ((flags & 0x8) != 0) {
                        if (overlay == null) {
                            overlay = new AssetManagerOverlay();
                        }
                        overlay.addLayer("eclipse/static_co.png", new Point(0, 0));
                    }
                    if ((flags & 0x10) != 0) {
                        if (overlay == null) {
                            overlay = new AssetManagerOverlay();
                        }
                        overlay.addLayer("eclipse/final_co.png", new Point(9, 0));
                    }
                    if ((flags & 0x10000) != 0) {
                        if (overlay == null) {
                            overlay = new AssetManagerOverlay();
                        }
                        overlay.addLayer("eclipse/constr_ovr.png", new Point(0, 0));
                    }
                    if ((!(CodeHierarchyView.this.unit instanceof INativeCodeUnit)) && ((flags & 0x100) != 0)) {
                        if (overlay == null) {
                            overlay = new AssetManagerOverlay();
                        }
                        overlay.addLayer("eclipse/native_co.png", new Point(0, 0));
                    }
                    if ((flags & 0x20) != 0) {
                        if (overlay == null) {
                            overlay = new AssetManagerOverlay();
                        }
                        overlay.addLayer("eclipse/synch_co.png", new Point(9, 0));
                    }
                    if ((flags & 0x400) != 0) {
                        if (overlay == null) {
                            overlay = new AssetManagerOverlay();
                        }
                        overlay.addLayer("eclipse/abstract_co.png", new Point(0, 0));
                    }
                }
            }
            if (text != null) {
                cell.setText(text);
            }
            if ((img == null) && (iconRelPath != null)) {
                img = UIAssetManager.getInstance().getImage(iconRelPath, overlay);
            }
            if (img != null) {
                cell.setImage(img);
            }
            super.update(cell);
        }

        public String getStringAt(Object element, int key) {
            if ((element instanceof IArrayGroup)) {
                return getArrayGroupStringAt((IArrayGroup) element, key);
            }
            return getCodeNodeStringAt(element, key);
        }

        public String getCodeNodeStringAt(Object element, int key) {
            Object[] r = this.cp.getRowElements(element);
            if ((r != null) && (key < r.length)) {
                Object o = r[key];
                if (key == 0) {
                    return (String) o;
                }
                if (key == 1) {
                    return formatLongHex(new StringBuilder(), (Long) o).toString();
                }
                if (key == 2) {
                    return Integer.toHexString(((Integer) o).intValue()).toUpperCase() + "h";
                }
                if (key == 3) {
                    if (o == null) {
                        return "";
                    }
                    if (this.procType == ProcessorType.ARM) {
                        return ((Integer) o).intValue() == 16 ? "T32" : "A32";
                    }
                    return o.toString();
                }
            }
            return "";
        }

        public String getArrayGroupStringAt(IArrayGroup element, int key) {
            if (((element instanceof ArrayLogicalGroup)) && (key == 0)) {
                return ((ArrayLogicalGroup) element).getGroupName();
            }
            if (element.isSingle()) {
                return getStringAt(element.getFirstElement(), key);
            }
            Object[] r = this.cp.getRowElements(element);
            if ((r != null) && (key < r.length)) {
                Object o = r[key];
                if (key == 0) {
                    if ((o instanceof Object[])) {
                        return ((Object[]) (Object[]) o)[0] + " .. " + Strings.safe(((Object[]) (Object[]) o)[1]);
                    }
                } else if ((key == 1) && ((o instanceof Object[])) && ((CodeHierarchyView.this.viewer.getTree().getSortColumn() == null) || (CodeHierarchyView.this.viewer.getTree().getSortColumn().getText().equals("Address")))) {
                    StringBuilder stb = new StringBuilder();
                    formatLongHex(stb, (Long) ((Object[]) (Object[]) o)[0]);
                    stb.append(" .. ");
                    formatLongHex(stb, (Long) ((Object[]) (Object[]) o)[1]);
                    return stb.toString();
                }
            }
            return "";
        }

        private StringBuilder formatLongHex(StringBuilder stb, Long o) {
            if (o == null) {
                return stb;
            }
            return stb.append(Long.toHexString(o.longValue()).toUpperCase()).append("h");
        }

        public String getString(Object element) {
            return getStringAt(element, 0);
        }
    }

    private AbstractFilteredFilter getAbstractFilter() {
        ViewerFilter[] filters = this.viewer.getFilters();
        if ((filters != null) && (filters.length > 0) && ((filters[0] instanceof AbstractFilteredFilter))) {
            return (AbstractFilteredFilter) filters[0];
        }
        return null;
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case REFRESH:
            case FIND:
                return true;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case REFRESH:
                this.refresher.request();
                return true;
            case FIND:
                this.pt.setFilterVisibility(true);
                return true;
        }
        return false;
    }

    private Object getSelectedElement() {
        ISelection selection = this.viewer.getSelection();
        if (!(selection instanceof TreeSelection)) {
            return null;
        }
        Object elt = ((TreeSelection) selection).getFirstElement();
        if (((elt instanceof IArrayGroup)) && (((IArrayGroup) elt).isSingle())) {
            elt = ((IArrayGroup) elt).getFirstElement();
        }
        return elt;
    }

    public ICodeNode getSelectedNode() {
        Object elt = getSelectedElement();
        if (!(elt instanceof ICodeNode)) {
            return null;
        }
        return (ICodeNode) elt;
    }

    public boolean isActiveItem(IItem item) {
        return (item != null) && (getActiveItem() == item);
    }

    public IItem getActiveItem() {
        Object elt = getSelectedElement();
        if (!(elt instanceof IItem)) {
            return null;
        }
        return (IItem) elt;
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        ICodeNode node = getSelectedNode();
        if (node == null) {
            return null;
        }
        ICodeItem item = node.getObject();
        if (item == null) {
            return null;
        }
        return item.getAddress();
    }

    public void dispose() {
        super.dispose();
        if (this.contentProvider != null) {
            this.contentProvider.dispose();
        }
    }

    public byte[] export() {
        return Strings.encodeUTF8(TreeUtil.buildXml(this.tree, 2));
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TREE;
    }

    public void focusOnAddress(String address) {
        ICodeNode node = ((ICodeUnit) this.unit).getHierarchy().findNode(address, true);
        if (node != null) {
            focusOnNode(node);
        }
    }

    public void focusOnNode(ICodeNode node) {
        ISelection selection = new StructuredSelection(node);
        List<Object> path = new ArrayList();
        path.add(((ICodeUnit) this.unit).getHierarchy().getRoot());
        path.add(node);
        selection = new TreeSelection(new TreePath(path.toArray()));
        this.viewer.reveal(node);
        this.viewer.setSelection(selection);
        if (getSelectedElement() == null) {
            path = new ArrayList();
            boolean autoExpand = true;
            if (autoExpand) {
                List<ICodeNode> pathNode = new ArrayList();
                getPathNode(node, this.contentProvider.getRoot(), pathNode, true);
                if ((usesExplicitDefaultPackage()) && (!this.extraDetails) && (!(((ICodeNode) pathNode.get(0)).getObject() instanceof ICodePackage)) && (this.contentProvider.defaultPackage != null)) {
                    pathNode.add(0, this.contentProvider.defaultPackage);
                }
                try {
                    this.viewer.getTree().setRedraw(false);
                    TreeItem[] items = this.viewer.getTree().getItems();
                    for (ICodeNode o : pathNode) {
                        List<IArrayGroup> arrayPath = new ArrayList();
                        TreeItem item = getIntermediateNodes(o, items, arrayPath);
                        for (IArrayGroup a : arrayPath) {
                            this.viewer.setExpandedState(a, true);
                        }
                        this.viewer.setExpandedState(o, true);
                        item = updateTreeItem(item, arrayPath, 0);
                        path.addAll(arrayPath);
                        if ((arrayPath.isEmpty()) || (!((IArrayGroup) arrayPath.get(arrayPath.size() - 1)).isSingle())) {
                            path.add(o);
                            item = updateTreeItem(item, o);
                        }
                        if (item == null) break;
                        items = item.getItems();
                    }
                    logger.debug("Path to item: %s", new Object[]{Strings.joinList(path)});
                    selection = new TreeSelection(new TreePath(path.toArray()));
                    this.viewer.setSelection(selection);
                    if (getSelectedElement() == null) {
                        AbstractFilteredFilter filter = getAbstractFilter();
                        String msg = null;
                        if ((filter != null) && (filter.isFiltered())) {
                            msg = Strings.f("The item %s can not be focused.\nMaybe it is hidden by current filter?", new Object[]{node.getLabel()});
                        } else {
                            msg = Strings.f("The item  %s can not be focused.", new Object[]{node.getLabel()});
                            this.context.getErrorHandler().processThrowableSilent(new JebRuntimeException(msg));
                        }
                        UI.error(msg);
                    }
                } finally {
                    this.viewer.getTree().setRedraw(true);
                }
                return;
            }
            getPath(node, this.viewer.getTree().getItems(), path);
            path.add(0, ((ICodeUnit) this.unit).getHierarchy().getRoot());
            int pathSize = path.size();
            while ((getSelectedElement() == null) && (!path.isEmpty())) {
                selection = new TreeSelection(new TreePath(path.toArray()));
                this.viewer.setSelection(selection);
                path.remove(path.size() - 1);
            }
            boolean focused = (getSelectedElement() != null) && (path.size() + 1 == pathSize);
            if (!focused) {
                String msg = "For performance reason, the node can not be automatically expanded.";
                logger.warn(msg, new Object[0]);
                UI.infoOptional(getViewer().getTree().getShell(), null, msg, "dlgCodeHierNoExpand");
            }
        }
    }

    private TreeItem updateTreeItem(TreeItem item, ICodeNode match) {
        if (item == null) {
            return null;
        }
        if (item.getData() == match) {
            return item;
        }
        for (TreeItem t : item.getItems()) {
            if (t.getData() == match) {
                return t;
            }
        }
        return null;
    }

    private TreeItem updateTreeItem(TreeItem item, List<IArrayGroup> arrayPath, int i) {
        if (arrayPath.isEmpty()) {
            return item;
        }
        IArrayGroup match = (IArrayGroup) arrayPath.get(i);
        if ((arrayPath.size() == 1) && (item.getData() == match)) {
            return item;
        }
        for (TreeItem t : item.getItems()) {
            if (t.getData() == match) {
                if (i >= arrayPath.size() - 1) {
                    return t;
                }
                return updateTreeItem(t, arrayPath, i + 1);
            }
        }
        return null;
    }

    private TreeItem getIntermediateNodes(ICodeNode node, TreeItem[] items, List<IArrayGroup> arrayPath) {
        if (items != null) {
            for (TreeItem t : items) {
                if (t.getData() == node) {
                    return t;
                }
                if (((t.getData() instanceof IArrayGroup)) && (getIntermediatePathArrayGroup(node, (IArrayGroup) t.getData(), arrayPath))) {
                    arrayPath.add(0, (IArrayGroup) t.getData());
                    return t;
                }
            }
        }
        return null;
    }

    private boolean getIntermediatePathArrayGroup(ICodeNode node, IArrayGroup ag, List<IArrayGroup> arrayPath) {
        if (ag.isSingle()) {
            if (ag.getFirstElement() == node) {
                return true;
            }
        } else {
            for (Object o : ag.getChildren()) {
                if (o == node) {
                    return true;
                }
                if (((o instanceof IArrayGroup)) && (getIntermediatePathArrayGroup(node, (IArrayGroup) o, arrayPath))) {
                    arrayPath.add(0, (IArrayGroup) o);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getPath(ICodeNode node, TreeItem[] items, List<Object> path) {
        if (items != null) {
            for (TreeItem t : items) {
                if (getPath(node, t.getData(), path)) {
                    return true;
                }
                if (getPath(node, t.getItems(), path)) {
                    path.add(0, t.getData());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getPath(ICodeNode node, Object o, List<Object> path) {
        if ((o instanceof ICodeNode)) {
            if (getPathNodeObj(node, (ICodeNode) o, path)) {
                path.add(0, o);
                return true;
            }
        } else if (((o instanceof IArrayGroup)) && (getPathArrayGroup(node, (IArrayGroup) o, path))) {
            path.add(0, o);
            return true;
        }
        return false;
    }

    private boolean getPathArrayGroup(ICodeNode node, IArrayGroup ag, List<Object> path) {
        if (ag.isSingle()) {
            if (ag.getFirstElement() == node) {
                return true;
            }
        } else {
            for (Object o : ag.getChildren()) {
                if (getPath(node, o, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getPathNodeObj(ICodeNode node, ICodeNode search, List<Object> path) {
        if (search == node) {
            return true;
        }
        for (ICodeNode o : search.getChildren()) {
            if (getPathNodeObj(node, o, path)) {
                path.add(0, o);
                return true;
            }
        }
        return false;
    }

    private boolean getPathNode(ICodeNode node, ICodeNode search, List<ICodeNode> path, boolean first) {
        if (search == node) {
            if (first) {
                path.add(search);
            }
            return true;
        }
        for (ICodeNode o : search.getChildren()) {
            if (getPathNode(node, o, path, false)) {
                path.add(0, o);
                return true;
            }
        }
        return false;
    }
}


