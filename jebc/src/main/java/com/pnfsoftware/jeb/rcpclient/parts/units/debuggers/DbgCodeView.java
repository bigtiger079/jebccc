package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IRegisterBank;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetInformation;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextViewer;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment.FragmentType;
import com.pnfsoftware.jeb.rcpclient.parts.units.ItemStyleProvider;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class DbgCodeView extends AbstractUnitFragment<IDebuggerUnit> implements IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(DbgCodeView.class);
    private ITextDocumentViewer viewer;
    private DbgCodeDocument document;
    private IDebuggerUnit dbg;
    private IEventListener listener;

    public DbgCodeView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit) {
        super(parent, flags, unit, null, context);
        setLayout(new FillLayout());
        if (unit == null) {
            throw new RuntimeException();
        }
        this.dbg = unit;
        this.document = new DbgCodeDocument(unit);
        this.viewer = new InteractiveTextViewer(this, 0, this.document, context.getPropertyManager(), null);
        context.getFontManager().registerWidget(this.viewer.getTextWidget());
        ItemStyleProvider tsa = new ItemStyleProvider(context.getStyleManager());
        tsa.registerTextViewer(this.viewer);
        this.viewer.setStyleAdapter(tsa);
        this.viewer.initialize(true);
        if (unit.isAttached()) {
            IDebuggerThread defThread = unit.getDefaultThread();
            if ((defThread != null) && (defThread.getStatus() == DebuggerThreadStatus.PAUSED)) {
                IRegisterBank regs = defThread.getRegisters();
                if (regs != null) {
                    this.viewer.setCaretCoordinates(new Coordinates(regs.getProgramCounter()), null, false);
                }
            }
        }
        this.viewer.getTextWidget().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!DbgCodeView.this.viewer.isDisposed()) {
                    if (e.keyCode == 119) {
                        DbgCodeView.this.document.switchViewType();
                        DbgCodeView.this.viewer.refresh();
                    } else if ((DbgCodeView.this.document.getViewType() == DbgCodeDocument.ViewType.CODE) && (DbgCodeView.this.dbg.isAttached()) && (DbgCodeView.this.dbg.getDefaultThread() != null) && (DbgCodeView.this.dbg.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
                        ICoordinates coords = DbgCodeView.this.viewer.getCaretCoordinates();
                        if (coords == null) {
                            return;
                        }
                        int parseMode = getParseMode(e.keyCode);
                        if (parseMode >= 0) {
                            if (DbgCodeView.this.document.hasInsnAt(coords.getAnchorId())) {
                                return;
                            }
                            long newAnchorId = DbgCodeView.this.document.getInsnAddressAt(coords.getAnchorId(), coords.getColumnOffset());
                            boolean changed = DbgCodeView.this.document.forceInsnAt(newAnchorId, parseMode);
                            if (changed) {
                                if (!DbgCodeView.this.document.hasInsnAt(coords.getAnchorId())) {
                                    DbgCodeView.this.viewer.setCaretCoordinates(new Coordinates(newAnchorId), null, false);
                                }
                                DbgCodeView.this.viewer.refresh();
                            }
                        } else if (e.keyCode == 117) {
                            DbgCodeView.this.document.removeInsn(coords.getAnchorId());
                            DbgCodeView.this.viewer.refresh();
                            ICoordinates newCoords = DbgCodeView.this.viewer.getCaretCoordinates();
                            if (newCoords == null) {
                                Long previousInsnAddress = null;
                                long previousAddress = coords.getAnchorId() - 1L;
                                while (coords.getAnchorId() - previousAddress < 16L) {
                                    if (DbgCodeView.this.document.hasInsnAt(previousAddress)) {
                                        previousInsnAddress = Long.valueOf(previousAddress);
                                        break;
                                    }
                                    previousAddress -= 1L;
                                }
                                long newAnchorId = coords.getAnchorId() & 0xFFFFFFF;
                                if (previousInsnAddress != null) {
                                    long gap = previousInsnAddress.longValue() + DbgCodeView.this.document.getInsnAt(previousInsnAddress.longValue()).getSize();
                                    newAnchorId = Math.max(newAnchorId, gap);
                                }
                                DbgCodeView.this.viewer.setCaretCoordinates(new Coordinates(newAnchorId), null, false);
                            }
                        }
                    }
                }
            }

            private int getParseMode(int keyCode) {
                switch (keyCode) {
                    case 100:
                        return 0;
                    case 102:
                        if (DbgCodeView.this.dbg.getProcessor().getDefaultMode() == 16) {
                            return 32;
                        }
                        return 16;
                    case 103:
                        int defaultMode = DbgCodeView.this.dbg.getProcessor().getDefaultMode();
                        switch (defaultMode) {
                            case 16:
                            case 32:
                                return 64;
                            case 64:
                                return 32;
                        }
                        throw new RuntimeException(Strings.f("Invalid default mode %d for processor %s", new Object[]{Integer.valueOf(defaultMode), DbgCodeView.this.dbg.getTargetInformation().getProcessorType()}));
                }
                return -1;
            }
        });
        this.listener = new IEventListener() {
            public void onEvent(IEvent e) {
                if ((!DbgCodeView.this.viewer.getTextWidget().isDisposed()) && (DbgCodeView.this.dbg != null) && (e.getSource() == DbgCodeView.this.dbg)) {
                    UIExecutor.async(DbgCodeView.this.viewer.getTextWidget(), new UIRunnable() {
                        public void runi() {
                            if ((DbgCodeView.this.dbg != null) && (!DbgCodeView.this.viewer.isDisposed())) {
                                DbgCodeView.this.viewer.refresh();
                                if ((DbgCodeView.this.dbg.isAttached()) && (DbgCodeView.this.dbg.getDefaultThread() != null) && (DbgCodeView.this.dbg.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
                                    String location = DbgCodeView.this.dbg.getDefaultThread().getLocation();
                                    DbgCodeView.this.viewer.setCaretCoordinates(new Coordinates(Conversion.stringToLong(location)), null, false);
                                }
                            }
                        }
                    });
                }
            }
        };
        unit.addListener(this.listener);
    }

    public void dispose() {
        super.dispose();
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        addOperationsToContextMenu(menuMgr);
    }

    public boolean isValidActiveAddress(String address, Object object) {
        if (this.dbg == null) {
            return false;
        }
        List<? extends ICodeUnit> units = null;
        try {
            units = this.dbg.getPotentialDebuggees();
        } catch (Exception e) {
            logger.catchingSilent(e);
            return false;
        }
        if (units != null) {
            for (ICodeUnit unit : units) {
                try {
                    if ((unit instanceof INativeCodeUnit)) {
                        long addr = ((INativeCodeUnit) unit).getCanonicalMemoryAddress(address);
                        if (addr != -1L) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    logger.catchingSilent(e);
                    return false;
                }
            }
        }
        return false;
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        ICoordinates coords = this.viewer.getCaretCoordinates();
        if (coords == null) {
            return null;
        }
        long anchorId = coords.getAnchorId();
        return String.format("%Xh", new Object[]{Long.valueOf(anchorId)});
    }

    public byte[] export() {
        return Strings.encodeUTF8(TextPartUtil.buildRawTextFromPart(this.viewer.getCurrentDocumentPart()));
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TEXT;
    }
}


