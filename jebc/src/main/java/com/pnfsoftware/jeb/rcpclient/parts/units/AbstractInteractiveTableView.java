/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*    */
/*    */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.export.IExportableData;
/*    */ import java.util.List;
/*    */ import org.eclipse.jface.viewers.IStructuredSelection;
/*    */ import org.eclipse.swt.widgets.Composite;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public abstract class AbstractInteractiveTableView<T extends IUnit, V>
        /*    */ extends AbstractUnitFragment<T>
        /*    */ implements IExportableData
        /*    */ {
    /*    */
    public AbstractInteractiveTableView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context)
    /*    */ {
        /* 31 */
        super(parent, style, unit, unitView, context);
        /*    */
    }

    /*    */
    /*    */
    public abstract IStructuredSelection getSelection();

    /*    */
    /*    */
    public abstract V getSelectedRow();

    /*    */
    /*    */
    public List<?> getSelectedRows() {
        /* 39 */
        IStructuredSelection sel = getSelection();
        /* 40 */
        if (sel.isEmpty()) {
            /* 41 */
            return null;
            /*    */
        }
        /* 43 */
        return sel.toList();
        /*    */
    }

    /*    */
    /*    */
    public boolean verifyOperation(OperationRequest req)
    /*    */ {
        /* 48 */
        switch (req.getOperation()) {
            /*    */
            case COPY:
                /* 50 */
                return getSelectedRow() != null;
            /*    */
        }
        /* 52 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public boolean doOperation(OperationRequest req)
    /*    */ {
        /* 58 */
        switch (req.getOperation()) {
            /*    */
            case COPY:
                /* 60 */
                return ExportUtil.copyLinesToClipboard(this, getSelectedRows());
            /*    */
        }
        /* 62 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*    */ {
        /* 68 */
        return AbstractUnitFragment.FragmentType.TABLE;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\AbstractInteractiveTableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */