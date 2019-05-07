/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ILoaderInformation;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractDataFrameView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.format.TimeFormatter;
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
/*     */ public class CodeLoaderInfoView
        /*     */ extends AbstractDataFrameView<ICodeObjectUnit>
        /*     */ {
    /*     */   private DataFrameView dfv;

    /*     */
    /*     */
    public CodeLoaderInfoView(Composite parent, int style, RcpClientContext context, ICodeObjectUnit unit)
    /*     */ {
        /*  37 */
        super(parent, style, unit, null, context);
        /*  38 */
        setLayout(new FillLayout());
        /*     */
        /*  40 */
        ILoaderInformation info = ((ICodeObjectUnit) getUnit()).getLoaderInformation();
        /*  41 */
        if (info == null) {
            /*  42 */
            return;
            /*     */
        }
        /*     */
        /*  45 */
        this.dfv = buildSimple(this, new String[]{"Field", "Value"});
        /*     */
        /*  47 */
        this.dfv.getContextMenu().addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  50 */
                CodeLoaderInfoView.this.addOperationsToContextMenu(menuMgr);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public ColumnViewer getViewer() {
        /*  56 */
        return this.dfv.getTableViewer();
        /*     */
    }

    /*     */
    /*     */
    protected void initDataFrame(DataFrame df)
    /*     */ {
        /*  61 */
        df.setRenderedBaseForNumberObjects(16);
        /*     */
        /*  63 */
        ILoaderInformation info = ((ICodeObjectUnit) getUnit()).getLoaderInformation();
        /*  64 */
        df.addRow(new Object[]{"Processor", Strings.safe(info.getTargetProcessor())});
        /*  65 */
        df.addRow(new Object[]{"Endianness", info.getEndianness()});
        /*  66 */
        df.addRow(new Object[]{"Word Size", info.getWordSize() + " bits"});
        /*  67 */
        df.addRow(new Object[]{"Subsystem", Strings.safe(info.getTargetSubsystem())});
        /*  68 */
        df.addRow(new Object[]{"Version", info.getVersion()});
        /*  69 */
        df.addRow(new Object[]{"Flags", formatObjectFlags(info.getFlags())});
        /*     */
        /*  71 */
        long ts = info.getCompilationTimestamp();
        /*  72 */
        df.addRow(new Object[]{"Compilation Time", ts == 0L ? "" : TimeFormatter.formatTimestampLocal(ts)});
        /*  73 */
        df.addRow(new Object[]{"Image Base", Long.valueOf(info.getImageBase())});
        /*  74 */
        df.addRow(new Object[]{"Image Size", Long.valueOf(info.getImageSize())});
        /*  75 */
        df.addRow(new Object[]{"Entry-Point", Long.valueOf(info.getEntryPoint())});
        /*  76 */
        df.addRow(new Object[]{"Overlay Offset", Long.valueOf(info.getOverlayOffset())});
        /*     */
    }

    /*     */
    /*     */
    private static String formatObjectFlags(int flags) {
        /*  80 */
        StringBuilder sb = new StringBuilder();
        /*  81 */
        if ((flags & 0x1) != 0) {
            /*  82 */
            sb.append("SYMB ");
            /*  83 */
            flags &= 0xFFFFFFFE;
            /*     */
        }
        /*  85 */
        if ((flags & 0x2) != 0) {
            /*  86 */
            sb.append("RELO ");
            /*  87 */
            flags &= 0xFFFFFFFE;
            /*     */
        }
        /*  89 */
        if ((flags & 0x4) != 0) {
            /*  90 */
            sb.append("LIBR ");
            /*  91 */
            flags &= 0xFFFFFFFB;
            /*     */
        }
        /*  93 */
        if (flags != 0) {
            /*  94 */
            sb.append(String.format("(other:%Xh)", new Object[]{Integer.valueOf(flags)}));
            /*     */
        }
        /*  96 */
        return sb.toString();
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 101 */
        TableItem[] sel = this.dfv.getTable().getSelection();
        /* 102 */
        if ((sel != null) && (sel.length == 1)) {
            /* 103 */
            int row = this.dfv.getSelectedRow();
            /* 104 */
            if ((row == 7) || (row == 9)) {
                /* 105 */
                return new CodeLoaderCellItem((ICodeObjectUnit) this.unit, sel[0].getText(0), sel[0].getText(1), row != 7);
                /*     */
            }
            /*     */
        }
        /* 108 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 113 */
        IItem item = getActiveItem();
        /* 114 */
        if ((item instanceof CodeLoaderCellItem)) {
            /* 115 */
            return ((CodeLoaderCellItem) item).getAddress();
            /*     */
        }
        /* 117 */
        return super.getActiveAddress(precision);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\codeobject\CodeLoaderInfoView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */