/*      */
package com.pnfsoftware.jeb.rcpclient.parts;
/*      */
/*      */

import com.pnfsoftware.jeb.client.S;
/*      */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*      */ import com.pnfsoftware.jeb.core.units.IUnit;
/*      */ import com.pnfsoftware.jeb.core.units.IXmlUnit;
/*      */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*      */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*      */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*      */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*      */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*      */ import com.pnfsoftware.jeb.rcpclient.JebApp;
/*      */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*      */ import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
/*      */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.AppService;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IAppService;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMDock;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMFolder;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgBreakpointsView;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgVariablesView;
/*      */ import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView;
/*      */ import com.pnfsoftware.jeb.util.collect.ItemHistory;
/*      */ import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
/*      */ import com.pnfsoftware.jeb.util.encoding.Conversion;
/*      */ import com.pnfsoftware.jeb.util.events.Event;
/*      */ import com.pnfsoftware.jeb.util.format.Strings;
/*      */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*      */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Shell;

/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */ public class PartManager
        /*      */ implements IViewManager
        /*      */ {
    /*   57 */   private static final ILogger logger = GlobalLog.getLogger(PartManager.class);

    /*      */
    /*      */   public static class PropertyProvider {
        /*      */     private RcpClientContext context;

        /*      */
        /*      */
        public PropertyProvider(RcpClientContext context) {
            /*   63 */
            this.context = context;
            /*      */
        }

        /*      */
        /*      */
        public boolean getTryInPlace() {
            /*   67 */
            return !this.context.getProperties().getDoNotReplaceViews();
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*   72 */   private static int unitPartId = 1;
    /*   73 */   private static Map<String, String> bestStackIdByType = new HashMap();
    /*      */
    /*      */   public static final int FLAG_PARTS_VISIBLE = 1;
    /*      */
    /*      */   public static final int FLAG_PARTS_NON_VISIBLE = 2;
    /*      */
    /*      */   public static final int FLAG_PARTS_ALL = 3;
    /*      */
    /*      */   public static final String idProjectExplorerPart = "jeb3.rcpclient.part.projectTree";
    /*      */
    /*      */   public static final String idLoggerPart = "jeb3.rcpclient.part.logger";
    /*      */   public static final String idTerminalPart = "jeb3.rcpclient.part.terminal";
    /*      */   public static final String pdataUnitFormatType = "unitFormatType";
    /*      */   public static final String pdataFragmentClasses = "fragmentClasses";
    /*      */   public static final String dataUnit = "unit";
    /*      */   public static final String dataUnitPartId = "unitPartId";
    /*      */   public static final String dataFragmentList = "fragmentList";
    /*      */   public static final String dataFragmentBlacklist = "fragmentBlacklist";
    /*      */   public static final String dataOriginatorUnitPartId = "originatorUnitPartId";
    /*      */   public static final String dataFocusTimestamp = "focusTimestamp";
    /*      */   public static final String dataParentId0 = "parentId0";
    /*      */   public static final String dataParentId1 = "parentId1";
    /*      */   public static final String dataStickyPart = "stickyPart";
    /*      */   private RcpClientContext context;
    /*      */   private PropertyProvider propertyProvider;
    /*      */   private JebApp app;
    /*      */   private IAppService appService;
    /*      */   private IMPart projectExplorerPart;
    /*      */   private IMPart loggerPart;
    /*      */   private IMPart terminalPart;
    /*      */   private Folder folderHierarchy;
    /*      */   private Folder folderGraphs;
    /*      */   private Folder folderDebuggers;

    /*      */
    /*      */
    public PartManager(RcpClientContext context, JebApp app, PropertyProvider propertyProvider)
    /*      */ {
        /*  109 */
        this.context = context;
        /*  110 */
        this.app = app;
        /*  111 */
        this.propertyProvider = propertyProvider;
        /*      */
        /*  113 */
        this.appService = new AppService(app);
        /*      */
    }

    /*      */
    /*      */
    public void initialize() {
        /*  117 */
        this.projectExplorerPart = createProjectExplorerPart();
        /*  118 */
        this.loggerPart = createLoggerPart();
        /*  119 */
        this.terminalPart = createTerminalPart();
        /*      */
        /*  121 */
        this.appService.activate(this.loggerPart);
        /*  122 */
        this.appService.activate(this.projectExplorerPart, true);
        /*      */
    }

    /*      */
    /*      */
    private IMPart createProjectExplorerPart() {
        /*  126 */
        IMPart part = this.appService.createPart(this.app.folderProject, new ProjectExplorerPartManager(this.context));
        /*  127 */
        part.setElementId("jeb3.rcpclient.part.projectTree");
        /*  128 */
        part.setLabel(S.s(765));
        /*  129 */
        part.setIcon(UIAssetManager.getInstance().getImage("eclipse/hierarchy_co.png"));
        /*  130 */
        this.appService.activate(part);
        /*  131 */
        return part;
        /*      */
    }

    /*      */
    /*      */
    private IMPart createLoggerPart() {
        /*  135 */
        IMPart part = this.appService.createPart(this.app.folderConsoles, new LoggerPartManager(this.context));
        /*  136 */
        part.setElementId("jeb3.rcpclient.part.logger");
        /*  137 */
        part.setLabel(S.s(764));
        /*  138 */
        part.setIcon(UIAssetManager.getInstance().getImage("eclipse/console_view.png"));
        /*  139 */
        this.appService.activate(part);
        /*  140 */
        return part;
        /*      */
    }

    /*      */
    /*      */
    private IMPart createTerminalPart() {
        /*  144 */
        IMPart part = this.appService.createPart(this.app.folderConsoles, new TerminalPartManager(this.context));
        /*  145 */
        part.setElementId("jeb3.rcpclient.part.terminal");
        /*  146 */
        part.setLabel("Terminal");
        /*  147 */
        part.setIcon(UIAssetManager.getInstance().getImage("eclipse/writeout_co.png"));
        /*  148 */
        this.appService.activate(part);
        /*  149 */
        return part;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public IMPart getActivePart()
    /*      */ {
        /*  157 */
        return this.appService.getActivePart();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public static boolean isUnitPart(IMPart part)
    /*      */ {
        /*  168 */
        return (part != null) && ((part.getManager() instanceof UnitPartManager));
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public List<IMPart> getAllParts()
    /*      */ {
        /*  177 */
        return new ArrayList(this.appService.findElements(null, null, IMPart.class, null, 0));
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public List<IMPart> getUnitParts()
    /*      */ {
        /*  186 */
        List<IMPart> list = new ArrayList();
        /*  187 */
        for (IMPart part : this.appService.findElements(null, null, IMPart.class, null, 0)) {
            /*  188 */
            if ((part.getManager() instanceof UnitPartManager)) {
                /*  189 */
                list.add(part);
                /*      */
            }
            /*      */
        }
        /*  192 */
        return list;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public IUnit getUnitForPart(IMPart part)
    /*      */ {
        /*  202 */
        UnitPartManager manager = getUnitPartManager(part);
        /*  203 */
        return manager == null ? null : manager.getUnit();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public UnitPartManager getUnitPartManager(IMPart part)
    /*      */ {
        /*  213 */
        if (!isUnitPart(part)) {
            /*  214 */
            return null;
            /*      */
        }
        /*  216 */
        return (UnitPartManager) part.getManager();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void focus(IMPart part)
    /*      */ {
        /*  225 */
        if (part != null) {
            /*  226 */
            this.appService.activate(part, true);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void onFocus(IMPart part)
    /*      */ {
        /*  237 */
        recordFocusTime(part);
        /*  238 */
        recordParent(part);
        /*      */
    }

    /*      */
    /*      */
    private String buildStackIdKey(String formatType, List<String> fragmentList) {
        /*  242 */
        String key = formatType;
        /*  243 */
        if ((fragmentList != null) && (!fragmentList.isEmpty())) {
            /*  244 */
            key = key + ":" + Strings.join(",", fragmentList);
            /*      */
        }
        /*  246 */
        return key;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private void recordParent(IMPart part)
    /*      */ {
        /*  259 */
        String parentId0 = (String) part.getData().get("parentId0");
        /*  260 */
        String parentId1 = (String) part.getData().get("parentId1");
        /*      */
        /*  262 */
        Object parent = part.getParentElement();
        /*  263 */
        if ((parent instanceof IMFolder)) {
            /*  264 */
            String parentId = ((IMFolder) parent).getElementId();
            /*  265 */
            parentId1 = parentId0;
            /*  266 */
            parentId0 = parentId;
            /*      */
            /*  268 */
            if ((parentId0 == null) || (!parentId0.equals(parentId1))) {
                /*  269 */
                IUnit unit = getUnitForPart(part);
                /*  270 */
                if (unit != null)
                    /*      */ {
                    /*  272 */
                    List<String> fragmentList = (List) part.getData().get("fragmentList");
                    /*  273 */
                    bestStackIdByType.put(buildStackIdKey(unit.getFormatType(), fragmentList), parentId);
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*      */
        /*  278 */
        part.getData().put("parentId0", parentId0);
        /*  279 */
        part.getData().put("parentId1", parentId1);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private void recordFocusTime(IMPart part)
    /*      */ {
        /*  289 */
        long ts = System.currentTimeMillis();
        /*  290 */
        part.getData().put("focusTimestamp", Long.valueOf(ts));
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private long getFocusTimestamp(IMPart part)
    /*      */ {
        /*  300 */
        Object val = part.getData().get("focusTimestamp");
        /*  301 */
        if (!(val instanceof Long)) {
            /*  302 */
            return 0L;
            /*      */
        }
        /*      */
        /*  305 */
        return ((Long) val).longValue();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public IMPart getMostRecentlyFocused(List<IMPart> parts)
    /*      */ {
        /*  315 */
        long ts0 = 0L;
        /*  316 */
        IMPart part0 = null;
        /*  317 */
        for (IMPart part : parts) {
            /*  318 */
            long ts = getFocusTimestamp(part);
            /*  319 */
            if (ts > ts0) {
                /*  320 */
                ts0 = ts;
                /*  321 */
                part0 = part;
                /*      */
            }
            /*      */
        }
        /*  324 */
        return part0;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void close(IMPart part)
    /*      */ {
        /*  333 */
        if (part != null) {
            /*  334 */
            this.appService.hidePart(part);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void close(IUnit unit)
    /*      */ {
        /*  344 */
        for (IMPart part : getPartsForUnit(unit)) {
            /*  345 */
            close(part);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    public void closeAllUnitParts()
    /*      */ {
        /*  353 */
        for (IMPart part : getUnitParts()) {
            /*  354 */
            this.appService.hidePart(part);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public IMPart getProjectExplorerPart() {
        /*  359 */
        return this.projectExplorerPart;
        /*      */
    }

    /*      */
    /*      */
    public ProjectExplorerPartManager getProjectExplorer() {
        /*  363 */
        IMPart part = getProjectExplorerPart();
        /*  364 */
        if (part == null) {
            /*  365 */
            return null;
            /*      */
        }
        /*  367 */
        return (ProjectExplorerPartManager) part.getManager();
        /*      */
    }

    /*      */
    /*      */
    public void activateProjectExplorer(boolean focus) {
        /*  371 */
        this.appService.activate(this.projectExplorerPart, focus);
        /*      */
    }

    /*      */
    /*      */
    public void activateLogger(boolean focus) {
        /*  375 */
        this.appService.activate(this.loggerPart, focus);
        /*      */
    }

    /*      */
    /*      */
    public void activateTerminal(boolean focus) {
        /*  379 */
        this.appService.activate(this.terminalPart, focus);
        /*      */
    }

    /*      */
    /*      */
    public boolean activatePart(IMPart part, boolean focus) {
        /*  383 */
        this.appService.activate(part, focus);
        /*  384 */
        return true;
        /*      */
    }

    /*      */
    /*      */
    public boolean activatePart(String id, boolean focus) {
        /*  388 */
        List<IMPart> parts = this.appService.findElements(null, id, IMPart.class, null, 0);
        /*  389 */
        if (parts.isEmpty()) {
            /*  390 */
            return false;
            /*      */
        }
        /*  392 */
        this.appService.activate((IMPart) parts.get(0), focus);
        /*  393 */
        return true;
        /*      */
    }

    /*      */
    /*      */
    public void closeUnitParts() {
        /*  397 */
        for (IMPart part : getUnitParts()) {
            /*  398 */
            closePart(part);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public void closePart(IMPart part) {
        /*  403 */
        logger.i("Closing part: %s", new Object[]{part.getLabel()});
        /*  404 */
        this.appService.hidePart(part);
        /*      */
    }

    /*      */
    /*      */
    public void unbindUnitParts() {
        /*  408 */
        for (IMPart part : getUnitParts()) {
            /*  409 */
            unbindUnitPart(part);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public void unbindUnitPart(IMPart part) {
        /*  414 */
        recordUnitPartAffinities(part, false);
        /*      */
        /*  416 */
        String type = getUnitPartType(part);
        /*      */
        /*  418 */
        cleanUnitPart(part);
        /*      */
        /*  420 */
        String label = type + "<Unbound>";
        /*  421 */
        part.setLabel(label);
        /*      */
        /*      */
        /*  424 */
        String hint = !type.isEmpty() ? String.format("This unbound part is a placeholder for a view of type \"%s\"", new Object[]{type}) : "This unbound part is a placeholder for any view";
        /*      */
        /*  426 */
        part.setTooltip(hint);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    private String getUnitPartType(IMPart part)
    /*      */ {
        /*  432 */
        String unitType = (String) part.getData().get("unitFormatType");
        /*  433 */
        UnitPartManager object = getUnitPartManager(part);
        /*  434 */
        if (object != null) {
            /*  435 */
            IUnit unit = object.getUnit();
            /*  436 */
            if (unit != null) {
                /*  437 */
                unitType = unit.getFormatType();
                /*      */
            }
            /*      */
        }
        /*  440 */
        return Strings.safe(unitType, "");
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private boolean recordUnitPartAffinities(IMPart part, boolean resetIfUnbound)
    /*      */ {
        /*  451 */
        UnitPartManager object = getUnitPartManager(part);
        /*  452 */
        if (object == null) {
            /*  453 */
            return false;
            /*      */
        }
        /*      */
        /*  456 */
        IUnit unit = object.getUnit();
        /*  457 */
        if (unit != null) {
            /*  458 */
            part.getData().put("unitFormatType", unit.getFormatType());
            /*  459 */
            recordFragments(part);
            /*      */
        }
        /*  461 */
        else if (resetIfUnbound) {
            /*  462 */
            part.getData().remove("fragmentClasses");
            /*      */
        }
        /*      */
        /*  465 */
        return true;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private boolean recordFragments(IMPart part)
    /*      */ {
        /*  474 */
        UnitPartManager object = getUnitPartManager(part);
        /*  475 */
        if (object == null) {
            /*  476 */
            return false;
            /*      */
        }
        /*      */
        /*  479 */
        List<String> cnames = new ArrayList();
        /*  480 */
        for (IRcpUnitFragment fragment : object.getFragments()) {
            /*  481 */
            cnames.add(fragment.getClass().getName());
            /*      */
        }
        /*  483 */
        part.getData().put("fragmentClasses", Strings.join(",", cnames));
        /*  484 */
        return true;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private void cleanUnitPart(IMPart part)
    /*      */ {
        /*  494 */
        part.getData().remove("unit");
        /*  495 */
        part.getData().remove("unitPartId");
        /*  496 */
        part.getData().remove("originatorUnitPartId");
        /*  497 */
        part.getData().remove("fragmentList");
        /*  498 */
        part.getData().remove("fragmentBlacklist");
        /*      */
        /*      */
        /*  501 */
        this.appService.clearPart(part);
        /*      */
        /*      */
        /*  504 */
        UnitPartManager pman = getUnitPartManager(part);
        /*  505 */
        if (pman != null) {
            /*  506 */
            pman.setup();
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*  543 */   private WeakIdentityHashMap<IDebuggerUnit, Folder> dbgFolders = new WeakIdentityHashMap();

    /*      */
    /*      */
    private IMFolder findBestFolder(IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist) {
        /*  546 */
        Folder folder = null;
        /*  547 */
        if ((unit instanceof ICodeUnit)) {
            /*  548 */
            if (isContainedInFragmentList(fragmentList, CodeHierarchyView.class)) {
                /*  549 */
                if ((this.folderHierarchy == null) || (this.folderHierarchy.isDisposed())) {
                    /*  550 */
                    this.folderHierarchy = this.app.getDock().splitFolder(this.app.folderProject, -2, 70);
                    /*      */
                }
                /*  552 */
                folder = this.folderHierarchy;
                /*      */
            }
            /*  554 */
            else if (isContainedInFragmentList(fragmentList, AbstractGlobalGraphView.class)) {
                /*  555 */
                if ((this.folderGraphs == null) || (this.folderGraphs.isDisposed())) {
                    /*  556 */
                    if (!this.context.getPropertyManager().getBoolean("graphs.KeepInMainDock", false)) {
                        /*  557 */
                        Rectangle r = this.app.getPrimaryShell().getBounds();
                        /*  558 */
                        int x = (int) (r.x + 0.5D * r.width);
                        /*  559 */
                        int w = Math.max(0, (int) (0.5D * r.width - 50.0D));
                        /*  560 */
                        int y = (int) (r.y + 0.5D * r.height);
                        /*  561 */
                        int h = Math.max(0, (int) (0.5D * r.height - 50.0D));
                        /*  562 */
                        IMDock dockGraphs = this.appService.createDock(true, new Rectangle(x, y, w, h));
                        /*  563 */
                        this.folderGraphs = ((Folder) dockGraphs.getInitialFolder());
                        /*      */
                    }
                    /*      */
                    else {
                        /*  566 */
                        this.folderGraphs = this.app.getDock().splitFolder(this.app.folderConsoles, -4, 40);
                        /*      */
                    }
                    /*      */
                }
                /*  569 */
                folder = this.folderGraphs;
                /*      */
            }
            /*      */
        }
        /*  572 */
        else if ((unit instanceof IDebuggerUnit))
            /*      */ {
            /*  574 */
            IDebuggerUnit dbg = (IDebuggerUnit) unit;
            /*  575 */
            folder = (Folder) this.dbgFolders.get(dbg);
            /*  576 */
            if ((folder == null) || (folder.isDisposed())) {
                /*  577 */
                if ((this.folderDebuggers == null) || (this.folderDebuggers.isDisposed())) {
                    /*  578 */
                    this.folderDebuggers = this.app.getDock().splitFolder(this.app.folderWorkspace, -4, 40);
                    /*  579 */
                    folder = this.folderDebuggers;
                    /*      */
                }
                /*      */
                else {
                    /*  582 */
                    folder = this.app.getDock().splitFolder(this.folderDebuggers, -2, 50);
                    /*      */
                }
                /*  584 */
                this.dbgFolders.put(dbg, folder);
                /*      */
            }
            /*      */
        }
        /*      */
        /*      */
        /*  589 */
        if (folder == null) {
            /*  590 */
            folder = this.app.folderWorkspace;
            /*      */
        }
        /*      */
        /*  593 */
        if (folder == null) {
            /*  594 */
            throw new RuntimeException();
            /*      */
        }
        /*  596 */
        return folder;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private IMPart createUnitPart(IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist)
    /*      */ {
        /*  608 */
        IMFolder folder = findBestFolder(unit, fragmentList, fragmentBlacklist);
        /*  609 */
        IMPart part = this.appService.createPart(folder, new UnitPartManager(this.context));
        /*  610 */
        prepareUnitPart(part, unit, fragmentList, fragmentBlacklist);
        /*  611 */
        return part;
        /*      */
    }

    /*      */
    /*      */
    private void prepareUnitPart(IMPart part, IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist) {
        /*  615 */
        part.setLabel(unit.getName());
        /*  616 */
        part.setTooltip("Hold and drag to move this tab elsewhere");
        /*  617 */
        part.getData().put("unit", unit);
        /*      */
        /*      */
        /*  620 */
        part.getData().put("unitPartId", Integer.valueOf(unitPartId));
        /*  621 */
        unitPartId += 1;
        /*  622 */
        part.getData().put("originatorUnitPartId", Integer.valueOf(0));
        /*  623 */
        part.getData().put("fragmentList", fragmentList);
        /*  624 */
        part.getData().put("fragmentBlacklist", fragmentBlacklist);
        /*  625 */
        part.setHideable(true);
        /*  626 */
        part.setCloseOnHide(true);
        /*  627 */
        String iconURI = UnitPartManager.getIconForUnit(unit);
        /*  628 */
        if (iconURI != null) {
            /*  629 */
            part.setIcon(UIAssetManager.getInstance().getImage(iconURI));
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    private void setStickyPart(IMPart part, boolean sticky) {
        /*  634 */
        part.getData().put("stickyPart", Boolean.valueOf(sticky));
        /*      */
    }

    /*      */
    /*      */
    private boolean isStickyPart(IMPart part) {
        /*  638 */
        Boolean o = (Boolean) part.getData().get("stickyPart");
        /*  639 */
        return o == null ? false : o.booleanValue();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private IMPart createInternal(IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist, boolean activate)
    /*      */ {
        /*  655 */
        if (fragmentList != null) {
            /*  656 */
            fragmentList = new ArrayList(fragmentList);
            /*      */
        }
        /*  658 */
        if (fragmentBlacklist != null) {
            /*  659 */
            fragmentBlacklist = new ArrayList(fragmentBlacklist);
            /*      */
        }
        /*      */
        /*  662 */
        IMPart part = createUnitPart(unit, fragmentList, fragmentBlacklist);
        /*  663 */
        if (activate) {
            /*  664 */
            this.appService.activate(part);
            /*      */
        }
        /*  666 */
        return part;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public IMPart createEmpty(IUnit unit)
    /*      */ {
        /*  676 */
        return createInternal(unit, createFragmentList(new Class[0]), null, true);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public IMPart createSingle(IUnit unit, Class<?> cl)
    /*      */ {
        /*  687 */
        return createInternal(unit, createFragmentList(new Class[]{cl}), null, true);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public List<IMPart> create(IUnit unit, boolean tryActivationFirst)
    /*      */ {
        /*  697 */
        boolean tryInPlace = false;
        /*      */
        /*  699 */
        if (tryActivationFirst)
            /*      */ {
            /*  701 */
            if ((((unit instanceof ISourceUnit)) || ((unit instanceof IXmlUnit))) &&
                    /*  702 */         (this.propertyProvider != null)) {
                /*  703 */
                tryInPlace = this.propertyProvider.getTryInPlace();
                /*      */
            }
            /*      */
        }
        /*      */
        /*  707 */
        return create(unit, tryActivationFirst, tryInPlace);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public List<IMPart> create(IUnit unit, boolean tryActivationFirst, boolean tryInPlace)
    /*      */ {
        /*  720 */
        if (unit.getPropertyManager() == null)
            /*      */ {
            /*      */
            /*      */
            /*  724 */
            throw new RuntimeException("Cannot create UnitPart of disposed Unit: " + unit.getName());
            /*      */
        }
        /*      */
        /*      */
        /*  728 */
        boolean sticky = !tryActivationFirst;
        /*      */
        /*      */
        /*  731 */
        IMPart reusedPart = null;
        /*  732 */
        IMPart part;
        if (tryActivationFirst)
            /*      */ {
            /*  734 */
            List<IMPart> parts = getPartsForUnit(unit);
            /*  735 */
            if (!parts.isEmpty()) {
                /*  736 */
                restoreMissingParts(unit, parts);
                /*  737 */
                part = getMostRecentlyFocused(parts);
                /*  738 */
                if (part == null) {
                    /*  739 */
                    part = (IMPart) parts.get(0);
                    /*      */
                }
                /*  741 */
                this.appService.activate(part, true);
                /*  742 */
                return parts;
                /*      */
            }
            /*      */
            /*      */
            /*      */
            /*  747 */
            if (tryInPlace) {
                /*  748 */
                parts = getLivePartsForUnitType(unit);
                /*  749 */
                for (IMPart part : parts) {
                    /*  750 */
                    if (!isStickyPart(part)) {
                        /*  751 */
                        unbindUnitPart(part);
                        /*  752 */
                        reusedPart = part;
                        /*  753 */
                        sticky = false;
                        /*  754 */
                        break;
                        /*      */
                    }
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*      */
        /*  760 */
        List<IMPart> parts = new ArrayList();
        /*  761 */
        IMPart part;
        if (reusedPart != null) {
            /*  762 */
            prepareUnitPart(reusedPart, unit, null, null);
            /*  763 */
            this.appService.activate(reusedPart, true);
            /*  764 */
            setStickyPart(reusedPart, sticky);
            /*  765 */
            parts.add(reusedPart);
            /*      */
        }
        /*  767 */
        else if (!tryActivationFirst) {
            /*  768 */
            part = createInternal(unit, null, null, true);
            /*  769 */
            setStickyPart(part, sticky);
            /*  770 */
            parts.add(part);
            /*      */
        }
        /*      */
        else
            /*      */ {
            /*  774 */
            if ((unit instanceof ICodeUnit)) {
                /*  775 */
                parts = createCodeUnitParts(unit, true, true, true);
                /*      */
            }
            /*  777 */
            else if ((unit instanceof IDebuggerUnit)) {
                /*  778 */
                parts = createDebuggerUnitParts(unit, true, true, true);
                /*      */
            }
            /*      */
            else {
                /*  781 */
                parts.add(createInternal(unit, null, null, false));
                /*      */
            }
            /*      */
            /*      */
            /*  785 */
            for (IMPart part : parts) {
                /*  786 */
                this.appService.activate(part, true);
                /*      */
            }
            /*      */
        }
        /*  789 */
        return parts;
        /*      */
    }

    /*      */
    /*      */
    private List<IMPart> createCodeUnitParts(IUnit unit, boolean createHierarchy, boolean createGraphs, boolean createMain) {
        /*  793 */
        List<IMPart> parts = new ArrayList();
        /*  794 */
        if (createHierarchy) {
            /*  795 */
            parts.add(createInternal(unit, createFragmentList(new Class[]{CodeHierarchyView.class}), null, false));
            /*      */
        }
        /*  797 */
        if (createGraphs) {
            /*  798 */
            parts.add(createInternal(unit, createFragmentList(new Class[]{AbstractGlobalGraphView.class}), null, false));
            /*      */
        }
        /*  800 */
        if (createMain) {
            /*  801 */
            parts.add(createInternal(unit, null, createFragmentList(new Class[]{CodeHierarchyView.class, AbstractGlobalGraphView.class}), false));
            /*      */
        }
        /*  803 */
        return parts;
        /*      */
    }

    /*      */
    /*      */
    private List<IMPart> createDebuggerUnitParts(IUnit unit, boolean createBpView, boolean createVarView, boolean createMain)
    /*      */ {
        /*  808 */
        List<IMPart> parts = new ArrayList();
        /*  809 */
        if (createBpView) {
            /*  810 */
            parts.add(createInternal(unit, createFragmentList(new Class[]{DbgBreakpointsView.class}), null, false));
            /*      */
        }
        /*  812 */
        if (createVarView) {
            /*  813 */
            parts.add(createInternal(unit, createFragmentList(new Class[]{DbgVariablesView.class}), null, false));
            /*      */
        }
        /*  815 */
        if (createMain)
            /*      */ {
            /*  817 */
            parts.add(createInternal(unit, null, createFragmentList(new Class[]{DbgBreakpointsView.class, DbgVariablesView.class}), false));
            /*      */
        }
        /*      */
        /*  820 */
        return parts;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public List<IMPart> restoreMissingParts(IUnit unit)
    /*      */ {
        /*  830 */
        return restoreMissingParts(unit, getPartsForUnit(unit));
        /*      */
    }

    /*      */
    /*      */
    private List<IMPart> restoreMissingParts(IUnit unit, List<IMPart> parts) {
        /*  834 */
        List<IMPart> newParts = new ArrayList();
        /*  835 */
        IMPart part;
        boolean createBpView;
        if ((unit instanceof ICodeUnit)) {
            /*  836 */
            boolean createHierarchy = true;
            /*      */
            /*  838 */
            boolean createMain = true;
            /*  839 */
            for (Iterator localIterator = parts.iterator(); localIterator.hasNext(); ) {
                part = (IMPart) localIterator.next();
                /*  840 */
                if (part.getData().get("fragmentList") != null)
                    /*      */ {
                    /*  842 */
                    List<String> fragmentList = (List) part.getData().get("fragmentList");
                    /*  843 */
                    if (fragmentList.contains(CodeHierarchyView.class.getName())) {
                        /*  844 */
                        createHierarchy = false;
                        /*      */
                        /*      */
                    }
                    /*      */
                    /*      */
                    /*      */
                }
                /*  850 */
                else if (part.getData().get("fragmentBlacklist") != null) {
                    /*  851 */
                    createMain = false;
                    /*      */
                }
                /*      */
            }
            /*  854 */
            newParts = createCodeUnitParts(unit, createHierarchy, false, createMain);
            /*      */
            /*      */
        }
        /*  857 */
        else if ((unit instanceof IDebuggerUnit)) {
            /*  858 */
            createBpView = true;
            boolean createVarView = true;
            boolean createMain = true;
            /*  859 */
            for (IMPart part : parts) {
                /*  860 */
                if (part.getData().get("fragmentList") != null)
                    /*      */ {
                    /*  862 */
                    List<String> fragmentList = (List) part.getData().get("fragmentList");
                    /*  863 */
                    if (!fragmentList.isEmpty()) {
                        /*  864 */
                        if (((String) fragmentList.get(0)).equals(DbgBreakpointsView.class.getName())) {
                            /*  865 */
                            createBpView = false;
                            /*      */
                        }
                        /*  867 */
                        else if (((String) fragmentList.get(0)).equals(DbgVariablesView.class.getName())) {
                            /*  868 */
                            createVarView = false;
                            /*      */
                        }
                        /*      */
                    }
                    /*      */
                }
                /*  872 */
                else if (part.getData().get("fragmentBlacklist") != null) {
                    /*  873 */
                    createMain = false;
                    /*      */
                }
                /*      */
            }
            /*  876 */
            newParts = createDebuggerUnitParts(unit, createBpView, createVarView, createMain);
            /*      */
        }
        /*      */
        /*  879 */
        if (!newParts.isEmpty())
            /*      */ {
            /*  881 */
            for (IMPart part : newParts) {
                /*  882 */
                this.appService.activate(part);
                /*      */
            }
            /*      */
            /*      */
            /*  886 */
            UIState uiState = this.propertyProvider.context.getUIState(unit);
            /*  887 */
            uiState.notifyListeners(new Event());
            /*      */
        }
        /*      */
        /*  890 */
        return newParts;
        /*      */
    }

    /*      */
    /*      */
    private List<IMPart> getLivePartsForUnitType(IUnit targetUnit) {
        /*  894 */
        String targetUnitType = targetUnit.getFormatType();
        /*  895 */
        List<IMPart> candidates = new ArrayList();
        /*  896 */
        for (IMPart part : getUnitParts()) {
            /*  897 */
            UnitPartManager object = getUnitPartManager(part);
            /*  898 */
            if ((object != null) && (object.getUnit() != null) &&
                    /*  899 */         (Strings.equals(targetUnitType, object.getUnit().getFormatType()))) {
                /*  900 */
                candidates.add(part);
                /*      */
            }
            /*      */
        }
        /*  903 */
        return candidates;
        /*      */
    }

    /*      */
    /*      */
    private List<String> createFragmentList(Class<?>... classes) {
        /*  907 */
        List<String> r = new ArrayList();
        /*  908 */
        for (Class<?> c : classes) {
            /*  909 */
            r.add(c.getName());
            /*      */
        }
        /*  911 */
        return r;
        /*      */
    }

    /*      */
    /*      */
    private static boolean isContainedInFragmentList(List<String> fragmentList, Class<?> targetClass)
    /*      */ {
        /*  916 */
        if (fragmentList == null) {
            /*  917 */
            return false;
            /*      */
        }
        /*  919 */
        if (fragmentList.contains(targetClass.getName())) {
            /*  920 */
            return true;
            /*      */
        }
        /*  922 */
        for (String name : fragmentList) {
            /*      */
            try {
                /*  924 */
                Class<?> c = Class.forName(name);
                /*  925 */
                if (targetClass.isAssignableFrom(c)) {
                    /*  926 */
                    return true;
                    /*      */
                }
                /*      */
            }
            /*      */ catch (ClassNotFoundException localClassNotFoundException) {
            }
            /*      */
        }
        /*      */
        /*  932 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public List<IMPart> getPartsForUnitFamily(IUnit base, int flags)
    /*      */ {
        /*  943 */
        List<IUnit> family = new ArrayList();
        /*  944 */
        buildFamily(base, family);
        /*  945 */
        List<IMPart> r = getPartsForUnits(family, flags);
        /*  946 */
        return r;
        /*      */
    }

    /*      */
    /*      */
    private void buildFamily(IUnit base, List<IUnit> r) {
        /*  950 */
        r.add(base);
        /*  951 */
        for (IUnit child : base.getChildren()) {
            /*  952 */
            buildFamily(child, r);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    private List<IMPart> getPartsForUnits(List<IUnit> units, int flags) {
        /*  957 */
        List<IMPart> r = new ArrayList();
        /*  958 */
        for (IMPart part : getUnitParts()) {
            /*  959 */
            boolean visible = this.appService.isPartVisible(part);
            /*  960 */
            if ((flags == 3) || ((flags == 1) && (visible)) || ((flags == 2) && (!visible)))
                /*      */ {
                /*  962 */
                UnitPartManager object = (UnitPartManager) part.getManager();
                /*  963 */
                if (object == null) {
                    /*  964 */
                    this.appService.activate(part);
                    /*      */
                }
                /*  966 */
                if ((object != null) && (units.contains(object.getUnit()))) {
                    /*  967 */
                    r.add(part);
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*  971 */
        return r;
        /*      */
    }

    /*      */
    /*      */
    public List<IMPart> getPartsForUnit(IUnit unit, int flags) {
        /*  975 */
        List<IUnit> units = new ArrayList();
        /*  976 */
        units.add(unit);
        /*  977 */
        return getPartsForUnits(units, flags);
        /*      */
    }

    /*      */
    /*      */
    public List<IMPart> getPartsForUnit(IUnit unit) {
        /*  981 */
        return getPartsForUnit(unit, 3);
        /*      */
    }

    /*      */
    /*      */
    public IMPart getFirstPartForUnit(IUnit unit) {
        /*  985 */
        for (IMPart part : getUnitParts()) {
            /*  986 */
            UnitPartManager object = (UnitPartManager) part.getManager();
            /*  987 */
            if ((object != null) && (object.getUnit() == unit)) {
                /*  988 */
                return part;
                /*      */
            }
            /*      */
        }
        /*  991 */
        return null;
        /*      */
    }

    /*      */
    /*      */
    public List<UnitPartManager> getPartManagersForUnit(IUnit unit) {
        /*  995 */
        return getPartManagersForUnit(unit, 3);
        /*      */
    }

    /*      */
    /*      */
    public List<UnitPartManager> getPartManagersForUnit(IUnit unit, int flags) {
        /*  999 */
        List<UnitPartManager> r = new ArrayList();
        /* 1000 */
        for (IMPart part : getUnitParts()) {
            /* 1001 */
            UnitPartManager object = (UnitPartManager) part.getManager();
            /* 1002 */
            if ((object != null) && (object.getUnit() == unit)) {
                /* 1003 */
                boolean visible = this.appService.isPartVisible(part);
                /* 1004 */
                if ((flags == 3) || ((flags == 1) && (visible)) || ((flags == 2) && (!visible)))
                    /*      */ {
                    /* 1006 */
                    r.add(object);
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /* 1010 */
        return r;
        /*      */
    }

    /*      */
    /*      */
    public UnitPartManager getFirstPartManagerrForUnit(IUnit unit) {
        /* 1014 */
        IMPart part = getFirstPartForUnit(unit);
        /* 1015 */
        if (part == null) {
            /* 1016 */
            return null;
            /*      */
        }
        /* 1018 */
        return (UnitPartManager) part.getManager();
        /*      */
    }

    /*      */
    /*      */
    public IMPart getPartById(int id) {
        /* 1022 */
        return getPartById(id, null);
        /*      */
    }

    /*      */
    /*      */
    public IMPart getPartById(int id, IUnit expectedUnit) {
        /* 1026 */
        for (IMPart part : getUnitParts()) {
            /* 1027 */
            if (getPartId(part) == id) {
                /* 1028 */
                if ((expectedUnit != null) && (expectedUnit != getUnitForPart(part))) {
                    /* 1029 */
                    throw new RuntimeException();
                    /*      */
                }
                /* 1031 */
                return part;
                /*      */
            }
            /*      */
        }
        /* 1034 */
        return null;
        /*      */
    }

    /*      */
    /*      */
    public int getPartId(IMPart part) {
        /* 1038 */
        return Conversion.toInt(part.getData().get("unitPartId"));
        /*      */
    }

    /*      */
    /*      */
    public int getOriginatorPartId(IMPart part) {
        /* 1042 */
        return Conversion.toInt(part.getData().get("originatorUnitPartId"));
        /*      */
    }

    /*      */
    /*      */
    public void setOriginator(IMPart target, IMPart origin) {
        /* 1046 */
        int id = getPartId(origin);
        /* 1047 */
        target.getData().put("originatorUnitPartId", Integer.valueOf(id));
        /*      */
    }

    /*      */
    /*      */
    public IMPart selectWithOriginator(List<IMPart> potentialOrigins, IMPart formerTarget) {
        /* 1051 */
        return selectWithOriginatorRecurse(potentialOrigins, formerTarget, null);
        /*      */
    }

    /*      */
    /*      */
    public IMPart selectWithOriginatorDeep(List<IMPart> potentialOrigins, IMPart formerTarget) {
        /* 1055 */
        List<IMPart> visited = new ArrayList();
        /* 1056 */
        return selectWithOriginatorRecurse(potentialOrigins, formerTarget, visited);
        /*      */
    }

    /*      */
    /*      */
    private IMPart selectWithOriginatorRecurse(List<IMPart> potentialOrigins, IMPart formerTarget, List<IMPart> visited)
    /*      */ {
        /* 1061 */
        if (visited != null) {
            /* 1062 */
            if ((formerTarget == null) || (visited.contains(formerTarget))) {
                /* 1063 */
                return null;
                /*      */
            }
            /* 1065 */
            visited.add(formerTarget);
            /*      */
        }
        /*      */
        /*      */
        /* 1069 */
        int originId = getOriginatorPartId(formerTarget);
        /* 1070 */
        for (IMPart part : potentialOrigins) {
            /* 1071 */
            if (getPartId(part) == originId) {
                /* 1072 */
                return part;
                /*      */
            }
            /*      */
        }
        /*      */
        /* 1076 */
        if (visited == null) {
            /* 1077 */
            return null;
            /*      */
        }
        /*      */
        /*      */
        /* 1081 */
        formerTarget = null;
        /* 1082 */
        for (IMPart part : this.appService.getParts()) {
            /* 1083 */
            if ((isUnitPart(part)) && (getPartId(part) == originId)) {
                /* 1084 */
                formerTarget = part;
                /* 1085 */
                break;
                /*      */
            }
            /*      */
        }
        /* 1088 */
        return selectWithOriginatorRecurse(potentialOrigins, formerTarget, visited);
        /*      */
    }

    /*      */
    /* 1091 */   private static ItemHistory<GlobalPosition> positionHistory = new ItemHistory();

    /*      */
    /*      */
    public ItemHistory<GlobalPosition> getGlobalPositionHistory()
    /*      */ {
        /* 1095 */
        return positionHistory;
        /*      */
    }

    /*      */
    /*      */
    public boolean recordGlobalPosition(GlobalPosition pos)
    /*      */ {
        /* 1100 */
        if (pos == null)
            /*      */ {
            /* 1102 */
            return false;
            /*      */
        }
        /* 1104 */
        positionHistory.add(pos);
        /* 1105 */
        return true;
        /*      */
    }

    /*      */
    /*      */
    public GlobalPosition getCurrentGlobalPosition()
    /*      */ {
        /* 1110 */
        IMPart part = getActivePart();
        /* 1111 */
        if (part == null) {
            /* 1112 */
            return null;
            /*      */
        }
        /* 1114 */
        UnitPartManager object = getUnitPartManager(part);
        /* 1115 */
        if (object == null) {
            /* 1116 */
            return null;
            /*      */
        }
        /* 1118 */
        return new GlobalPosition(object.getUnit(), getPartId(part), 0L, object.getActivePosition());
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public boolean setRedraw(IUnit unit, boolean redraw)
    /*      */ {
        /* 1134 */
        List<AbstractUnitFragment<?>> fragmentsDisabled = new ArrayList();
        /*      */
        try {
            /* 1136 */
            List<IMPart> parts = getPartsForUnit(unit);
            /* 1137 */
            for (IMPart p : parts) {
                /* 1138 */
                UnitPartManager up = (UnitPartManager) p.getManager();
                /* 1139 */
                if (up != null) {
                    /* 1140 */
                    AbstractUnitFragment<?> fr = up.getActiveFragment();
                    /* 1141 */
                    if (fr != null) {
                        /* 1142 */
                        fr.setRedraw(redraw);
                        /* 1143 */
                        fragmentsDisabled.add(fr);
                        /*      */
                    }
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*      */ catch (Exception e)
            /*      */ {
            /* 1150 */
            logger.catchingSilent(e);
            /* 1151 */
            if (!redraw)
                /*      */ {
                /* 1153 */
                for (??? =fragmentsDisabled.iterator(); ???.hasNext();){
                    AbstractUnitFragment<?> f = (AbstractUnitFragment) ???.next();
                    /* 1154 */
                    f.setRedraw(true);
                    /*      */
                }
                /*      */
            }
            /* 1157 */
            return false;
            /*      */
        }
        /* 1159 */
        return true;
        /*      */
    }
    /*      */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\PartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */