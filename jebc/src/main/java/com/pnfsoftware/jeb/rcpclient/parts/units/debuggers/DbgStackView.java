
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;


import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.units.code.asm.memory.IVirtualMemory;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.impl.DebuggerUtil;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueRaw;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractInfiniTableSectionProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.InfiniTableViewer;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.util.DbgTypedValueUtil;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class DbgStackView
        extends AbstractUnitFragment<IDebuggerUnit>
        implements IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(DbgStackView.class);
    private InfiniTableView view;
    private InfiniTableViewer viewer;
    private StackItemsProvider provider;


    public static class StackEntry {
        long address;
        byte[] bytes;

    }


    public DbgStackView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit) {

        super(parent, flags, unit, null, context);

        setLayout(new FillLayout());


        if (unit == null) {

            throw new RuntimeException();

        }


        this.view = new InfiniTableView(this, 4, new String[]{"Address", "Value", "Extra"});

        this.viewer = new InfiniTableViewer(this.view);


        this.provider = new StackItemsProvider();

        this.viewer.setContentProvider(this.provider);

        this.viewer.setLabelProvider(new StackItemLabelProvider(this.provider));

        this.viewer.setTopId(0L, false);

        this.viewer.setInput(unit);


        new ContextMenu(this.view.getTable()).addContextMenu(this);

    }


    public void fillContextMenu(IMenuManager menuMgr) {

        addOperationsToContextMenu(menuMgr);

    }


    class StackItemLabelProvider extends DefaultCellLabelProvider {
        private Map<Long, String> cacheExtra = new HashMap();


        public StackItemLabelProvider(DbgStackView.StackItemsProvider provider) {
            super(provider);
        }


        public void update(ViewerCell cell) {

            super.update(cell);

            if ((cell.getColumnIndex() == 0) || (cell.getColumnIndex() == 1)) {

                cell.setFont(DbgStackView.this.context.getFontManager().getCodeFont());

            }

        }


        public String getStringAt(Object element, int key) {

            DbgStackView.StackEntry e = (DbgStackView.StackEntry) element;

            switch (key) {

                case 0:

                    return DbgTypedValueUtil.formatAddress(e.address, (IDebuggerUnit) DbgStackView.this.unit);

                case 1:

                    if (e.bytes != null) {

                        return DbgTypedValueUtil.formatValue(new ValueRaw(e.bytes), 0, (IDebuggerUnit) DbgStackView.this.unit);

                    }

                    return null;

                case 2:

                    if (e.bytes != null) {

                        long ptr = DbgTypedValueUtil.bytesToAddress(e.bytes, (IDebuggerUnit) DbgStackView.this.unit);

                        if (ptr != 0L) {

                            String extra = (String) this.cacheExtra.get(Long.valueOf(ptr));

                            if (extra == null) {

                                byte[] mem = DebuggerUtil.readMemoryStringSafe((IDebuggerUnit) DbgStackView.this.unit, ptr, 256);

                                if (mem == null) {

                                    extra = "";

                                } else {

                                    int asciiLength = Strings.getAsciiLength(mem);

                                    extra = Strings.decodeASCII(mem, 0, asciiLength);

                                }

                                this.cacheExtra.put(Long.valueOf(ptr), extra);

                            }

                            return extra;

                        }

                    }

                    return null;

            }

            return super.getStringAt(element, key);

        }

    }


    static class StackItemsProvider
            extends AbstractInfiniTableSectionProvider {
        IEventListener listener;
        IDebuggerUnit dbg;
        int asize;


        public void dispose() {
        }


        public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {

            if ((oldInput != null) && (this.listener != null)) {

                ((IDebuggerUnit) oldInput).removeListener(this.listener);

                this.listener = null;

            }


            this.dbg = ((IDebuggerUnit) newInput);

            if (this.dbg == null) {

                return;

            }


            this.listener = new IEventListener() {

                public void onEvent(IEvent e) {

                    if ((DbgStackView.StackItemsProvider.this.dbg != null) && (e.getSource() == DbgStackView.StackItemsProvider.this.dbg)) {

                        UIExecutor.async(viewer.getControl(), new UIRunnable() {

                            public void runi() {

                                if ((DbgStackView.StackItemsProvider.this.dbg != null) && (!viewer.getControl().isDisposed())){

                                    viewer.refresh();

                                }


                            }

                        });

                    }

                }

            };

            this.dbg.addListener(this.listener);

        }


        public Object[] getRowElements(Object row) {

            DbgStackView.StackEntry e = (DbgStackView.StackEntry) row;

            return new Object[]{Long.valueOf(e.address), e.bytes};

        }


        public Object[] get(Object inputElement, long id, int cnt) {

            IDebuggerUnit unit = (IDebuggerUnit) inputElement;

            if ((unit.isAttached()) && (unit.getDefaultThread() != null) &&
                    (unit.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {

                IVirtualMemory vm = unit.getMemory();

                if (vm != null) {

                    this.asize = (vm.getSpaceBits() / 8);

                    try {

                        return readStackArea(unit, id, cnt);

                    } catch (Exception e) {

                        DbgStackView.logger.catching(e);

                    }

                }

            }

            return ArrayUtil.NO_OBJECT;

        }


        private Object[] readStackArea(IDebuggerUnit unit, long id, int cnt) {

            long address = id * this.asize;

            int size = cnt * this.asize;

            byte[] data = new byte[size];


            int readsize = unit.readMemory(address, size, data, 0);

            DbgStackView.logger.i("Stack @%Xh, read %Xh bytes", new Object[]{Long.valueOf(address), Integer.valueOf(readsize)});


            List<DbgStackView.StackEntry> r = new ArrayList();

            for (int i = 0; i < cnt; i++) {

                DbgStackView.StackEntry e = new DbgStackView.StackEntry();

                e.address = (address + i * this.asize);

                if (i >= readsize) {

                    e.bytes = null;

                } else {

                    e.bytes = new byte[this.asize];

                    ArrayUtil.copyBytes(e.bytes, 0, data, i * this.asize, this.asize);

                }

                r.add(e);

            }

            return r.toArray();

        }

    }


    public StackEntry getSelectedRow() {

        IStructuredSelection sel = (IStructuredSelection) this.viewer.getSelection();

        return sel.isEmpty() ? null : (StackEntry) sel.getFirstElement();

    }


    public String getActiveAddress(AddressConversionPrecision precision) {

        StackEntry e = getSelectedRow();

        return e == null ? null : String.format("%Xh", new Object[]{Long.valueOf(e.address)});

    }


    public boolean verifyOperation(OperationRequest req) {

        switch (req.getOperation()) {

            case JUMP_TO:

                return true;

        }

        return false;

    }


    public boolean doOperation(OperationRequest req) {

        switch (req.getOperation()) {

            case JUMP_TO:

                JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));

                String symbol = dlg.open();

                if (symbol != null) {

                    long memoryAddress = ((IDebuggerUnit) this.unit).convertSymbolicAddressToMemoryToAddress(symbol, null);

                    if ((memoryAddress != 0L) &&
                            (this.provider.asize > 0)) {

                        long id = memoryAddress / this.provider.asize;

                        this.viewer.setTopId(id, true);

                    }

                }


                return true;

        }

        return false;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgStackView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */