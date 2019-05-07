/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IRegisterBank;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetInformation;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.ItemStyleProvider;
/*     */ import com.pnfsoftware.jeb.util.encoding.Conversion;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;

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
/*     */ public class DbgCodeView
        /*     */ extends AbstractUnitFragment<IDebuggerUnit>
        /*     */ implements IContextMenu
        /*     */ {
    /*  55 */   private static final ILogger logger = GlobalLog.getLogger(DbgCodeView.class);
    /*     */
    /*     */   private ITextDocumentViewer viewer;
    /*     */   private DbgCodeDocument document;
    /*     */   private IDebuggerUnit dbg;
    /*     */   private IEventListener listener;

    /*     */
    /*     */
    public DbgCodeView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit)
    /*     */ {
        /*  64 */
        super(parent, flags, unit, null, context);
        /*  65 */
        setLayout(new FillLayout());
        /*     */
        /*  67 */
        if (unit == null) {
            /*  68 */
            throw new RuntimeException();
            /*     */
        }
        /*  70 */
        this.dbg = unit;
        /*  71 */
        this.document = new DbgCodeDocument(unit);
        /*     */
        /*  73 */
        this.viewer = new InteractiveTextViewer(this, 0, this.document, context.getPropertyManager(), null);
        /*     */
        /*  75 */
        context.getFontManager().registerWidget(this.viewer.getTextWidget());
        /*     */
        /*  77 */
        ItemStyleProvider tsa = new ItemStyleProvider(context.getStyleManager());
        /*  78 */
        tsa.registerTextViewer(this.viewer);
        /*  79 */
        this.viewer.setStyleAdapter(tsa);
        /*     */
        /*  81 */
        this.viewer.initialize(true);
        /*     */
        /*  83 */
        if (unit.isAttached()) {
            /*  84 */
            IDebuggerThread defThread = unit.getDefaultThread();
            /*  85 */
            if ((defThread != null) && (defThread.getStatus() == DebuggerThreadStatus.PAUSED)) {
                /*  86 */
                IRegisterBank regs = defThread.getRegisters();
                /*  87 */
                if (regs != null)
                    /*     */ {
                    /*  89 */
                    this.viewer.setCaretCoordinates(new Coordinates(regs.getProgramCounter()), null, false);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*  95 */
        this.viewer.getTextWidget().addKeyListener(new KeyAdapter()
                /*     */ {
            /*     */
            public void keyPressed(KeyEvent e) {
                /*  98 */
                if (!DbgCodeView.this.viewer.isDisposed()) {
                    /*  99 */
                    if (e.keyCode == 119) {
                        /* 100 */
                        DbgCodeView.this.document.switchViewType();
                        /* 101 */
                        DbgCodeView.this.viewer.refresh();
                        /*     */
                    }
                    /* 103 */
                    else if ((DbgCodeView.this.document.getViewType() == DbgCodeDocument.ViewType.CODE) &&
                            /* 104 */             (DbgCodeView.this.dbg.isAttached()) && (DbgCodeView.this.dbg.getDefaultThread() != null) &&
                            /* 105 */             (DbgCodeView.this.dbg.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
                        /* 106 */
                        ICoordinates coords = DbgCodeView.this.viewer.getCaretCoordinates();
                        /* 107 */
                        if (coords == null) {
                            /* 108 */
                            return;
                            /*     */
                        }
                        /* 110 */
                        int parseMode = getParseMode(e.keyCode);
                        /* 111 */
                        if (parseMode >= 0) {
                            /* 112 */
                            if (DbgCodeView.this.document.hasInsnAt(coords.getAnchorId()))
                                /*     */ {
                                /* 114 */
                                return;
                                /*     */
                            }
                            /*     */
                            /* 117 */
                            long newAnchorId = DbgCodeView.this.document.getInsnAddressAt(coords.getAnchorId(), coords
/* 118 */.getColumnOffset());
                            /* 119 */
                            boolean changed = DbgCodeView.this.document.forceInsnAt(newAnchorId, parseMode);
                            /* 120 */
                            if (changed) {
                                /* 121 */
                                if (!DbgCodeView.this.document.hasInsnAt(coords.getAnchorId())) {
                                    /* 122 */
                                    DbgCodeView.this.viewer.setCaretCoordinates(new Coordinates(newAnchorId), null, false);
                                    /*     */
                                }
                                /* 124 */
                                DbgCodeView.this.viewer.refresh();
                                /*     */
                            }
                            /*     */
                        }
                        /* 127 */
                        else if (e.keyCode == 117) {
                            /* 128 */
                            DbgCodeView.this.document.removeInsn(coords.getAnchorId());
                            /* 129 */
                            DbgCodeView.this.viewer.refresh();
                            /* 130 */
                            ICoordinates newCoords = DbgCodeView.this.viewer.getCaretCoordinates();
                            /* 131 */
                            if (newCoords == null)
                                /*     */ {
                                /* 133 */
                                Long previousInsnAddress = null;
                                /* 134 */
                                long previousAddress = coords.getAnchorId() - 1L;
                                /* 135 */
                                while (coords.getAnchorId() - previousAddress < 16L) {
                                    /* 136 */
                                    if (DbgCodeView.this.document.hasInsnAt(previousAddress)) {
                                        /* 137 */
                                        previousInsnAddress = Long.valueOf(previousAddress);
                                        /* 138 */
                                        break;
                                        /*     */
                                    }
                                    /* 140 */
                                    previousAddress -= 1L;
                                    /*     */
                                }
                                /*     */
                                /* 143 */
                                long newAnchorId = coords.getAnchorId() & 0xFFFFFFFFFFFFFFF0;
                                /* 144 */
                                if (previousInsnAddress != null)
                                    /*     */ {
                                    /* 146 */
                                    long gap = previousInsnAddress.longValue() + DbgCodeView.this.document.getInsnAt(previousInsnAddress.longValue()).getSize();
                                    /* 147 */
                                    newAnchorId = Math.max(newAnchorId, gap);
                                    /*     */
                                }
                                /*     */
                                /* 150 */
                                DbgCodeView.this.viewer.setCaretCoordinates(new Coordinates(newAnchorId), null, false);
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }

            /*     */
            /*     */
            /*     */
            private int getParseMode(int keyCode)
            /*     */ {
                /* 160 */
                switch (keyCode) {
                    /*     */
                    case 100:
                        /* 162 */
                        return 0;
                    /*     */
                    /*     */
                    case 102:
                        /* 165 */
                        if (DbgCodeView.this.dbg.getProcessor().getDefaultMode() == 16) {
                            /* 166 */
                            return 32;
                            /*     */
                        }
                        /* 168 */
                        return 16;
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    case 103:
                        /* 173 */
                        int defaultMode = DbgCodeView.this.dbg.getProcessor().getDefaultMode();
                        /* 174 */
                        switch (defaultMode) {
                            /*     */
                            case 16:
                                /*     */
                            case 32:
                                /* 177 */
                                return 64;
                            /*     */
                            case 64:
                                /* 179 */
                                return 32;
                            /*     */
                        }
                        /*     */
                        /* 182 */
                        throw new RuntimeException(Strings.f("Invalid default mode %d for processor %s", new Object[]{Integer.valueOf(defaultMode),
/* 183 */             DbgCodeView.this.dbg.getTargetInformation().getProcessorType()}));
                        /*     */
                }
                /*     */
                /*     */
                /* 187 */
                return -1;
                /*     */
            }
            /*     */
            /*     */
            /* 191 */
        });
        /* 192 */
        this.listener = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e)
            /*     */ {
                /* 196 */
                if ((!DbgCodeView.this.viewer.getTextWidget().isDisposed()) && (DbgCodeView.this.dbg != null) &&
                        /* 197 */           (e.getSource() == DbgCodeView.this.dbg)) {
                    /* 198 */
                    UIExecutor.async(DbgCodeView.this.viewer.getTextWidget(), new UIRunnable()
                            /*     */ {
                        /*     */
                        public void runi() {
                            /* 201 */
                            if ((DbgCodeView.this.dbg != null) &&
                                    /* 202 */                 (!DbgCodeView.this.viewer.isDisposed())) {
                                /* 203 */
                                DbgCodeView.this.viewer.refresh();
                                /* 204 */
                                if ((DbgCodeView.this.dbg.isAttached()) &&
                                        /* 205 */                   (DbgCodeView.this.dbg.getDefaultThread() != null) &&
                                        /* 206 */                   (DbgCodeView.this.dbg.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
                                    /* 207 */
                                    String location = DbgCodeView.this.dbg.getDefaultThread().getLocation();
                                    /* 208 */
                                    DbgCodeView.this.viewer.setCaretCoordinates(new Coordinates(Conversion.stringToLong(location)), null, false);
                                    /*     */
                                }
                                /*     */
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
            /* 217 */
        };
        /* 218 */
        unit.addListener(this.listener);
        /*     */
    }

    /*     */
    /*     */
    public void dispose()
    /*     */ {
        /* 223 */
        super.dispose();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 229 */
        addOperationsToContextMenu(menuMgr);
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /* 234 */
        if (this.dbg == null) {
            /* 235 */
            return false;
            /*     */
        }
        /* 237 */
        List<? extends ICodeUnit> units = null;
        /*     */
        try {
            /* 239 */
            units = this.dbg.getPotentialDebuggees();
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 242 */
            logger.catchingSilent(e);
            /* 243 */
            return false;
            /*     */
        }
        /* 245 */
        if (units != null) {
            /* 246 */
            for (ICodeUnit unit : units) {
                /*     */
                try {
                    /* 248 */
                    if ((unit instanceof INativeCodeUnit)) {
                        /* 249 */
                        long addr = ((INativeCodeUnit) unit).getCanonicalMemoryAddress(address);
                        /* 250 */
                        if (addr != -1L) {
                            /* 251 */
                            return true;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */ catch (Exception e) {
                    /* 256 */
                    logger.catchingSilent(e);
                    /* 257 */
                    return false;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 261 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 266 */
        ICoordinates coords = this.viewer.getCaretCoordinates();
        /* 267 */
        if (coords == null) {
            /* 268 */
            return null;
            /*     */
        }
        /* 270 */
        long anchorId = coords.getAnchorId();
        /* 271 */
        return String.format("%Xh", new Object[]{Long.valueOf(anchorId)});
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 277 */
        return Strings.encodeUTF8(TextPartUtil.buildRawTextFromPart(this.viewer.getCurrentDocumentPart()));
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 282 */
        return AbstractUnitFragment.FragmentType.TEXT;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgCodeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */