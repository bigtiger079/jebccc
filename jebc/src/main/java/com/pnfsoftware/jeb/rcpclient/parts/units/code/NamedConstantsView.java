/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.CodeConstant;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.CodeConstantManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeLibrary;
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
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.ILabelProviderListener;
/*     */ import org.eclipse.jface.viewers.ITableLabelProvider;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;

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
/*     */
/*     */ public class NamedConstantsView
        /*     */ extends AbstractFilteredTableView<INativeCodeUnit<?>, CodeConstant>
        /*     */ {
    /*  45 */   private static final ILogger logger = GlobalLog.getLogger(NativeTypesView.class);

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public NamedConstantsView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<?> pbcu, IRcpUnitView unitView, Object sourceValue)
    /*     */ {
        /*  55 */
        super(parent, style, pbcu, unitView, context, new ContentProvider(pbcu, sourceValue));
        /*  56 */
        setLayout(new GridLayout(1, false));
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
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*  88 */
        String[] columnNames = {S.s(591)};
        /*  89 */
        setLabelProvider(new LabelProvider());
        /*  90 */
        buildFilteredViewer(this, columnNames, false);
        /*     */
        /*     */
        /*  93 */
        layout();
        /*     */
    }

    /*     */
    /*     */
    protected void initFilteredView(FilteredTableView view)
    /*     */ {
        /*  98 */
        view.setLayoutData(UIUtil.createGridDataFill(true, true));
        /*     */
    }

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
    /*     */
    /*     */
    /*     */
    protected boolean isCorrectRow(Object obj)
    /*     */ {
        /* 126 */
        return obj instanceof CodeConstant;
        /*     */
    }

    /*     */
    /*     */
    public CodeConstant getSelectedRow()
    /*     */ {
        /* 131 */
        Object row = getSelectedRawRow();
        /* 132 */
        if (!(row instanceof CodeConstant)) {
            /* 133 */
            return null;
            /*     */
        }
        /*     */
        /* 136 */
        return (CodeConstant) row;
        /*     */
    }

    /*     */
    /*     */   static class ContentProvider implements IFilteredTableContentProvider {
        /*     */ IEventListener listener;
        /*     */ INativeCodeUnit<?> pbcu;
        /*     */ Object sourceValue;
        /*     */ ViewerRefresher refresher;

        /*     */
        /*     */
        public ContentProvider(INativeCodeUnit<?> pbcu, Object sourceValue) {
            /* 146 */
            this.pbcu = pbcu;
            /* 147 */
            this.sourceValue = sourceValue;
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
            /* 156 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 157 */
                ((INativeCodeUnit) oldInput).removeListener(this.listener);
                /* 158 */
                this.listener = null;
                /*     */
            }
            /*     */
            /* 161 */
            if (this.refresher == null) {
                /* 162 */
                this.refresher = new ViewerRefresher(viewer.getControl().getDisplay(), viewer);
                /*     */
            }
            /*     */
            /* 165 */
            this.pbcu = ((INativeCodeUnit) newInput);
            /* 166 */
            if (this.pbcu == null) {
                /* 167 */
                return;
                /*     */
            }
            /*     */
            /* 170 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e) {
                    /* 173 */
                    if (NamedConstantsView.ContentProvider.this.pbcu != null) {
                        /* 174 */
                        NamedConstantsView.ContentProvider.this.refresher.request();
                        /*     */
                    }
                    /*     */
                }
                /* 177 */
            };
            /* 178 */
            this.pbcu.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getElements(Object inputElement)
        /*     */ {
            /* 183 */
            List<CodeConstant> entries = new ArrayList();
            /* 184 */
            TypeLibraryService tlsvc = this.pbcu.getTypeLibraryService();
            /* 185 */
            for (ITypeLibrary tlib : tlsvc.getLoadedTypeLibraries()) {
                /* 186 */
                entries.addAll(tlib.getConstantManager().getNamedConstantsByValue(this.sourceValue));
                /*     */
            }
            /* 188 */
            return entries.toArray();
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 193 */
            CodeConstant e = (CodeConstant) row;
            /* 194 */
            return new Object[]{e.getName()};
            /*     */
        }

        /*     */
        /*     */
        public boolean isChecked(Object row)
        /*     */ {
            /* 199 */
            return false;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
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
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        public boolean isLabelProperty(Object element, String property)
        /*     */ {
            /* 231 */
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
            /* 240 */
            return null;
            /*     */
        }

        /*     */
        /*     */
        public String getColumnText(Object element, int columnIndex)
        /*     */ {
            /* 245 */
            CodeConstant e = (CodeConstant) element;
            /* 246 */
            if (columnIndex == 0) {
                /* 247 */
                return e == null ? "" : e.getName();
                /*     */
            }
            /* 249 */
            throw new RuntimeException();
            /*     */
        }

        /*     */
        /*     */
        public String getString(Object element)
        /*     */ {
            /* 254 */
            return getColumnText(element, 0);
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int key)
        /*     */ {
            /* 259 */
            return getColumnText(element, key);
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\NamedConstantsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */