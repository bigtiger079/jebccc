package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Part;
import com.pnfsoftware.jeb.rcpclient.util.PartUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AppService implements IAppService {
    private static final ILogger logger = GlobalLog.getLogger(AppService.class);
    App app;

    public AppService(App app) {
        if (app == null) {
            throw new NullPointerException();
        }
        this.app = app;
    }

    public <T extends IMElement> List<T> findElements(IMElement root, String id, Class<T> type, Collection<String> tags, int flags) {
        if (id != null) {
            throw new RuntimeException("Not supported yet");
        }
        if ((tags != null) && (!tags.isEmpty())) {
            throw new RuntimeException("Not supported yet");
        }
        List<T> out = new ArrayList();
        if (root == null) {
            for (Shell s : this.app.getDisplay().getShells()) {
                for (Control c : s.getChildren()) {
                    if ((c instanceof Dock)) {
                        findElementsRecurse((Dock) c, id, type, tags, flags, out);
                    }
                }
            }
        } else {
            findElementsRecurse(root, id, type, tags, flags, out);
        }
        return out;
    }

    public <T extends IMElement> void findElementsRecurse(IMElement elt, String id, Class<T> type, Collection<String> tags, int flags, List<T> out) {
        boolean include = true;
        if ((type != null) && (!type.isAssignableFrom(elt.getClass()))) {
            include = false;
        }
        if (include) {
            out.add((T) elt);
        }
        for (IMElement c : elt.getChildrenElements()) {
            findElementsRecurse(c, id, type, tags, flags, out);
        }
    }

    public IMDock createDock() {
        return this.app.getDock().createAdditionalDock();
    }

    public IMDock createDock(boolean onTop, Rectangle initialBounds) {
        IMDock dock2 = this.app.getDock().createAdditionalDock(onTop, 16, initialBounds);
        return dock2;
    }

    public IMPart createPart(IMFolder folder, IMPartManager partManager) {
        if (folder == null) {
            folder = (IMFolder) this.app.getDock().getFolders().get(0);
        }
        IMPart part = folder.addPart();
        if (partManager != null) {
            part.setManager(partManager);
        }
        return part;
    }

    public void activate(IMPart part) {
        activate(part, false);
    }

    public void activate(IMPart part, boolean focus) {
        if (focus) {
            focusPart(part);
        } else {
            showPart(part);
        }
    }

    public void hidePart(IMPart part) {
        Folder folder = (Folder) part.getParentElement();
        folder.hidePart((Part) part);
    }

    public void unhidePart(IMPart part) {
        Folder folder = (Folder) part.getParentElement();
        folder.unhidePart((Part) part);
    }

    public void clearPart(IMPart part) {
        Folder folder = (Folder) part.getParentElement();
        folder.clearPart((Part) part);
    }

    public void showPart(IMPart part) {
        Folder folder = (Folder) part.getParentElement();
        folder.showPart((Part) part);
    }

    public void focusPart(IMPart part) {
        Folder folder = (Folder) part.getParentElement();
        folder.focusPart((Part) part);
    }

    public boolean isPartVisible(IMPart part) {
        return !((Part) part).isHidden();
    }

    public IMPart getActivePart() {
        Control ctl = this.app.getDisplay().getFocusControl();
        return PartUtil.getPart(ctl);
    }

    public Collection<IMPart> getParts() {
        List<IMPart> r = new ArrayList();
        for (Dock dock : Dock.findDocks(this.app.getDisplay())) {
            for (Folder folder : dock.getFolders()) {
                for (Part part : folder.getParts()) {
                    r.add(part);
                }
            }
        }
        return r;
    }
}