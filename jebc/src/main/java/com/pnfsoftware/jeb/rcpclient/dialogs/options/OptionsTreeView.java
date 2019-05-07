package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinition;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.properties.IPropertyType;
import com.pnfsoftware.jeb.core.properties.IPropertyTypeBoolean;
import com.pnfsoftware.jeb.core.properties.impl.PropertyDefinitionManager;
import com.pnfsoftware.jeb.rcpclient.extensions.ContextMenuFilter;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


public class OptionsTreeView
        extends PatternTreeView {
    private static final ILogger logger = GlobalLog.getLogger(OptionsTreeView.class);

    private static final String[] titleColumns = {S.s(667), S.s(779), S.s(247), S.s(815)};
    private final OptionsChanges.Changes changes;

    public static OptionsTreeView build(Composite parent, IPropertyManager pm, OptionsChanges.Changes changes, boolean expandAfterFilter) {
        LabelProvider labelProvider = new LabelProvider();
        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);
        return new OptionsTreeView(parent, pm, changes, labelProvider, patternMatcher, expandAfterFilter);
    }


    private OptionsTreeView(Composite parent, IPropertyManager pm, OptionsChanges.Changes changes, LabelProvider labelProvider, IPatternMatcher patternMatcher, boolean expandAfterFilter) {
        super(parent, 65664, titleColumns, null, patternMatcher, expandAfterFilter);
        this.changes = changes;


        final FilteredTreeViewer viewer = getTreeViewer();
        ContextMenuFilter.addContextMenu(viewer.getViewer(), getFilterText(), labelProvider, new String[]{
                S.s(667), S.s(779)}, new Boolean[]{Boolean.FALSE, Boolean.TRUE});

        final TreeContentProvider contentProvider = new TreeContentProvider();

        EditingSupport editingSupport = new ValueEditingSupport(viewer.getViewer());
        ColumnViewerToolTipSupport.enableFor(viewer.getViewer());
        changes.listeners.add(new Listener() {
            public void handleEvent(Event event) {
                Object[] data = (Object[]) event.data;
                for (OptionsTreeView.PropertyLine prop : contentProvider.propertyLines) {
                    if (prop.fqname.substring(1).equals(data[0])) {
                        prop.value = data[1];
                        prop.display = Objects.toString(data[1]);
                        ((TreeViewer) viewer.getViewer()).update(prop, null);
                    }

                }

            }
        });
        Tree tree = getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        TreeColumn[] cols = tree.getColumns();
        TreeViewerColumn tcv = new TreeViewerColumn((TreeViewer) viewer.getViewer(), cols[3]);
        tcv.setEditingSupport(editingSupport);


        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.setInput(pm);


        viewer.expandAll();
        for (TreeColumn col : cols) {
            col.pack();
        }
        cols[0].setWidth(cols[0].getWidth() + 20);
        if (cols[2].getWidth() > 200) {
            cols[2].setWidth(200);
        }
        if (cols[3].getWidth() < 200) {
            cols[3].setWidth(200);
        }
    }

    public static class TreeContentProvider implements IFilteredTreeContentProvider {
        IPropertyManager pm;
        List<OptionsTreeView.PropertyLine> propertyLines = new ArrayList();


        public void dispose() {
        }


        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.pm = ((IPropertyManager) newInput);
        }

        public Object[] getElements(Object inputElement) {
            IPropertyManager pm = (IPropertyManager) inputElement;
            IPropertyDefinitionManager pdm = pm.getPropertyDefinitionManager();
            if (pdm == null) {
                return ArrayUtil.NO_OBJECT;
            }
            return new Object[]{pdm};
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element) != null;
        }

        public Object[] getChildren(Object parentElement) {
            if ((parentElement instanceof IPropertyDefinitionManager)) {
                IPropertyDefinitionManager pdm = (IPropertyDefinitionManager) parentElement;
                return enumerate(this.pm, pdm);
            }

            return ArrayUtil.NO_OBJECT;
        }

        private Object[] enumerate(IPropertyManager pm, IPropertyDefinitionManager pdm) {
            List<Object> r = new ArrayList();


            for (IPropertyDefinition definition : pdm.getDefinitions())
                if (!definition.isInternal()) {

                    OptionsTreeView.PropertyLine line = new OptionsTreeView.PropertyLine();
                    line.fqname = (pdm.getNamespace() + "." + definition.getName());
                    line.definition = definition;
                    line.value = pm.getValue(line.fqname, 0, true);
                    if (line.value != null) {
                        line.display = line.value.toString();
                    } else {
                        line.value = pm.getValue(line.fqname, 1, true);
                        if (line.value != null) {
                            line.display = "<master>";
                        } else {
                            line.value = pm.getValue(line.fqname, 3, true);
                            if (line.value != null) {
                                line.display = "<default>";
                            } else {
                                line.display = "<error>";
                            }
                        }
                    }
                    r.add(line);
                    this.propertyLines.add(line);
                }
            Collections.sort(r, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((OptionsTreeView.PropertyLine) o1).definition.getName().compareTo(((OptionsTreeView.PropertyLine) o2).definition.getName());
                }
            });


            for (IPropertyDefinitionManager pdmNext : pdm.getChildren()) {
                r.add(pdmNext);
            }


            return r.toArray();
        }

        public String getString(Object element) {
            if ((element instanceof OptionsTreeView.PropertyLine)) {
                return ((OptionsTreeView.PropertyLine) element).fqname.substring(1);
            }
            if ((element instanceof PropertyDefinitionManager)) {
                return ((PropertyDefinitionManager) element).getRegion();
            }
            if (element != null) {
                return element.toString();
            }
            return null;
        }

        public Object[] getRowElements(Object row) {
            return new Object[]{getString(row)};
        }
    }

    static class LabelProvider extends StyledCellLabelProvider implements IValueProvider {
        public void update(ViewerCell cell) {
            String text = "";
            Object elt = cell.getElement();
            int index = cell.getColumnIndex();

            if (((elt instanceof IPropertyDefinitionManager)) &&
                    (index == 0)) {
                text = ((IPropertyDefinitionManager) elt).getRegion();
                if (text.isEmpty()) {
                    text = "<root>";
                }
            }

            if ((elt instanceof OptionsTreeView.PropertyLine)) {
                if (index == 0) {
                    text = ((OptionsTreeView.PropertyLine) elt).definition.getName();
                } else if (index == 1) {
                    text = ((OptionsTreeView.PropertyLine) elt).definition.getType().toString();
                } else if (index == 2) {
                    text = ((OptionsTreeView.PropertyLine) elt).definition.getType().getDefault().toString();
                } else if (index == 3) {
                    text = ((OptionsTreeView.PropertyLine) elt).display;
                }
            }

            cell.setText(text);
            super.update(cell);
        }

        public String getStringAt(Object element, int key) {
            if ((element instanceof OptionsTreeView.PropertyLine)) {
                if (key == 0) {
                    return ((OptionsTreeView.PropertyLine) element).fqname.substring(1);
                }
                if (key == 1) {
                    return ((OptionsTreeView.PropertyLine) element).definition.getType().toString();
                }
            } else if (((element instanceof PropertyDefinitionManager)) &&
                    (key == 0)) {
                return ((PropertyDefinitionManager) element).getRegion();
            }

            if (element != null) {
                return element.toString();
            }
            return null;
        }

        public String getString(Object element) {
            return getStringAt(element, 0);
        }
    }

    class ValueEditingSupport extends EditingSupport {
        ColumnViewer viewer;
        TextCellEditor editor;
        CheckboxCellEditor booleanEditor;

        public ValueEditingSupport(ColumnViewer viewer) {
            super(viewer);
            this.viewer = viewer;

            Composite parent = (Composite) viewer.getControl();
            this.editor = new TextCellEditor(parent);
            this.booleanEditor = new CheckboxCellEditor(parent);
        }

        protected CellEditor getCellEditor(Object element) {
            if (!(element instanceof OptionsTreeView.PropertyLine)) {
                return null;
            }

            OptionsTreeView.PropertyLine line = (OptionsTreeView.PropertyLine) element;
            if ((line.definition.getType() instanceof IPropertyTypeBoolean)) {
                return this.booleanEditor;
            }

            return this.editor;
        }

        protected boolean canEdit(Object element) {
            return true;
        }

        protected Object getValue(Object element) {
            OptionsTreeView.PropertyLine line = (OptionsTreeView.PropertyLine) element;
            if ((line.definition.getType() instanceof IPropertyTypeBoolean)) {
                return Boolean.valueOf(line.value.toString());
            }

            Object value = ((OptionsTreeView.PropertyLine) element).value;
            if (value == null) {
                return "";
            }
            return value.toString();
        }

        protected void setValue(Object element, Object value) {
            OptionsTreeView.PropertyLine line = (OptionsTreeView.PropertyLine) element;
            if (!line.definition.getType().validate(value)) {
                OptionsTreeView.logger.i("Illegal value for property", new Object[0]);
                return;
            }

            OptionsTreeView.logger.i("Property %s is being updated", new Object[]{line.fqname});
            line.value = value;
            line.display = value.toString();

            this.viewer.update(element, null);

            OptionsTreeView.this.changes.addChange(line.fqname, value);
        }
    }

    static class PropertyLine {
        public String fqname;
        public IPropertyDefinition definition;
        public Object value;
        public String display;
        public boolean dirty;

        public String toString() {
            return this.fqname + ": " + this.value;
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\options\OptionsTreeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */