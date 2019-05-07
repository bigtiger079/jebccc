
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;


import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.exceptions.DebuggerException;
import com.pnfsoftware.jeb.core.exceptions.JebException;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThreadStackFrame;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerVariable;
import com.pnfsoftware.jeb.core.units.code.debug.ITypedValue;
import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueComposite;
import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueNumber;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueDouble;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueFloat;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueObject;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueRaw;
import com.pnfsoftware.jeb.rcpclient.AssetManagerOverlay;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractArrayGroupFilteredTreeContentProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredViewerComparator;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
import com.pnfsoftware.jeb.rcpclient.util.DbgTypedValueUtil;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;
import java.util.Objects;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


public class DbgVariablesView
        extends AbstractUnitFragment<IDebuggerUnit>
        implements IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(DbgVariablesView.class);


    private FilteredTreeViewer viewer;


    private IDebuggerThreadStackFrame targetFrame;


    public DbgVariablesView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit) {

        super(parent, flags, unit, null, context);

        setLayout(new FillLayout());


        String[] titleColumns = {"Name", "Type", "Value", "Extra"};

        LabelProvider labelProvider = new LabelProvider();

        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);

        boolean expandAfterFilter = context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");

        PatternTreeView ftv = new PatternTreeView(this, 65536, titleColumns, null, patternMatcher, expandAfterFilter);


        ftv.setFilterVisibility(false, false);

        this.viewer = ftv.getTreeViewer();


        Tree tree = ftv.getTree();

        tree.setHeaderVisible(true);

        tree.setLinesVisible(true);


        TreeColumn[] cols = tree.getColumns();

        TreeViewerColumn tcv1 = new TreeViewerColumn((TreeViewer) this.viewer.getViewer(), cols[1]);

        tcv1.setEditingSupport(new ValueEditingSupport(this.viewer.getViewer(), 2));

        TreeViewerColumn tcv2 = new TreeViewerColumn((TreeViewer) this.viewer.getViewer(), cols[2]);

        tcv2.setEditingSupport(new ValueEditingSupport(this.viewer.getViewer(), 0));

        TreeViewerColumn tcv3 = new TreeViewerColumn((TreeViewer) this.viewer.getViewer(), cols[3]);

        tcv3.setEditingSupport(new ValueEditingSupport(this.viewer.getViewer(), 1));


        this.viewer.setContentProvider(new TreeContentProvider());

        this.viewer.setLabelProvider(labelProvider);

        this.viewer.setInput(unit);


        this.viewer.expandToLevel(2);


        for (TreeColumn col : cols) {

            col.pack();

        }


        new ContextMenu(tree).addContextMenu(this);

    }


    public void fillContextMenu(IMenuManager menuMgr) {

        Object elt = getSelectedNode();

        if ((elt instanceof IDebuggerVariable)) {

            final IDebuggerVariable v = (IDebuggerVariable) elt;

            menuMgr.add(new Action("View string representation") {

                public void run() {

                    if ((v.getTypedValue() instanceof ValueObject)) {

                        try {

                            IDebuggerThread t = ((IDebuggerUnit) DbgVariablesView.this.getUnit()).getDefaultThread();

                            if (t != null) {

                                long threadId = t.getId();

                                ITypedValue result = ((ValueObject) v.getTypedValue()).invoke("toString", threadId, null);


                                if (result != null) {

                                    DbgVariablesView.logger.info(result.toString(), new Object[0]);

                                }

                            } else {

                                DbgVariablesView.logger.error("Can not call toString: no default thread", new Object[0]);

                            }

                        } catch (JebException e) {

                            DbgVariablesView.logger.catching(e);

                        }

                    }

                }


                public boolean isEnabled() {

                    return ((v.getTypedValue() instanceof ValueObject)) &&
                            (((ValueObject) v.getTypedValue()).getObjectId() != 0L);

                }

            });

        }


        addOperationsToContextMenu(menuMgr);

    }


    public void setTargetFrame(IDebuggerThreadStackFrame frame) {

        this.targetFrame = frame;

        this.viewer.refresh();

    }


    public IDebuggerThreadStackFrame getTargetFrame() {

        if (this.targetFrame != null) {

            return this.targetFrame;

        }


        IDebuggerThread t = ((IDebuggerUnit) getUnit()).getDefaultThread();

        if ((t != null) &&
                (t.getStatus() == DebuggerThreadStatus.PAUSED)) {

            List<? extends IDebuggerThreadStackFrame> frames = t.getFrames();

            if ((frames != null) && (!frames.isEmpty())) {

                return (IDebuggerThreadStackFrame) frames.get(0);

            }

        }


        return null;

    }


    private Object getSelectedNode() {

        ITreeSelection treesel = (ITreeSelection) this.viewer.getSelection();

        if (treesel.isEmpty()) {

            return null;

        }


        return treesel.getFirstElement();

    }


    private boolean copyValueToClipboard(ITypedValue value) {

        String s = formatValue(value);

        if (s != null) {

            UIUtil.copyTextToClipboard(s);

            return true;

        }

        return false;

    }


    private String formatValue(ITypedValue value) {

        String s = null;

        if ((value instanceof AbstractValueComposite)) {

            s = ((AbstractValueComposite) value).format();

        } else {

            s = value.toString();

        }

        return s;

    }


    public class TreeContentProvider extends AbstractArrayGroupFilteredTreeContentProvider {
        IDebuggerUnit dbg;
        IEventListener listener;


        public TreeContentProvider() {
            super();
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

                    DbgVariablesView.logger.i("Event: %s", new Object[]{e});

                    if ((DbgVariablesView.TreeContentProvider.this.dbg != null) && (e.getSource() == DbgVariablesView.TreeContentProvider.this.dbg)) {

                        UIExecutor.async(viewer.getControl(), new UIRunnable() {

                            public void runi() {

                                if ((DbgVariablesView.TreeContentProvider.this.dbg != null) && (!viewer.getControl().isDisposed())) {
                                    viewer.refresh();

                                }
                            }

                        });

                    }

                }

            };

            this.dbg.addListener(this.listener);

        }


        public Object[] getElements(Object inputElement) {

            try {

                if (((IDebuggerUnit) DbgVariablesView.this.unit).isAttached()) {


                    IDebuggerThreadStackFrame frame = DbgVariablesView.this.getTargetFrame();

                    if (frame != null) {

                        List<? extends IDebuggerVariable> variables = frame.getVariables();

                        if (variables != null) {

                            return variables.toArray();

                        }

                    }

                }

            } catch (DebuggerException e) {

                DbgVariablesView.logger.catching(e);

            }

            return ArrayUtil.NO_OBJECT;

        }


        public Object getParent(Object element) {

            return null;

        }


        public boolean hasChildren2(Object element) {

            ITypedValue val = getTypedValue(element);

            if ((val instanceof AbstractValueComposite)) {

                return ((AbstractValueComposite) val).hasChildren();

            }

            return false;

        }


        public List<?> getChildren2(Object parentElement) {

            ITypedValue val = getTypedValue(parentElement);

            if ((val instanceof AbstractValueComposite)) {

                return ((AbstractValueComposite) val).getValue();

            }

            return null;

        }


        public void sort(Object[] elements) {

            DbgVariablesView.this.viewer.getComparator().sort(DbgVariablesView.this.viewer.getViewer(), elements);

        }


        private ITypedValue getTypedValue(Object parentElement) {

            if ((parentElement instanceof IDebuggerVariable)) {

                return ((IDebuggerVariable) parentElement).getTypedValue();

            }

            if ((parentElement instanceof ITypedValue)) {

                return (ITypedValue) parentElement;

            }

            return null;

        }


        public String getString(Object element) {

            return null;

        }


        public Object[] getRowElements(Object row) {

            return new Object[]{getString(row)};

        }

    }


    class LabelProvider extends StyledCellLabelProvider implements IValueProvider {
        LabelProvider() {
        }


        public void update(ViewerCell cell) {

            Object elt = cell.getElement();

            int index = cell.getColumnIndex();

            cell.setText(Strings.safe(getStringAt(elt, index)));


            if (index == 0) {

                Image img = null;

                if ((elt instanceof IDebuggerVariable)) {

                    int flags = ((IDebuggerVariable) elt).getFlags();

                    if (flags != 0) {

                        String visibilityFlag = null;

                        if ((flags & 0x1) != 0) {

                            visibilityFlag = "eclipse/field_public_obj.png";

                        } else if ((flags & 0x4) != 0) {

                            visibilityFlag = "eclipse/field_protected_obj.png";

                        } else if ((flags & 0x2) != 0) {

                            visibilityFlag = "eclipse/field_private_obj.png";

                        } else {

                            visibilityFlag = "eclipse/field_default_obj.png";

                        }


                        AssetManagerOverlay overlay = null;

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


                        img = UIAssetManager.getInstance().getImage(visibilityFlag, overlay);

                    }

                }

                if (img != null) {

                    cell.setImage(img);

                }

            } else if ((index == 2) &&
                    ((elt instanceof IDebuggerVariable)) &&
                    ((((IDebuggerVariable) elt).getTypedValue() instanceof ValueRaw))) {

                cell.setFont(DbgVariablesView.this.context.getFontManager().getCodeFont());

            }


            super.update(cell);

        }


        public String getString(Object element) {

            return null;

        }


        public String getStringAt(Object element, int index) {

            if ((element instanceof IDebuggerVariable)) {

                IDebuggerVariable v = (IDebuggerVariable) element;

                if (index == 0) {

                    return v.getName();

                }

                if (index == 1) {

                    return v.getTypedValue().getTypeName();

                }

                if ((index >= 2) && (index < 5)) {

                    return DbgVariablesView.this.formatCell(v.getTypedValue(), index - 2);

                }

            }

            if ((element instanceof ITypedValue)) {

                ITypedValue v = (ITypedValue) element;

                if (index == 0) {

                    return "";

                }

                if (index == 1) {

                    return v.getTypeName();

                }

                if ((index >= 2) && (index < 5)) {

                    return DbgVariablesView.this.formatCell(v, index - 2);

                }

            }

            return AbstractArrayGroupFilteredTreeContentProvider.getStringAt(element, index);

        }

    }


    private String formatCell(ITypedValue value, int index) {

        if ((index == 0) || (index == 1)) {

            return DbgTypedValueUtil.formatValue(value, index, (IDebuggerUnit) this.unit);

        }


        return "";

    }


    class ValueEditingSupport extends EditingSupport {
        private static final int COLUMN_VALUE = 0;
        private static final int COLUMN_EXTRA = 1;
        private static final int COLUMN_TYPE = 2;
        ColumnViewer columnVviewer;
        TextCellEditor editor;
        int index;


        public ValueEditingSupport(ColumnViewer viewer, int index) {

            super(viewer);

            this.columnVviewer = viewer;


            Composite parent = (Composite) viewer.getControl();

            this.editor = new TextCellEditor(parent);

            this.index = index;

        }


        protected CellEditor getCellEditor(Object element) {

            if (!(element instanceof IDebuggerVariable)) {

                return null;

            }

            return this.editor;

        }


        protected boolean canEdit(Object element) {

            if (!(element instanceof IDebuggerVariable)) {

                return false;

            }


            IDebuggerVariable v = (IDebuggerVariable) element;

            ITypedValue t = v.getTypedValue();


            if (this.index == 0) {

                return v.canEditValue();

            }

            if (this.index == 1) {

                if ((t instanceof AbstractValueNumber)) {

                    return (v.canEditValue()) && (!(t instanceof ValueFloat)) && (!(t instanceof ValueDouble));

                }

            } else if (this.index == 2) {

                return v.canEditType();

            }


            return false;

        }


        protected Object getValue(Object element) {

            IDebuggerVariable v = (IDebuggerVariable) element;

            return DbgVariablesView.this.formatCell(v.getTypedValue(), this.index);

        }


        protected void setValue(Object element, Object value) {

            IDebuggerVariable v = (IDebuggerVariable) element;


            if ((this.index == 0) || (this.index == 1)) {

                ITypedValue newValue = buildValue(v.getTypedValue(), (String) value);

                if (newValue != null) {

                    IDebuggerThreadStackFrame frame = DbgVariablesView.this.getTargetFrame();

                    if (frame != null) {

                        boolean success = v.setTypedValue(newValue);

                        if (success) {

                            this.columnVviewer.refresh();

                        }

                    }

                }

            } else if (this.index == 2) {

                String visuType = (String) value;

                DbgVariablesView.logger.i("visuType= %s", new Object[]{visuType});

                if ((!Strings.isBlank(visuType)) &&
                        (v.setTypeHint(visuType))) {

                    this.columnVviewer.refresh();

                }

            }

        }


        public ITypedValue buildValue(ITypedValue value, String newValue) {

            if (newValue == null) {

                return null;

            }

            if (DbgTypedValueUtil.equals(value, newValue, (IDebuggerUnit) DbgVariablesView.this.unit)) {

                return null;

            }

            ITypedValue typedValue = DbgTypedValueUtil.buildValue(value, newValue);

            if ((typedValue != null) && (Objects.equals(typedValue.getValue(), value.getValue()))) {

                return null;

            }

            if (typedValue == null) {

                DbgVariablesView.logger.i("Illegal value for IDebuggerVariable '%s'", new Object[]{newValue});

            }

            return typedValue;

        }

    }


    public boolean verifyOperation(OperationRequest req) {

        switch (req.getOperation()) {

            case COPY:

                return getSelectedNode() != null;

        }

        return false;

    }


    public boolean doOperation(OperationRequest req) {

        switch (req.getOperation()) {

            case COPY:

                Object elt = getSelectedNode();

                if ((elt instanceof IDebuggerVariable)) {

                    return copyValueToClipboard(((IDebuggerVariable) elt).getTypedValue());

                }

                if ((elt instanceof ITypedValue)) {

                    return copyValueToClipboard((ITypedValue) elt);

                }

                return false;

        }


        return false;

    }


    public byte[] export() {

        return Strings.encodeUTF8(this.viewer.exportToString());

    }


    public AbstractUnitFragment.FragmentType getFragmentType() {

        return AbstractUnitFragment.FragmentType.TREE;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgVariablesView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */