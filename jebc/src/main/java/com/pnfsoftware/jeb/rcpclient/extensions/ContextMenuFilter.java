
package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.IFilterText;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.OperationCopy;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;

public class ContextMenuFilter
        implements IContextMenu {
    private ColumnViewer viewer;
    private IFilterText filterText;
    private IValueProvider valueProvider;
    private String[] filterLabels;
    private Boolean[] displayPrefixes;

    public static ContextMenu addContextMenu(ColumnViewer viewer, IFilterText filterText, IValueProvider valueProvider, String[] filterLabels, Boolean[] displayPrefix) {
        return addContextMenu(viewer, filterText, valueProvider, filterLabels, displayPrefix, null);
    }

    public static ContextMenu addContextMenu(ColumnViewer viewer, IFilterText filterText, IValueProvider valueProvider, String[] filterLabels, Boolean[] displayPrefix, IContextMenu contextMenu) {
        ContextMenu contextMenuMgr = new ContextMenu(viewer.getControl());
        if (contextMenu != null) {
            contextMenuMgr.addContextMenu(contextMenu);
        }
        ContextMenuFilter cmf = new ContextMenuFilter(viewer, filterText, valueProvider, filterLabels, displayPrefix);
        contextMenuMgr.addContextMenu(cmf);
        return contextMenuMgr;
    }

    public static ContextMenu addCopyEntry(ContextMenu ctxMenu, Control control, IOperable operable) {
        ctxMenu.addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                menuMgr.add(new Separator());
                menuMgr.add(new OperationCopy(operable));
            }
        });
        control.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (((e.stateMask & SWT.MOD1) == SWT.MOD1) && (e.keyCode == 99)) {
                    OperationRequest op = new OperationRequest(Operation.COPY);
                    if (operable.verifyOperation(op)) {
                        boolean success = operable.doOperation(op);
                        if (success) {
                            e.doit = false;
                        }
                    }
                }
            }
        });
        return ctxMenu;
    }

    protected ContextMenuFilter(ColumnViewer viewer, IFilterText filterText, IValueProvider valueProvider, String[] filterLabels, Boolean[] displayPrefixes) {
        this.viewer = viewer;
        this.filterText = filterText;
        this.valueProvider = valueProvider;
        this.filterLabels = filterLabels;
        this.displayPrefixes = displayPrefixes;
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
        Object element = selection.getFirstElement();
        if (element == null) {
            return;
        }
        for (int i = 0; i < this.filterLabels.length; i++) {
            if ((this.filterLabels[i] != null) && (this.valueProvider.getStringAt(element, i) != null)) {
                boolean displayPrefix = true;
                if ((this.displayPrefixes != null) && (i < this.displayPrefixes.length)) {
                    displayPrefix = this.displayPrefixes[i].booleanValue();
                }
                menuMgr.add(new FilterAction(this.filterLabels[i], i, displayPrefix));
            }
        }
    }

    private class FilterAction extends Action {
        private int filterLabelIndex;
        private boolean displayPrefix;
        private String columnName;

        public FilterAction(String columnName, int filterLabelIndex, boolean displayPrefix) {
            super();
            setEnabled(true);
            this.filterLabelIndex = filterLabelIndex;
            this.displayPrefix = displayPrefix;
            this.columnName = columnName;
        }

        public void run() {
            IStructuredSelection selection = (IStructuredSelection) ContextMenuFilter.this.viewer.getSelection();
            String newFilterValue = ContextMenuFilter.this.valueProvider.getStringAt(selection.getFirstElement(), this.filterLabelIndex);
            if (newFilterValue != null) {
                ContextMenuFilter.this.filterText.submitText(this.displayPrefix ? addSearchPrefix(newFilterValue) : newFilterValue);
            }
        }

        protected String addSearchPrefix(String value) {
            if (Strings.isBlank(this.columnName)) {
                return value;
            }
            return this.columnName.toLowerCase() + ":\"" + value.replace("\"", "\\\"") + "\"";
        }
    }
}


