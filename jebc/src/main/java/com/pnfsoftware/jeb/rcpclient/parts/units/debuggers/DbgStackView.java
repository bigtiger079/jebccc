/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.memory.IVirtualMemory;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.DebuggerUtil;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueRaw;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractInfiniTableSectionProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.InfiniTableViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.DbgTypedValueUtil;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.viewers.IStructuredSelection;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;

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
/*     */
/*     */ public class DbgStackView
        /*     */ extends AbstractUnitFragment<IDebuggerUnit>
        /*     */ implements IContextMenu
        /*     */ {
    /*  58 */   private static final ILogger logger = GlobalLog.getLogger(DbgStackView.class);
    /*     */   private InfiniTableView view;
    /*     */   private InfiniTableViewer viewer;
    /*     */   private StackItemsProvider provider;

    /*     */
    /*     */   public static class StackEntry
            /*     */ {
        /*     */ long address;
        /*     */ byte[] bytes;
        /*     */
    }

    /*     */
    /*     */
    public DbgStackView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit) {
        /*  70 */
        super(parent, flags, unit, null, context);
        /*  71 */
        setLayout(new FillLayout());
        /*     */
        /*  73 */
        if (unit == null) {
            /*  74 */
            throw new RuntimeException();
            /*     */
        }
        /*     */
        /*  77 */
        this.view = new InfiniTableView(this, 4, new String[]{"Address", "Value", "Extra"});
        /*  78 */
        this.viewer = new InfiniTableViewer(this.view);
        /*     */
        /*  80 */
        this.provider = new StackItemsProvider();
        /*  81 */
        this.viewer.setContentProvider(this.provider);
        /*  82 */
        this.viewer.setLabelProvider(new StackItemLabelProvider(this.provider));
        /*  83 */
        this.viewer.setTopId(0L, false);
        /*  84 */
        this.viewer.setInput(unit);
        /*     */
        /*     */
        /*  87 */
        new ContextMenu(this.view.getTable()).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /*  92 */
        addOperationsToContextMenu(menuMgr);
        /*     */
    }

    /*     */
    /*     */   class StackItemLabelProvider extends DefaultCellLabelProvider {
        /*  96 */     private Map<Long, String> cacheExtra = new HashMap();

        /*     */
        /*     */
        public StackItemLabelProvider(DbgStackView.StackItemsProvider provider) {
            /*  99 */
            super();
            /*     */
        }

        /*     */
        /*     */
        public void update(ViewerCell cell)
        /*     */ {
            /* 104 */
            super.update(cell);
            /* 105 */
            if ((cell.getColumnIndex() == 0) || (cell.getColumnIndex() == 1)) {
                /* 106 */
                cell.setFont(DbgStackView.this.context.getFontManager().getCodeFont());
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int key)
        /*     */ {
            /* 112 */
            DbgStackView.StackEntry e = (DbgStackView.StackEntry) element;
            /* 113 */
            switch (key) {
                /*     */
                case 0:
                    /* 115 */
                    return DbgTypedValueUtil.formatAddress(e.address, (IDebuggerUnit) DbgStackView.this.unit);
                /*     */
                case 1:
                    /* 117 */
                    if (e.bytes != null) {
                        /* 118 */
                        return DbgTypedValueUtil.formatValue(new ValueRaw(e.bytes), 0, (IDebuggerUnit) DbgStackView.this.unit);
                        /*     */
                    }
                    /* 120 */
                    return null;
                /*     */
                case 2:
                    /* 122 */
                    if (e.bytes != null) {
                        /* 123 */
                        long ptr = DbgTypedValueUtil.bytesToAddress(e.bytes, (IDebuggerUnit) DbgStackView.this.unit);
                        /* 124 */
                        if (ptr != 0L) {
                            /* 125 */
                            String extra = (String) this.cacheExtra.get(Long.valueOf(ptr));
                            /* 126 */
                            if (extra == null) {
                                /* 127 */
                                byte[] mem = DebuggerUtil.readMemoryStringSafe((IDebuggerUnit) DbgStackView.this.unit, ptr, 256);
                                /* 128 */
                                if (mem == null) {
                                    /* 129 */
                                    extra = "";
                                    /*     */
                                }
                                /*     */
                                else {
                                    /* 132 */
                                    int asciiLength = Strings.getAsciiLength(mem);
                                    /* 133 */
                                    extra = Strings.decodeASCII(mem, 0, asciiLength);
                                    /*     */
                                }
                                /* 135 */
                                this.cacheExtra.put(Long.valueOf(ptr), extra);
                                /*     */
                            }
                            /* 137 */
                            return extra;
                            /*     */
                        }
                        /*     */
                    }
                    /* 140 */
                    return null;
                /*     */
            }
            /* 142 */
            return super.getStringAt(element, key);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */   static class StackItemsProvider
            /*     */ extends AbstractInfiniTableSectionProvider
            /*     */ {
        /*     */ IEventListener listener;
        /*     */ IDebuggerUnit dbg;
        /*     */ int asize;

        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public void inputChanged(final Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 158 */
            if ((oldInput != null) && (this.listener != null)) {
                /* 159 */
                ((IDebuggerUnit) oldInput).removeListener(this.listener);
                /* 160 */
                this.listener = null;
                /*     */
            }
            /*     */
            /* 163 */
            this.dbg = ((IDebuggerUnit) newInput);
            /* 164 */
            if (this.dbg == null) {
                /* 165 */
                return;
                /*     */
            }
            /*     */
            /* 168 */
            this.listener = new IEventListener()
                    /*     */ {
                /*     */
                public void onEvent(IEvent e)
                /*     */ {
                    /* 172 */
                    if ((DbgStackView.StackItemsProvider.this.dbg != null) && (e.getSource() == DbgStackView.StackItemsProvider.this.dbg)) {
                        /* 173 */
                        UIExecutor.async(viewer.getControl(), new UIRunnable()
                                /*     */ {
                            /*     */
                            public void runi() {
                                /* 176 */
                                if ((DbgStackView.StackItemsProvider.this.dbg != null) &&
                                        /* 177 */                   (!DbgStackView.StackItemsProvider .1.
                                this.val$viewer.getControl().isDisposed())){
                                    /* 178 */
                                    DbgStackView.StackItemsProvider .1. this.val$viewer.refresh();
                                    /*     */
                                }
                                /*     */
                                /*     */
                            }
                            /*     */
                        });
                        /*     */
                    }
                    /*     */
                }
                /* 185 */
            };
            /* 186 */
            this.dbg.addListener(this.listener);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 191 */
            DbgStackView.StackEntry e = (DbgStackView.StackEntry) row;
            /* 192 */
            return new Object[]{Long.valueOf(e.address), e.bytes};
            /*     */
        }

        /*     */
        /*     */
        public Object[] get(Object inputElement, long id, int cnt)
        /*     */ {
            /* 197 */
            IDebuggerUnit unit = (IDebuggerUnit) inputElement;
            /* 198 */
            if ((unit.isAttached()) && (unit.getDefaultThread() != null) &&
                    /* 199 */         (unit.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
                /* 200 */
                IVirtualMemory vm = unit.getMemory();
                /* 201 */
                if (vm != null) {
                    /* 202 */
                    this.asize = (vm.getSpaceBits() / 8);
                    /*     */
                    try {
                        /* 204 */
                        return readStackArea(unit, id, cnt);
                        /*     */
                    }
                    /*     */ catch (Exception e) {
                        /* 207 */
                        DbgStackView.logger.catching(e);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /* 211 */
            return ArrayUtil.NO_OBJECT;
            /*     */
        }

        /*     */
        /*     */
        private Object[] readStackArea(IDebuggerUnit unit, long id, int cnt) {
            /* 215 */
            long address = id * this.asize;
            /* 216 */
            int size = cnt * this.asize;
            /* 217 */
            byte[] data = new byte[size];
            /*     */
            /* 219 */
            int readsize = unit.readMemory(address, size, data, 0);
            /* 220 */
            DbgStackView.logger.i("Stack @%Xh, read %Xh bytes", new Object[]{Long.valueOf(address), Integer.valueOf(readsize)});
            /*     */
            /* 222 */
            List<DbgStackView.StackEntry> r = new ArrayList();
            /* 223 */
            for (int i = 0; i < cnt; i++) {
                /* 224 */
                DbgStackView.StackEntry e = new DbgStackView.StackEntry();
                /* 225 */
                e.address = (address + i * this.asize);
                /* 226 */
                if (i >= readsize) {
                    /* 227 */
                    e.bytes = null;
                    /*     */
                }
                /*     */
                else {
                    /* 230 */
                    e.bytes = new byte[this.asize];
                    /* 231 */
                    ArrayUtil.copyBytes(e.bytes, 0, data, i * this.asize, this.asize);
                    /*     */
                }
                /* 233 */
                r.add(e);
                /*     */
            }
            /* 235 */
            return r.toArray();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public StackEntry getSelectedRow() {
        /* 240 */
        IStructuredSelection sel = (IStructuredSelection) this.viewer.getSelection();
        /* 241 */
        return sel.isEmpty() ? null : (StackEntry) sel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 246 */
        StackEntry e = getSelectedRow();
        /* 247 */
        return e == null ? null : String.format("%Xh", new Object[]{Long.valueOf(e.address)});
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 252 */
        switch (req.getOperation()) {
            /*     */
            case JUMP_TO:
                /* 254 */
                return true;
            /*     */
        }
        /* 256 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 262 */
        switch (req.getOperation()) {
            /*     */
            case JUMP_TO:
                /* 264 */
                JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
                /* 265 */
                String symbol = dlg.open();
                /* 266 */
                if (symbol != null) {
                    /* 267 */
                    long memoryAddress = ((IDebuggerUnit) this.unit).convertSymbolicAddressToMemoryToAddress(symbol, null);
                    /* 268 */
                    if ((memoryAddress != 0L) &&
                            /* 269 */           (this.provider.asize > 0)) {
                        /* 270 */
                        long id = memoryAddress / this.provider.asize;
                        /* 271 */
                        this.viewer.setTopId(id, true);
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 275 */
                return true;
            /*     */
        }
        /* 277 */
        return false;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgStackView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */