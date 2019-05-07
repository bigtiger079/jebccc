/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.Operation;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.client.events.JC;
/*     */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*     */ import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
/*     */ import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.input.SubInput;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.core.units.IBinaryUnit;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.impl.ContainerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ArtifactPropertiesDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ProjectPropertiesDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ReparseDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ReparseDialog.Information;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.UnitPropertiesDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionExtractToHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionParseAtHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileDeleteHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FilePropertiesHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.io.Endianness;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.annotation.PostConstruct;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.action.Separator;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.jface.viewers.DoubleClickEvent;
/*     */ import org.eclipse.jface.viewers.IDoubleClickListener;
/*     */ import org.eclipse.jface.viewers.ISelection;
/*     */ import org.eclipse.jface.viewers.ISelectionChangedListener;
/*     */ import org.eclipse.jface.viewers.ITreeSelection;
/*     */ import org.eclipse.jface.viewers.SelectionChangedEvent;
/*     */ import org.eclipse.jface.viewers.StructuredSelection;
/*     */ import org.eclipse.jface.viewers.TreePath;
/*     */ import org.eclipse.jface.viewers.TreeSelection;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.swt.dnd.DropTarget;
/*     */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*     */ import org.eclipse.swt.dnd.DropTargetEvent;
/*     */ import org.eclipse.swt.dnd.FileTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.MessageBox;

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
/*     */ public class ProjectExplorerPartManager
        /*     */ extends AbstractPartManager
        /*     */ implements IOperable
        /*     */ {
    /*  94 */   private static final ILogger logger = GlobalLog.getLogger(ProjectExplorerPartManager.class);
    /*     */   private Composite parent;
    /*     */   private PatternTreeView pt;
    /*     */   private FilteredTreeViewer ftv;
    /*     */   private PartManager pman;

    /*     */
    /*     */
    public ProjectExplorerPartManager(RcpClientContext context)
    /*     */ {
        /* 102 */
        super(context);
        /*     */
    }

    /*     */
    /*     */
    @PostConstruct
    /*     */ public void createView(Composite parent, IMPart part)
    /*     */ {
        /* 108 */
        parent.setLayout(new FillLayout());
        /*     */
        /* 110 */
        this.parent = parent;
        /* 111 */
        this.pman = this.context.getPartManager();
        /*     */
        /*     */
        /*     */
        /*     */
        /* 116 */
        ProjectTreeLabelProvider labelProvider = new ProjectTreeLabelProvider();
        /* 117 */
        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);
        /* 118 */
        boolean expandAfterFilter = this.context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");
        /* 119 */
        this.pt = new PatternTreeView(parent, 0, null, null, patternMatcher, expandAfterFilter);
        /* 120 */
        this.ftv = this.pt.getTreeViewer();
        /*     */
        /*     */
        /* 123 */
        this.ftv.addSelectionChangedListener(new ISelectionChangedListener()
                /*     */ {
            /*     */
            public void selectionChanged(SelectionChangedEvent e) {
                /* 126 */
                if (ProjectExplorerPartManager.this.context.getProperties().getProjectUnitSync()) {
                    /* 127 */
                    ProjectExplorerPartManager.this.handleSelectionChangedEvent(e);
                    /*     */
                }
                /*     */
            }
            /* 130 */
        });
        /* 131 */
        this.ftv.addDoubleClickListener(new IDoubleClickListener()
                /*     */ {
            /*     */
            public void doubleClick(DoubleClickEvent e) {
                /* 134 */
                ProjectExplorerPartManager.this.handleDoubleClickEvent(e);
                /*     */
            }
            /*     */
            /*     */
            /* 138 */
        });
        /* 139 */
        this.ftv.setContentProvider(new ProjectTreeContentProvider(this.context, this.pman));
        /* 140 */
        this.ftv.setLabelProvider(labelProvider);
        /* 141 */
        this.ftv.setInput(this.context.getEnginesContext());
        /*     */
        /*     */
        /* 144 */
        this.context.addListener(new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e)
            /*     */ {
                /* 148 */
                if ((e.getType() == JC.InitializationComplete) && (!((TreeViewer) ProjectExplorerPartManager.this.ftv.getViewer()).getControl().isDisposed())) {
                    /* 149 */
                    ProjectExplorerPartManager.this.ftv.setInput(ProjectExplorerPartManager.this.context.getEnginesContext());
                    /*     */
                }
                /*     */
                /*     */
            }
            /*     */
            /* 154 */
        });
        /* 155 */
        new ContextMenu(((TreeViewer) this.ftv.getViewer()).getControl()).addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /* 158 */
                if (!ProjectExplorerPartManager.this.context.hasOpenedProject()) {
                    /* 159 */
                    return;
                    /*     */
                }
                /* 161 */
                menuMgr.add(new ActionExtractToHandler());
                /* 162 */
                menuMgr.add(new ActionParseAtHandler());
                /* 163 */
                menuMgr.add(new Separator());
                /* 164 */
                menuMgr.add(new FileDeleteHandler());
                /* 165 */
                menuMgr.add(new Separator());
                /* 166 */
                menuMgr.add(new FilePropertiesHandler());
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public Object getSelectedNode()
    /*     */ {
        /* 178 */
        ITreeSelection treesel = (ITreeSelection) this.ftv.getSelection();
        /* 179 */
        if (treesel.isEmpty()) {
            /* 180 */
            return null;
            /*     */
        }
        /*     */
        /* 183 */
        return treesel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */
    public boolean focusOnNode(Object node) {
        /* 187 */
        if ((!(node instanceof IRuntimeProject)) && (!(node instanceof ILiveArtifact)) && (!(node instanceof IUnit))) {
            /* 188 */
            return false;
            /*     */
        }
        /*     */
        /* 191 */
        TreeViewer v = (TreeViewer) this.ftv.getViewer();
        /* 192 */
        ISelection selection = new StructuredSelection(node);
        /*     */
        /* 194 */
        IRuntimeProject project = this.context.getOpenedProject();
        /*     */
        /* 196 */
        if ((node instanceof IRuntimeProject)) {
            /* 197 */
            if (node != project) {
                /* 198 */
                return false;
                /*     */
            }
            /*     */
            /* 201 */
            selection = new StructuredSelection(project);
            /*     */
        }
        /* 203 */
        else if ((node instanceof ILiveArtifact)) {
            /* 204 */
            ILiveArtifact artifact = null;
            /* 205 */
            for (ILiveArtifact a : project.getLiveArtifacts()) {
                /* 206 */
                if (a == node) {
                    /* 207 */
                    artifact = a;
                    /* 208 */
                    break;
                    /*     */
                }
                /*     */
            }
            /* 211 */
            if (artifact == null) {
                /* 212 */
                return false;
                /*     */
            }
            /*     */
            /* 215 */
            selection = new TreeSelection(new TreePath(new Object[]{project, artifact}));
            /*     */
        }
        /* 217 */
        else if ((node instanceof IUnit)) {
            /* 218 */
            IUnit unit = (IUnit) node;
            /*     */
            /* 220 */
            Object path = new ArrayList();
            /*     */
            for (; ; ) {
                /* 222 */
                ((List) path).add(0, unit);
                /* 223 */
                if (!(unit.getParent() instanceof IUnit)) {
                    /*     */
                    break;
                    /*     */
                }
                /*     */
                /* 227 */
                unit = (IUnit) unit.getParent();
                /*     */
            }
            /*     */
            /* 230 */
            IArtifact artifact0 = (IArtifact) unit.getParent();
            /* 231 */
            ILiveArtifact artifact = null;
            /* 232 */
            for (ILiveArtifact a : project.getLiveArtifacts()) {
                /* 233 */
                if (a.getArtifact() == artifact0) {
                    /* 234 */
                    artifact = a;
                    /*     */
                }
                /*     */
            }
            /* 237 */
            if (artifact == null) {
                /* 238 */
                return false;
                /*     */
            }
            /* 240 */
            ((List) path).add(0, artifact);
            /*     */
            /* 242 */
            ((List) path).add(0, project);
            /*     */
            /* 244 */
            selection = new TreeSelection(new TreePath(((List) path).toArray()));
            /*     */
        }
        /*     */
        /* 247 */
        v.reveal(node);
        /* 248 */
        v.setSelection(selection);
        /* 249 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public void setFocus()
    /*     */ {
        /* 254 */
        this.pt.setFocus();
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
    /*     */
    public static void setupDragAndDrop(final Control parent, RcpClientContext context)
    /*     */ {
        /* 266 */
        DropTarget dt = new DropTarget(parent, 7);
        /* 267 */
        dt.setTransfer(new Transfer[]{FileTransfer.getInstance()});
        /* 268 */
        dt.addDropListener(new DropTargetAdapter()
                /*     */ {
            /*     */
            public void drop(DropTargetEvent event) {
                /* 271 */
                if ((FileTransfer.getInstance().isSupportedType(event.currentDataType)) && ((event.data instanceof String[])))
                    /*     */ {
                    /* 273 */
                    String[] paths = (String[]) event.data;
                    /* 274 */
                    int i = 0;
                    /* 275 */
                    for (String path : paths)
                        /*     */ {
                        /* 277 */
                        if (i == 0) {
                            /* 278 */
                            if (this.val$context.hasOpenedProject()) {
                                /* 279 */
                                MessageBox mb = new MessageBox(this.val$context.getActiveShell(), 456);
                                /*     */
                                /* 281 */
                                mb.setText(S.s(207));
                                /* 282 */
                                mb.setMessage(
                                        /* 283 */                   S.s(659) + ".\n\nWould you like to create a new project?");
                                /* 284 */
                                int r = mb.open();
                                /* 285 */
                                if (r == 64) {
                                    /* 286 */
                                    if (!this.val$context.loadInputAsProject(parent.getShell(), path)) {
                                        /*     */
                                        break;
                                        /*     */
                                    }
                                    /*     */
                                }
                                /* 290 */
                                else if (r == 128) {
                                    /* 291 */
                                    this.val$context.loadInputAsAdditionalArtifact(parent.getShell(), path);
                                    /*     */
                                } else {
                                    /* 293 */
                                    if (r == 256) {
                                        /*     */
                                        break;
                                        /*     */
                                    }
                                    /*     */
                                }
                                /*     */
                            }
                            /* 298 */
                            else if (!this.val$context.loadInputAsProject(parent.getShell(), path))
                                /*     */ {
                                /*     */
                                break;
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                        else {
                            /* 304 */
                            this.val$context.loadInputAsAdditionalArtifact(parent.getShell(), path);
                            /*     */
                        }
                        /* 306 */
                        i++;
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    private void handleSelectionChangedEvent(SelectionChangedEvent e) {
        /* 314 */
        handleSelectionEvent(e.getSelection(), 0);
        /*     */
    }

    /*     */
    /*     */
    private void handleDoubleClickEvent(DoubleClickEvent e) {
        /* 318 */
        handleSelectionEvent(e.getSelection(), 1);
        /*     */
    }

    /*     */
    /*     */
    private void handleSelectionEvent(ISelection selection, int action) {
        /* 322 */
        if (!(selection instanceof TreeSelection)) {
            /* 323 */
            return;
            /*     */
        }
        /*     */
        /* 326 */
        TreeSelection treesel = (TreeSelection) selection;
        /* 327 */
        Object elt = treesel.getFirstElement();
        /* 328 */
        if (elt == null) {
            /* 329 */
            return;
            /*     */
        }
        /*     */
        /* 332 */
        handleNodeAction(elt, action);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private void handleNodeAction(Object elt, int action)
    /*     */ {
        /* 342 */
        if (((elt instanceof IUnit)) && (!(elt instanceof ContainerUnit))) {
            /* 343 */
            IUnit unit = (IUnit) elt;
            /*     */
            /* 345 */
            this.context.getTelemetry().record("handlerOpenUnit", "unitType", unit.getFormatType());
            /*     */
            /*     */
            /* 348 */
            if (!HandlerUtil.processUnit(this.parent.getShell(), this.context, unit, true)) {
                /* 349 */
                return;
                /*     */
            }
            /*     */
            /*     */
            /* 353 */
            this.pman.create(unit, true);
            /*     */
            /*     */
            /* 356 */
            if (action == 0) {
                /* 357 */
                setFocus();
                /*     */
            }
            /*     */
            /*     */
        }
        /* 361 */
        else if (action == 1) {
            /* 362 */
            ((TreeViewer) this.ftv.getViewer()).setExpandedState(elt, !((TreeViewer) this.ftv.getViewer()).getExpandedState(elt));
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 368 */
        Object node = getSelectedNode();
        /* 369 */
        switch (req.getOperation()) {
            /*     */
            case PROPERTIES:
                /* 371 */
                return ((node instanceof IRuntimeProject)) || ((node instanceof ILiveArtifact)) || ((node instanceof IUnit));
            /*     */
            /*     */
            case EXTRACT_TO:
                /* 374 */
                return ((node instanceof ILiveArtifact)) || ((node instanceof IBinaryUnit));
            /*     */
            /*     */
            case PARSE_AT:
                /*     */
            case VIEW:
                /*     */
            case VIEW_NEW:
                /*     */
            case DELETE:
                /* 380 */
                return ((node instanceof IUnit)) || ((node instanceof ILiveArtifact));
            /*     */
            /*     */
            case FIND:
                /* 383 */
                return true;
            /*     */
        }
        /*     */
        /* 386 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 392 */
        Object node = getSelectedNode();
        /* 393 */
        boolean successfullyIdentified;
        switch (req.getOperation())
            /*     */ {
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            case PARSE_AT:
                /* 401 */
                if (!(node instanceof IUnit)) {
                    /* 402 */
                    return false;
                    /*     */
                }
                /* 404 */
                IUnit unit = (IUnit) node;
                /*     */
                /* 406 */
                ReparseDialog dlg = new ReparseDialog(this.parent.getShell(), unit);
                /* 407 */
                ReparseDialog.Information info = dlg.open();
                /* 408 */
                if (info == null) {
                    /* 409 */
                    return false;
                    /*     */
                }
                /* 411 */
                logger.i("info= %s", new Object[]{info});
                /*     */
                /* 413 */
                IInput subinput = null;
                /* 414 */
                if ((unit instanceof IBinaryUnit)) {
                    /* 415 */
                    IInput input = ((IBinaryUnit) unit).getInput();
                    /* 416 */
                    long offset = info.getOffset();
                    /* 417 */
                    long size = info.getSize();
                    /* 418 */
                    long maxsize = input.getCurrentSize();
                    /* 419 */
                    if (offset + size > maxsize)
                        /*     */ {
                        /* 421 */
                        size = maxsize - offset;
                        /*     */
                    }
                    /* 423 */
                    if ((offset >= 0L) && (size >= 0L) && (offset <= maxsize)) {
                        /*     */
                        try {
                            /* 425 */
                            subinput = new SubInput(input, offset, size);
                            /*     */
                        }
                        /*     */ catch (IOException e) {
                            /* 428 */
                            logger.catching(e);
                            /* 429 */
                            return false;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 434 */
                IUnitProcessor processor = unit.getUnitProcessor();
                /* 435 */
                IUnitIdentifier ident = processor.getUnitIdentifier(info.getWantedType());
                /* 436 */
                if (ident == null) {
                    /* 437 */
                    return false;
                    /*     */
                }
                /*     */
                /* 440 */
                successfullyIdentified = ident.canIdentify(subinput, unit);
                /* 441 */
                if (!successfullyIdentified) {
                    /* 442 */
                    String msg = String.format("%s: \"%s\".\n\n%s", new Object[]{S.s(389), ident
                            /* 443 */.getFormatType(), S.s(662)});
                    /* 444 */
                    if (!MessageDialog.openQuestion(this.parent.getShell(), S.s(377), msg)) {
                        /* 445 */
                        return false;
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 449 */
                IUnit subunit = null;
                /*     */
                try {
                    /* 451 */
                    subunit = ident.prepare(info.getSubUnitName(), subinput, processor, unit);
                    /*     */
                }
                /*     */ catch (Exception e)
                    /*     */ {
                    /* 455 */
                    if (!successfullyIdentified) {
                        /* 456 */
                        logger.catching(e);
                        /*     */
                    }
                    /*     */
                    else {
                        /* 459 */
                        throw e;
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 463 */
                if (subunit == null) {
                    /* 464 */
                    String msg = String.format("%s: \"%s\".", new Object[]{S.s(390), ident.getFormatType()});
                    /* 465 */
                    MessageDialog.openError(this.parent.getShell(), S.s(629), msg);
                    /* 466 */
                    return false;
                    /*     */
                }
                /*     */
                /* 469 */
                if ((subunit instanceof INativeCodeUnit)) {
                    /* 470 */
                    NativeImageReparseExtraOptionsDialog dlg2 = new NativeImageReparseExtraOptionsDialog(this.parent.getShell());
                    /* 471 */
                    dlg2.setSelected(true);
                    /* 472 */
                    String str = dlg2.open();
                    /* 473 */
                    if (str == null) {
                        /* 474 */
                        return false;
                        /*     */
                    }
                    /*     */
                    /* 477 */
                    INativeCodeUnit<?> pbcu = (INativeCodeUnit) subunit;
                    /* 478 */
                    pbcu.setVirtualImageBase(dlg2.getImageBase());
                    /* 479 */
                    pbcu.getProcessor().setEndianness(dlg2.isBigEndian() ? Endianness.BIG_ENDIAN : Endianness.LITTLE_ENDIAN);
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
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /* 496 */
                unit.addChild(subunit);
                /* 497 */
                UI.info("A reparsed sub-unit was created");
                /* 498 */
                return true;
            /*     */
            /*     */
            case EXTRACT_TO:
                /* 501 */
                IInput input = null;
                /* 502 */
                String name = null;
                /* 503 */
                if ((node instanceof ILiveArtifact)) {
                    /* 504 */
                    input = ((ILiveArtifact) node).getArtifact().getInput();
                    /* 505 */
                    name = ((ILiveArtifact) node).getArtifact().getName();
                    /*     */
                }
                /* 507 */
                else if ((node instanceof IBinaryUnit)) {
                    /* 508 */
                    input = ((IBinaryUnit) node).getInput();
                    /* 509 */
                    name = ((IBinaryUnit) node).getName();
                    /*     */
                }
                /*     */
                else {
                    /* 512 */
                    return false;
                    /*     */
                }
                /*     */
                /* 515 */
                FileDialog dlg = new FileDialog(this.parent.getShell(), 8192);
                /* 516 */
                dlg.setText(S.s(339));
                /* 517 */
                dlg.setOverwrite(true);
                /* 518 */
                dlg.setFileName(name);
                /* 519 */
                String filepath = dlg.open();
                /* 520 */
                if (filepath == null) {
                    /* 521 */
                    return false;
                    /*     */
                }
                /*     */
                /* 524 */
                logger.i("%s: %s", new Object[]{S.s(340), filepath});
                /*     */
                /* 526 */
                byte[] data = null;
                /* 527 */
                try {
                    InputStream in = input.getStream();
                    successfullyIdentified = null;
                    /* 528 */
                    try {
                        data = IO.readInputStream(in);
                        /* 529 */
                        IO.writeFile(new File(filepath), data);
                        /*     */
                    }
                    /*     */ catch (Throwable localThrowable1)
                        /*     */ {
                        /* 527 */
                        successfullyIdentified = localThrowable1;
                        throw localThrowable1;
                        /*     */
                    }
                    /*     */ finally {
                        /* 530 */
                        if (in != null) if (successfullyIdentified != null) try {
                            in.close();
                        } catch (Throwable localThrowable2) {
                            successfullyIdentified.addSuppressed(localThrowable2);
                        }
                        else in.close();
                        /*     */
                    }
                    /* 532 */
                } catch (IOException e) {
                    logger.catching(e);
                    /* 533 */
                    return false;
                    /*     */
                }
                /* 535 */
                return true;
            /*     */
            /*     */
            case PROPERTIES:
                /* 538 */
                JebDialog dlg = null;
                /* 539 */
                if ((node instanceof IRuntimeProject)) {
                    /* 540 */
                    dlg = new ProjectPropertiesDialog(this.parent.getShell(), (IRuntimeProject) node);
                    /*     */
                }
                /* 542 */
                else if ((node instanceof ILiveArtifact)) {
                    /* 543 */
                    dlg = new ArtifactPropertiesDialog(this.parent.getShell(), (ILiveArtifact) node);
                    /*     */
                }
                /* 545 */
                else if ((node instanceof IUnit)) {
                    /* 546 */
                    dlg = new UnitPropertiesDialog(this.parent.getShell(), (IUnit) node);
                    /*     */
                }
                /*     */
                else {
                    /* 549 */
                    return false;
                    /*     */
                }
                /* 551 */
                dlg.open();
                /* 552 */
                return true;
            /*     */
            /*     */
            case VIEW:
                /*     */
            case VIEW_NEW:
                /* 556 */
                if ((node instanceof IUnit)) {
                    /* 557 */
                    this.pman.create((IUnit) node, req.getOperation() == Operation.VIEW);
                    /*     */
                }
                /*     */
                else {
                    /* 560 */
                    return false;
                    /*     */
                }
                /* 562 */
                return true;
            /*     */
            /*     */
            case DELETE:
                /* 565 */
                if ((node instanceof IUnit)) {
                    /* 566 */
                    if (MessageDialog.openConfirm(UI.getShellTracker().get(), S.s(207),
                            /* 567 */           S.s(791))) {
                        /* 568 */
                        IUnit unit = (IUnit) node;
                        /* 569 */
                        return this.context.getOpenedProject().destroyUnit(unit);
                        /*     */
                    }
                    /*     */
                }
                /* 572 */
                else if ((node instanceof ILiveArtifact))
                    /*     */ {
                    /* 574 */
                    if (MessageDialog.openConfirm(UI.getShellTracker().get(), S.s(207),
                            /* 575 */           S.s(791))) {
                        /* 576 */
                        return RuntimeProjectUtil.destroyLiveArtifact((ILiveArtifact) node);
                        /*     */
                    }
                    /*     */
                }
                /* 579 */
                return false;
            /*     */
            /*     */
            case FIND:
                /* 582 */
                this.pt.setFilterVisibility(true, true);
                /* 583 */
                return true;
            /*     */
        }
        /*     */
        /* 586 */
        return false;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\ProjectExplorerPartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */