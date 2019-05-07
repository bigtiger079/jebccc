/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.input.IInputLocation;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.table.ICellCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableDocument;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableRow;
/*     */ import com.pnfsoftware.jeb.core.output.table.impl.CellCoordinates;
/*     */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.export.ExportUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.table.InteractiveTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.table.TableUtil;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.ColumnViewer;
/*     */ import org.eclipse.jface.viewers.ISelectionChangedListener;
/*     */ import org.eclipse.jface.viewers.IStructuredSelection;
/*     */ import org.eclipse.jface.viewers.SelectionChangedEvent;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Table;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class InteractiveTableView
        /*     */ extends AbstractInteractiveTableView<IUnit, ITableRow>
        /*     */ {
    /*  45 */   private static final ILogger logger = GlobalLog.getLogger(InteractiveTableView.class);
    /*     */
    /*     */   private ITableDocument idoc;
    /*     */   private InteractiveTableViewer iviewer;

    /*     */
    /*     */
    public InteractiveTableView(Composite parent, int flags, final RcpClientContext context, final IUnit unit, ITableDocument idoc, IRcpUnitView unitView)
    /*     */ {
        /*  52 */
        super(parent, flags, unit, unitView, context);
        /*  53 */
        setLayout(new FillLayout());
        /*     */
        /*  55 */
        this.idoc = idoc;
        /*     */
        /*  57 */
        this.iviewer = new InteractiveTableViewer(this, 0, idoc, context);
        /*  58 */
        this.iviewer.setStyleAdapter(new ItemStyleProvider(context.getStyleManager()));
        /*  59 */
        this.iviewer.initialize();
        /*     */
        /*  61 */
        addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /*  64 */
                InteractiveTableView.this.iviewer.dispose();
                /*     */
            }
            /*     */
            /*  67 */
        });
        /*  68 */
        setPrimaryWidget(this.iviewer.getTableWidget());
        /*     */
        /*     */
        /*  71 */
        this.iviewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener()
                /*     */ {
            /*     */
            public void selectionChanged(SelectionChangedEvent event)
            /*     */ {
                /*  75 */
                context.refreshHandlersStates();
                /*     */
                /*  77 */
                CellCoordinates coord = InteractiveTableView.this.iviewer.getPosition();
                /*  78 */
                String address = null;
                /*  79 */
                IInputLocation location = null;
                /*  80 */
                if (coord != null) {
                    /*  81 */
                    address = InteractiveTableView.this.getAddressAt(coord);
                    /*  82 */
                    if ((address != null) && ((unit instanceof IInteractiveUnit))) {
                        /*  83 */
                        IInteractiveUnit iunit = (IInteractiveUnit) unit;
                        /*  84 */
                        location = iunit.addressToLocation(address);
                        /*     */
                    }
                    /*     */
                }
                /*  87 */
                String statusText = String.format("coord: %s | addr: %s | loc: %s", new Object[]{Strings.safe(coord, "?"),
/*  88 */           Strings.safe(address, "?"), Strings.safe(location, "?")});
                /*  89 */
                context.getStatusIndicator().setText(statusText);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public ITableDocument getDocument() {
        /*  95 */
        return this.idoc;
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 100 */
        return (item != null) && (getActiveItem() == item);
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 105 */
        ITableRow row = this.iviewer.getSelectedRow();
        /* 106 */
        if ((row == null) || (row.getCells() == null)) {
            /* 107 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 112 */
        return (IItem) row.getCells().get(0);
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 117 */
        ICellCoordinates coord = this.iviewer.getPosition();
        /* 118 */
        return getAddressAt(coord);
        /*     */
    }

    /*     */
    /*     */
    public String getAddressAt(ICellCoordinates coord) {
        /* 122 */
        if (coord == null) {
            /* 123 */
            return null;
            /*     */
        }
        /* 125 */
        if ((this.iviewer.getFilteredTableViewer().isFiltered()) || (this.iviewer.getTableWidget().getSortColumn() != null))
            /*     */ {
            /*     */
            /*     */
            /* 129 */
            return null;
            /*     */
        }
        /* 131 */
        return this.idoc.coordinatesToAddress(coord);
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /*     */
        try {
            /* 137 */
            ICellCoordinates coord = this.idoc.addressToCoordinates(address);
            /* 138 */
            return coord != null;
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 141 */
            logger.catching(e);
        }
        /* 142 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /*     */
        try
            /*     */ {
            /* 151 */
            coord = this.idoc.addressToCoordinates(address);
            /*     */
        } catch (Exception e) {
            /*     */
            ICellCoordinates coord;
            /* 154 */
            logger.catching(e);
            /* 155 */
            return false;
            /*     */
        }
        /*     */
        ICellCoordinates coord;
        /* 158 */
        if (coord == null) {
            /* 159 */
            return false;
            /*     */
        }
        /*     */
        /* 162 */
        this.iviewer.setPosition(coord, true);
        /* 163 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 168 */
        ITableDocumentPart wholeTable = this.idoc.getTable();
        /* 169 */
        return Strings.encodeUTF8(TableUtil.buildCsv(wholeTable, this.iviewer.getTableWidget()));
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 174 */
        if (!this.iviewer.getTableWidget().isFocusControl()) {
            /* 175 */
            return false;
            /*     */
        }
        /* 177 */
        if (this.iviewer.verifyOperation(req)) {
            /* 178 */
            return true;
            /*     */
        }
        /*     */
        /* 181 */
        switch (req.getOperation()) {
            /*     */
            case JUMP_TO:
                /* 183 */
                return true;
            /*     */
        }
        /* 185 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 191 */
        if (this.iviewer.doOperation(req)) {
            /* 192 */
            return true;
            /*     */
        }
        /* 194 */
        if (!req.proceed()) {
            /* 195 */
            return false;
            /*     */
        }
        /*     */
        /* 198 */
        switch (req.getOperation()) {
            /*     */
            case JUMP_TO:
                /* 200 */
                JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
                /* 201 */
                String address = dlg.open();
                /* 202 */
                if (address != null) {
                    /* 203 */
                    return setActiveAddress(address);
                    /*     */
                }
                /* 205 */
                return false;
            /*     */
        }
        /*     */
        /* 208 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public ITableRow getSelectedRow()
    /*     */ {
        /* 214 */
        return this.iviewer.getSelectedRow();
        /*     */
    }

    /*     */
    /*     */
    public IStructuredSelection getSelection()
    /*     */ {
        /* 219 */
        return this.iviewer.getSelection();
        /*     */
    }

    /*     */
    /*     */
    public String exportElementToString(Object obj)
    /*     */ {
        /* 224 */
        if ((obj instanceof ITableRow)) {
            /* 225 */
            return exportRowToString((ITableRow) obj);
            /*     */
        }
        /* 227 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    protected String exportRowToString(ITableRow obj) {
        /* 231 */
        Object[] row = this.iviewer.getProvider().getRowElements(obj);
        /* 232 */
        return ExportUtil.buildCsvLine(this.iviewer.getLabelProvider(), obj, row.length);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\InteractiveTableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */