
package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ILoaderInformation;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractDataFrameView;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.format.TimeFormatter;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CodeLoaderInfoView
        extends AbstractDataFrameView<ICodeObjectUnit> {
    private DataFrameView dfv;

    public CodeLoaderInfoView(Composite parent, int style, RcpClientContext context, ICodeObjectUnit unit) {
        super(parent, style, unit, null, context);
        setLayout(new FillLayout());
        ILoaderInformation info = ((ICodeObjectUnit) getUnit()).getLoaderInformation();
        if (info == null) {
            return;
        }
        this.dfv = buildSimple(this, new String[]{"Field", "Value"});
        this.dfv.getContextMenu().addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                CodeLoaderInfoView.this.addOperationsToContextMenu(menuMgr);
            }
        });
    }

    public ColumnViewer getViewer() {
        return this.dfv.getTableViewer();
    }

    protected void initDataFrame(DataFrame df) {
        df.setRenderedBaseForNumberObjects(16);
        ILoaderInformation info = ((ICodeObjectUnit) getUnit()).getLoaderInformation();
        df.addRow(new Object[]{"Processor", Strings.safe(info.getTargetProcessor())});
        df.addRow(new Object[]{"Endianness", info.getEndianness()});
        df.addRow(new Object[]{"Word Size", info.getWordSize() + " bits"});
        df.addRow(new Object[]{"Subsystem", Strings.safe(info.getTargetSubsystem())});
        df.addRow(new Object[]{"Version", info.getVersion()});
        df.addRow(new Object[]{"Flags", formatObjectFlags(info.getFlags())});
        long ts = info.getCompilationTimestamp();
        df.addRow(new Object[]{"Compilation Time", ts == 0L ? "" : TimeFormatter.formatTimestampLocal(ts)});
        df.addRow(new Object[]{"Image Base", Long.valueOf(info.getImageBase())});
        df.addRow(new Object[]{"Image Size", Long.valueOf(info.getImageSize())});
        df.addRow(new Object[]{"Entry-Point", Long.valueOf(info.getEntryPoint())});
        df.addRow(new Object[]{"Overlay Offset", Long.valueOf(info.getOverlayOffset())});
    }

    private static String formatObjectFlags(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0x1) != 0) {
            sb.append("SYMB ");
            flags &= 0xFFFFFFFE;
        }
        if ((flags & 0x2) != 0) {
            sb.append("RELO ");
            flags &= 0xFFFFFFFE;
        }
        if ((flags & 0x4) != 0) {
            sb.append("LIBR ");
            flags &= 0xFFFFFFFB;
        }
        if (flags != 0) {
            sb.append(String.format("(other:%Xh)", new Object[]{Integer.valueOf(flags)}));
        }
        return sb.toString();
    }

    public IItem getActiveItem() {
        TableItem[] sel = this.dfv.getTable().getSelection();
        if ((sel != null) && (sel.length == 1)) {
            int row = this.dfv.getSelectedRow();
            if ((row == 7) || (row == 9)) {
                return new CodeLoaderCellItem((ICodeObjectUnit) this.unit, sel[0].getText(0), sel[0].getText(1), row != 7);
            }
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\codeobject\CodeLoaderInfoView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */