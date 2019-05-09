package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMElement;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMFolder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPartManager;
import com.pnfsoftware.jeb.rcpclient.util.CTabFolderUtils;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Folder extends Composite implements IMFolder {
    private static final ILogger logger = GlobalLog.getLogger(Folder.class);
    private static int internalFolderCreationCount = 0;
    int internalFolderId;
    String elementId;
    int defaultTabStyle;
    boolean insideDock;
    boolean closeOnEmpty;
    private CTabFolder folderWidget;
    List<IFolderListener> folderListeners = new ArrayList<>();
    GhostWidget g;
    CTabItem draggedTab;
    CTabItem previouslySelectedTab;
    CTabItem selectedTab;
    List<Part> parts = new ArrayList<>();
    public static final int HIDDEN = 0;
    public static final int VISIBLE = 1;
    public static final int SHOWN = 2;
    public static final int FOCUSED = 3;

    public Folder(Composite parent, int style, int tabStyle) {
        this(parent, style, tabStyle, false, false);
    }

    Folder(Composite parent, int style, int tabStyle, boolean insideDock, boolean closeOnEmpty) {
        super(parent, 0);
        this.internalFolderId = (internalFolderCreationCount++);
        this.defaultTabStyle = tabStyle;
        this.insideDock = insideDock;
        this.closeOnEmpty = closeOnEmpty;
        setLayout(new FillLayout());
        this.folderWidget = new CTabFolder(this, style);
        CTabFolderUtils.setCTabFolderHeight(this.folderWidget, (int) (this.folderWidget.getTabHeight() * 1.2D));
        this.folderWidget.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void close(CTabFolderEvent event) {
                CTabItem tab = (CTabItem) event.item;
                Part part = Folder.this.getPartByTab(tab);
                Folder.this.hidePart(part);
                event.doit = false;
            }
        });
        this.folderWidget.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Folder.this.previouslySelectedTab = Folder.this.selectedTab;
                CTabItem tab = Folder.this.folderWidget.getSelection();
                Folder.this.selectedTab = tab;
                if (tab != null) {
                    Part part = Folder.this.getPartByTab(tab);
                    part.getControl().setFocus();
                    if (part.getManager() != null) {
                        part.getManager().setFocus();
                    }
                    Folder.this.notifyPartSelected(Folder.this.getPartByTab(tab));
                }
            }
        });
        this.folderWidget.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                Folder.logger.i("FocusGained: %s@%d", new Object[]{e.widget, Integer.valueOf(e.widget.hashCode())});
            }

            public void focusLost(FocusEvent e) {
                Folder.logger.i("FocusLost: %s@%d", new Object[]{e.widget, Integer.valueOf(e.widget.hashCode())});
            }
        });
        this.folderWidget.addListener(29, new Listener() {
            public void handleEvent(Event event) {
                CTabItem item = Folder.this.folderWidget.getSelection();
                if (item == null) {
                    return;
                }
                Display display = Folder.this.getDisplay();
                Control topLevelContainer;
                List<DropZone> list;
                if (!Folder.this.isInsideDock()) {
                    list = new DropZones(Folder.this, item, false, false).determine();
                    topLevelContainer = Folder.this;
                } else {
                    Dock dock = Folder.this.getDock();
                    boolean includeSelfDockingAreas = (!dock.onEmptyCloseFolder) || (item.getParent().getItemCount() != 1);
                    list = new DropZones(dock, item, true, includeSelfDockingAreas).determine();
                    topLevelContainer = dock;
                    if (dock.allowForeignDocking) {
                        for (Dock dock2 : Dock.findDocks(display))
                            if (dock2 != dock) {
                                List<DropZone> list2 = new DropZones(dock2, item, true, true).determine();
                                list.addAll(list2);
                            }
                        topLevelContainer = null;
                    }
                }
                if (list.isEmpty()) {
                    return;
                }
                Folder.this.folderWidget.forceFocus();
                Folder.this.draggedTab = Folder.this.folderWidget.getSelection();
                Folder.this.g = new GhostWidget(topLevelContainer, display);
                Folder.this.g.registerDropZones(list);
                Rectangle r = Folder.this.folderWidget.getSelection().getBounds();
                Folder.this.g.setRectangle(event.display.map(Folder.this.folderWidget, Folder.this.g.getTopLevelContainer(), r));
                Folder.this.g.setPositionDelta(new Point(event.x - r.x, event.y - r.y));
            }
        });
        this.folderWidget.addListener(5, new Listener() {
            public void handleEvent(Event event) {
                if (Folder.this.g != null) {
                    Point p = event.display.map(Folder.this.folderWidget, Folder.this.g.getTopLevelContainer(), event.x, event.y);
                    Folder.this.g.updatePosition(p);
                }
            }
        });
        this.folderWidget.addListener(4, new Listener() {
            public void handleEvent(Event event) {
                if (Folder.this.g != null) {
                    try {
                        DropZone dropzone = Folder.this.g.getActiveDropZone();
                        Dock dock = Folder.this.getDock();
                        Folder srcFolder = Folder.tabToFolder(Folder.this.draggedTab);
                        Folder dstFolder = null;
                        if (dropzone != null) {
                            dstFolder = (Folder) dropzone.ctl;
                            Dock dstDock = dstFolder.getDock();
                            if (dropzone.index >= 0) {
                                Folder.this.moveTab(Folder.this.draggedTab, dstFolder.folderWidget, dropzone.index, true);
                            } else {
                                Folder folder2 = dstDock.splitFolder(dstFolder, dropzone.index);
                                Folder.this.moveTab(Folder.this.draggedTab, folder2.folderWidget, 0, true);
                            }
                        } else if ((dock != null) && (dock.allowForeignDocking)) {
                            Point p = event.display.map(Folder.this.folderWidget, null, event.x, event.y);
                            if (!Folder.this.g.inCandidateArea(p)) {
                                Dock dstDock = dock.createAdditionalDock();
                                dstFolder = dstDock.getInitialFolder();
                                Folder.this.moveTab(Folder.this.draggedTab, dstFolder.folderWidget, 0, true);
                            }
                        }
                        if ((dock != null) && (dstFolder != null)) {
                            Folder.this.updateFolder(srcFolder);
                        } else if ((Folder.this.previouslySelectedTab != null) && (!Folder.this.previouslySelectedTab.isDisposed())) {
                            Folder.this.focusPart(Folder.this.getPartByTab(Folder.this.previouslySelectedTab));
                            Folder.this.selectedTab = Folder.this.previouslySelectedTab;
                        }
                    } finally {
                        Folder.this.g.dispose();
                        Folder.this.g = null;
                        Folder.this.draggedTab = null;
                    }
                } else if (event.button == 2) {
                    CTabItem item = Folder.this.folderWidget.getItem(new Point(event.x, event.y));
                    if (item == null) {
                        return;
                    }
                    Part part = Folder.this.getPartByTab(item);
                    if ((!part.isHidden()) && (part.isHideable())) {
                        if (part.isCloseOnHide()) {
                            Folder.this.removePart(part, true);
                        } else {
                            part.hide();
                            Folder.this.notifyPartHidden(part);
                        }
                    }
                }
            }
        });
        this.folderWidget.addListener(16, new Listener() {
            public void handleEvent(Event event) {
                if (Folder.this.g != null) {
                    Folder.this.g.dispose();
                }
            }
        });
        this.folderWidget.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                Folder.logger.i("KeyPressed: %s", new Object[]{e});
                if ((Folder.this.g != null) && (e.keyCode == 27)) {
                    Folder.this.g.dispose();
                    Folder.this.g = null;
                    Folder.this.draggedTab = null;
                }
            }
        });
        this.folderWidget.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                Folder.this.dispose();
            }
        });
    }

    public boolean isInsideDock() {
        return this.insideDock;
    }

    public Dock getDock() {
        if (!this.insideDock) {
            return null;
        }
        Control ctl = this;
        while (!(ctl instanceof Dock)) {
            ctl = ctl.getParent();
            if (ctl == null) {
                throw new RuntimeException("This folder should be inside a dock");
            }
        }
        return (Dock) ctl;
    }

    public boolean isCloseOnEmpty() {
        return this.closeOnEmpty;
    }

    public void setCloseOnEmpty(boolean closeOnEmpty) {
        this.closeOnEmpty = closeOnEmpty;
    }

    CTabFolder getFolderWidget() {
        return this.folderWidget;
    }

    public Panel getParentPanel() {
        if (!(getParent() instanceof SashForm)) {
            throw new IllegalStateException("Folder in unexpected location");
        }
        Control ctl = getParent().getParent();
        if (!(ctl instanceof Panel)) {
            throw new IllegalStateException("Folder not in a panel");
        }
        return (Panel) ctl;
    }

    private Part getPartByTab(CTabItem tab) {
        for (Part part : this.parts) {
            if (part.tab == tab) {
                return part;
            }
        }
        throw new RuntimeException();
    }

    public Part getPartByControl(Control control) {
        for (Part part : this.parts) {
            if (part.getControl() == control) {
                return part;
            }
        }
        return null;
    }

    static Folder widgetToFolder(CTabFolder folderWidget) {
        return (Folder) folderWidget.getParent();
    }

    static Folder tabToFolder(CTabItem tab) {
        return (Folder) tab.getParent().getParent();
    }

    private void moveTab(CTabItem srcTab, CTabFolder dstFolderWidget, int dstIndex, boolean notify) {
        Part part = getPartByTab(srcTab);
        CTabFolder srcFolderWidget = srcTab.getParent();
        int srcIndex = Arrays.asList(srcFolderWidget.getItems()).indexOf(srcTab);
        Folder srcFolder = (Folder) srcFolderWidget.getParent();
        Folder dstFolder = (Folder) dstFolderWidget.getParent();
        if ((srcFolderWidget == dstFolderWidget) && (dstIndex > srcIndex)) {
            dstIndex--;
        }
        part.hide();
        this.parts.remove(part);
        CTabItem newTab = new CTabItem(dstFolderWidget, this.defaultTabStyle, dstIndex);
        dstFolder.parts.add(dstIndex, part);
        part.restoreInto(newTab);
        dstFolderWidget.showItem(newTab);
        dstFolderWidget.setSelection(newTab);
        if (notify) {
            notifyPartMoved(srcFolder, part);
        }
    }

    void restoreTab(Part part, CTabFolder dstFolderWidget) {
        Folder dstFolder = (Folder) dstFolderWidget.getParent();
        part.hide();
        this.parts.remove(part);
        CTabItem newTab = new CTabItem(dstFolderWidget, this.defaultTabStyle);
        dstFolder.parts.add(part);
        part.restoreInto(newTab);
        dstFolderWidget.showItem(newTab);
        dstFolderWidget.setSelection(newTab);
    }

    private void updateFolder(Folder srcFolder) {
        Dock dock = srcFolder.getDock();
        if ((dock != null) && (srcFolder.closeOnEmpty) && (srcFolder.getPartsCount() == 0)) {
            dock.notifyFolderRemoving(srcFolder);
            Panel panel = srcFolder.getParentPanel();
            srcFolder.dispose();
            if (panel.isEmpty()) {
                if ((panel.getParent() instanceof Dock)) {
                    Shell sh = (Shell) panel.getParent().getParent();
                    sh.dispose();
                    return;
                }
                Panel panelParent = (Panel) panel.getParent().getParent();
                panel.dispose();
                panel = panelParent;
            }
            panel.layout(true, true);
        }
    }

    public void addFolderListener(IFolderListener listener) {
        this.folderListeners.add(listener);
    }

    public void removeFolderListener(IFolderListener listener) {
        this.folderListeners.remove(listener);
    }

    void notifyPartSelected(Part part) {
        for (IFolderListener listener : this.folderListeners) {
            listener.partSelected(part);
        }
    }

    void notifyPartAdded(Part part) {
        for (IFolderListener listener : this.folderListeners) {
            listener.partAdded(part);
        }
    }

    void notifyPartRemoved(Part part) {
        for (IFolderListener listener : this.folderListeners) {
            listener.partRemoved(part);
        }
    }

    void notifyPartMoved(Folder src, Part part) {
        for (IFolderListener listener : this.folderListeners) {
            listener.partMoved(src, part);
        }
    }

    void notifyPartHidden(Part part) {
        for (IFolderListener listener : this.folderListeners) {
            listener.partHidden(part);
        }
    }

    void notifyPartVisible(Part part) {
        for (IFolderListener listener : this.folderListeners) {
            listener.partVisible(part);
        }
    }

    public Part addPart() {
        return addPart(true);
    }

    Part addPart(boolean notify) {
        return addPart(this.folderWidget.getItemCount(), notify);
    }

    public Part addPart(int index) {
        return addPart(index, true);
    }

    Part addPart(int index, boolean notify) {
        if ((index < 0) || (index > this.parts.size())) {
            index = this.parts.size();
        }
        Part part = new Part(this);
        this.parts.add(part);
        if (notify) {
            notifyPartAdded(part);
        }
        return part;
    }

    public void hidePart(Part part) {
        setPartVisibility(part, 0);
    }

    public void unhidePart(Part part) {
        setPartVisibility(part, 1);
    }

    public void showPart(Part part) {
        setPartVisibility(part, 2);
    }

    public void focusPart(Part part) {
        setPartVisibility(part, 3);
    }

    void setPartVisibility(Part part, int action) {
        setPartVisibility(part, action, true);
    }

    void setPartVisibility(Part part, int action, boolean notify) {
        if ((action < 0) || (action > 3)) {
            throw new IllegalArgumentException();
        }
        int index = this.parts.indexOf(part);
        if ((index < 0) || (index > this.folderWidget.getItemCount())) {
            index = this.folderWidget.getItemCount();
        }
        if (action == 0) {
            if (!part.isHidden()) {
                if (part.isCloseOnHide()) {
                    removePart(part, notify);
                } else {
                    part.hide();
                    if (notify) {
                        notifyPartHidden(part);
                    }
                }
            }
            return;
        }
        if ((action >= 1) && (part.isHidden())) {
            CTabItem tab = new CTabItem(this.folderWidget, this.defaultTabStyle, index);
            part.restoreInto(tab);
            if (notify) {
                notifyPartVisible(part);
            }
        }
        if (action >= 2) {
            IMPartManager partManager = part.getManager();
            if ((part.state == 0) && (partManager != null)) {
                Composite container = part.getContainerWidget();
                partManager.createView(container, part);
                container.layout();
                part.state = 1;
            }
            this.folderWidget.setSelection(part.tab);
        }
        if (action >= 3) {
            this.folderWidget.setSelection(part.tab);
            part.tab.getControl().setFocus();
            if (part.getManager() != null) {
                part.getManager().setFocus();
            }
        }
    }

    public int getPartVisibility(Part part) {
        int index = this.parts.indexOf(part);
        if ((index < 0) || (index > this.folderWidget.getItemCount())) {
            index = this.folderWidget.getItemCount();
        }
        if (part.isHidden()) {
            return 0;
        }
        if (this.folderWidget.getSelection() != part.tab) {
            return 1;
        }
        if (!hasFocus(part.getContainerWidget(), false)) {
            return 2;
        }
        return 3;
    }

    public static boolean hasFocus(Control ctl, boolean direct) {
        if (ctl == null) {
            return false;
        }
        Control c = ctl.getDisplay().getFocusControl();
        if (c == ctl) {
            return true;
        }
        if (direct) {
            return false;
        }
        while (c != null) {
            c = c.getParent();
            if (c == ctl) {
                return true;
            }
            if (c == null) {
                return false;
            }
        }
        return false;
    }

    public void clearPart(Part part) {
        if (part.state == 0) {
            return;
        }
        if (part.getManager() != null) {
            part.getManager().deleteView();
        }
        for (Control child : part.getContainerWidget().getChildren()) {
            if (!child.isDisposed()) {
                child.dispose();
            }
        }
        part.getContainerWidget().layout(true, true);
        part.state = 0;
    }

    public void removePart(Part part) {
        removePart(part, true);
    }

    void removePart(Part part, boolean notify) {
        if (!part.isCloseOnHide()) {
            throw new IllegalArgumentException("Cannot close part");
        }
        if (this.parts.indexOf(part) == -1) {
            throw new IllegalArgumentException("Invalid part");
        }
        part.hide();
        this.parts.remove(part);
        clearPart(part);
        if (notify) {
            notifyPartRemoved(part);
        }
        Folder folder = (Folder) part.getParentElement();
        if (folder != null) {
            updateFolder(folder);
        }
    }

    public String toString() {
        return String.format("Folder@%d", new Object[]{Integer.valueOf(this.internalFolderId)});
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return this.elementId;
    }

    public IMElement getParentElement() {
        return getParentPanel();
    }

    public List<? extends IMElement> getChildrenElements() {
        return this.parts;
    }

    public List<Part> getParts() {
        return Collections.unmodifiableList(this.parts);
    }

    public int getPartsCount() {
        return this.parts.size();
    }

    public int getVisiblePartsCount() {
        int cnt = 0;
        for (Part part : this.parts) {
            if (!part.isHidden()) {
                cnt++;
            }
        }
        if (cnt != this.folderWidget.getItemCount()) {
            throw new IllegalStateException(String.format("Unexpected count of visible parts: %d, %d", new Object[]{Integer.valueOf(cnt), Integer.valueOf(this.folderWidget.getItemCount())}));
        }
        return cnt;
    }

    public int getPanelShare() {
        Panel panel = getParentPanel();
        int ratio = panel.getSplitRatio();
        if (ratio == 100) {
            return 100;
        }
        int i = panel.getChildrenElements().indexOf(this);
        if (i == 0) {
            return ratio;
        }
        return 100 - ratio;
    }

    public void initPreset(int start, int cnt, final int autoRefreshPeriodMs) {
        for (int i = start; i < start + cnt; i++) {
            Part part = addPart(false);
            part.setLabel("Item " + i);
            final StyledText text = new StyledText(part.getContainerWidget(), 0);
            text.setText("Content for Item " + i);
            showPart(part);
            if (autoRefreshPeriodMs > 0) {
                ThreadUtil.start(new Runnable() {
                    public void run() {
                        try {
                            for (; ; ) {
                                Folder.this.getDisplay().asyncExec(new Runnable() {
                                    public void run() {
                                        text.setText("Nanotime: " + System.nanoTime());
                                    }
                                });
                                Thread.sleep(autoRefreshPeriodMs);
                            }
//                            return;
                        } catch (InterruptedException e) {
                        } catch (SWTException e) {
                        }
                    }
                });
            }
        }
    }
}