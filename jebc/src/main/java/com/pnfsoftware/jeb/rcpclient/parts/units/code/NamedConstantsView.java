package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.CodeConstant;
import com.pnfsoftware.jeb.core.units.code.asm.type.CodeConstantManager;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeLibrary;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeLibraryService;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractFilteredTableView;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.util.regex.ILabelValueProvider;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class NamedConstantsView extends AbstractFilteredTableView<INativeCodeUnit<?>, CodeConstant> {
    private static final ILogger logger = GlobalLog.getLogger(NativeTypesView.class);

    public NamedConstantsView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<?> pbcu, IRcpUnitView unitView, Object sourceValue) {
        super(parent, style, pbcu, unitView, context, new ContentProvider(pbcu, sourceValue));
        setLayout(new GridLayout(1, false));
        String[] columnNames = {S.s(591)};
        setLabelProvider(new LabelProvider());
        buildFilteredViewer(this, columnNames, false);
        layout();
    }

    protected void initFilteredView(FilteredTableView view) {
        view.setLayoutData(UIUtil.createGridDataFill(true, true));
    }

    protected boolean isCorrectRow(Object obj) {
        return obj instanceof CodeConstant;
    }

    public CodeConstant getSelectedRow() {
        Object row = getSelectedRawRow();
        if (!(row instanceof CodeConstant)) {
            return null;
        }
        return (CodeConstant) row;
    }

    static class ContentProvider implements IFilteredTableContentProvider {
        IEventListener listener;
        INativeCodeUnit<?> pbcu;
        Object sourceValue;
        ViewerRefresher refresher;

        public ContentProvider(INativeCodeUnit<?> pbcu, Object sourceValue) {
            this.pbcu = pbcu;
            this.sourceValue = sourceValue;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if ((oldInput != null) && (this.listener != null)) {
                ((INativeCodeUnit) oldInput).removeListener(this.listener);
                this.listener = null;
            }
            if (this.refresher == null) {
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer);
            }
            this.pbcu = ((INativeCodeUnit) newInput);
            if (this.pbcu == null) {
                return;
            }
            this.listener = new IEventListener() {
                public void onEvent(IEvent e) {
                    if (NamedConstantsView.ContentProvider.this.pbcu != null) {
                        NamedConstantsView.ContentProvider.this.refresher.request();
                    }
                }
            };
            this.pbcu.addListener(this.listener);
        }

        public Object[] getElements(Object inputElement) {
            List<CodeConstant> entries = new ArrayList();
            TypeLibraryService tlsvc = this.pbcu.getTypeLibraryService();
            for (ITypeLibrary tlib : tlsvc.getLoadedTypeLibraries()) {
                entries.addAll(tlib.getConstantManager().getNamedConstantsByValue(this.sourceValue));
            }
            return entries.toArray();
        }

        public Object[] getRowElements(Object row) {
            CodeConstant e = (CodeConstant) row;
            return new Object[]{e.getName()};
        }

        public boolean isChecked(Object row) {
            return false;
        }
    }

    static class LabelProvider implements ILabelValueProvider, ITableLabelProvider {
        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            CodeConstant e = (CodeConstant) element;
            if (columnIndex == 0) {
                return e == null ? "" : e.getName();
            }
            throw new RuntimeException();
        }

        public String getString(Object element) {
            return getColumnText(element, 0);
        }

        public String getStringAt(Object element, int key) {
            return getColumnText(element, key);
        }
    }
}


