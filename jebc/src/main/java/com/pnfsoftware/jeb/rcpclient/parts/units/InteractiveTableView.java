package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.input.IInputLocation;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.table.ICellCoordinates;
import com.pnfsoftware.jeb.core.output.table.ITableDocument;
import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.ITableRow;
import com.pnfsoftware.jeb.core.output.table.impl.CellCoordinates;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
import com.pnfsoftware.jeb.rcpclient.iviewers.table.InteractiveTableViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.table.TableUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class InteractiveTableView extends AbstractInteractiveTableView<IUnit, ITableRow> {
    private static final ILogger logger = GlobalLog.getLogger(InteractiveTableView.class);
    private ITableDocument idoc;
    private InteractiveTableViewer iviewer;

    public InteractiveTableView(Composite parent, int flags, final RcpClientContext context, final IUnit unit, ITableDocument idoc, IRcpUnitView unitView) {
        super(parent, flags, unit, unitView, context);
        setLayout(new FillLayout());
        this.idoc = idoc;
        this.iviewer = new InteractiveTableViewer(this, 0, idoc, context);
        this.iviewer.setStyleAdapter(new ItemStyleProvider(context.getStyleManager()));
        this.iviewer.initialize();
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                InteractiveTableView.this.iviewer.dispose();
            }
        });
        setPrimaryWidget(this.iviewer.getTableWidget());
        this.iviewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                context.refreshHandlersStates();
                CellCoordinates coord = InteractiveTableView.this.iviewer.getPosition();
                String address = null;
                IInputLocation location = null;
                if (coord != null) {
                    address = InteractiveTableView.this.getAddressAt(coord);
                    if ((address != null) && ((unit instanceof IInteractiveUnit))) {
                        IInteractiveUnit iunit = (IInteractiveUnit) unit;
                        location = iunit.addressToLocation(address);
                    }
                }
                String statusText = String.format("coord: %s | addr: %s | loc: %s", Strings.safe(coord, "?"), Strings.safe(address, "?"), Strings.safe(location, "?"));
                context.getStatusIndicator().setText(statusText);
            }
        });
    }

    public ITableDocument getDocument() {
        return this.idoc;
    }

    public boolean isActiveItem(IItem item) {
        return (item != null) && (getActiveItem() == item);
    }

    public IItem getActiveItem() {
        ITableRow row = this.iviewer.getSelectedRow();
        if ((row == null) || (row.getCells() == null)) {
            return null;
        }
        return row.getCells().get(0);
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        ICellCoordinates coord = this.iviewer.getPosition();
        return getAddressAt(coord);
    }

    public String getAddressAt(ICellCoordinates coord) {
        if (coord == null) {
            return null;
        }
        if ((this.iviewer.getFilteredTableViewer().isFiltered()) || (this.iviewer.getTableWidget().getSortColumn() != null)) {
            return null;
        }
        return this.idoc.coordinatesToAddress(coord);
    }

    public boolean isValidActiveAddress(String address, Object object) {
        try {
            ICellCoordinates coord = this.idoc.addressToCoordinates(address);
            return coord != null;
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    public boolean setActiveAddress(String address, Object extra, boolean record) {
        ICellCoordinates coord;
        try {
            coord = this.idoc.addressToCoordinates(address);
        } catch (Exception e) {
            logger.catching(e);
            return false;
        }
        if (coord == null) {
            return false;
        }
        this.iviewer.setPosition(coord, true);
        return true;
    }

    public byte[] export() {
        ITableDocumentPart wholeTable = this.idoc.getTable();
        return Strings.encodeUTF8(TableUtil.buildCsv(wholeTable, this.iviewer.getTableWidget()));
    }

    public boolean verifyOperation(OperationRequest req) {
        if (!this.iviewer.getTableWidget().isFocusControl()) {
            return false;
        }
        if (this.iviewer.verifyOperation(req)) {
            return true;
        }
        switch (req.getOperation()) {
            case JUMP_TO:
                return true;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        if (this.iviewer.doOperation(req)) {
            return true;
        }
        if (!req.proceed()) {
            return false;
        }
        switch (req.getOperation()) {
            case JUMP_TO:
                JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
                String address = dlg.open();
                if (address != null) {
                    return setActiveAddress(address);
                }
                return false;
        }
        return false;
    }

    public ITableRow getSelectedRow() {
        return this.iviewer.getSelectedRow();
    }

    public IStructuredSelection getSelection() {
        return this.iviewer.getSelection();
    }

    public String exportElementToString(Object obj) {
        if ((obj instanceof ITableRow)) {
            return exportRowToString((ITableRow) obj);
        }
        return null;
    }

    protected String exportRowToString(ITableRow obj) {
        Object[] row = this.iviewer.getProvider().getRowElements(obj);
        return ExportUtil.buildCsvLine(this.iviewer.getLabelProvider(), obj, row.length);
    }
}


