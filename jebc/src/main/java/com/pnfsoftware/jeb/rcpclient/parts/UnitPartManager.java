package com.pnfsoftware.jeb.rcpclient.parts;

import com.google.common.net.MediaType;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.client.events.JC;
import com.pnfsoftware.jeb.client.events.JebClientEvent;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.exceptions.JebRuntimeException;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IGenericDocument;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.IUnitDocumentPresentation;
import com.pnfsoftware.jeb.core.output.IUnitFormatter;
import com.pnfsoftware.jeb.core.output.table.ITableDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextDocument;
import com.pnfsoftware.jeb.core.output.text.impl.AsciiDocument;
import com.pnfsoftware.jeb.core.output.text.impl.HexDumpDocument;
import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IAddressableUnit;
import com.pnfsoftware.jeb.core.units.IBinaryUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.IERoutineContext;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilationTarget;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.ir.IEStatement;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
import com.pnfsoftware.jeb.core.units.codeobject.IPECOFFUnit;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.UnitPropertiesDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.AbstractRefresher;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabContextMenuManager;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView.Entry;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.BinaryDataView;
import com.pnfsoftware.jeb.rcpclient.parts.units.DescriptionView;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTableView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTreeView;
import com.pnfsoftware.jeb.rcpclient.parts.units.Position;
import com.pnfsoftware.jeb.rcpclient.parts.units.UnitImageView;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.NativeTypesView;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.ReferencedMethodsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.StringsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.codeobject.CodeLoaderInfoView;
import com.pnfsoftware.jeb.rcpclient.parts.units.codeobject.CodeLoaderSegmentsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.codeobject.CodeLoaderSymbolsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgBreakpointsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgCodeView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgLogView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgStackView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgThreadsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgVariablesView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.DalvikCallgraphView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.DalvikCodeGraphView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.NativeCallgraphView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.NativeCodeGraphView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.StaticCodeGraphView;
import com.pnfsoftware.jeb.rcpclient.util.Extensions;
import com.pnfsoftware.jeb.util.collect.ItemHistory;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class UnitPartManager extends AbstractPartManager implements IRcpUnitView {
    private static final ILogger logger = GlobalLog.getLogger(UnitPartManager.class, Integer.MAX_VALUE);
    private Display display;
    private AbstractRefresher refresher1;
    private AbstractRefresher refresher2;
    private Composite parent;
    private TabFolderView tabman;
    private IMPart part;
    private IUnit unit;
    private List<String> fragmentList;
    private List<String> fragmentBlacklist;
    private IEventListener unitListener;
    private IUnitFormatter unitFormatter;
    private AbstractTextDocument rawDoc;

    public UnitPartManager(RcpClientContext context) {
        super(context);
    }

    public String getLabel() {
        return this.part.getLabel();
    }

    public void setLabel(String label) {
        this.part.setLabel(label);
    }

    public void createView(Composite parent, IMPart part) {
        logger.i("Constructing unit part... part=%s", new Object[]{part});
        this.parent = parent;
        this.part = part;
        this.display = parent.getDisplay();
        this.refresher1 = new AbstractRefresher(this.display, "UnitPartName") {
            protected void performRefresh() {
                if (UnitPartManager.this.unit != null) {
                    UnitPartManager.this.updatePartName();
                }
            }
        };
        this.refresher2 = new AbstractRefresher(this.display, "UnitPartTabs") {
            protected void performRefresh() {
                if (UnitPartManager.this.unit != null) {
                    UnitPartManager.this.addUnitDocuments(UnitPartManager.this.tabman.getContainer());
                }
            }
        };
        parent.setLayout(new FillLayout());
        boolean lazyInit = !this.context.getPropertyManager().getBoolean("ui.AlwaysLoadFragments");
        this.tabman = new TabFolderView(parent, 3074, false, lazyInit);
        new TabContextMenuManager(this.tabman.getContainer()) {
            public void addActions(final CTabItem selectedItem, IMenuManager menuMgr) {
                menuMgr.add(new ActionEx(null, "Pull Out") {
                    public void run() {
                        if (selectedItem == null) {
                            return;
                        }
                        UnitPartManager.logger.i("Pulling out the fragment (tab) into its own part (view)", new Object[0]);
                        Control fragment = selectedItem.getControl();
                        UnitPartManager.this.tabman.removeEntry(fragment);
                        UnitPartManager.this.context.getPartManager().createSingle(UnitPartManager.this.unit, fragment.getClass());
                    }
                });
            }
        }.bind();
        setup();
    }

    public void setup() {
        this.unit = ((IUnit) this.part.getData().get("unit"));
        this.fragmentList = ((List) this.part.getData().get("fragmentList"));
        this.fragmentBlacklist = ((List) this.part.getData().get("fragmentBlacklist"));
        if (this.unit == null) {
            return;
        }
        if (!this.unit.isProcessed()) {
            String message = String.format("%s.\n\n%s", new Object[]{S.s(790), S.s(662)});
            boolean r = MessageDialog.openQuestion(this.parent.getShell(), S.s(821), message);
            if (!r) {
                return;
            }
        }
        logger.i("Building a part for unit: %s", new Object[]{this.unit});
        CTabFolder folder = this.tabman.getContainer();
        this.unit.addListener(this.unitListener = new IEventListener() {
            public void onEvent(IEvent e) {
                if (e.getType() == J.UnitPropertyChanged) {
                    UnitPartManager.this.refresher1.request();
                } else if (e.getType() == J.UnitChange) {
                    UnitPartManager.this.refresher2.request();
                }
            }
        });
        folder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                UnitPartManager.this.updatePartName();
                UnitPartManager.this.context.refreshHandlersStates();
            }
        });
        Control focusedControl = null;
        if (shouldDisplay(DescriptionView.class)) {
            this.tabman.addEntry(S.s(268), new DescriptionView(folder, 0, this.context, this.unit));
        }
        if ((this.unit instanceof IBinaryUnit)) {
            if (shouldDisplay(InteractiveTextView.class)) {
                IInput input = ((IBinaryUnit) this.unit).getInput();
                if (input.getCurrentSize() > 0L) {
                    this.rawDoc = null;
                    String unitName = this.unit.getName();
                    if ((!Extensions.hasExtension(unitName)) || (Extensions.hasKnownTextDocumentExtension(unitName))) {
                        long size = input.getCurrentSize();
                        int minExpectedAsciiSize = (int) Math.min(size, 30L);
                        byte[] headerBytes = new byte[minExpectedAsciiSize];
                        input.getHeader().get(headerBytes);
                        if (Strings.getAsciiLength(headerBytes, minExpectedAsciiSize) == minExpectedAsciiSize) {
                            this.rawDoc = new AsciiDocument(input);
                            InteractiveTextView textdump = new InteractiveTextView(folder, 0, this.context, this.unit, this, this.rawDoc);
                            this.tabman.addEntry(S.s(769), textdump);
                            focusedControl = textdump;
                        }
                    }
                    if (this.rawDoc == null) {
                        this.rawDoc = new HexDumpDocument(input, this.unit);
                        BinaryDataView hexdump = new BinaryDataView(folder, 0, this.context, this.unit, this, (HexDumpDocument) this.rawDoc);
                        this.tabman.addEntry(S.s(366), hexdump);
                        hexdump.setFocusPriority(0);
                        focusedControl = hexdump;
                    }
                }
            }
            if (shouldDisplay(UnitImageView.class)) {
                MediaType mt = getMediaTypeForUnit(this.unit);
                if (mt.is(MediaType.ANY_IMAGE_TYPE)) {
                    UnitImageView img = new UnitImageView(folder, 0, this.context, (IBinaryUnit) this.unit);
                    this.tabman.addEntry(S.s(378), img);
                    focusedControl = img;
                }
            }
        }
        this.unitFormatter = this.unit.getFormatter();
        Control focusedDocument = addUnitDocuments(folder);
        if (focusedDocument != null) {
            focusedControl = focusedDocument;
        }
        if ((this.unit instanceof ICodeObjectUnit)) {
            ICodeObjectUnit co = (ICodeObjectUnit) this.unit;
            if ((shouldDisplay(CodeLoaderInfoView.class)) && (co.getLoaderInformation() != null)) {
                CodeLoaderInfoView infoview = new CodeLoaderInfoView(folder, 0, this.context, co);
                this.tabman.addEntry(S.s(625), infoview);
                focusedControl = infoview;
                new AddressNavigator(this.context, infoview.getViewer(), infoview, this.unit);
            }
            if ((shouldDisplay(CodeLoaderSegmentsView.class)) && (co.getSegments() != null)) {
                CodeLoaderSegmentsView segview = new CodeLoaderSegmentsView(folder, 0, this.context, co, true);
                String label = (this.unit instanceof IPECOFFUnit) ? S.s(718) : S.s(720);
                this.tabman.addEntry(label, segview);
                new AddressNavigator(this.context, segview.getViewer(), segview, this.unit);
            }
            if ((shouldDisplay(CodeLoaderSegmentsView.class)) && (co.getSections() != null)) {
                CodeLoaderSegmentsView sectview = new CodeLoaderSegmentsView(folder, 0, this.context, co, false);
                String label = (this.unit instanceof IPECOFFUnit) ? "Directory Entries" : S.s(718);
                this.tabman.addEntry(label, sectview);
                new AddressNavigator(this.context, sectview.getViewer(), sectview, this.unit);
            }
            if ((shouldDisplay(CodeLoaderSymbolsView.class)) && (co.getSymbols() != null)) {
                CodeLoaderSymbolsView symbolsView = new CodeLoaderSymbolsView(folder, 0, this.context, this, co, 0, null);
                this.tabman.addEntry(S.s(762), symbolsView);
                new AddressNavigator(this.context, symbolsView.getViewer(), symbolsView, this.unit);
            }
        }
        if ((this.unit instanceof ICodeUnit)) {
            if ((this.unit instanceof INativeCodeUnit)) {
                if (shouldDisplay(NativeCodeGraphView.class)) {
                    NativeCodeGraphView v = new NativeCodeGraphView(folder, 0, this.context, (INativeCodeUnit) this.unit, this);
                    this.tabman.addEntry("Graph", v);
                }
                if (shouldDisplay(NativeCallgraphView.class)) {
                    NativeCallgraphView v = new NativeCallgraphView(folder, 0, this.context, (INativeCodeUnit) this.unit, this);
                    this.tabman.addEntry("Callgraph", v);
                }
            }
            if ((this.unit instanceof IDexUnit)) {
                if (shouldDisplay(DalvikCodeGraphView.class)) {
                    DalvikCodeGraphView v = new DalvikCodeGraphView(folder, 0, this.context, (IDexUnit) this.unit, this);
                    this.tabman.addEntry("Graph", v);
                }
                if (shouldDisplay(DalvikCallgraphView.class)) {
                    DalvikCallgraphView v = new DalvikCallgraphView(folder, 0, this.context, (IDexUnit) this.unit, this);
                    this.tabman.addEntry("Callgraph", v);
                }
            }
            if (shouldDisplay(CodeHierarchyView.class)) {
                CodeHierarchyView codeHier = new CodeHierarchyView(folder, 0, this.context, (ICodeUnit) this.unit, null, 256, 65536, false);
                this.tabman.addEntry(S.s(369), codeHier);
                new AddressNavigator(this.context, codeHier.getViewer(), codeHier, this.unit);
                wrapCustomFragment(this.unit, codeHier, "codehier");
            }
            if (shouldDisplay(StringsView.class)) {
                this.tabman.addEntry(S.s(754), new StringsView(folder, 0, this.context, (ICodeUnit) this.unit, this));
            }
        }
        if ((this.unit instanceof INativeCodeUnit)) {
            INativeCodeUnit<?> pbcu = (INativeCodeUnit) this.unit;
            if (shouldDisplay(NativeTypesView.class)) {
                this.tabman.addEntry(S.s(781), new NativeTypesView(folder, 0, this.context, pbcu, this, 1));
            }
            if ((shouldDisplay(CodeLoaderSymbolsView.class)) && (pbcu.getCodeObjectContainer() != null)) {
                ICodeObjectUnit co = pbcu.getCodeObjectContainer();
                if (co != null) {
                    CodeLoaderSymbolsView impview = new CodeLoaderSymbolsView(folder, 0, this.context, this, co, 1, pbcu);
                    this.tabman.addEntry(S.s(381), impview);
                    CodeLoaderSymbolsView expview = new CodeLoaderSymbolsView(folder, 0, this.context, this, co, 2, pbcu);
                    this.tabman.addEntry(S.s(331), expview);
                }
            }
            if (shouldDisplay(ReferencedMethodsView.class)) {
                this.tabman.addEntry("Referenced Methods", new ReferencedMethodsView(folder, 0, this.context, pbcu, this));
            }
        }
        if ((this.unit instanceof IDebuggerUnit)) {
            IDebuggerUnit dbg = (IDebuggerUnit) this.unit;
            IUnit parentUnit = (IUnit) dbg.getParent();
            if ((parentUnit instanceof IDebuggerUnit)) {
                parentUnit = (IUnit) parentUnit.getParent();
            }
            if (shouldDisplay(DbgLogView.class)) {
                DbgLogView logView = new DbgLogView(folder, 0, this.context, dbg);
                this.tabman.addEntry(S.s(448), logView);
                if (focusedControl == null) {
                    focusedControl = logView;
                }
                wrapCustomFragment(dbg, logView, "log");
            }
            if (shouldDisplay(DbgThreadsView.class)) {
                DbgThreadsView threadsView = new DbgThreadsView(folder, 0, this.context, dbg);
                this.tabman.addEntry(S.s(774), threadsView);
                focusedControl = threadsView;
                new AddressNavigator(this.context, threadsView.getJfaceViewer(), threadsView, parentUnit);
                wrapCustomFragment(dbg, threadsView, "threads");
            }
            if (shouldDisplay(DbgBreakpointsView.class)) {
                DbgBreakpointsView bpView = new DbgBreakpointsView(folder, 0, this.context, dbg);
                this.tabman.addEntry(S.s(101), bpView);
                if (focusedControl == null) {
                    focusedControl = bpView;
                }
                new AddressNavigator(this.context, bpView.getJfaceViewer(), bpView, parentUnit);
                wrapCustomFragment(dbg, bpView, "breakpoints");
            }
            if (shouldDisplay(DbgVariablesView.class)) {
                DbgVariablesView varView = new DbgVariablesView(folder, 0, this.context, dbg);
                this.tabman.addEntry(S.s(446), varView);
                if (focusedControl == null) {
                    focusedControl = varView;
                }
                wrapCustomFragment(dbg, varView, "variables");
            }
            if (shouldDisplay(DbgStackView.class)) {
                DbgStackView stkView = new DbgStackView(folder, 0, this.context, dbg);
                this.tabman.addEntry(S.s(745), stkView);
                if (focusedControl == null) {
                    focusedControl = stkView;
                }
                wrapCustomFragment(dbg, stkView, "stack");
            }
            if (shouldDisplay(DbgCodeView.class)) {
                DbgCodeView stkView = new DbgCodeView(folder, 0, this.context, dbg);
                this.tabman.addEntry(S.s(455), stkView);
                if (focusedControl == null) {
                    focusedControl = stkView;
                }
                wrapCustomFragment(dbg, stkView, "memoryCode");
            }
        }
        if (((this.unit instanceof INativeSourceUnit)) && (shouldDisplay(StaticCodeGraphView.class))) {
            INativeSourceUnit srcUnit = (INativeSourceUnit) this.unit;
            List<INativeDecompilationTarget> targets = srcUnit.getDecompilationTargets();
            if (!targets.isEmpty()) {
                INativeDecompilationTarget target = (INativeDecompilationTarget) targets.get(0);
                CFG<IEStatement> cfg = target.getContext().getCfg();
                StaticCodeGraphView v = new StaticCodeGraphView(folder, 0, this.context, srcUnit, this, cfg);
                this.tabman.addEntry("IR-CFG", v);
            }
        }
        if ((focusedControl == null) && (this.tabman.getEntryCount() >= 1)) {
            focusedControl = this.tabman.getEntryControl(0);
        }
        if (focusedControl != null) {
            this.tabman.showEntry(focusedControl, true);
        }
        if (this.tabman.getEntryCount() <= 1) {
            this.tabman.getContainer().setTabHeight(0);
        }
        updatePartName();
    }

    private Control addUnitDocuments(CTabFolder folder) {
        Control focusedControl = null;
        List<Long> currentPresIds = new ArrayList<>();
        IUnitDocumentPresentation pres;
        for (Iterator localIterator = this.unitFormatter.getPresentations().iterator(); localIterator.hasNext(); ) {
            pres = (IUnitDocumentPresentation) localIterator.next();
            if (pres.getId() != 0L) {
                currentPresIds.add(Long.valueOf(pres.getId()));
            }
        }
        Object uiPresIds = new ArrayList<>();
        Iterator<Control> iterator = this.tabman.getControls().iterator();
        while (iterator.hasNext()) {
            Control ctl = (Control) iterator.next();
            Long presId = (Long) ctl.getData("presentationId");
            if ((presId != null) && (presId != 0L)) {
                ((List) uiPresIds).add(presId);
            }
        }
        List<Long> tbrPresIds = new ArrayList<>();
        Iterator iter = ((List) uiPresIds).iterator();
        while (iter.hasNext()) {
            long presId = (Long) iter.next();
            if (!currentPresIds.contains(presId)) {
                tbrPresIds.add(presId);
            }
        }
        Iterator<IUnitDocumentPresentation> iterator1 = this.unitFormatter.getPresentations().iterator();
        while (iterator1.hasNext()) {
            pres = (IUnitDocumentPresentation) iterator1.next();
            long presId = pres.getId();
            if ((presId == 0L) || (!((List) uiPresIds).contains(Long.valueOf(presId)))) {
                String label = pres.getLabel();
                IGenericDocument doc = pres.getDocument();
                if (((doc instanceof HexDumpDocument)) && (shouldDisplay(InteractiveTextView.class))) {
                    BinaryDataView itext = new BinaryDataView(folder, 0, this.context, this.unit, this, (HexDumpDocument) doc);
                    wrapDocumentFragment(pres, this.unit, itext, "hextext");
                    this.tabman.addEntry(label, itext);
                    if (pres.isDefaultRepresentation()) {
                        focusedControl = itext;
                    } else {
                        itext.setFocusPriority(0);
                    }
                } else if (((doc instanceof ITextDocument)) && (shouldDisplay(InteractiveTextView.class))) {
                    InteractiveTextView itext = new InteractiveTextView(folder, 0, this.context, this.unit, this, (ITextDocument) doc);
                    wrapDocumentFragment(pres, this.unit, itext, "text");
                    this.tabman.addEntry(label, itext);
                    if (pres.isDefaultRepresentation()) {
                        focusedControl = itext;
                    }
                    if (((this.unit.getParent() instanceof IDecompilerUnit)) && ((this.unit instanceof IAddressableUnit))) {
                        itext.setViewNavigatorHelper(new DecompiledViewNavigator((IAddressableUnit) this.unit, this.context, this.part));
                    }
                } else if (((doc instanceof ITableDocument)) && (shouldDisplay(InteractiveTableView.class))) {
                    InteractiveTableView itable = new InteractiveTableView(folder, 0, this.context, this.unit, (ITableDocument) doc, this);
                    wrapDocumentFragment(pres, this.unit, itable, "table");
                    this.tabman.addEntry(label, itable);
                    if (pres.isDefaultRepresentation()) {
                        focusedControl = itable;
                    }
                } else if (((doc instanceof ITreeDocument)) && (shouldDisplay(InteractiveTreeView.class))) {
                    InteractiveTreeView itree = new InteractiveTreeView(folder, 0, this.context, this.unit, (ITreeDocument) doc, this);
                    wrapDocumentFragment(pres, this.unit, itree, "tree");
                    this.tabman.addEntry(label, itree);
                    if (pres.isDefaultRepresentation()) {
                        focusedControl = itree;
                    }
                } else {
                    doc.dispose();
                }
            }
        }
        List<Control> tbrControls = new ArrayList<>();
        for (Control ctl : this.tabman.getControls()) {
            Long presId = (Long) ctl.getData("presentationId");
            if ((presId != null) && (tbrPresIds.contains(presId))) {
                tbrControls.add(ctl);
            }
        }
        for (Control ctl : tbrControls) {
            this.tabman.removeEntry(ctl);
        }
        return focusedControl;
    }

    private void updatePartName() {
        String n0 = this.unit.getName();
        String n1 = this.tabman.getCurrentEntryName();
        String name = String.format("%s/%s", new Object[]{n0, n1});
        this.part.setLabel(name);
    }

    private void wrapDocumentFragment(IUnitDocumentPresentation pres, IUnit unit, AbstractUnitFragment<?> ctl, String doctype) {
        long id = pres.getId();
        String label = Strings.safe(pres.getLabel(), "noLabel");
        String sfx = id != 0L ? "" + id : label;
        RcpClientContext.wrapWidget(this.context, ctl, unit.getFormatType() + "_" + doctype + "_" + sfx);
        ctl.setData("presentationId", Long.valueOf(id));
        if (pres.isDefaultRepresentation()) {
            ctl.setDefaultFragment(true);
        }
    }

    private void wrapCustomFragment(IUnit unit, Control ctl, String doctype) {
        RcpClientContext.wrapWidget(this.context, ctl, unit.getFormatType() + "_" + doctype);
    }

    public void deleteView() {
        if (this.unitListener != null) {
            this.unit.removeListener(this.unitListener);
            this.unitListener = null;
        }
        if (this.unitFormatter != null) {
            this.unitFormatter.dispose();
            this.unitFormatter = null;
        }
        if (this.rawDoc != null) {
            this.rawDoc.dispose();
            this.rawDoc = null;
        }
        this.tabman.removeAllEntries();
        this.unit = null;
        this.fragmentList = null;
        this.fragmentBlacklist = null;
    }

    public void setFocus() {
        if (this.unit == null) {
            return;
        }
        this.context.getPartManager().onFocus(this.part);
        notifyListeners(new JebClientEvent(JC.FocusGained, this, this));
        this.tabman.setFocus();
        if (this.tabman.getCurrentEntryControl() == null) {
            this.tabman.showEntry(0, true);
        }
    }

    private boolean shouldDisplay(Class<? extends IRcpUnitFragment> fragmentClass) {
        if (this.fragmentList == null) {
            if (this.fragmentBlacklist != null) {
                return !inList(this.fragmentBlacklist, fragmentClass);
            }
            return true;
        }
        return inList(this.fragmentList, fragmentClass);
    }

    private static boolean inList(List<String> fragmentList, Class<? extends IRcpUnitFragment> targetFragmentClass) {
        if (fragmentList == null) {
            return false;
        }
        if (fragmentList.contains(targetFragmentClass.getName())) {
            return true;
        }
        for (String name : fragmentList) {
            if ("*".equalsIgnoreCase(name)) {
                return true;
            }
            try {
                Class<?> c = Class.forName(name);
                if (c.isAssignableFrom(targetFragmentClass)) {
                    return true;
                }
            } catch (ClassNotFoundException localClassNotFoundException) {
            }
        }
        return false;
    }

    public TabFolderView getFolder() {
        return this.tabman;
    }

    public IUnit getUnit() {
        return this.unit;
    }

    public void setActiveFragment(IUnitFragment fragment) {
        if (!(fragment instanceof IRcpUnitFragment)) {
            throw new IllegalArgumentException();
        }
        this.tabman.showEntry(((IRcpUnitFragment) fragment).getFragmentControl(), true);
    }

    public AbstractUnitFragment<?> getActiveFragment() {
        Control ctl = this.tabman.getCurrentEntryControl();
        if (!(ctl instanceof AbstractUnitFragment)) {
            return null;
        }
        return (AbstractUnitFragment) ctl;
    }

    public List<AbstractUnitFragment<?>> getPreviouslyActiveFragments() {
        List<AbstractUnitFragment<?>> r = new ArrayList<>();
        for (CTabItem tab : this.tabman.getPreviouslyFocusedTabs()) {
            Control ctl = tab.getControl();
            if ((ctl instanceof AbstractUnitFragment)) {
                r.add((AbstractUnitFragment) ctl);
            }
        }
        return r;
    }

    public List<IRcpUnitFragment> getFragments() {
        List<IRcpUnitFragment> r = new ArrayList<>();
        for (Control ctl : this.tabman.getControls()) {
            if ((ctl instanceof AbstractUnitFragment)) {
                r.add((IRcpUnitFragment) ctl);
            }
        }
        return r;
    }

    public <T extends AbstractUnitFragment<?>> T getFragmentByType(Class<T> type) {
        for (Control ctl : this.tabman.getControls()) {
            if (type.isInstance(ctl)) {
                return (T) ctl;
            }
        }
        return null;
    }

    public AbstractUnitFragment<?> getFragmentByName(String name) {
        for (TabFolderView.Entry e : this.tabman.getEntries()) {
            if (((e.getControl() instanceof AbstractUnitFragment)) && (Strings.equals(name, e.getName()))) {
                return (AbstractUnitFragment) e.getControl();
            }
        }
        return null;
    }

    public IItem getActiveItem() {
        Control ctl = this.tabman.getCurrentEntryControl();
        if ((ctl instanceof AbstractUnitFragment)) {
            return ((IRcpUnitFragment) ctl).getActiveItem();
        }
        return null;
    }

    public String getActiveAddress() {
        return getActiveAddress(AddressConversionPrecision.DEFAULT);
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        Control ctl = this.tabman.getCurrentEntryControl();
        if ((ctl instanceof AbstractUnitFragment)) {
            return ((IRcpUnitFragment) ctl).getActiveAddress(precision);
        }
        return null;
    }

    public Position getActivePosition() {
        Control ctl = this.tabman.getCurrentEntryControl();
        if ((ctl instanceof AbstractUnitFragment)) {
            return ((IRcpUnitFragment) ctl).getActivePosition();
        }
        return null;
    }

    public boolean setActiveAddress(String address, Object extra, boolean record) {
        Control ctl = this.tabman.getCurrentEntryControl();
        if ((ctl instanceof AbstractUnitFragment)) {
            return ((IRcpUnitFragment) ctl).setActiveAddress(address, extra, record);
        }
        return false;
    }

    public String getFragmentLabel(IUnitFragment fragment) {
        if (!(fragment instanceof AbstractUnitFragment)) {
            throw new IllegalArgumentException();
        }
        Composite f = (AbstractUnitFragment) fragment;
        return this.tabman.getTab(f).getText();
    }

    public void setFragmentLabel(IUnitFragment fragment, String label) {
        if (!(fragment instanceof AbstractUnitFragment)) {
            throw new IllegalArgumentException();
        }
        Composite f = (AbstractUnitFragment) fragment;
        this.tabman.getTab(f).setText(label);
    }

    public boolean verifyOperation(OperationRequest req) {
        if (delegateOperation(req, true)) {
            return true;
        }
        switch (req.getOperation()) {
            case PROPERTIES:
                return this.unit != null;
            case NAVIGATE_BACKWARD:
                return this.context.getViewManager().getGlobalPositionHistory().hasPrevious();
            case NAVIGATE_FORWARD:
                return this.context.getViewManager().getGlobalPositionHistory().hasNext();
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        if (delegateOperation(req, false)) {
            return true;
        }
        if (!req.proceed()) {
            return false;
        }
        switch (req.getOperation()) {
            case PROPERTIES:
                new UnitPropertiesDialog(this.parent.getShell(), this.unit).open();
                return true;
            case NAVIGATE_BACKWARD:
            case NAVIGATE_FORWARD:
                PartManager pman = this.context.getPartManager();
                logger.i("GlobalPositionHistory=\n%s", new Object[]{this.context.getViewManager().getGlobalPositionHistory()});
                GlobalPosition pos0 = this.context.getViewManager().getCurrentGlobalPosition();
                GlobalPosition pos = req.getOperation() == Operation.NAVIGATE_BACKWARD ? (GlobalPosition) this.context.getViewManager().getGlobalPositionHistory().getPrevious(pos0) : (GlobalPosition) this.context.getViewManager().getGlobalPositionHistory().getNext(pos0);
                if (pos == null) {
                    return false;
                }
                IUnit unit = pos.getUnit();
                if (unit == null) {
                    return false;
                }
                IMPart targetPart = pman.getPartById(pos.getPartId(), unit);
                if (targetPart == null) {
                    if (unit.getPropertyManager() == null) {
                        return false;
                    }
                    targetPart = (IMPart) pman.create(unit, true).get(0);
                }
                pman.focus(targetPart);
                UnitPartManager object = pman.getUnitPartManager(targetPart);
                if (object == null) {
                    throw new JebRuntimeException(String.format("Can not restore position of %s: is it a unit part?", new Object[]{targetPart.toString()}));
                }
                Position p = pos.getPosition();
                if (p == null) {
                    return false;
                }
                object.setActiveAddress(p.getAddress(), p.getExtra(), false);
                return true;
        }
        return false;
    }

    private boolean delegateOperation(OperationRequest req, boolean verify) {
        CTabItem sel = this.tabman.getContainer().getSelection();
        if (sel != null) {
            Control ctl = sel.getControl();
            if ((ctl instanceof IOperable)) {
                if (verify) {
                    return ((IOperable) ctl).verifyOperation(req);
                }
                return ((IOperable) ctl).doOperation(req);
            }
        }
        return false;
    }

    public static MediaType getMediaTypeForUnit(IUnit unit) {
        if (!(unit instanceof IBinaryUnit)) {
            return MediaType.OCTET_STREAM;
        }
        String mimeType = ((IBinaryUnit) unit).getMimeType();
        if (mimeType == null) {
            return MediaType.OCTET_STREAM;
        }
        String[] parts = mimeType.split("/");
        if (parts.length != 2) {
            return MediaType.OCTET_STREAM;
        }
        try {
            return MediaType.create(parts[0], parts[1]);
        } catch (IllegalArgumentException e) {
        }
        return MediaType.OCTET_STREAM;
    }

    public static String getIconForUnit(IUnit unit) {
        return ProjectTreeLabelProvider.getUnitIconRelativePath(unit);
    }

    public String toString() {
        return String.format("View:{%s}", new Object[]{getLabel()});
    }
}


