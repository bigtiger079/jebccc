package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMDock;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMElement;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPanel;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class Dock extends Composite implements IMDock {
    private static final ILogger logger = GlobalLog.getLogger(Dock.class);
    private static int internalDockCreationCount = 0;
    public static final int TOP = -1;
    public static final int BOTTOM = -2;
    public static final int LEFT = -3;
    public static final int RIGHT = -4;
    int internalDockId;
    String elementId;
    boolean allowTabClose;
    boolean onEmptyCloseFolder;
    boolean allowForeignDocking;
    Dock masterDock;
    private Panel mainPanel;
    private Folder initialFolder;
    private List<IDockListener> dockListeners = new ArrayList();

    public Dock(Shell parentShell, boolean onEmptyCloseFolder) {
        this(parentShell, onEmptyCloseFolder, null);
    }

    private Dock(Shell parentShell, boolean onEmptyCloseFolder, Dock masterDock) {
        super(parentShell, 0);
        this.internalDockId = (internalDockCreationCount++);
        boolean ok = false;
        for (Control child : parentShell.getChildren()) {
            if ((child instanceof Dock)) {
                if (child == this) {
                    ok = true;
                } else {
                    ok = false;
                    break;
                }
            }
        }
        if (!ok) {
            throw new IllegalStateException("One Dock per Shell at most");
        }
        setLayout(new FillLayout());
        this.onEmptyCloseFolder = onEmptyCloseFolder;
        this.allowForeignDocking = true;
        this.masterDock = masterDock;
        build();
    }

    private void build() {
        this.mainPanel = new Panel(this, 0);
        this.initialFolder = createFolder(this.mainPanel, true);
    }

    public void clear() {
        this.mainPanel.dispose();
        build();
        layout();
    }

    public Dock getMasterDock() {
        return this.masterDock;
    }

    public Panel getMainPanel() {
        return this.mainPanel;
    }

    public Folder getInitialFolder() {
        return this.initialFolder;
    }

    Folder createFolder(Panel panel, boolean notify) {
        int style = 2048;
        if (this.allowTabClose) {
            style |= 0x40;
        }
        int tabStyle = 0;
        Folder folder = panel.createFolder(style, tabStyle, this.onEmptyCloseFolder);
        if (notify) {
            notifyFolderAdded(folder);
        }
        return folder;
    }

    public List<Folder> getFolders() {
        List<Folder> folders = new ArrayList();
        collectFolders(getMainPanel(), folders);
        return folders;
    }

    private void collectFolders(Widget w, List<Folder> folders) {
        if ((w instanceof Panel)) {
            Panel panel = (Panel) w;
            for (Control elt : panel.getElements()) {
                collectFolders(elt, folders);
            }
        } else if ((w instanceof Folder)) {
            folders.add((Folder) w);
        }
    }

    public Folder splitFolder(Folder folder, int split) {
        return splitFolder(folder, split, 50);
    }

    public Folder splitFolder(Folder folder, int split, int ratio) {
        if (folder.getDock() != this) {
            throw new IllegalArgumentException("Folder does not belong to this dock!");
        }
        if ((ratio < 0) || (ratio > 100)) {
            throw new IllegalArgumentException("Illegal ratio: " + ratio);
        }
        int orientation;
        if ((split == -1) || (split == -2)) {
            orientation = 512;
        } else {
            if ((split == -3) || (split == -4)) {
                orientation = 256;
            } else throw new RuntimeException("Invalid orientation: " + split);
        }
        boolean first = (split == -1) || (split == -3);
        Panel panel0 = folder.getParentPanel();
        int[] weights0 = panel0.getWeights();
        int cnt = weights0.length;
        Folder folder2;
        if (cnt == 1) {
            panel0.setOrientation(orientation);
            folder2 = createFolder(panel0, false);
            if (first) {
                folder2.moveAbove(null);
            }
            //TODO  panel0.setWeights(new int[]{ 100 - ratio, first ? new int[]{ratio, 100 - ratio} : ratio});
            panel0.setWeights(new int[]{100 - ratio, first ? 100 - ratio : ratio});
        } else if (cnt == 2) {
            int index = panel0.getElementIndex(folder);
            Panel panel1 = panel0.createPanel(orientation);
            if (index == 0) {
                panel1.moveAbove(null);
            }
            folder.setParent(panel1.getSashFormWidget());
            folder2 = createFolder(panel1, false);
            if (first) {
                folder2.moveAbove(null);
            }
            //TODO  panel0.setWeights(new int[]{ 100 - ratio, first ? new int[]{ratio, 100 - ratio} : ratio});
            panel1.setWeights(new int[]{100 - ratio, first ? 100 - ratio : ratio});
            panel1.layout();
            panel0.setWeights(weights0);
        } else {
            throw new RuntimeException(String.format("Illegal sashform (%d parts)", new Object[]{Integer.valueOf(cnt)}));
        }
        panel0.layout();
        notifyFolderAdded(folder2);
        return folder2;
    }

    public void addDockListener(IDockListener listener) {
        this.dockListeners.add(listener);
    }

    public void removeDockListener(IDockListener listener) {
        this.dockListeners.remove(listener);
    }

    void notifyFolderAdded(Folder folder) {
        for (IDockListener listener : this.dockListeners) {
            listener.folderAdded(folder);
        }
    }

    void notifyFolderRemoving(Folder folder) {
        for (IDockListener listener : this.dockListeners) {
            listener.folderRemoving(folder);
        }
    }

    public static List<Dock> findDocks(Display display) {
        List<Dock> docks = new ArrayList();
        for (Shell shell : display.getShells()) {
            for (Control ctl : shell.getChildren()) {
                if ((ctl instanceof Dock)) {
                    docks.add((Dock) ctl);
                }
            }
        }
        return docks;
    }

    public Dock createAdditionalDock() {
        return createAdditionalDock(false, 1264, null);
    }

    public Dock createAdditionalDock(boolean onTop, int shellStyle, Rectangle shellBounds) {
        Shell shell2 = onTop ? new Shell((Shell) getParent(), shellStyle) : new Shell(getDisplay(), shellStyle);
        if (shellBounds != null) {
            shell2.setBounds(shellBounds);
        } else {
            shell2.setSize(500, 400);
        }
        shell2.setLayout(new FillLayout());
        final Dock dock2 = new Dock(shell2, this.onEmptyCloseFolder, this.masterDock == null ? this : this.masterDock);
        shell2.addListener(21, new Listener() {
            public void handleEvent(Event event) {
                Panel p = dock2.getMainPanel();
                List<Part> parts = p.getParts();
                if (!parts.isEmpty()) {
                    for (Part pa : parts) {
                        if (!pa.isHideable()) {
                            Folder folder = (Folder) pa.getParentElement();
                            folder.restoreTab(pa, pa.defaultOwner.getFolderWidget());
                        }
                    }
                }
            }
        });
        shell2.layout();
        shell2.open();
        return dock2;
    }

    public String formatStructure() {
        StringBuilder sb = new StringBuilder();
        formatStructure(null, this, 0, sb);
        return Strings.rtrim(sb.toString());
    }

    private void formatStructure(IMElement parent, IMElement elt, int depth, StringBuilder sb) {
        if ((parent != null) && (elt.getParentElement() != parent)) {
            throw new RuntimeException(String.format("Unexpected parent for element %s: %s != %s", new Object[]{elt, parent, elt.getParentElement()}));
        }
        Part part;
        if ((elt instanceof Dock)) {
            Dock dock = (Dock) elt;
            sb.append(String.format("%sDock(#%d)\n", new Object[]{Strings.generate(' ', depth * 2), Integer.valueOf(dock.internalDockId)}));
        } else if ((elt instanceof Panel)) {
            Panel panel = (Panel) elt;
            sb.append(String.format("%sPanel(%s %s)\n", new Object[]{Strings.generate(' ', depth * 2), Arrays.toString(panel.getWeights()), panel.isVertical() ? "vertical" : "horizontal"}));
        } else if ((elt instanceof Folder)) {
            Folder folder = (Folder) elt;
            sb.append(String.format("%sFolder(%d)\n", new Object[]{Strings.generate(' ', depth * 2), Integer.valueOf(folder.getPartsCount())}));
        } else if ((elt instanceof Part)) {
            part = (Part) elt;
            sb.append(String.format("%sPart(%s)\n", new Object[]{Strings.generate(' ', depth * 2), part.getLabel()}));
        } else {
            throw new RuntimeException(String.format("Element not supported in this dock: %s", new Object[]{elt == null ? null : elt.getClass().getSimpleName()}));
        }
        for (IMElement child : elt.getChildrenElements()) {
            formatStructure(elt, child, depth + 1, sb);
        }
    }

    public String toString() {
        return String.format("Dock@%d", new Object[]{Integer.valueOf(this.internalDockId)});
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return this.elementId;
    }

    public IMElement getParentElement() {
        return null;
    }

    public List<? extends IMElement> getChildrenElements() {
        return Arrays.asList(new IMPanel[]{getPanelElement()});
    }

    public IMPanel getPanelElement() {
        return this.mainPanel;
    }
}


