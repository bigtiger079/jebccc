/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.google.common.net.MediaType;
/*     */ import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.IUnitFragment;
/*     */ import com.pnfsoftware.jeb.client.api.Operation;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.client.events.JC;
/*     */ import com.pnfsoftware.jeb.client.events.JebClientEvent;
/*     */ import com.pnfsoftware.jeb.core.events.J;
/*     */ import com.pnfsoftware.jeb.core.exceptions.JebRuntimeException;
/*     */ import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IGenericDocument;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.IUnitDocumentPresentation;
/*     */ import com.pnfsoftware.jeb.core.output.IUnitFormatter;
/*     */ import com.pnfsoftware.jeb.core.output.table.ITableDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.AsciiDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.HexDumpDocument;
/*     */ import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.IAddressableUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IBinaryUnit;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.IERoutineContext;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilationTarget;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.ir.IEStatement;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.IPECOFFUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.UnitPropertiesDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.AbstractRefresher;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabContextMenuManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView.Entry;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.BinaryDataView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.DescriptionView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTreeView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.Position;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.UnitImageView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.NativeTypesView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.ReferencedMethodsView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.StringsView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.codeobject.CodeLoaderInfoView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.codeobject.CodeLoaderSegmentsView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.codeobject.CodeLoaderSymbolsView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgBreakpointsView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgCodeView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgLogView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgStackView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgThreadsView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgVariablesView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.DalvikCallgraphView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.DalvikCodeGraphView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.NativeCallgraphView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.NativeCodeGraphView;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.StaticCodeGraphView;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.Extensions;
/*     */ import com.pnfsoftware.jeb.util.collect.ItemHistory;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.swt.custom.CTabFolder;
/*     */ import org.eclipse.swt.custom.CTabItem;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class UnitPartManager
        /*     */ extends AbstractPartManager
        /*     */ implements IRcpUnitView
        /*     */ {
    /* 113 */   private static final ILogger logger = GlobalLog.getLogger(UnitPartManager.class, Integer.MAX_VALUE);
    /*     */
    /*     */   private Display display;
    /*     */   private AbstractRefresher refresher1;
    /*     */   private AbstractRefresher refresher2;
    /*     */   private Composite parent;
    /*     */   private TabFolderView tabman;
    /*     */   private IMPart part;
    /*     */   private IUnit unit;
    /*     */   private List<String> fragmentList;
    /*     */   private List<String> fragmentBlacklist;
    /*     */   private IEventListener unitListener;
    /*     */   private IUnitFormatter unitFormatter;
    /*     */   private AbstractTextDocument rawDoc;

    /*     */
    /*     */
    public UnitPartManager(RcpClientContext context)
    /*     */ {
        /* 130 */
        super(context);
        /*     */
    }

    /*     */
    /*     */
    public String getLabel()
    /*     */ {
        /* 135 */
        return this.part.getLabel();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void setLabel(String label)
    /*     */ {
        /* 141 */
        this.part.setLabel(label);
        /*     */
    }

    /*     */
    /*     */
    public void createView(Composite parent, IMPart part)
    /*     */ {
        /* 146 */
        logger.i("Constructing unit part... part=%s", new Object[]{part});
        /*     */
        /*     */
        /* 149 */
        this.parent = parent;
        /* 150 */
        this.part = part;
        /*     */
        /* 152 */
        this.display = parent.getDisplay();
        /*     */
        /* 154 */
        this.refresher1 = new AbstractRefresher(this.display, "UnitPartName")
                /*     */ {
            /*     */
            protected void performRefresh() {
                /* 157 */
                if (UnitPartManager.this.unit != null) {
                    /* 158 */
                    UnitPartManager.this.updatePartName();
                    /*     */
                }
                /*     */
            }
            /* 161 */
        };
        /* 162 */
        this.refresher2 = new AbstractRefresher(this.display, "UnitPartTabs")
                /*     */ {
            /*     */
            protected void performRefresh() {
                /* 165 */
                if (UnitPartManager.this.unit != null) {
                    /* 166 */
                    UnitPartManager.this.addUnitDocuments(UnitPartManager.this.tabman.getContainer());
                    /*     */
                }
                /*     */
                /*     */
            }
            /* 170 */
        };
        /* 171 */
        parent.setLayout(new FillLayout());
        /* 172 */
        boolean lazyInit = !this.context.getPropertyManager().getBoolean("ui.AlwaysLoadFragments");
        /* 173 */
        this.tabman = new TabFolderView(parent, 3074, false, lazyInit);
        /*     */
        /*     */
        /* 176 */
        new TabContextMenuManager(this.tabman.getContainer())
                /*     */ {
            /*     */
            public void addActions(final CTabItem selectedItem, IMenuManager menuMgr) {
                /* 179 */
                menuMgr.add(new ActionEx(null, "Pull Out")
                        /*     */ {
                    /*     */
                    public void run() {
                        /* 182 */
                        if (selectedItem == null) {
                            /* 183 */
                            return;
                            /*     */
                        }
                        /* 185 */
                        UnitPartManager.logger.i("Pulling out the fragment (tab) into its own part (view)", new Object[0]);
                        /* 186 */
                        Control fragment = selectedItem.getControl();
                        /* 187 */
                        UnitPartManager.this.tabman.removeEntry(fragment);
                        /* 188 */
                        UnitPartManager.this.context.getPartManager().createSingle(UnitPartManager.this.unit, fragment.getClass());
                        /*     */
                    }
                    /*     */
                    /*     */
                });
                /*     */
            }
            /* 193 */
        }.bind();
        /* 194 */
        setup();
        /*     */
    }

    /*     */
    /*     */
    public void setup()
    /*     */ {
        /* 199 */
        this.unit = ((IUnit) this.part.getData().get("unit"));
        /* 200 */
        this.fragmentList = ((List) this.part.getData().get("fragmentList"));
        /* 201 */
        this.fragmentBlacklist = ((List) this.part.getData().get("fragmentBlacklist"));
        /*     */
        /*     */
        /* 204 */
        if (this.unit == null)
            /*     */ {
            /* 206 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /* 210 */
        if (!this.unit.isProcessed()) {
            /* 211 */
            String message = String.format("%s.\n\n%s", new Object[]{S.s(790), S.s(662)});
            /* 212 */
            boolean r = MessageDialog.openQuestion(this.parent.getShell(), S.s(821), message);
            /* 213 */
            if (!r) {
                /* 214 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 218 */
        logger.i("Building a part for unit: %s", new Object[]{this.unit});
        /*     */
        /* 220 */
        CTabFolder folder = this.tabman.getContainer();
        /*     */
        /*     */
        /*     */
        /* 224 */
        this.unit.addListener(this. = new IEventListener()
                /*     */ {
            /*     */
            /*     */
            /*     */
            public void onEvent(IEvent e)
            /*     */ {
                /*     */
                /*     */
                /* 232 */
                if (e.getType() == J.UnitPropertyChanged) {
                    /* 233 */
                    UnitPartManager.this.refresher1.request();
                    /*     */
                }
                /* 235 */
                else if (e.getType() == J.UnitChange) {
                    /* 236 */
                    UnitPartManager.this.refresher2.request();
                    /*     */
                }
                /*     */
                /*     */
            }
            /* 240 */
        });
        /* 241 */
        folder.addSelectionListener(new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent event) {
                /* 244 */
                UnitPartManager.this.updatePartName();
                /* 245 */
                UnitPartManager.this.context.refreshHandlersStates();
                /*     */
            }
            /*     */
            /*     */
            /* 249 */
        });
        /* 250 */
        Control focusedControl = null;
        /*     */
        /*     */
        /* 253 */
        if (shouldDisplay(DescriptionView.class)) {
            /* 254 */
            this.tabman.addEntry(S.s(268), new DescriptionView(folder, 0, this.context, this.unit));
            /*     */
        }
        /*     */
        /*     */
        /* 258 */
        if ((this.unit instanceof IBinaryUnit))
            /*     */ {
            /* 260 */
            if (shouldDisplay(InteractiveTextView.class)) {
                /* 261 */
                IInput input = ((IBinaryUnit) this.unit).getInput();
                /* 262 */
                if (input.getCurrentSize() > 0L) {
                    /* 263 */
                    this.rawDoc = null;
                    /* 264 */
                    String unitName = this.unit.getName();
                    /* 265 */
                    if ((!Extensions.hasExtension(unitName)) || (Extensions.hasKnownTextDocumentExtension(unitName))) {
                        /* 266 */
                        long size = input.getCurrentSize();
                        /* 267 */
                        int minExpectedAsciiSize = (int) Math.min(size, 30L);
                        /* 268 */
                        byte[] headerBytes = new byte[minExpectedAsciiSize];
                        /* 269 */
                        input.getHeader().get(headerBytes);
                        /* 270 */
                        if (Strings.getAsciiLength(headerBytes, minExpectedAsciiSize) == minExpectedAsciiSize) {
                            /* 271 */
                            this.rawDoc = new AsciiDocument(input);
                            /* 272 */
                            InteractiveTextView textdump = new InteractiveTextView(folder, 0, this.context, this.unit, this, this.rawDoc);
                            /*     */
                            /* 274 */
                            this.tabman.addEntry(S.s(769), textdump);
                            /* 275 */
                            focusedControl = textdump;
                            /*     */
                        }
                        /*     */
                    }
                    /* 278 */
                    if (this.rawDoc == null) {
                        /* 279 */
                        this.rawDoc = new HexDumpDocument(input, this.unit);
                        /* 280 */
                        BinaryDataView hexdump = new BinaryDataView(folder, 0, this.context, this.unit, this, (HexDumpDocument) this.rawDoc);
                        /*     */
                        /* 282 */
                        this.tabman.addEntry(S.s(366), hexdump);
                        /* 283 */
                        hexdump.setFocusPriority(0);
                        /* 284 */
                        focusedControl = hexdump;
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 289 */
            if (shouldDisplay(UnitImageView.class)) {
                /* 290 */
                MediaType mt = getMediaTypeForUnit(this.unit);
                /* 291 */
                if (mt.is(MediaType.ANY_IMAGE_TYPE)) {
                    /* 292 */
                    UnitImageView img = new UnitImageView(folder, 0, this.context, (IBinaryUnit) this.unit);
                    /* 293 */
                    this.tabman.addEntry(S.s(378), img);
                    /* 294 */
                    focusedControl = img;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 300 */
        this.unitFormatter = this.unit.getFormatter();
        /* 301 */
        Control focusedDocument = addUnitDocuments(folder);
        /* 302 */
        if (focusedDocument != null) {
            /* 303 */
            focusedControl = focusedDocument;
            /*     */
        }
        /*     */
        /*     */
        /* 307 */
        if ((this.unit instanceof ICodeObjectUnit)) {
            /* 308 */
            ICodeObjectUnit co = (ICodeObjectUnit) this.unit;
            /* 309 */
            if ((shouldDisplay(CodeLoaderInfoView.class)) && (co.getLoaderInformation() != null)) {
                /* 310 */
                CodeLoaderInfoView infoview = new CodeLoaderInfoView(folder, 0, this.context, co);
                /* 311 */
                this.tabman.addEntry(S.s(625), infoview);
                /* 312 */
                focusedControl = infoview;
                /*     */
                /* 314 */
                new AddressNavigator(this.context, infoview.getViewer(), infoview, this.unit);
                /*     */
            }
            /* 316 */
            if ((shouldDisplay(CodeLoaderSegmentsView.class)) && (co.getSegments() != null)) {
                /* 317 */
                CodeLoaderSegmentsView segview = new CodeLoaderSegmentsView(folder, 0, this.context, co, true);
                /* 318 */
                String label = (this.unit instanceof IPECOFFUnit) ? S.s(718) : S.s(720);
                /* 319 */
                this.tabman.addEntry(label, segview);
                /*     */
                /* 321 */
                new AddressNavigator(this.context, segview.getViewer(), segview, this.unit);
                /*     */
            }
            /* 323 */
            if ((shouldDisplay(CodeLoaderSegmentsView.class)) && (co.getSections() != null)) {
                /* 324 */
                CodeLoaderSegmentsView sectview = new CodeLoaderSegmentsView(folder, 0, this.context, co, false);
                /* 325 */
                String label = (this.unit instanceof IPECOFFUnit) ? "Directory Entries" : S.s(718);
                /* 326 */
                this.tabman.addEntry(label, sectview);
                /*     */
                /* 328 */
                new AddressNavigator(this.context, sectview.getViewer(), sectview, this.unit);
                /*     */
            }
            /* 330 */
            if ((shouldDisplay(CodeLoaderSymbolsView.class)) && (co.getSymbols() != null)) {
                /* 331 */
                CodeLoaderSymbolsView symbolsView = new CodeLoaderSymbolsView(folder, 0, this.context, this, co, 0, null);
                /* 332 */
                this.tabman.addEntry(S.s(762), symbolsView);
                /*     */
                /* 334 */
                new AddressNavigator(this.context, symbolsView.getViewer(), symbolsView, this.unit);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 339 */
        if ((this.unit instanceof ICodeUnit)) {
            /* 340 */
            if ((this.unit instanceof INativeCodeUnit)) {
                /* 341 */
                if (shouldDisplay(NativeCodeGraphView.class)) {
                    /* 342 */
                    NativeCodeGraphView v = new NativeCodeGraphView(folder, 0, this.context, (INativeCodeUnit) this.unit, this);
                    /* 343 */
                    this.tabman.addEntry("Graph", v);
                    /*     */
                }
                /* 345 */
                if (shouldDisplay(NativeCallgraphView.class)) {
                    /* 346 */
                    NativeCallgraphView v = new NativeCallgraphView(folder, 0, this.context, (INativeCodeUnit) this.unit, this);
                    /* 347 */
                    this.tabman.addEntry("Callgraph", v);
                    /*     */
                }
                /*     */
            }
            /* 350 */
            if ((this.unit instanceof IDexUnit)) {
                /* 351 */
                if (shouldDisplay(DalvikCodeGraphView.class)) {
                    /* 352 */
                    DalvikCodeGraphView v = new DalvikCodeGraphView(folder, 0, this.context, (IDexUnit) this.unit, this);
                    /* 353 */
                    this.tabman.addEntry("Graph", v);
                    /*     */
                }
                /* 355 */
                if (shouldDisplay(DalvikCallgraphView.class)) {
                    /* 356 */
                    DalvikCallgraphView v = new DalvikCallgraphView(folder, 0, this.context, (IDexUnit) this.unit, this);
                    /* 357 */
                    this.tabman.addEntry("Callgraph", v);
                    /*     */
                }
                /*     */
            }
            /* 360 */
            if (shouldDisplay(CodeHierarchyView.class)) {
                /* 361 */
                CodeHierarchyView codeHier = new CodeHierarchyView(folder, 0, this.context, (ICodeUnit) this.unit, null, 256, 65536, false);
                /*     */
                /* 363 */
                this.tabman.addEntry(S.s(369), codeHier);
                /*     */
                /* 365 */
                new AddressNavigator(this.context, codeHier.getViewer(), codeHier, this.unit);
                /*     */
                /* 367 */
                wrapCustomFragment(this.unit, codeHier, "codehier");
                /*     */
            }
            /* 369 */
            if (shouldDisplay(StringsView.class)) {
                /* 370 */
                this.tabman.addEntry(S.s(754), new StringsView(folder, 0, this.context, (ICodeUnit) this.unit, this));
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 375 */
        if ((this.unit instanceof INativeCodeUnit)) {
            /* 376 */
            INativeCodeUnit<?> pbcu = (INativeCodeUnit) this.unit;
            /* 377 */
            if (shouldDisplay(NativeTypesView.class)) {
                /* 378 */
                this.tabman.addEntry(S.s(781), new NativeTypesView(folder, 0, this.context, pbcu, this, 1));
                /*     */
            }
            /*     */
            /* 381 */
            if ((shouldDisplay(CodeLoaderSymbolsView.class)) && (pbcu.getCodeObjectContainer() != null)) {
                /* 382 */
                ICodeObjectUnit co = pbcu.getCodeObjectContainer();
                /* 383 */
                if (co != null) {
                    /* 384 */
                    CodeLoaderSymbolsView impview = new CodeLoaderSymbolsView(folder, 0, this.context, this, co, 1, pbcu);
                    /*     */
                    /* 386 */
                    this.tabman.addEntry(S.s(381), impview);
                    /* 387 */
                    CodeLoaderSymbolsView expview = new CodeLoaderSymbolsView(folder, 0, this.context, this, co, 2, pbcu);
                    /*     */
                    /* 389 */
                    this.tabman.addEntry(S.s(331), expview);
                    /*     */
                }
                /*     */
            }
            /* 392 */
            if (shouldDisplay(ReferencedMethodsView.class)) {
                /* 393 */
                this.tabman.addEntry("Referenced Methods", new ReferencedMethodsView(folder, 0, this.context, pbcu, this));
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 398 */
        if ((this.unit instanceof IDebuggerUnit)) {
            /* 399 */
            IDebuggerUnit dbg = (IDebuggerUnit) this.unit;
            /* 400 */
            IUnit parentUnit = (IUnit) dbg.getParent();
            /* 401 */
            if ((parentUnit instanceof IDebuggerUnit)) {
                /* 402 */
                parentUnit = (IUnit) parentUnit.getParent();
                /*     */
            }
            /*     */
            /* 405 */
            if (shouldDisplay(DbgLogView.class)) {
                /* 406 */
                DbgLogView logView = new DbgLogView(folder, 0, this.context, dbg);
                /* 407 */
                this.tabman.addEntry(S.s(448), logView);
                /* 408 */
                if (focusedControl == null) {
                    /* 409 */
                    focusedControl = logView;
                    /*     */
                }
                /* 411 */
                wrapCustomFragment(dbg, logView, "log");
                /*     */
            }
            /*     */
            /* 414 */
            if (shouldDisplay(DbgThreadsView.class)) {
                /* 415 */
                DbgThreadsView threadsView = new DbgThreadsView(folder, 0, this.context, dbg);
                /* 416 */
                this.tabman.addEntry(S.s(774), threadsView);
                /* 417 */
                focusedControl = threadsView;
                /* 418 */
                new AddressNavigator(this.context, threadsView.getJfaceViewer(), threadsView, parentUnit);
                /* 419 */
                wrapCustomFragment(dbg, threadsView, "threads");
                /*     */
            }
            /*     */
            /* 422 */
            if (shouldDisplay(DbgBreakpointsView.class)) {
                /* 423 */
                DbgBreakpointsView bpView = new DbgBreakpointsView(folder, 0, this.context, dbg);
                /* 424 */
                this.tabman.addEntry(S.s(101), bpView);
                /* 425 */
                if (focusedControl == null) {
                    /* 426 */
                    focusedControl = bpView;
                    /*     */
                }
                /* 428 */
                new AddressNavigator(this.context, bpView.getJfaceViewer(), bpView, parentUnit);
                /* 429 */
                wrapCustomFragment(dbg, bpView, "breakpoints");
                /*     */
            }
            /*     */
            /* 432 */
            if (shouldDisplay(DbgVariablesView.class)) {
                /* 433 */
                DbgVariablesView varView = new DbgVariablesView(folder, 0, this.context, dbg);
                /* 434 */
                this.tabman.addEntry(S.s(446), varView);
                /* 435 */
                if (focusedControl == null) {
                    /* 436 */
                    focusedControl = varView;
                    /*     */
                }
                /* 438 */
                wrapCustomFragment(dbg, varView, "variables");
                /*     */
            }
            /*     */
            /*     */
            /* 442 */
            if (shouldDisplay(DbgStackView.class)) {
                /* 443 */
                DbgStackView stkView = new DbgStackView(folder, 0, this.context, dbg);
                /* 444 */
                this.tabman.addEntry(S.s(745), stkView);
                /* 445 */
                if (focusedControl == null) {
                    /* 446 */
                    focusedControl = stkView;
                    /*     */
                }
                /* 448 */
                wrapCustomFragment(dbg, stkView, "stack");
                /*     */
            }
            /*     */
            /* 451 */
            if (shouldDisplay(DbgCodeView.class)) {
                /* 452 */
                DbgCodeView stkView = new DbgCodeView(folder, 0, this.context, dbg);
                /* 453 */
                this.tabman.addEntry(S.s(455), stkView);
                /* 454 */
                if (focusedControl == null) {
                    /* 455 */
                    focusedControl = stkView;
                    /*     */
                }
                /* 457 */
                wrapCustomFragment(dbg, stkView, "memoryCode");
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 465 */
        if (((this.unit instanceof INativeSourceUnit)) &&
                /* 466 */       (shouldDisplay(StaticCodeGraphView.class))) {
            /* 467 */
            INativeSourceUnit srcUnit = (INativeSourceUnit) this.unit;
            /* 468 */
            List<INativeDecompilationTarget> targets = srcUnit.getDecompilationTargets();
            /* 469 */
            if (!targets.isEmpty()) {
                /* 470 */
                INativeDecompilationTarget target = (INativeDecompilationTarget) targets.get(0);
                /* 471 */
                CFG<IEStatement> cfg = target.getContext().getCfg();
                /* 472 */
                StaticCodeGraphView v = new StaticCodeGraphView(folder, 0, this.context, srcUnit, this, cfg);
                /* 473 */
                this.tabman.addEntry("IR-CFG", v);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 478 */
        if ((focusedControl == null) && (this.tabman.getEntryCount() >= 1)) {
            /* 479 */
            focusedControl = this.tabman.getEntryControl(0);
            /*     */
        }
        /* 481 */
        if (focusedControl != null) {
            /* 482 */
            this.tabman.showEntry(focusedControl, true);
            /*     */
        }
        /*     */
        /*     */
        /* 486 */
        if (this.tabman.getEntryCount() <= 1) {
            /* 487 */
            this.tabman.getContainer().setTabHeight(0);
            /*     */
        }
        /*     */
        /* 490 */
        updatePartName();
        /*     */
    }

    /*     */
    /*     */
    private Control addUnitDocuments(CTabFolder folder) {
        /* 494 */
        Control focusedControl = null;
        /*     */
        /*     */
        /* 497 */
        List<Long> currentPresIds = new ArrayList();
        /* 498 */
        for (Iterator localIterator = this.unitFormatter.getPresentations().iterator(); localIterator.hasNext(); ) {
            pres = (IUnitDocumentPresentation) localIterator.next();
            /* 499 */
            if (pres.getId() != 0L) {
                /* 500 */
                currentPresIds.add(Long.valueOf(pres.getId()));
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 505 */
        Object uiPresIds = new ArrayList();
        /* 506 */
        for (IUnitDocumentPresentation pres = this.tabman.getControls().iterator(); pres.hasNext(); ) {
            ctl = (Control) pres.next();
            /* 507 */
            Long presId = (Long) ctl.getData("presentationId");
            /* 508 */
            if ((presId != null) && (presId.longValue() != 0L)) {
                /* 509 */
                ((List) uiPresIds).add(presId);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 514 */
        List<Long> tbrPresIds = new ArrayList();
        /* 515 */
        for (Control ctl = ((List) uiPresIds).iterator(); ctl.hasNext(); ) {
            long presId = ((Long) ctl.next()).longValue();
            /* 516 */
            if (!currentPresIds.contains(Long.valueOf(presId))) {
                /* 517 */
                tbrPresIds.add(Long.valueOf(presId));
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 522 */
        for (ctl = this.unitFormatter.getPresentations().iterator(); ctl.hasNext(); ) {
            pres = (IUnitDocumentPresentation) ctl.next();
            /*     */
            /* 524 */
            long presId = pres.getId();
            /* 525 */
            if ((presId == 0L) || (!((List) uiPresIds).contains(Long.valueOf(presId))))
                /*     */ {
                /*     */
                /*     */
                /* 529 */
                String label = pres.getLabel();
                /* 530 */
                IGenericDocument doc = pres.getDocument();
                /*     */
                /* 532 */
                if (((doc instanceof HexDumpDocument)) && (shouldDisplay(InteractiveTextView.class))) {
                    /* 533 */
                    BinaryDataView itext = new BinaryDataView(folder, 0, this.context, this.unit, this, (HexDumpDocument) doc);
                    /* 534 */
                    wrapDocumentFragment(pres, this.unit, itext, "hextext");
                    /*     */
                    /* 536 */
                    this.tabman.addEntry(label, itext);
                    /*     */
                    /* 538 */
                    if (pres.isDefaultRepresentation()) {
                        /* 539 */
                        focusedControl = itext;
                        /*     */
                    }
                    /*     */
                    else {
                        /* 542 */
                        itext.setFocusPriority(0);
                        /*     */
                    }
                    /*     */
                }
                /* 545 */
                else if (((doc instanceof ITextDocument)) && (shouldDisplay(InteractiveTextView.class))) {
                    /* 546 */
                    InteractiveTextView itext = new InteractiveTextView(folder, 0, this.context, this.unit, this, (ITextDocument) doc);
                    /*     */
                    /* 548 */
                    wrapDocumentFragment(pres, this.unit, itext, "text");
                    /*     */
                    /* 550 */
                    this.tabman.addEntry(label, itext);
                    /*     */
                    /* 552 */
                    if (pres.isDefaultRepresentation()) {
                        /* 553 */
                        focusedControl = itext;
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 557 */
                    if (((this.unit.getParent() instanceof IDecompilerUnit)) && ((this.unit instanceof IAddressableUnit))) {
                        /* 558 */
                        itext.setViewNavigatorHelper(new DecompiledViewNavigator((IAddressableUnit) this.unit, this.context, this.part));
                        /*     */
                    }
                    /*     */
                }
                /* 561 */
                else if (((doc instanceof ITableDocument)) && (shouldDisplay(InteractiveTableView.class))) {
                    /* 562 */
                    InteractiveTableView itable = new InteractiveTableView(folder, 0, this.context, this.unit, (ITableDocument) doc, this);
                    /*     */
                    /* 564 */
                    wrapDocumentFragment(pres, this.unit, itable, "table");
                    /*     */
                    /* 566 */
                    this.tabman.addEntry(label, itable);
                    /* 567 */
                    if (pres.isDefaultRepresentation()) {
                        /* 568 */
                        focusedControl = itable;
                        /*     */
                    }
                    /*     */
                }
                /* 571 */
                else if (((doc instanceof ITreeDocument)) && (shouldDisplay(InteractiveTreeView.class))) {
                    /* 572 */
                    InteractiveTreeView itree = new InteractiveTreeView(folder, 0, this.context, this.unit, (ITreeDocument) doc, this);
                    /*     */
                    /* 574 */
                    wrapDocumentFragment(pres, this.unit, itree, "tree");
                    /*     */
                    /* 576 */
                    this.tabman.addEntry(label, itree);
                    /* 577 */
                    if (pres.isDefaultRepresentation()) {
                        /* 578 */
                        focusedControl = itree;
                        /*     */
                        /*     */
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                }
                /*     */
                else
                    /*     */ {
                    /*     */
                    /*     */
                    /*     */
                    /* 592 */
                    doc.dispose();
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        IUnitDocumentPresentation pres;
        /* 598 */
        List<Control> tbrControls = new ArrayList();
        /* 599 */
        for (Control ctl : this.tabman.getControls()) {
            /* 600 */
            Long presId = (Long) ctl.getData("presentationId");
            /* 601 */
            if ((presId != null) && (tbrPresIds.contains(presId))) {
                /* 602 */
                tbrControls.add(ctl);
                /*     */
            }
            /*     */
        }
        /* 605 */
        for (Control ctl : tbrControls) {
            /* 606 */
            this.tabman.removeEntry(ctl);
            /*     */
        }
        /*     */
        /* 609 */
        return focusedControl;
        /*     */
    }

    /*     */
    /*     */
    private void updatePartName() {
        /* 613 */
        String n0 = this.unit.getName();
        /* 614 */
        String n1 = this.tabman.getCurrentEntryName();
        /* 615 */
        String name = String.format("%s/%s", new Object[]{n0, n1});
        /* 616 */
        this.part.setLabel(name);
        /*     */
    }

    /*     */
    /*     */
    private void wrapDocumentFragment(IUnitDocumentPresentation pres, IUnit unit, AbstractUnitFragment<?> ctl, String doctype)
    /*     */ {
        /* 621 */
        long id = pres.getId();
        /* 622 */
        String label = Strings.safe(pres.getLabel(), "noLabel");
        /* 623 */
        String sfx = id != 0L ? "" + id : label;
        /* 624 */
        RcpClientContext.wrapWidget(this.context, ctl, unit.getFormatType() + "_" + doctype + "_" + sfx);
        /* 625 */
        ctl.setData("presentationId", Long.valueOf(id));
        /* 626 */
        if (pres.isDefaultRepresentation()) {
            /* 627 */
            ctl.setDefaultFragment(true);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void wrapCustomFragment(IUnit unit, Control ctl, String doctype) {
        /* 632 */
        RcpClientContext.wrapWidget(this.context, ctl, unit.getFormatType() + "_" + doctype);
        /*     */
    }

    /*     */
    /*     */
    public void deleteView()
    /*     */ {
        /* 637 */
        if (this.unitListener != null) {
            /* 638 */
            this.unit.removeListener(this.unitListener);
            /* 639 */
            this.unitListener = null;
            /*     */
        }
        /*     */
        /* 642 */
        if (this.unitFormatter != null) {
            /* 643 */
            this.unitFormatter.dispose();
            /* 644 */
            this.unitFormatter = null;
            /*     */
        }
        /*     */
        /* 647 */
        if (this.rawDoc != null) {
            /* 648 */
            this.rawDoc.dispose();
            /* 649 */
            this.rawDoc = null;
            /*     */
        }
        /*     */
        /* 652 */
        this.tabman.removeAllEntries();
        /*     */
        /* 654 */
        this.unit = null;
        /* 655 */
        this.fragmentList = null;
        /* 656 */
        this.fragmentBlacklist = null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void setFocus()
    /*     */ {
        /* 666 */
        if (this.unit == null) {
            /* 667 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 676 */
        this.context.getPartManager().onFocus(this.part);
        /* 677 */
        notifyListeners(new JebClientEvent(JC.FocusGained, this, this));
        /*     */
        /*     */
        /* 680 */
        this.tabman.setFocus();
        /* 681 */
        if (this.tabman.getCurrentEntryControl() == null) {
            /* 682 */
            this.tabman.showEntry(0, true);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private boolean shouldDisplay(Class<? extends IRcpUnitFragment> fragmentClass) {
        /* 687 */
        if (this.fragmentList == null) {
            /* 688 */
            if (this.fragmentBlacklist != null) {
                /* 689 */
                return !inList(this.fragmentBlacklist, fragmentClass);
                /*     */
            }
            /* 691 */
            return true;
            /*     */
        }
        /* 693 */
        return inList(this.fragmentList, fragmentClass);
        /*     */
    }

    /*     */
    /*     */
    private static boolean inList(List<String> fragmentList, Class<? extends IRcpUnitFragment> targetFragmentClass)
    /*     */ {
        /* 698 */
        if (fragmentList == null) {
            /* 699 */
            return false;
            /*     */
        }
        /* 701 */
        if (fragmentList.contains(targetFragmentClass.getName())) {
            /* 702 */
            return true;
            /*     */
        }
        /* 704 */
        for (String name : fragmentList) {
            /* 705 */
            if ("*".equalsIgnoreCase(name)) {
                /* 706 */
                return true;
                /*     */
            }
            /*     */
            try {
                /* 709 */
                Class<?> c = Class.forName(name);
                /*     */
                /* 711 */
                if (c.isAssignableFrom(targetFragmentClass)) {
                    /* 712 */
                    return true;
                    /*     */
                }
                /*     */
            }
            /*     */ catch (ClassNotFoundException localClassNotFoundException) {
            }
            /*     */
        }
        /*     */
        /* 718 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public TabFolderView getFolder() {
        /* 722 */
        return this.tabman;
        /*     */
    }

    /*     */
    /*     */
    public IUnit getUnit()
    /*     */ {
        /* 727 */
        return this.unit;
        /*     */
    }

    /*     */
    /*     */
    public void setActiveFragment(IUnitFragment fragment)
    /*     */ {
        /* 732 */
        if (!(fragment instanceof IRcpUnitFragment)) {
            /* 733 */
            throw new IllegalArgumentException();
            /*     */
        }
        /* 735 */
        this.tabman.showEntry(((IRcpUnitFragment) fragment).getFragmentControl(), true);
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment<?> getActiveFragment()
    /*     */ {
        /* 740 */
        Control ctl = this.tabman.getCurrentEntryControl();
        /* 741 */
        if (!(ctl instanceof AbstractUnitFragment)) {
            /* 742 */
            return null;
            /*     */
        }
        /*     */
        /* 745 */
        return (AbstractUnitFragment) ctl;
        /*     */
    }

    /*     */
    /*     */
    public List<AbstractUnitFragment<?>> getPreviouslyActiveFragments() {
        /* 749 */
        List<AbstractUnitFragment<?>> r = new ArrayList();
        /* 750 */
        for (CTabItem tab : this.tabman.getPreviouslyFocusedTabs()) {
            /* 751 */
            Control ctl = tab.getControl();
            /* 752 */
            if ((ctl instanceof AbstractUnitFragment)) {
                /* 753 */
                r.add((AbstractUnitFragment) ctl);
                /*     */
            }
            /*     */
        }
        /* 756 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public List<IRcpUnitFragment> getFragments()
    /*     */ {
        /* 761 */
        List<IRcpUnitFragment> r = new ArrayList();
        /* 762 */
        for (Control ctl : this.tabman.getControls()) {
            /* 763 */
            if ((ctl instanceof AbstractUnitFragment)) {
                /* 764 */
                r.add((IRcpUnitFragment) ctl);
                /*     */
            }
            /*     */
        }
        /* 767 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public <T extends AbstractUnitFragment<?>> T getFragmentByType(Class<T> type)
    /*     */ {
        /* 778 */
        for (Control ctl : this.tabman.getControls()) {
            /* 779 */
            if (type.isInstance(ctl)) {
                /* 780 */
                return (AbstractUnitFragment) ctl;
                /*     */
            }
            /*     */
        }
        /* 783 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public AbstractUnitFragment<?> getFragmentByName(String name)
    /*     */ {
        /* 793 */
        for (TabFolderView.Entry e : this.tabman.getEntries()) {
            /* 794 */
            if (((e.getControl() instanceof AbstractUnitFragment)) && (Strings.equals(name, e.getName()))) {
                /* 795 */
                return (AbstractUnitFragment) e.getControl();
                /*     */
            }
            /*     */
        }
        /* 798 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem() {
        /* 802 */
        Control ctl = this.tabman.getCurrentEntryControl();
        /* 803 */
        if ((ctl instanceof AbstractUnitFragment)) {
            /* 804 */
            return ((IRcpUnitFragment) ctl).getActiveItem();
            /*     */
        }
        /* 806 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress() {
        /* 810 */
        return getActiveAddress(AddressConversionPrecision.DEFAULT);
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision) {
        /* 814 */
        Control ctl = this.tabman.getCurrentEntryControl();
        /* 815 */
        if ((ctl instanceof AbstractUnitFragment)) {
            /* 816 */
            return ((IRcpUnitFragment) ctl).getActiveAddress(precision);
            /*     */
        }
        /* 818 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public Position getActivePosition() {
        /* 822 */
        Control ctl = this.tabman.getCurrentEntryControl();
        /* 823 */
        if ((ctl instanceof AbstractUnitFragment)) {
            /* 824 */
            return ((IRcpUnitFragment) ctl).getActivePosition();
            /*     */
        }
        /* 826 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /* 831 */
        Control ctl = this.tabman.getCurrentEntryControl();
        /* 832 */
        if ((ctl instanceof AbstractUnitFragment)) {
            /* 833 */
            return ((IRcpUnitFragment) ctl).setActiveAddress(address, extra, record);
            /*     */
        }
        /* 835 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public String getFragmentLabel(IUnitFragment fragment)
    /*     */ {
        /* 840 */
        if (!(fragment instanceof AbstractUnitFragment)) {
            /* 841 */
            throw new IllegalArgumentException();
            /*     */
        }
        /*     */
        /* 844 */
        Composite f = (AbstractUnitFragment) fragment;
        /* 845 */
        return this.tabman.getTab(f).getText();
        /*     */
    }

    /*     */
    /*     */
    public void setFragmentLabel(IUnitFragment fragment, String label)
    /*     */ {
        /* 850 */
        if (!(fragment instanceof AbstractUnitFragment)) {
            /* 851 */
            throw new IllegalArgumentException();
            /*     */
        }
        /*     */
        /* 854 */
        Composite f = (AbstractUnitFragment) fragment;
        /* 855 */
        this.tabman.getTab(f).setText(label);
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 860 */
        if (delegateOperation(req, true)) {
            /* 861 */
            return true;
            /*     */
        }
        /*     */
        /* 864 */
        switch (req.getOperation()) {
            /*     */
            case PROPERTIES:
                /* 866 */
                return this.unit != null;
            /*     */
            /*     */
            case NAVIGATE_BACKWARD:
                /* 869 */
                return this.context.getViewManager().getGlobalPositionHistory().hasPrevious();
            /*     */
            /*     */
            case NAVIGATE_FORWARD:
                /* 872 */
                return this.context.getViewManager().getGlobalPositionHistory().hasNext();
            /*     */
        }
        /*     */
        /* 875 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 881 */
        if (delegateOperation(req, false)) {
            /* 882 */
            return true;
            /*     */
        }
        /* 884 */
        if (!req.proceed()) {
            /* 885 */
            return false;
            /*     */
        }
        /*     */
        /* 888 */
        switch (req.getOperation()) {
            /*     */
            case PROPERTIES:
                /* 890 */
                new UnitPropertiesDialog(this.parent.getShell(), this.unit).open();
                /* 891 */
                return true;
            /*     */
            /*     */
            case NAVIGATE_BACKWARD:
                /*     */
            case NAVIGATE_FORWARD:
                /* 895 */
                PartManager pman = this.context.getPartManager();
                /* 896 */
                logger.i("GlobalPositionHistory=\n%s", new Object[]{this.context.getViewManager().getGlobalPositionHistory()});
                /*     */
                /*     */
                /* 899 */
                GlobalPosition pos0 = this.context.getViewManager().getCurrentGlobalPosition();
                /*     */
                /*     */
                /*     */
                /* 903 */
                GlobalPosition pos = req.getOperation() == Operation.NAVIGATE_BACKWARD ? (GlobalPosition) this.context.getViewManager().getGlobalPositionHistory().getPrevious(pos0) : (GlobalPosition) this.context.getViewManager().getGlobalPositionHistory().getNext(pos0);
                /*     */
                /* 905 */
                if (pos == null) {
                    /* 906 */
                    return false;
                    /*     */
                }
                /*     */
                /* 909 */
                IUnit unit = pos.getUnit();
                /* 910 */
                if (unit == null) {
                    /* 911 */
                    return false;
                    /*     */
                }
                /*     */
                /* 914 */
                IMPart targetPart = pman.getPartById(pos.getPartId(), unit);
                /* 915 */
                if (targetPart == null) {
                    /* 916 */
                    if (unit.getPropertyManager() == null)
                        /*     */ {
                        /*     */
                        /* 919 */
                        return false;
                        /*     */
                    }
                    /* 921 */
                    targetPart = (IMPart) pman.create(unit, true).get(0);
                    /*     */
                }
                /* 923 */
                pman.focus(targetPart);
                /*     */
                /* 925 */
                UnitPartManager object = pman.getUnitPartManager(targetPart);
                /* 926 */
                if (object == null)
                    /*     */ {
                    /* 928 */
                    throw new JebRuntimeException(String.format("Can not restore position of %s: is it a unit part?", new Object[]{targetPart.toString()}));
                    /*     */
                }
                /*     */
                /* 931 */
                Position p = pos.getPosition();
                /* 932 */
                if (p == null) {
                    /* 933 */
                    return false;
                    /*     */
                }
                /*     */
                /* 936 */
                object.setActiveAddress(p.getAddress(), p.getExtra(), false);
                /* 937 */
                return true;
            /*     */
        }
        /*     */
        /* 940 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    private boolean delegateOperation(OperationRequest req, boolean verify)
    /*     */ {
        /* 946 */
        CTabItem sel = this.tabman.getContainer().getSelection();
        /* 947 */
        if (sel != null) {
            /* 948 */
            Control ctl = sel.getControl();
            /* 949 */
            if ((ctl instanceof IOperable)) {
                /* 950 */
                if (verify) {
                    /* 951 */
                    return ((IOperable) ctl).verifyOperation(req);
                    /*     */
                }
                /*     */
                /* 954 */
                return ((IOperable) ctl).doOperation(req);
                /*     */
            }
            /*     */
        }
        /*     */
        /* 958 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public static MediaType getMediaTypeForUnit(IUnit unit) {
        /* 962 */
        if (!(unit instanceof IBinaryUnit)) {
            /* 963 */
            return MediaType.OCTET_STREAM;
            /*     */
        }
        /*     */
        /* 966 */
        String mimeType = ((IBinaryUnit) unit).getMimeType();
        /* 967 */
        if (mimeType == null) {
            /* 968 */
            return MediaType.OCTET_STREAM;
            /*     */
        }
        /*     */
        /* 971 */
        String[] parts = mimeType.split("/");
        /* 972 */
        if (parts.length != 2) {
            /* 973 */
            return MediaType.OCTET_STREAM;
            /*     */
        }
        /*     */
        try
            /*     */ {
            /* 977 */
            return MediaType.create(parts[0], parts[1]);
            /*     */
        }
        /*     */ catch (IllegalArgumentException e) {
        }
        /* 980 */
        return MediaType.OCTET_STREAM;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static String getIconForUnit(IUnit unit)
    /*     */ {
        /* 989 */
        return ProjectTreeLabelProvider.getUnitIconRelativePath(unit);
        /*     */
    }

    /*     */
    /*     */
    public String toString()
    /*     */ {
        /* 994 */
        return String.format("View:{%s}", new Object[]{getLabel()});
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\UnitPartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */