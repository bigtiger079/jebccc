package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.client.events.JC;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.input.SubInput;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IBinaryUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.impl.ContainerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
import com.pnfsoftware.jeb.rcpclient.dialogs.ArtifactPropertiesDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.ProjectPropertiesDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.ReparseDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.ReparseDialog.Information;
import com.pnfsoftware.jeb.rcpclient.dialogs.UnitPropertiesDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PatternTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionExtractToHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionParseAtHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileDeleteHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FilePropertiesHandler;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.io.Endianness;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

public class ProjectExplorerPartManager extends AbstractPartManager implements IOperable {
    private static final ILogger logger = GlobalLog.getLogger(ProjectExplorerPartManager.class);
    private Composite parent;
    private PatternTreeView pt;
    private FilteredTreeViewer ftv;
    private PartManager pman;

    public ProjectExplorerPartManager(RcpClientContext context) {
        super(context);
    }

    @PostConstruct
    public void createView(Composite parent, IMPart part) {
        parent.setLayout(new FillLayout());
        this.parent = parent;
        this.pman = this.context.getPartManager();
        ProjectTreeLabelProvider labelProvider = new ProjectTreeLabelProvider();
        IPatternMatcher patternMatcher = new SimplePatternMatcher(labelProvider);
        boolean expandAfterFilter = this.context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");
        this.pt = new PatternTreeView(parent, 0, null, null, patternMatcher, expandAfterFilter);
        this.ftv = this.pt.getTreeViewer();
        this.ftv.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent e) {
                if (ProjectExplorerPartManager.this.context.getProperties().getProjectUnitSync()) {
                    ProjectExplorerPartManager.this.handleSelectionChangedEvent(e);
                }
            }
        });
        this.ftv.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                ProjectExplorerPartManager.this.handleDoubleClickEvent(e);
            }
        });
        this.ftv.setContentProvider(new ProjectTreeContentProvider(this.context, this.pman));
        this.ftv.setLabelProvider(labelProvider);
        this.ftv.setInput(this.context.getEnginesContext());
        this.context.addListener(new IEventListener() {
            public void onEvent(IEvent e) {
                if ((e.getType() == JC.InitializationComplete) && (!((TreeViewer) ProjectExplorerPartManager.this.ftv.getViewer()).getControl().isDisposed())) {
                    ProjectExplorerPartManager.this.ftv.setInput(ProjectExplorerPartManager.this.context.getEnginesContext());
                }
            }
        });
        new ContextMenu(((TreeViewer) this.ftv.getViewer()).getControl()).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                if (!ProjectExplorerPartManager.this.context.hasOpenedProject()) {
                    return;
                }
                menuMgr.add(new ActionExtractToHandler());
                menuMgr.add(new ActionParseAtHandler());
                menuMgr.add(new Separator());
                menuMgr.add(new FileDeleteHandler());
                menuMgr.add(new Separator());
                menuMgr.add(new FilePropertiesHandler());
            }
        });
    }

    public Object getSelectedNode() {
        ITreeSelection treesel = (ITreeSelection) this.ftv.getSelection();
        if (treesel.isEmpty()) {
            return null;
        }
        return treesel.getFirstElement();
    }

    public boolean focusOnNode(Object node) {
        if ((!(node instanceof IRuntimeProject)) && (!(node instanceof ILiveArtifact)) && (!(node instanceof IUnit))) {
            return false;
        }
        TreeViewer v = (TreeViewer) this.ftv.getViewer();
        ISelection selection = new StructuredSelection(node);
        IRuntimeProject project = this.context.getOpenedProject();
        if ((node instanceof IRuntimeProject)) {
            if (node != project) {
                return false;
            }
            selection = new StructuredSelection(project);
        } else if ((node instanceof ILiveArtifact)) {
            ILiveArtifact artifact = null;
            for (ILiveArtifact a : project.getLiveArtifacts()) {
                if (a == node) {
                    artifact = a;
                    break;
                }
            }
            if (artifact == null) {
                return false;
            }
            selection = new TreeSelection(new TreePath(new Object[]{project, artifact}));
        } else if ((node instanceof IUnit)) {
            IUnit unit = (IUnit) node;
            Object path = new ArrayList();
            for (; ; ) {
                ((List) path).add(0, unit);
                if (!(unit.getParent() instanceof IUnit)) {
                    break;
                }
                unit = (IUnit) unit.getParent();
            }
            IArtifact artifact0 = (IArtifact) unit.getParent();
            ILiveArtifact artifact = null;
            for (ILiveArtifact a : project.getLiveArtifacts()) {
                if (a.getArtifact() == artifact0) {
                    artifact = a;
                }
            }
            if (artifact == null) {
                return false;
            }
            ((List) path).add(0, artifact);
            ((List) path).add(0, project);
            selection = new TreeSelection(new TreePath(((List) path).toArray()));
        }
        v.reveal(node);
        v.setSelection(selection);
        return true;
    }

    public void setFocus() {
        this.pt.setFocus();
    }

    public static void setupDragAndDrop(final Control parent, RcpClientContext context) {
        DropTarget dt = new DropTarget(parent, 7);
        dt.setTransfer(new Transfer[]{FileTransfer.getInstance()});
        dt.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                if ((FileTransfer.getInstance().isSupportedType(event.currentDataType)) && ((event.data instanceof String[]))) {
                    String[] paths = (String[]) event.data;
                    int i = 0;
                    for (String path : paths) {
                        if (i == 0) {
                            if (context.hasOpenedProject()) {
                                MessageBox mb = new MessageBox(context.getActiveShell(), 456);
                                mb.setText(S.s(207));
                                mb.setMessage(S.s(659) + ".\n\nWould you like to create a new project?");
                                int r = mb.open();
                                if (r == 64) {
                                    if (!context.loadInputAsProject(parent.getShell(), path)) {
                                        break;
                                    }
                                } else if (r == 128) {
                                    context.loadInputAsAdditionalArtifact(parent.getShell(), path);
                                } else {
                                    if (r == 256) {
                                        break;
                                    }
                                }
                            } else if (!context.loadInputAsProject(parent.getShell(), path)) {
                                break;
                            }
                        } else {
                            context.loadInputAsAdditionalArtifact(parent.getShell(), path);
                        }
                        i++;
                    }
                }
            }
        });
    }

    private void handleSelectionChangedEvent(SelectionChangedEvent e) {
        handleSelectionEvent(e.getSelection(), 0);
    }

    private void handleDoubleClickEvent(DoubleClickEvent e) {
        handleSelectionEvent(e.getSelection(), 1);
    }

    private void handleSelectionEvent(ISelection selection, int action) {
        if (!(selection instanceof TreeSelection)) {
            return;
        }
        TreeSelection treesel = (TreeSelection) selection;
        Object elt = treesel.getFirstElement();
        if (elt == null) {
            return;
        }
        handleNodeAction(elt, action);
    }

    private void handleNodeAction(Object elt, int action) {
        if (((elt instanceof IUnit)) && (!(elt instanceof ContainerUnit))) {
            IUnit unit = (IUnit) elt;
            this.context.getTelemetry().record("handlerOpenUnit", "unitType", unit.getFormatType());
            if (!HandlerUtil.processUnit(this.parent.getShell(), this.context, unit, true)) {
                return;
            }
            this.pman.create(unit, true);
            if (action == 0) {
                setFocus();
            }
        } else if (action == 1) {
            ((TreeViewer) this.ftv.getViewer()).setExpandedState(elt, !((TreeViewer) this.ftv.getViewer()).getExpandedState(elt));
        }
    }

    public boolean verifyOperation(OperationRequest req) {
        Object node = getSelectedNode();
        switch (req.getOperation()) {
            case PROPERTIES:
                return ((node instanceof IRuntimeProject)) || ((node instanceof ILiveArtifact)) || ((node instanceof IUnit));
            case EXTRACT_TO:
                return ((node instanceof ILiveArtifact)) || ((node instanceof IBinaryUnit));
            case PARSE_AT:
            case VIEW:
            case VIEW_NEW:
            case DELETE:
                return ((node instanceof IUnit)) || ((node instanceof ILiveArtifact));
            case FIND:
                return true;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        Object node = getSelectedNode();
        switch (req.getOperation()) {
            case PARSE_AT: {
                boolean successfullyIdentified;
                if (!(node instanceof IUnit)) {
                    return false;
                }
                IUnit unit = (IUnit) node;
                ReparseDialog dlg = new ReparseDialog(this.parent.getShell(), unit);
                ReparseDialog.Information info = dlg.open();
                if (info == null) {
                    return false;
                }
                logger.i("info= %s", new Object[]{info});
                IInput subinput = null;
                if ((unit instanceof IBinaryUnit)) {
                    IInput input = ((IBinaryUnit) unit).getInput();
                    long offset = info.getOffset();
                    long size = info.getSize();
                    long maxsize = input.getCurrentSize();
                    if (offset + size > maxsize) {
                        size = maxsize - offset;
                    }
                    if ((offset >= 0L) && (size >= 0L) && (offset <= maxsize)) {
                        try {
                            subinput = new SubInput(input, offset, size);
                        } catch (IOException e) {
                            logger.catching(e);
                            return false;
                        }
                    }
                }
                IUnitProcessor processor = unit.getUnitProcessor();
                IUnitIdentifier ident = processor.getUnitIdentifier(info.getWantedType());
                if (ident == null) {
                    return false;
                }
                successfullyIdentified = ident.canIdentify(subinput, unit);
                if (!successfullyIdentified) {
                    String msg = String.format("%s: \"%s\".\n\n%s", S.s(389), ident.getFormatType(), S.s(662));
                    if (!MessageDialog.openQuestion(this.parent.getShell(), S.s(377), msg)) {
                        return false;
                    }
                }
                IUnit subunit = null;
                try {
                    subunit = ident.prepare(info.getSubUnitName(), subinput, processor, unit);
                } catch (Exception e) {
                    if (!successfullyIdentified) {
                        logger.catching(e);
                    } else {
                        throw e;
                    }
                }
                if (subunit == null) {
                    String msg = String.format("%s: \"%s\".", new Object[]{S.s(390), ident.getFormatType()});
                    MessageDialog.openError(this.parent.getShell(), S.s(629), msg);
                    return false;
                }
                if ((subunit instanceof INativeCodeUnit)) {
                    NativeImageReparseExtraOptionsDialog dlg2 = new NativeImageReparseExtraOptionsDialog(this.parent.getShell());
                    dlg2.setSelected(true);
                    String str = dlg2.open();
                    if (str == null) {
                        return false;
                    }
                    INativeCodeUnit<?> pbcu = (INativeCodeUnit) subunit;
                    pbcu.setVirtualImageBase(dlg2.getImageBase());
                    pbcu.getProcessor().setEndianness(dlg2.isBigEndian() ? Endianness.BIG_ENDIAN : Endianness.LITTLE_ENDIAN);
                }
                unit.addChild(subunit);
                UI.info("A reparsed sub-unit was created");
                return true;
            }
            case EXTRACT_TO: {
                IInput input;
                String name;
                if ((node instanceof ILiveArtifact)) {
                    input = ((ILiveArtifact) node).getArtifact().getInput();
                    name = ((ILiveArtifact) node).getArtifact().getName();
                } else if ((node instanceof IBinaryUnit)) {
                    input = ((IBinaryUnit) node).getInput();
                    name = ((IBinaryUnit) node).getName();
                } else {
                    return false;
                }
                FileDialog dlg = new FileDialog(this.parent.getShell(), 8192);
                dlg.setText(S.s(339));
                dlg.setOverwrite(true);
                dlg.setFileName(name);
                String filepath = dlg.open();
                if (filepath == null) {
                    return false;
                }
                logger.i("%s: %s", new Object[]{S.s(340), filepath});
                byte[] data;
                try {
                    InputStream in = input.getStream();
                    Throwable successfullyIdentified = null;
                    try {
                        data = IO.readInputStream(in);
                        IO.writeFile(new File(filepath), data);
                    } catch (Throwable localThrowable1) {
                        successfullyIdentified = localThrowable1;
                        throw localThrowable1;
                    } finally {
                        if (in != null) if (successfullyIdentified != null) try {
                            in.close();
                        } catch (Throwable localThrowable2) {
                            successfullyIdentified.addSuppressed(localThrowable2);
                        }
                        else in.close();
                    }
                } catch (IOException e) {
                    logger.catching(e);
                    return false;
                }
                return true;
            }
            case PROPERTIES: {
                JebDialog dlg;
                if ((node instanceof IRuntimeProject)) {
                    dlg = new ProjectPropertiesDialog(this.parent.getShell(), (IRuntimeProject) node);
                } else if ((node instanceof ILiveArtifact)) {
                    dlg = new ArtifactPropertiesDialog(this.parent.getShell(), (ILiveArtifact) node);
                } else if ((node instanceof IUnit)) {
                    dlg = new UnitPropertiesDialog(this.parent.getShell(), (IUnit) node);
                } else {
                    return false;
                }
                dlg.open();
                return true;
            }
            case VIEW:
            case VIEW_NEW: {
                if ((node instanceof IUnit)) {
                    this.pman.create((IUnit) node, req.getOperation() == Operation.VIEW);
                } else {
                    return false;
                }
                return true;
            }
            case DELETE: {
                if ((node instanceof IUnit)) {
                    if (MessageDialog.openConfirm(UI.getShellTracker().get(), S.s(207), S.s(791))) {
                        IUnit unit = (IUnit) node;
                        return this.context.getOpenedProject().destroyUnit(unit);
                    }
                } else if ((node instanceof ILiveArtifact)) {
                    if (MessageDialog.openConfirm(UI.getShellTracker().get(), S.s(207), S.s(791))) {
                        return RuntimeProjectUtil.destroyLiveArtifact((ILiveArtifact) node);
                    }
                }
                return false;
            }
            case FIND:
                this.pt.setFilterVisibility(true, true);
                return true;
        }
        return false;
    }
}


