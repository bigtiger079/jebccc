package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;

import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ISymbolInformation;
import com.pnfsoftware.jeb.core.units.codeobject.SymbolInformation;
import com.pnfsoftware.jeb.core.units.codeobject.SymbolType;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractDataFrameView;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class CodeLoaderSymbolsView extends AbstractDataFrameView<ICodeObjectUnit> {
    private int symbolFlag;
    private DataFrameView dfv;

    public CodeLoaderSymbolsView(Composite parent, int style, RcpClientContext context, IRcpUnitView unitView, ICodeObjectUnit co, int symbolFlag, INativeCodeUnit<?> pbcu) {
        super(parent, style, co, unitView, context);
        setLayout(new FillLayout());
        this.symbolFlag = symbolFlag;
        this.dfv = buildSimple(this, "Type", "Flags", "Name", "Identifier", "Symbol@", "Address", "Size");
        this.dfv.getContextMenu().addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                CodeLoaderSymbolsView.this.addOperationsToContextMenu(menuMgr);
            }
        });
        if (pbcu != null) {
            this.dfv.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    CodeLoaderSymbolsView.this.jumpToRoutineReference();
                }
            });
        }
    }

    public ColumnViewer getViewer() {
        return this.dfv.getTableViewer();
    }

    protected void initDataFrame(DataFrame df) {
        df.setRenderedBaseForNumberObjects(16);
        long a;
        if ((this.symbolFlag & 0x2) != 0) {
            a = getUnit().getLoaderInformation().getEntryPoint();
            ISymbolInformation s = new SymbolInformation(SymbolType.PTRFUNCTION, 2, -1L, "start", 0L, a, 0L);
            addSymbolAsRow(df, s);
        }
        for (ISymbolInformation s : ((ICodeObjectUnit) getUnit()).getSymbols()) {
            if ((s.getFlags() & this.symbolFlag) == this.symbolFlag) {
                addSymbolAsRow(df, s);
            }
        }
    }

    private void addSymbolAsRow(DataFrame df, ISymbolInformation s) {
        List<Object> row = new ArrayList<>();
        row.add(formatSymbolType(s.getType()));
        row.add(formatSymbolFlags(s.getFlags()));
        row.add(s.getName());
        row.add(s.getIdentifier());
        row.add(s.getRelativeAddress());
        row.add(s.getSymbolRelativeAddress());
        row.add(s.getSymbolSize());
        df.addRow(row);
    }

    private void jumpToRoutineReference() {
        String address = getActiveAddress();
        if (address == null) {
            return;
        }
        for (IUnitFragment fragment : this.unitView.getFragments()) {
            if ((fragment instanceof InteractiveTextView)) {
                this.unitView.setActiveFragment(fragment);
                this.unitView.setActiveAddress(address, null, false);
            }
        }
    }

    private static String formatSymbolType(SymbolType type) {
        return type == null ? "" : type.toString();
    }

    private static String formatSymbolFlags(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0x2) != 0) {
            sb.append("EXPORT ");
            flags &= 0xFFFFFFFD;
        }
        if ((flags & 0x1) != 0) {
            sb.append("IMPORT ");
            flags &= 0xFFFFFFFE;
        }
        if ((flags & 0x4) != 0) {
            sb.append("CONTI. ");
            flags &= 0xFFFFFFFB;
        }
        if ((flags & 0x10) != 0) {
            flags &= 0xFFFFFFEF;
        }
        if (flags != 0) {
            sb.append(String.format("(other:%Xh) ", flags));
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public IItem getActiveItem() {
        TableItem[] sel = this.dfv.getTable().getSelection();
        if ((sel != null) && (sel.length == 1)) {
            return new CodeLoaderCellItem(this.unit, sel[0].getText(2), sel[0].getText(5));
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


