package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IXmlUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.JebApp;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.AppService;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IAppService;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMDock;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMFolder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgBreakpointsView;
import com.pnfsoftware.jeb.rcpclient.parts.units.debuggers.DbgVariablesView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractGlobalGraphView;
import com.pnfsoftware.jeb.util.collect.ItemHistory;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.events.Event;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;

public class PartManager implements IViewManager {
    private static final ILogger logger = GlobalLog.getLogger(PartManager.class);

    public static class PropertyProvider {
        private RcpClientContext context;

        public PropertyProvider(RcpClientContext context) {
            this.context = context;
        }

        public boolean getTryInPlace() {
            return !this.context.getProperties().getDoNotReplaceViews();
        }
    }

    private static int unitPartId = 1;
    private static Map<String, String> bestStackIdByType = new HashMap<>();
    public static final int FLAG_PARTS_VISIBLE = 1;
    public static final int FLAG_PARTS_NON_VISIBLE = 2;
    public static final int FLAG_PARTS_ALL = 3;
    public static final String idProjectExplorerPart = "jeb3.rcpclient.part.projectTree";
    public static final String idLoggerPart = "jeb3.rcpclient.part.logger";
    public static final String idTerminalPart = "jeb3.rcpclient.part.terminal";
    public static final String pdataUnitFormatType = "unitFormatType";
    public static final String pdataFragmentClasses = "fragmentClasses";
    public static final String dataUnit = "unit";
    public static final String dataUnitPartId = "unitPartId";
    public static final String dataFragmentList = "fragmentList";
    public static final String dataFragmentBlacklist = "fragmentBlacklist";
    public static final String dataOriginatorUnitPartId = "originatorUnitPartId";
    public static final String dataFocusTimestamp = "focusTimestamp";
    public static final String dataParentId0 = "parentId0";
    public static final String dataParentId1 = "parentId1";
    public static final String dataStickyPart = "stickyPart";
    private RcpClientContext context;
    private PropertyProvider propertyProvider;
    private JebApp app;
    private IAppService appService;
    private IMPart projectExplorerPart;
    private IMPart loggerPart;
    private IMPart terminalPart;
    private Folder folderHierarchy;
    private Folder folderGraphs;
    private Folder folderDebuggers;

    public PartManager(RcpClientContext context, JebApp app, PropertyProvider propertyProvider) {
        this.context = context;
        this.app = app;
        this.propertyProvider = propertyProvider;
        this.appService = new AppService(app);
    }

    public void initialize() {
        this.projectExplorerPart = createProjectExplorerPart();
        this.loggerPart = createLoggerPart();
        this.terminalPart = createTerminalPart();
        this.appService.activate(this.loggerPart);
        this.appService.activate(this.projectExplorerPart, true);
    }

    private IMPart createProjectExplorerPart() {
        IMPart part = this.appService.createPart(this.app.folderProject, new ProjectExplorerPartManager(this.context));
        part.setElementId(idProjectExplorerPart);
        part.setLabel(S.s(765));
        part.setIcon(UIAssetManager.getInstance().getImage("eclipse/hierarchy_co.png"));
        this.appService.activate(part);
        return part;
    }

    private IMPart createLoggerPart() {
        IMPart part = this.appService.createPart(this.app.folderConsoles, new LoggerPartManager(this.context));
        part.setElementId(idLoggerPart);
        part.setLabel(S.s(764));
        part.setIcon(UIAssetManager.getInstance().getImage("eclipse/console_view.png"));
        this.appService.activate(part);
        return part;
    }

    private IMPart createTerminalPart() {
        IMPart part = this.appService.createPart(this.app.folderConsoles, new TerminalPartManager(this.context));
        part.setElementId(idTerminalPart);
        part.setLabel("Terminal");
        part.setIcon(UIAssetManager.getInstance().getImage("eclipse/writeout_co.png"));
        this.appService.activate(part);
        return part;
    }

    public IMPart getActivePart() {
        return this.appService.getActivePart();
    }

    public static boolean isUnitPart(IMPart part) {
        return (part != null) && ((part.getManager() instanceof UnitPartManager));
    }

    public List<IMPart> getAllParts() {
        return new ArrayList<>(this.appService.findElements(null, null, IMPart.class, null, 0));
    }

    public List<IMPart> getUnitParts() {
        List<IMPart> list = new ArrayList<>();
        for (IMPart part : this.appService.findElements(null, null, IMPart.class, null, 0)) {
            if ((part.getManager() instanceof UnitPartManager)) {
                list.add(part);
            }
        }
        return list;
    }

    public IUnit getUnitForPart(IMPart part) {
        UnitPartManager manager = getUnitPartManager(part);
        return manager == null ? null : manager.getUnit();
    }

    public UnitPartManager getUnitPartManager(IMPart part) {
        if (!isUnitPart(part)) {
            return null;
        }
        return (UnitPartManager) part.getManager();
    }

    public void focus(IMPart part) {
        if (part != null) {
            this.appService.activate(part, true);
        }
    }

    public void onFocus(IMPart part) {
        recordFocusTime(part);
        recordParent(part);
    }

    private String buildStackIdKey(String formatType, List<String> fragmentList) {
        String key = formatType;
        if ((fragmentList != null) && (!fragmentList.isEmpty())) {
            key = key + ":" + Strings.join(",", fragmentList);
        }
        return key;
    }

    private void recordParent(IMPart part) {
        String parentId0 = (String) part.getData().get(dataParentId0);
        String parentId1 = (String) part.getData().get(dataParentId1);
        IMFolder parent = part.getParentElement();
        if ((parent != null)) {
            String parentId = parent.getElementId();
            parentId1 = parentId0;
            parentId0 = parentId;
            if ((parentId0 == null) || (!parentId0.equals(parentId1))) {
                IUnit unit = getUnitForPart(part);
                if (unit != null) {
                    List fragmentList = (List) part.getData().get(dataFragmentList);
                    bestStackIdByType.put(buildStackIdKey(unit.getFormatType(), fragmentList), parentId);
                }
            }
        }
        part.getData().put(dataParentId0, parentId0);
        part.getData().put(dataParentId1, parentId1);
    }

    private void recordFocusTime(IMPart part) {
        long ts = System.currentTimeMillis();
        part.getData().put(dataFocusTimestamp, ts);
    }

    private long getFocusTimestamp(IMPart part) {
        Object val = part.getData().get(dataFocusTimestamp);
        if (!(val instanceof Long)) {
            return 0L;
        }
        return (Long) val;
    }

    public IMPart getMostRecentlyFocused(List<IMPart> parts) {
        long ts0 = 0L;
        IMPart part0 = null;
        for (IMPart part : parts) {
            long ts = getFocusTimestamp(part);
            if (ts > ts0) {
                ts0 = ts;
                part0 = part;
            }
        }
        return part0;
    }

    public void close(IMPart part) {
        if (part != null) {
            this.appService.hidePart(part);
        }
    }

    public void close(IUnit unit) {
        for (IMPart part : getPartsForUnit(unit)) {
            close(part);
        }
    }

    public void closeAllUnitParts() {
        for (IMPart part : getUnitParts()) {
            this.appService.hidePart(part);
        }
    }

    public IMPart getProjectExplorerPart() {
        return this.projectExplorerPart;
    }

    public ProjectExplorerPartManager getProjectExplorer() {
        IMPart part = getProjectExplorerPart();
        if (part == null) {
            return null;
        }
        return (ProjectExplorerPartManager) part.getManager();
    }

    public void activateProjectExplorer(boolean focus) {
        this.appService.activate(this.projectExplorerPart, focus);
    }

    public void activateLogger(boolean focus) {
        this.appService.activate(this.loggerPart, focus);
    }

    public void activateTerminal(boolean focus) {
        this.appService.activate(this.terminalPart, focus);
    }

    public boolean activatePart(IMPart part, boolean focus) {
        this.appService.activate(part, focus);
        return true;
    }

    public boolean activatePart(String id, boolean focus) {
        List<IMPart> parts = this.appService.findElements(null, id, IMPart.class, null, 0);
        if (parts.isEmpty()) {
            return false;
        }
        this.appService.activate(parts.get(0), focus);
        return true;
    }

    public void closeUnitParts() {
        for (IMPart part : getUnitParts()) {
            closePart(part);
        }
    }

    public void closePart(IMPart part) {
        logger.i("Closing part: %s", part.getLabel());
        this.appService.hidePart(part);
    }

    public void unbindUnitParts() {
        for (IMPart part : getUnitParts()) {
            unbindUnitPart(part);
        }
    }

    public void unbindUnitPart(IMPart part) {
        recordUnitPartAffinities(part, false);
        String type = getUnitPartType(part);
        cleanUnitPart(part);
        String label = type + "<Unbound>";
        part.setLabel(label);
        String hint = !type.isEmpty() ? String.format("This unbound part is a placeholder for a view of type \"%s\"", type) : "This unbound part is a placeholder for any view";
        part.setTooltip(hint);
    }

    private String getUnitPartType(IMPart part) {
        String unitType = (String) part.getData().get(pdataUnitFormatType);
        UnitPartManager object = getUnitPartManager(part);
        if (object != null) {
            IUnit unit = object.getUnit();
            if (unit != null) {
                unitType = unit.getFormatType();
            }
        }
        return Strings.safe(unitType, "");
    }

    private boolean recordUnitPartAffinities(IMPart part, boolean resetIfUnbound) {
        UnitPartManager object = getUnitPartManager(part);
        if (object == null) {
            return false;
        }
        IUnit unit = object.getUnit();
        if (unit != null) {
            part.getData().put(pdataUnitFormatType, unit.getFormatType());
            recordFragments(part);
        } else if (resetIfUnbound) {
            part.getData().remove(pdataFragmentClasses);
        }
        return true;
    }

    private boolean recordFragments(IMPart part) {
        UnitPartManager object = getUnitPartManager(part);
        if (object == null) {
            return false;
        }
        List<String> cnames = new ArrayList<>();
        for (IRcpUnitFragment fragment : object.getFragments()) {
            cnames.add(fragment.getClass().getName());
        }
        part.getData().put(pdataFragmentClasses, Strings.join(",", cnames));
        return true;
    }

    private void cleanUnitPart(IMPart part) {
        part.getData().remove(dataUnit);
        part.getData().remove(dataUnitPartId);
        part.getData().remove(dataOriginatorUnitPartId);
        part.getData().remove(dataFragmentList);
        part.getData().remove(dataFragmentBlacklist);
        this.appService.clearPart(part);
        UnitPartManager pman = getUnitPartManager(part);
        if (pman != null) {
            pman.setup();
        }
    }

    private WeakIdentityHashMap<IDebuggerUnit, Folder> dbgFolders = new WeakIdentityHashMap();

    private IMFolder findBestFolder(IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist) {
        Folder folder = null;
        if ((unit instanceof ICodeUnit)) {
            if (isContainedInFragmentList(fragmentList, CodeHierarchyView.class)) {
                if ((this.folderHierarchy == null) || (this.folderHierarchy.isDisposed())) {
                    this.folderHierarchy = this.app.getDock().splitFolder(this.app.folderProject, -2, 70);
                }
                folder = this.folderHierarchy;
            } else if (isContainedInFragmentList(fragmentList, AbstractGlobalGraphView.class)) {
                if ((this.folderGraphs == null) || (this.folderGraphs.isDisposed())) {
                    if (!this.context.getPropertyManager().getBoolean("graphs.KeepInMainDock", false)) {
                        Rectangle r = this.app.getPrimaryShell().getBounds();
                        int x = (int) (r.x + 0.5D * r.width);
                        int w = Math.max(0, (int) (0.5D * r.width - 50.0D));
                        int y = (int) (r.y + 0.5D * r.height);
                        int h = Math.max(0, (int) (0.5D * r.height - 50.0D));
                        IMDock dockGraphs = this.appService.createDock(true, new Rectangle(x, y, w, h));
                        this.folderGraphs = ((Folder) dockGraphs.getInitialFolder());
                    } else {
                        this.folderGraphs = this.app.getDock().splitFolder(this.app.folderConsoles, -4, 40);
                    }
                }
                folder = this.folderGraphs;
            }
        } else if ((unit instanceof IDebuggerUnit)) {
            IDebuggerUnit dbg = (IDebuggerUnit) unit;
            folder = this.dbgFolders.get(dbg);
            if ((folder == null) || (folder.isDisposed())) {
                if ((this.folderDebuggers == null) || (this.folderDebuggers.isDisposed())) {
                    this.folderDebuggers = this.app.getDock().splitFolder(this.app.folderWorkspace, -4, 40);
                    folder = this.folderDebuggers;
                } else {
                    folder = this.app.getDock().splitFolder(this.folderDebuggers, -2, 50);
                }
                this.dbgFolders.put(dbg, folder);
            }
        }
        if (folder == null) {
            folder = this.app.folderWorkspace;
        }
        if (folder == null) {
            throw new RuntimeException();
        }
        return folder;
    }

    private IMPart createUnitPart(IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist) {
        IMFolder folder = findBestFolder(unit, fragmentList, fragmentBlacklist);
        //TODO: create UnitPart
        IMPart part = this.appService.createPart(folder, new UnitPartManager(this.context));
        logger.info("CreateUnitPart: %s", part.getClass().getName());
        prepareUnitPart(part, unit, fragmentList, fragmentBlacklist);
        return part;
    }

    private void prepareUnitPart(IMPart part, IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist) {
        part.setLabel(unit.getName());
        part.setTooltip("Hold and drag to move this tab elsewhere");
        part.getData().put(dataUnit, unit);
        part.getData().put(dataUnitPartId, unitPartId);
        unitPartId += 1;
        part.getData().put(dataOriginatorUnitPartId, 0);
        part.getData().put(dataFragmentList, fragmentList);
        part.getData().put(dataFragmentBlacklist, fragmentBlacklist);
        part.setHideable(true);
        part.setCloseOnHide(true);
        String iconURI = UnitPartManager.getIconForUnit(unit);
        if (iconURI != null) {
            part.setIcon(UIAssetManager.getInstance().getImage(iconURI));
        }
    }

    private void setStickyPart(IMPart part, boolean sticky) {
        part.getData().put(dataStickyPart, sticky);
    }

    private boolean isStickyPart(IMPart part) {
        Boolean o = (Boolean) part.getData().get(dataStickyPart);
        return o == null ? false : o;
    }

    private IMPart createInternal(IUnit unit, List<String> fragmentList, List<String> fragmentBlacklist, boolean activate) {
        if (fragmentList != null) {
            fragmentList = new ArrayList<>(fragmentList);
        }
        if (fragmentBlacklist != null) {
            fragmentBlacklist = new ArrayList<>(fragmentBlacklist);
        }
        IMPart part = createUnitPart(unit, fragmentList, fragmentBlacklist);
        if (activate) {
            this.appService.activate(part);
        }
        return part;
    }

    public IMPart createEmpty(IUnit unit) {
        return createInternal(unit, createFragmentList(), null, true);
    }

    public IMPart createSingle(IUnit unit, Class<?> cl) {
        return createInternal(unit, createFragmentList(cl), null, true);
    }

    public List<IMPart> create(IUnit unit, boolean tryActivationFirst) {
        boolean tryInPlace = false;
        if (tryActivationFirst) {
            if ((((unit instanceof ISourceUnit)) || ((unit instanceof IXmlUnit))) && (this.propertyProvider != null)) {
                tryInPlace = this.propertyProvider.getTryInPlace();
            }
        }
        return create(unit, tryActivationFirst, tryInPlace);
    }

    public List<IMPart> create(IUnit unit, boolean tryActivationFirst, boolean tryInPlace) {
        if (unit.getPropertyManager() == null) {
            throw new RuntimeException("Cannot create UnitPart of disposed Unit: " + unit.getName());
        }
        boolean sticky = !tryActivationFirst;
        IMPart reusedPart = null;
        IMPart part;
        if (tryActivationFirst) {
            List<IMPart> parts = getPartsForUnit(unit);
            if (!parts.isEmpty()) {
                restoreMissingParts(unit, parts);
                part = getMostRecentlyFocused(parts);
                if (part == null) {
                    part = parts.get(0);
                }
                this.appService.activate(part, true);
                return parts;
            }
            if (tryInPlace) {
                parts = getLivePartsForUnitType(unit);
                for (IMPart p : parts) {
                    if (!isStickyPart(p)) {
                        unbindUnitPart(p);
                        reusedPart = p;
                        sticky = false;
                        break;
                    }
                }
            }
        }
        List<IMPart> parts = new ArrayList<>();
        if (reusedPart != null) {
            prepareUnitPart(reusedPart, unit, null, null);
            this.appService.activate(reusedPart, true);
            setStickyPart(reusedPart, sticky);
            parts.add(reusedPart);
        } else if (!tryActivationFirst) {
            part = createInternal(unit, null, null, true);
            setStickyPart(part, sticky);
            parts.add(part);
        } else {
            if ((unit instanceof ICodeUnit)) {
                parts = createCodeUnitParts(unit, true, true, true);
            } else if ((unit instanceof IDebuggerUnit)) {
                parts = createDebuggerUnitParts(unit, true, true, true);
            } else {
                parts.add(createInternal(unit, null, null, false));
            }
            for (IMPart p : parts) {
                this.appService.activate(p, true);
            }
        }
        return parts;
    }

    private List<IMPart> createCodeUnitParts(IUnit unit, boolean createHierarchy, boolean createGraphs, boolean createMain) {
        List<IMPart> parts = new ArrayList<>();
        if (createHierarchy) {
            parts.add(createInternal(unit, createFragmentList(CodeHierarchyView.class), null, false));
        }
        if (createGraphs) {
            parts.add(createInternal(unit, createFragmentList(AbstractGlobalGraphView.class), null, false));
        }
        if (createMain) {
            parts.add(createInternal(unit, null, createFragmentList(CodeHierarchyView.class, AbstractGlobalGraphView.class), false));
        }
        return parts;
    }

    private List<IMPart> createDebuggerUnitParts(IUnit unit, boolean createBpView, boolean createVarView, boolean createMain) {
        List<IMPart> parts = new ArrayList<>();
        if (createBpView) {
            parts.add(createInternal(unit, createFragmentList(DbgBreakpointsView.class), null, false));
        }
        if (createVarView) {
            parts.add(createInternal(unit, createFragmentList(DbgVariablesView.class), null, false));
        }
        if (createMain) {
            parts.add(createInternal(unit, null, createFragmentList(DbgBreakpointsView.class, DbgVariablesView.class), false));
        }
        return parts;
    }

    public List<IMPart> restoreMissingParts(IUnit unit) {
        return restoreMissingParts(unit, getPartsForUnit(unit));
    }

    private List<IMPart> restoreMissingParts(IUnit unit, List<IMPart> parts) {
        List<IMPart> newParts = new ArrayList<>();
        IMPart part;
        boolean createBpView;
        if ((unit instanceof ICodeUnit)) {
            boolean createHierarchy = true;
            boolean createMain = true;
            for (IMPart part1 : parts) {
                part = part1;
                if (part.getData().get(dataFragmentList) != null) {
                    List<String> fragmentList = (List) part.getData().get(dataFragmentList);
                    if (fragmentList.contains(CodeHierarchyView.class.getName())) {
                        createHierarchy = false;
                    }
                } else if (part.getData().get(dataFragmentBlacklist) != null) {
                    createMain = false;
                }
            }
            newParts = createCodeUnitParts(unit, createHierarchy, false, createMain);
        } else if ((unit instanceof IDebuggerUnit)) {
            createBpView = true;
            boolean createVarView = true;
            boolean createMain = true;
            for (IMPart p : parts) {
                if (p.getData().get(dataFragmentList) != null) {
                    List<String> fragmentList = (List) p.getData().get(dataFragmentList);
                    if (!fragmentList.isEmpty()) {
                        if (fragmentList.get(0).equals(DbgBreakpointsView.class.getName())) {
                            createBpView = false;
                        } else if (fragmentList.get(0).equals(DbgVariablesView.class.getName())) {
                            createVarView = false;
                        }
                    }
                } else if (p.getData().get(dataFragmentBlacklist) != null) {
                    createMain = false;
                }
            }
            newParts = createDebuggerUnitParts(unit, createBpView, createVarView, createMain);
        }
        if (!newParts.isEmpty()) {
            for (IMPart p : newParts) {
                this.appService.activate(p);
            }
            UIState uiState = this.propertyProvider.context.getUIState(unit);
            uiState.notifyListeners(new Event());
        }
        return newParts;
    }

    private List<IMPart> getLivePartsForUnitType(IUnit targetUnit) {
        String targetUnitType = targetUnit.getFormatType();
        List<IMPart> candidates = new ArrayList<>();
        for (IMPart part : getUnitParts()) {
            UnitPartManager object = getUnitPartManager(part);
            if ((object != null) && (object.getUnit() != null) && (Strings.equals(targetUnitType, object.getUnit().getFormatType()))) {
                candidates.add(part);
            }
        }
        return candidates;
    }

    private List<String> createFragmentList(Class<?>... classes) {
        List<String> r = new ArrayList<>();
        for (Class<?> c : classes) {
            r.add(c.getName());
        }
        return r;
    }

    private static boolean isContainedInFragmentList(List<String> fragmentList, Class<?> targetClass) {
        if (fragmentList == null) {
            return false;
        }
        if (fragmentList.contains(targetClass.getName())) {
            return true;
        }
        for (String name : fragmentList) {
            try {
                Class<?> c = Class.forName(name);
                if (targetClass.isAssignableFrom(c)) {
                    return true;
                }
            } catch (ClassNotFoundException localClassNotFoundException) {
            }
        }
        return false;
    }

    public List<IMPart> getPartsForUnitFamily(IUnit base, int flags) {
        List<IUnit> family = new ArrayList<>();
        buildFamily(base, family);
        return getPartsForUnits(family, flags);
    }

    private void buildFamily(IUnit base, List<IUnit> r) {
        r.add(base);
        for (IUnit child : base.getChildren()) {
            buildFamily(child, r);
        }
    }

    private List<IMPart> getPartsForUnits(List<IUnit> units, int flags) {
        List<IMPart> r = new ArrayList<>();
        for (IMPart part : getUnitParts()) {
            boolean visible = this.appService.isPartVisible(part);
            if ((flags == 3) || ((flags == 1) && (visible)) || ((flags == 2) && (!visible))) {
                UnitPartManager object = (UnitPartManager) part.getManager();
                if (object == null) {
                    this.appService.activate(part);
                }
                if ((object != null) && (units.contains(object.getUnit()))) {
                    r.add(part);
                }
            }
        }
        return r;
    }

    public List<IMPart> getPartsForUnit(IUnit unit, int flags) {
        List<IUnit> units = new ArrayList<>();
        units.add(unit);
        return getPartsForUnits(units, flags);
    }

    public List<IMPart> getPartsForUnit(IUnit unit) {
        return getPartsForUnit(unit, 3);
    }

    public IMPart getFirstPartForUnit(IUnit unit) {
        for (IMPart part : getUnitParts()) {
            UnitPartManager object = (UnitPartManager) part.getManager();
            if ((object != null) && (object.getUnit() == unit)) {
                return part;
            }
        }
        return null;
    }

    public List<UnitPartManager> getPartManagersForUnit(IUnit unit) {
        return getPartManagersForUnit(unit, 3);
    }

    public List<UnitPartManager> getPartManagersForUnit(IUnit unit, int flags) {
        List<UnitPartManager> r = new ArrayList<>();
        for (IMPart part : getUnitParts()) {
            UnitPartManager object = (UnitPartManager) part.getManager();
            if ((object != null) && (object.getUnit() == unit)) {
                boolean visible = this.appService.isPartVisible(part);
                if ((flags == 3) || ((flags == 1) && (visible)) || ((flags == 2) && (!visible))) {
                    r.add(object);
                }
            }
        }
        return r;
    }

    public UnitPartManager getFirstPartManagerrForUnit(IUnit unit) {
        IMPart part = getFirstPartForUnit(unit);
        if (part == null) {
            return null;
        }
        return (UnitPartManager) part.getManager();
    }

    public IMPart getPartById(int id) {
        return getPartById(id, null);
    }

    public IMPart getPartById(int id, IUnit expectedUnit) {
        for (IMPart part : getUnitParts()) {
            if (getPartId(part) == id) {
                if ((expectedUnit != null) && (expectedUnit != getUnitForPart(part))) {
                    throw new RuntimeException();
                }
                return part;
            }
        }
        return null;
    }

    public int getPartId(IMPart part) {
        return Conversion.toInt(part.getData().get(dataUnitPartId));
    }

    public int getOriginatorPartId(IMPart part) {
        return Conversion.toInt(part.getData().get(dataOriginatorUnitPartId));
    }

    public void setOriginator(IMPart target, IMPart origin) {
        int id = getPartId(origin);
        target.getData().put(dataOriginatorUnitPartId, id);
    }

    public IMPart selectWithOriginator(List<IMPart> potentialOrigins, IMPart formerTarget) {
        return selectWithOriginatorRecurse(potentialOrigins, formerTarget, null);
    }

    public IMPart selectWithOriginatorDeep(List<IMPart> potentialOrigins, IMPart formerTarget) {
        List<IMPart> visited = new ArrayList<>();
        return selectWithOriginatorRecurse(potentialOrigins, formerTarget, visited);
    }

    private IMPart selectWithOriginatorRecurse(List<IMPart> potentialOrigins, IMPart formerTarget, List<IMPart> visited) {
        if (visited != null) {
            if ((formerTarget == null) || (visited.contains(formerTarget))) {
                return null;
            }
            visited.add(formerTarget);
        }
        int originId = getOriginatorPartId(formerTarget);
        for (IMPart part : potentialOrigins) {
            if (getPartId(part) == originId) {
                return part;
            }
        }
        if (visited == null) {
            return null;
        }
        formerTarget = null;
        for (IMPart part : this.appService.getParts()) {
            if ((isUnitPart(part)) && (getPartId(part) == originId)) {
                formerTarget = part;
                break;
            }
        }
        return selectWithOriginatorRecurse(potentialOrigins, formerTarget, visited);
    }

    private static ItemHistory<GlobalPosition> positionHistory = new ItemHistory();

    public ItemHistory<GlobalPosition> getGlobalPositionHistory() {
        return positionHistory;
    }

    public boolean recordGlobalPosition(GlobalPosition pos) {
        if (pos == null) {
            return false;
        }
        positionHistory.add(pos);
        return true;
    }

    public GlobalPosition getCurrentGlobalPosition() {
        IMPart part = getActivePart();
        if (part == null) {
            return null;
        }
        UnitPartManager object = getUnitPartManager(part);
        if (object == null) {
            return null;
        }
        return new GlobalPosition(object.getUnit(), getPartId(part), 0L, object.getActivePosition());
    }

    public boolean setRedraw(IUnit unit, boolean redraw) {
        List<AbstractUnitFragment<?>> fragmentsDisabled = new ArrayList<>();
        try {
            List<IMPart> parts = getPartsForUnit(unit);
            for (IMPart p : parts) {
                UnitPartManager up = (UnitPartManager) p.getManager();
                if (up != null) {
                    AbstractUnitFragment<?> fr = up.getActiveFragment();
                    if (fr != null) {
                        fr.setRedraw(redraw);
                        fragmentsDisabled.add(fr);
                    }
                }
            }
        } catch (Exception e) {
            logger.catchingSilent(e);
            if (!redraw) {
                for (AbstractUnitFragment<?> f : fragmentsDisabled) {
                    f.setRedraw(true);
                }
            }
            return false;
        }
        return true;
    }
}