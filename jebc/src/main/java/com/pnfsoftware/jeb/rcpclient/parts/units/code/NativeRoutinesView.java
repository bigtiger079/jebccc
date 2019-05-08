
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class NativeRoutinesView
        extends AbstractFilteredTableView<INativeCodeUnit<?>, INativeMethodItem> {
    private static final ILogger logger = GlobalLog.getLogger(NativeRoutinesView.class);
    public static final int TYPESOURCE_INUSE = 1;
    public static final int TYPESOURCE_TYPELIBS = 2;
    public static final int TYPESOURCE_ALL = 3;
    private Combo comboSource;

    public NativeRoutinesView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<?> pbcu, IRcpUnitView unitView, int initialTypesource) {
        super(parent, style, pbcu, unitView, context, new ContentProvider(pbcu, initialTypesource));
        setLayout(new GridLayout(1, false));
        ToolBar bar = new ToolBar(this, 320);
        bar.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        ToolItem item = new ToolItem(bar, 2);
        Label label = new Label(bar, 0);
        label.setText("Source: ");
        label.pack();
        item.setWidth(label.getSize().x);
        item.setControl(label);
        item = new ToolItem(bar, 2);
        this.comboSource = new Combo(bar, 2056);
        this.comboSource.add("Show all available routines");
        this.comboSource.add("Show only in-use routines (from unit)");
        this.comboSource.add("Show only importable routines (from typelibs)");
        this.comboSource.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = NativeRoutinesView.this.comboSource.getSelectionIndex();
                ((NativeRoutinesView.ContentProvider) NativeRoutinesView.this.getProvider()).setTypeSource(index);
                NativeRoutinesView.this.refresh();
            }
        });
        this.comboSource.pack();
        item.setWidth(this.comboSource.getSize().x);
        item.setControl(this.comboSource);
        setTypeSource(initialTypesource);
        bar.pack();
        String[] columnNames = {S.s(738)};
        setLabelProvider(new LabelProvider());
        buildFilteredViewer(this, columnNames, false);
        layout();
    }

    protected void initFilteredView(FilteredTableView view) {
        view.setLayoutData(UIUtil.createGridDataFill(true, true));
    }

    public void setTypeSource(int typesource) {
        typesource &= 0x3;
        int index = -1;
        if (typesource == 3) {
            index = 0;
        } else if (typesource == 1) {
            index = 1;
        } else if (typesource == 2) {
            index = 2;
        }
        if (index >= 0) {
            this.comboSource.select(index);
        } else {
            this.comboSource.clearSelection();
        }
    }

    protected boolean isCorrectRow(Object obj) {
        return obj instanceof INativeMethodItem;
    }

    public INativeMethodItem getSelectedRow() {
        Object row = getSelectedRawRow();
        if (!(row instanceof INativeMethodItem)) {
            return null;
        }
        return (INativeMethodItem) row;
    }

    static class ContentProvider implements IFilteredTableContentProvider {
        IEventListener listener;
        INativeCodeUnit<?> pbcu;
        int typesource;
        ViewerRefresher refresher;

        public ContentProvider(INativeCodeUnit<?> pbcu, int initialTypesource) {
            this.pbcu = pbcu;
            this.typesource = initialTypesource;
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
                    if (NativeRoutinesView.ContentProvider.this.pbcu != null) {
                        NativeRoutinesView.ContentProvider.this.refresher.request();
                    }
                }
            };
            this.pbcu.addListener(this.listener);
        }

        public Object[] getElements(Object inputElement) {
            List<INativeMethodItem> entries = new ArrayList();
            Iterator localIterator1;
            INativeMethodItem m;
            if ((this.typesource & 0x1) != 0)
                for (localIterator1 = this.pbcu.getMethods().iterator(); localIterator1.hasNext(); ) {
                    m = (INativeMethodItem) localIterator1.next();
                    entries.add(m);
                }
            if ((this.typesource & 0x2) != 0) {
                TypeLibraryService tlsvc = this.pbcu.getTypeLibraryService();
                for (ITypeLibrary tlib : tlsvc.getLoadedTypeLibraries()) {
                    for (INativeMethodItem item : tlib.getRoutines()) {
                        entries.add(item);
                    }
                }
            }
            return entries.toArray();
        }

        public Object[] getRowElements(Object row) {
            INativeMethodItem e = (INativeMethodItem) row;
            return new Object[]{e.getSignature(true)};
        }

        public boolean isChecked(Object row) {
            return false;
        }

        public void setTypeSource(int index) {
            switch (index) {
                case 0:
                    this.typesource = 3;
                    break;
                case 1:
                    this.typesource = 1;
                    break;
                case 2:
                    this.typesource = 2;
                    break;
                default:
                    this.typesource = 0;
            }
        }
    }

    static class LabelProvider
            implements ILabelValueProvider, ITableLabelProvider {
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
            INativeMethodItem m = (INativeMethodItem) element;
            if (columnIndex == 0) {
                return m == null ? "" : m.getSignature(true);
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


