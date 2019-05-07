/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeLibrary;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.TypeLibraryService;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractFilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.ILabelValueProvider;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.ILabelProviderListener;
/*     */ import org.eclipse.jface.viewers.ITableLabelProvider;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.ToolBar;
/*     */ import org.eclipse.swt.widgets.ToolItem;

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
/*     */ public class NativeTypesView
        /*     */ extends AbstractFilteredTableView<INativeCodeUnit<?>, INativeType>
        /*     */ {
    /*  52 */   private static final ILogger logger = GlobalLog.getLogger(NativeTypesView.class);
    /*     */
    /*     */   public static final int TYPESOURCE_INUSE = 1;
    /*     */
    /*     */   public static final int TYPESOURCE_TYPELIBS = 2;
    /*     */   public static final int TYPESOURCE_ALL = 3;
    /*     */   private Combo comboSource;

    /*     */
    /*     */
    public NativeTypesView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<?> pbcu, IRcpUnitView unitView, int initialTypesource)
    /*     */ {
        /*  62 */
        super(parent, style, pbcu, unitView, context, new ContentProvider(pbcu, initialTypesource));
        /*  63 */
        setLayout(new GridLayout(1, false));
        /*     */
        /*  65 */
        ToolBar bar = new ToolBar(this, 320);
        /*  66 */
        bar.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        /*     */
        /*  68 */
        ToolItem item = new ToolItem(bar, 2);
        /*  69 */
        Label label = new Label(bar, 0);
        /*  70 */
        label.setText("Source: ");
        /*  71 */
        label.pack();
        /*  72 */
        item.setWidth(label.getSize().x);
        /*  73 */
        item.setControl(label);
        /*     */
        /*  75 */
        item = new ToolItem(bar, 2);
        /*  76 */
        this.comboSource = new Combo(bar, 2056);
        /*  77 */
        this.comboSource.add("Show all available types");
        /*  78 */
        this.comboSource.add("Show only in-use types (from unit)");
        /*  79 */
        this.comboSource.add("Show only importable types (from typelibs)");
        /*  80 */
        this.comboSource.addSelectionListener(new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /*  83 */
                int index = NativeTypesView.this.comboSource.getSelectionIndex();
                /*  84 */
                ((NativeTypesView.ContentProvider) NativeTypesView.this.getProvider()).setTypeSource(index);
                /*  85 */
                NativeTypesView.this.refresh();
                /*     */
            }
            /*  87 */
        });
        /*  88 */
        this.comboSource.pack();
        /*  89 */
        item.setWidth(this.comboSource.getSize().x);
        /*  90 */
        item.setControl(this.comboSource);
        /*  91 */
        setTypeSource(initialTypesource);
        /*     */
        /*  93 */
        bar.pack();
        /*     */
        /*  95 */
        String[] columnNames = {S.s(738)};
        /*  96 */
        setLabelProvider(new LabelProvider());
        /*  97 */
        buildFilteredViewer(this, columnNames, false);
        /*     */
        /*     */
        /* 100 */
        layout();
        /*     */
    }

    /*     */
    /*     */
    protected void initFilteredView(FilteredTableView view)
    /*     */ {
        /* 105 */
        view.setLayoutData(UIUtil.createGridDataFill(true, true));
        /*     */
    }

    /*     */
    /*     */
    public void setTypeSource(int typesource)
    /*     */ {
        /* 110 */
        typesource &= 0x3;
        /*     */
        /* 112 */
        int index = -1;
        /* 113 */
        if (typesource == 3) {
            /* 114 */
            index = 0;
            /*     */
        }
        /* 116 */
        else if (typesource == 1) {
            /* 117 */
            index = 1;
            /*     */
        }
        /* 119 */
        else if (typesource == 2) {
            /* 120 */
            index = 2;
            /*     */
        }
        /*     */
        /* 123 */
        if (index >= 0) {
            /* 124 */
            this.comboSource.select(index);
            /*     */
        }
        /*     */
        else {
            /* 127 */
            this.comboSource.clearSelection();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    protected boolean isCorrectRow(Object obj)
    /*     */ {
        /* 133 */
        return obj instanceof INativeType;
        /*     */
    }

    /*     */
    /*     */
    public INativeType getSelectedRow()
    /*     */ {
        /* 138 */
        Object row = getSelectedRawRow();
        /* 139 */
        if (!(row instanceof INativeType)) {
            /* 140 */
            return null;
            /*     */
        }
        /*     */
        /* 143 */
        return (INativeType) row;
        /*     */
    }

    /*     */
    /*     */   static class ContentProvider implements IFilteredTableContentProvider {
        /*     */ IEventListener listener;
        /*     */ INativeCodeUnit<?> pbcu;
        /*     */ int typesource;
        /*     */ ViewerRefresher refresher;

        /*     */
        /*     */
        public ContentProvider(INativeCodeUnit<?> pbcu, int initialTypesource) {
            /* 153 */
            this.pbcu = pbcu;
            /* 154 */
            this.typesource = initialTypesource;
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
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 163 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 164 */
                ((INativeCodeUnit) oldInput).removeListener(this.listener);
                /* 165 */
                this.listener = null;
                /*     */
            }
            /*     */
            /* 168 */
            if (this.refresher == null) {
                /* 169 */
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer);
                /*     */
            }
            /*     */
            /* 172 */
            this.pbcu = ((INativeCodeUnit) newInput);
            /* 173 */
            if (this.pbcu == null) {
                /* 174 */
                return;
                /*     */
            }
            /*     */
            /* 177 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e) {
                    /* 180 */
                    if (NativeTypesView.ContentProvider.this.pbcu != null) {
                        /* 181 */
                        NativeTypesView.ContentProvider.this.refresher.request();
                        /*     */
                    }
                    /*     */
                }
                /* 184 */
            };
            /* 185 */
            this.pbcu.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /* 190 */
            List<INativeType> entries = new ArrayList();
            /* 191 */
            Iterator localIterator1;
            if ((this.typesource & 0x1) != 0)
                /* 192 */
                for (localIterator1 = this.pbcu.getTypeManager().getTypes().iterator(); localIterator1.hasNext(); ) {
                    t = (INativeType) localIterator1.next();
                    /* 193 */
                    entries.add(t);
                    /*     */
                }
            /*     */
            INativeType t;
            /* 196 */
            if ((this.typesource & 0x2) != 0) {
                /* 197 */
                TypeLibraryService tlsvc = this.pbcu.getTypeLibraryService();
                /* 198 */
                for (ITypeLibrary tlib : tlsvc.getLoadedTypeLibraries()) {
                    /* 199 */
                    for (INativeType t : tlib.getTypes()) {
                        /* 200 */
                        entries.add(t);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /* 204 */
            return entries.toArray();
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 209 */
            INativeType e = (INativeType) row;
            /* 210 */
            return new Object[]{e.getSignature(true)};
            /*     */
        }

        /*     */
        /*     */
        public boolean isChecked(Object row)
        /*     */ {
            /* 215 */
            return false;
            /*     */
        }

        /*     */
        /*     */
        public void setTypeSource(int index) {
            /* 219 */
            switch (index) {
                /*     */
                case 0:
                    /* 221 */
                    this.typesource = 3;
                    /* 222 */
                    break;
                /*     */
                case 1:
                    /* 224 */
                    this.typesource = 1;
                    /* 225 */
                    break;
                /*     */
                case 2:
                    /* 227 */
                    this.typesource = 2;
                    /* 228 */
                    break;
                /*     */
                default:
                    /* 230 */
                    this.typesource = 0;
                    /*     */
            }
            /*     */
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */   static class LabelProvider
            /*     */ implements ILabelValueProvider, ITableLabelProvider
            /*     */ {
        /*     */
        public void addListener(ILabelProviderListener listener) {
        }

        /*     */
        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public boolean isLabelProperty(Object element, String property)
        /*     */ {
            /* 247 */
            return false;
            /*     */
        }

        /*     */
        /*     */
        /*     */
        public void removeListener(ILabelProviderListener listener) {
        }

        /*     */
        /*     */
        /*     */
        public Image getColumnImage(Object element, int columnIndex)
        /*     */ {
            /* 256 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public String getColumnText(Object element, int columnIndex)
        /*     */ {
            /* 261 */
            INativeType t = (INativeType) element;
            /* 262 */
            if (columnIndex == 0) {
                /* 263 */
                return t == null ? "" : t.getSignature(true);
                /*     */
            }
            /* 265 */
            throw new RuntimeException();
            /*     */
        }

        /*     */
        /*     */
        public String getString(Object element)
        /*     */ {
            /* 270 */
            return getColumnText(element, 0);
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int key)
        /*     */ {
            /* 275 */
            return getColumnText(element, key);
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\NativeTypesView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */