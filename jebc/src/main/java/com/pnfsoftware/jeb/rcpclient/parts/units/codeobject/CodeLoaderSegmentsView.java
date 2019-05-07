/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ISegmentInformation;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractDataFrameView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.ColumnViewer;
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
/*     */ public class CodeLoaderSegmentsView
        /*     */ extends AbstractDataFrameView<ICodeObjectUnit>
        /*     */ {
    /*     */   private boolean segmentsOrNotSections;
    /*  37 */   private DataFrameView dfv = null;

    /*     */
    /*     */
    public CodeLoaderSegmentsView(Composite parent, int style, RcpClientContext context, ICodeObjectUnit unit, boolean segmentsOrNotSections)
    /*     */ {
        /*  41 */
        super(parent, style, unit, null, context);
        /*  42 */
        setLayout(new FillLayout());
        /*  43 */
        this.segmentsOrNotSections = segmentsOrNotSections;
        /*     */
        /*  45 */
        this.dfv = buildSimple(this, new String[]{"Name", "Flags", "Offset in File", "Size in File", "Offset in Memory", "Size in Memory"});
        /*     */
        /*     */
        /*  48 */
        this.dfv.getContextMenu().addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  51 */
                CodeLoaderSegmentsView.this.addOperationsToContextMenu(menuMgr);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public ColumnViewer getViewer() {
        /*  57 */
        return this.dfv.getTableViewer();
        /*     */
    }

    /*     */
    /*     */
    protected void initDataFrame(DataFrame df)
    /*     */ {
        /*  62 */
        df.setRenderedBaseForNumberObjects(16);
        /*     */
        List<? extends ISegmentInformation> list;
        /*     */
        List<? extends ISegmentInformation> list;
        /*  65 */
        if (this.segmentsOrNotSections) {
            /*  66 */
            list = ((ICodeObjectUnit) this.unit).getSegments();
            /*     */
        }
        /*     */
        else {
            /*  69 */
            list = ((ICodeObjectUnit) this.unit).getSections();
            /*     */
        }
        /*  71 */
        if (list == null) {
            /*  72 */
            list = new ArrayList();
            /*     */
        }
        /*     */
        /*     */
        /*  76 */
        for (ISegmentInformation s : list) {
            /*  77 */
            List<Object> row = new ArrayList();
            /*  78 */
            row.add(s.getName());
            /*  79 */
            row.add(formatSegmentFlags(s.getFlags()));
            /*  80 */
            row.add(Long.valueOf(s.getOffsetInFile()));
            /*  81 */
            row.add(Long.valueOf(s.getSizeInFile()));
            /*  82 */
            row.add(Long.valueOf(s.getOffsetInMemory()));
            /*  83 */
            row.add(Long.valueOf(s.getSizeInMemory()));
            /*  84 */
            df.addRow(row);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private static String formatSegmentFlags(int flags) {
        /*  89 */
        StringBuilder sb = new StringBuilder();
        /*  90 */
        if ((flags & 0x2) != 0) {
            /*  91 */
            sb.append("READ ");
            /*  92 */
            flags &= 0xFFFFFFFD;
            /*     */
        }
        /*  94 */
        if ((flags & 0x1) != 0) {
            /*  95 */
            sb.append("WRTE ");
            /*  96 */
            flags &= 0xFFFFFFFE;
            /*     */
        }
        /*  98 */
        if ((flags & 0x4) != 0) {
            /*  99 */
            sb.append("EXEC ");
            /* 100 */
            flags &= 0xFFFFFFFB;
            /*     */
        }
        /* 102 */
        if ((flags & 0x80000000) != 0) {
            /* 103 */
            sb.append("SYNT ");
            /* 104 */
            flags &= 0x7FFFFFFF;
            /*     */
        }
        /* 106 */
        if ((flags & 0x40000000) != 0) {
            /* 107 */
            sb.append("ALCW ");
            /* 108 */
            flags &= 0xBFFFFFFF;
            /*     */
        }
        /* 110 */
        if ((flags & 0x20000000) != 0) {
            /* 111 */
            sb.append("INVD ");
            /* 112 */
            flags &= 0xDFFFFFFF;
            /*     */
        }
        /* 114 */
        if (flags != 0) {
            /* 115 */
            sb.append(String.format("(other:%X)", new Object[]{Integer.valueOf(flags)}));
            /*     */
        }
        /* 117 */
        return sb.toString().trim();
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 122 */
        TableItem[] sel = this.dfv.getTable().getSelection();
        /* 123 */
        if ((sel != null) && (sel.length == 1)) {
            /* 124 */
            return new CodeLoaderCellItem((ICodeObjectUnit) this.unit, sel[0].getText(0), sel[0].getText(4));
            /*     */
        }
        /* 126 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 131 */
        IItem item = getActiveItem();
        /* 132 */
        if ((item instanceof CodeLoaderCellItem)) {
            /* 133 */
            return ((CodeLoaderCellItem) item).getAddress();
            /*     */
        }
        /* 135 */
        return super.getActiveAddress(precision);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\codeobject\CodeLoaderSegmentsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */