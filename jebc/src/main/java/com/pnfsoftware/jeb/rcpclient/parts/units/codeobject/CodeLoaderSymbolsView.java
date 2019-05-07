/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IUnitFragment;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ILoaderInformation;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ISymbolInformation;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.SymbolInformation;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.SymbolType;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractDataFrameView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.ColumnViewer;
/*     */ import org.eclipse.jface.viewers.DoubleClickEvent;
/*     */ import org.eclipse.jface.viewers.IDoubleClickListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableItem;

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
/*     */ public class CodeLoaderSymbolsView
        /*     */ extends AbstractDataFrameView<ICodeObjectUnit>
        /*     */ {
    /*     */   private int symbolFlag;
    /*     */   private DataFrameView dfv;

    /*     */
    /*     */
    public CodeLoaderSymbolsView(Composite parent, int style, RcpClientContext context, IRcpUnitView unitView, ICodeObjectUnit co, int symbolFlag, INativeCodeUnit<?> pbcu)
    /*     */ {
        /*  62 */
        super(parent, style, co, unitView, context);
        /*  63 */
        setLayout(new FillLayout());
        /*     */
        /*  65 */
        this.symbolFlag = symbolFlag;
        /*     */
        /*     */
        /*     */
        /*     */
        /*  70 */
        this.dfv = buildSimple(this, new String[]{"Type", "Flags", "Name", "Identifier", "Symbol@", "Address", "Size"});
        /*     */
        /*  72 */
        this.dfv.getContextMenu().addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  75 */
                CodeLoaderSymbolsView.this.addOperationsToContextMenu(menuMgr);
                /*     */
            }
            /*     */
        });
        /*     */
        /*  79 */
        if (pbcu != null)
            /*     */ {
            /*  81 */
            this.dfv.getTableViewer().addDoubleClickListener(new IDoubleClickListener()
                    /*     */ {
                /*     */
                public void doubleClick(DoubleClickEvent event) {
                    /*  84 */
                    CodeLoaderSymbolsView.this.jumpToRoutineReference();
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public ColumnViewer getViewer() {
        /*  91 */
        return this.dfv.getTableViewer();
        /*     */
    }

    /*     */
    /*     */
    protected void initDataFrame(DataFrame df)
    /*     */ {
        /*  96 */
        df.setRenderedBaseForNumberObjects(16);
        /*     */
        /*     */
        long a;
        /*  99 */
        if ((this.symbolFlag & 0x2) != 0) {
            /* 100 */
            a = ((ICodeObjectUnit) getUnit()).getLoaderInformation().getEntryPoint();
            /* 101 */
            ISymbolInformation s = new SymbolInformation(SymbolType.PTRFUNCTION, 2, -1L, "start", 0L, a, 0L);
            /*     */
            /* 103 */
            addSymbolAsRow(df, s);
            /*     */
        }
        /*     */
        /* 106 */
        for (ISymbolInformation s : ((ICodeObjectUnit) getUnit()).getSymbols()) {
            /* 107 */
            if ((s.getFlags() & this.symbolFlag) == this.symbolFlag)
                /*     */ {
                /*     */
                /* 110 */
                addSymbolAsRow(df, s);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void addSymbolAsRow(DataFrame df, ISymbolInformation s) {
        /* 116 */
        List<Object> row = new ArrayList();
        /* 117 */
        row.add(formatSymbolType(s.getType()));
        /* 118 */
        row.add(formatSymbolFlags(s.getFlags()));
        /* 119 */
        row.add(s.getName());
        /* 120 */
        row.add(Long.valueOf(s.getIdentifier()));
        /* 121 */
        row.add(Long.valueOf(s.getRelativeAddress()));
        /* 122 */
        row.add(Long.valueOf(s.getSymbolRelativeAddress()));
        /* 123 */
        row.add(Long.valueOf(s.getSymbolSize()));
        /* 124 */
        df.addRow(row);
        /*     */
    }

    /*     */
    /*     */
    private void jumpToRoutineReference() {
        /* 128 */
        String address = getActiveAddress();
        /* 129 */
        if (address == null) {
            /* 130 */
            return;
            /*     */
        }
        /* 132 */
        for (IUnitFragment fragment : this.unitView.getFragments()) {
            /* 133 */
            if ((fragment instanceof InteractiveTextView)) {
                /* 134 */
                this.unitView.setActiveFragment(fragment);
                /* 135 */
                this.unitView.setActiveAddress(address, null, false);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private static String formatSymbolType(SymbolType type)
    /*     */ {
        /* 145 */
        return type == null ? "" : type.toString();
        /*     */
    }

    /*     */
    /*     */
    private static String formatSymbolFlags(int flags) {
        /* 149 */
        StringBuilder sb = new StringBuilder();
        /* 150 */
        if ((flags & 0x2) != 0) {
            /* 151 */
            sb.append("EXPORT ");
            /* 152 */
            flags &= 0xFFFFFFFD;
            /*     */
        }
        /* 154 */
        if ((flags & 0x1) != 0) {
            /* 155 */
            sb.append("IMPORT ");
            /* 156 */
            flags &= 0xFFFFFFFE;
            /*     */
        }
        /* 158 */
        if ((flags & 0x4) != 0) {
            /* 159 */
            sb.append("CONTI. ");
            /* 160 */
            flags &= 0xFFFFFFFB;
            /*     */
        }
        /* 162 */
        if ((flags & 0x10) != 0)
            /*     */ {
            /* 164 */
            flags &= 0xFFFFFFEF;
            /*     */
        }
        /* 166 */
        if (flags != 0) {
            /* 167 */
            sb.append(String.format("(other:%Xh) ", new Object[]{Integer.valueOf(flags)}));
            /*     */
        }
        /* 169 */
        if (sb.length() > 0)
            /*     */ {
            /* 171 */
            sb.deleteCharAt(sb.length() - 1);
            /*     */
        }
        /* 173 */
        return sb.toString();
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 178 */
        TableItem[] sel = this.dfv.getTable().getSelection();
        /* 179 */
        if ((sel != null) && (sel.length == 1)) {
            /* 180 */
            return new CodeLoaderCellItem((ICodeObjectUnit) this.unit, sel[0].getText(2), sel[0].getText(5));
            /*     */
        }
        /* 182 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 187 */
        IItem item = getActiveItem();
        /* 188 */
        if ((item instanceof CodeLoaderCellItem)) {
            /* 189 */
            return ((CodeLoaderCellItem) item).getAddress();
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
        /* 201 */
        return super.getActiveAddress(precision);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\codeobject\CodeLoaderSymbolsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */