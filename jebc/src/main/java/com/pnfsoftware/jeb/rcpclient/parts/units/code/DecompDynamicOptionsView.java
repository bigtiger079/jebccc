/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.IBasicInformation;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.IOptimizerInfo;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.ITableEventListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCheckStateProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.util.base.Assert;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;

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
/*     */ public class DecompDynamicOptionsView
        /*     */ extends Composite
        /*     */ {
    /*     */ INativeSourceUnit src;
    /*     */ String address;
    /*     */ FilteredTableView ft;
    /*  42 */ IdentityHashMap<IOptimizerInfo, Boolean> changes = new IdentityHashMap();

    /*     */
    /*     */
    public DecompDynamicOptionsView(Composite parent, int style, INativeSourceUnit src, String address) {
        /*  45 */
        super(parent, style);
        /*  46 */
        setLayout(new GridLayout(1, false));
        /*     */
        /*  48 */
        if (src == null) {
            /*  49 */
            throw new NullPointerException();
            /*     */
        }
        /*  51 */
        this.src = src;
        /*  52 */
        this.address = address;
        /*     */
        /*     */
        /*  55 */
        this.ft = new FilteredTableView(this, 160, new String[]{S.s(591), S.s(268)});
        /*  56 */
        this.ft.setLayoutData(UIUtil.createGridDataFill(true, true));
        /*     */
        /*  58 */
        FilteredTableViewer ftv = new FilteredTableViewer(this.ft);
        /*  59 */
        ContentProviderListener p = new ContentProviderListener();
        /*  60 */
        ftv.setContentProvider(p);
        /*  61 */
        ftv.setCheckStateProvider(new DefaultCheckStateProvider(p));
        /*  62 */
        ftv.setLabelProvider(new DefaultCellLabelProvider(p));
        /*  63 */
        this.ft.addTableEventListener(p);
        /*  64 */
        ftv.setInput(src);
        /*     */
    }

    /*     */
    /*     */
    public IdentityHashMap<IOptimizerInfo, Boolean> getChanges() {
        /*  68 */
        return this.changes;
        /*     */
    }

    /*     */
    /*     */   class ContentProviderListener implements ITableEventListener, IFilteredTableContentProvider {
        /*     */     private INativeSourceUnit input;

        /*     */
        /*     */     ContentProviderListener() {
        }

        /*     */
        /*  76 */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.input = ((INativeSourceUnit) newInput);
        }

        /*     */
        /*     */
        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        /*     */
        public Object[] getElements(Object elt)
        /*     */ {
            /*  85 */
            Assert.a(this.input == elt);
            /*  86 */
            if (this.input != null) {
                /*  87 */
                List<? extends IOptimizerInfo> optInfoList = this.input.getDecompiler().getOnDemandIROptimizers(DecompDynamicOptionsView.this.address);
                /*  88 */
                return optInfoList.toArray();
                /*     */
            }
            /*  90 */
            return ArrayUtil.NO_OBJECT;
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /*  95 */
            if ((row instanceof IOptimizerInfo)) {
                /*  96 */
                IOptimizerInfo e = (IOptimizerInfo) row;
                /*  97 */
                String name = e.getInformation().getName();
                /*  98 */
                String description = e.getInformation().getDescription();
                /*  99 */
                return new Object[]{name, description};
                /*     */
            }
            /* 101 */
            return ArrayUtil.NO_OBJECT;
            /*     */
        }

        /*     */
        /*     */
        public boolean isChecked(Object row)
        /*     */ {
            /* 106 */
            if ((row instanceof IOptimizerInfo)) {
                /* 107 */
                IOptimizerInfo e = (IOptimizerInfo) row;
                /*     */
                /* 109 */
                Boolean value = (Boolean) DecompDynamicOptionsView.this.changes.get(e);
                /* 110 */
                if (value == null) {
                    /* 111 */
                    return e.isEnabled();
                    /*     */
                }
                /*     */
                /* 114 */
                return value.booleanValue();
                /*     */
            }
            /*     */
            /* 117 */
            return false;
            /*     */
        }

        /*     */
        /*     */
        public void onTableEvent(Object row, boolean isSelected, boolean isChecked)
        /*     */ {
            /* 122 */
            if ((row instanceof IOptimizerInfo)) {
                /* 123 */
                IOptimizerInfo e = (IOptimizerInfo) row;
                /*     */
                /* 125 */
                boolean original = e.isEnabled();
                /* 126 */
                if (original != isChecked) {
                    /* 127 */
                    DecompDynamicOptionsView.this.changes.put(e, Boolean.valueOf(isChecked));
                    /*     */
                }
                /*     */
                else {
                    /* 130 */
                    DecompDynamicOptionsView.this.changes.remove(e);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\DecompDynamicOptionsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */