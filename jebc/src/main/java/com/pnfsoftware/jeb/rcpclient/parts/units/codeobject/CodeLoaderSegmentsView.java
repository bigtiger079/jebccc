package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ISegmentInformation;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractDataFrameView;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CodeLoaderSegmentsView extends AbstractDataFrameView<ICodeObjectUnit> {
    private boolean segmentsOrNotSections;
    private DataFrameView dfv = null;

    public CodeLoaderSegmentsView(Composite parent, int style, RcpClientContext context, ICodeObjectUnit unit, boolean segmentsOrNotSections) {
        super(parent, style, unit, null, context);
        setLayout(new FillLayout());
        this.segmentsOrNotSections = segmentsOrNotSections;
        this.dfv = buildSimple(this, new String[]{"Name", "Flags", "Offset in File", "Size in File", "Offset in Memory", "Size in Memory"});
        this.dfv.getContextMenu().addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                CodeLoaderSegmentsView.this.addOperationsToContextMenu(menuMgr);
            }
        });
    }

    public ColumnViewer getViewer() {
        return this.dfv.getTableViewer();
    }

    protected void initDataFrame(DataFrame df) {
        df.setRenderedBaseForNumberObjects(16);
        List<? extends ISegmentInformation> list;
        if (this.segmentsOrNotSections) {
            list = ((ICodeObjectUnit) this.unit).getSegments();
        } else {
            list = ((ICodeObjectUnit) this.unit).getSections();
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        for (ISegmentInformation s : list) {
            List<Object> row = new ArrayList<>();
            row.add(s.getName());
            row.add(formatSegmentFlags(s.getFlags()));
            row.add(Long.valueOf(s.getOffsetInFile()));
            row.add(Long.valueOf(s.getSizeInFile()));
            row.add(Long.valueOf(s.getOffsetInMemory()));
            row.add(Long.valueOf(s.getSizeInMemory()));
            df.addRow(row);
        }
    }

    private static String formatSegmentFlags(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0x2) != 0) {
            sb.append("READ ");
            flags &= 0xFFFFFFFD;
        }
        if ((flags & 0x1) != 0) {
            sb.append("WRTE ");
            flags &= 0xFFFFFFFE;
        }
        if ((flags & 0x4) != 0) {
            sb.append("EXEC ");
            flags &= 0xFFFFFFFB;
        }
        if ((flags & 0x80000000) != 0) {
            sb.append("SYNT ");
            flags &= 0x7FFFFFFF;
        }
        if ((flags & 0x40000000) != 0) {
            sb.append("ALCW ");
            flags &= 0xBFFFFFFF;
        }
        if ((flags & 0x20000000) != 0) {
            sb.append("INVD ");
            flags &= 0xDFFFFFFF;
        }
        if (flags != 0) {
            sb.append(String.format("(other:%X)", new Object[]{Integer.valueOf(flags)}));
        }
        return sb.toString().trim();
    }

    public IItem getActiveItem() {
        TableItem[] sel = this.dfv.getTable().getSelection();
        if ((sel != null) && (sel.length == 1)) {
            return new CodeLoaderCellItem((ICodeObjectUnit) this.unit, sel[0].getText(0), sel[0].getText(4));
        }
        return null;
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        IItem item = getActiveItem();
        if ((item instanceof CodeLoaderCellItem)) {
            return ((CodeLoaderCellItem) item).getAddress();
        }
        return super.getActiveAddress(precision);
    }
}


