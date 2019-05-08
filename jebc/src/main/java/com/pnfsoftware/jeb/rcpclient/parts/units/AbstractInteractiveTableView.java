
package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.export.IExportableData;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractInteractiveTableView<T extends IUnit, V>
        extends AbstractUnitFragment<T>
        implements IExportableData {
    public AbstractInteractiveTableView(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context) {
        super(parent, style, unit, unitView, context);
    }

    public abstract IStructuredSelection getSelection();

    public abstract V getSelectedRow();

    public List<?> getSelectedRows() {
        IStructuredSelection sel = getSelection();
        if (sel.isEmpty()) {
            return null;
        }
        return sel.toList();
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case COPY:
                return getSelectedRow() != null;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case COPY:
                return ExportUtil.copyLinesToClipboard(this, getSelectedRows());
        }
        return false;
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TABLE;
    }
}


