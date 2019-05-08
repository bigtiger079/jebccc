
package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMElement;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPanel;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPanelElement;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

public class Panel
        extends Composite
        implements IMPanel {
    private static final ILogger logger = GlobalLog.getLogger(Panel.class);
    private static int internalPanelCreationCount = 0;
    int internalPanelId;
    String elementId;
    private SashForm sashform;

    public Panel(Composite parent, int style) {
        super(parent, 0);
        this.internalPanelId = (internalPanelCreationCount++);
        setLayout(new FillLayout());
        this.sashform = new SashForm(this, style);
        this.sashform.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                Panel.this.dispose();
            }
        });
    }

    SashForm getSashFormWidget() {
        return this.sashform;
    }

    public boolean isEmpty() {
        return this.sashform.getWeights().length == 0;
    }

    public Control[] getElements() {
        int cnt = this.sashform.getWeights().length;
        Control[] parts = new Control[cnt];
        int i = 0;
        for (Control child : this.sashform.getChildren()) {
            if (!(child instanceof Sash)) {
                parts[(i++)] = child;
            }
        }
        if (i != parts.length) {
            throw new RuntimeException("The panel is missing an element");
        }
        if (i > 2) {
            throw new IllegalStateException("A panel should contain less than 2 elements");
        }
        return parts;
    }

    public int getElementIndex(Control elt) {
        if (elt.getParent() != this.sashform) {
            throw new RuntimeException("Element is not part of this panel");
        }
        int index = Arrays.asList(getElements()).indexOf(elt);
        if (index == -1) {
            throw new RuntimeException("Child not referenced by its parent!");
        }
        return index;
    }

    public List<Part> getParts() {
        List<Part> parts = new ArrayList();
        getParts(parts);
        return parts;
    }

    private void getParts(List<Part> parts) {
        for (Control c : getElements()) {
            if ((c instanceof Folder)) {
                parts.addAll(((Folder) c).getParts());
            } else if ((c instanceof Panel)) {
                ((Panel) c).getParts(parts);
            } else {
                throw new RuntimeException("Invalid element in panel");
            }
        }
    }

    public int[] getWeights() {
        return this.sashform.getWeights();
    }

    public void setWeights(int[] weights) {
        this.sashform.setWeights(weights);
    }

    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        this.sashform.setOrientation(orientation);
    }

    Folder createFolder(int style, int tabStyle, boolean onEmptyCloseFolder) {
        return new Folder(this.sashform, style, tabStyle, true, onEmptyCloseFolder);
    }

    Panel createPanel(int style) {
        return new Panel(this.sashform, style);
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return this.elementId;
    }

    public IMElement getParentElement() {
        Control p0 = getParent();
        if ((p0 instanceof Dock)) {
            return (Dock) p0;
        }
        if (((p0 instanceof SashForm)) && ((p0.getParent() instanceof Panel))) {
            return (Panel) p0.getParent();
        }
        throw new IllegalStateException("Illegal parent for Panel");
    }

    public List<IMPanelElement> getChildrenElements() {
        List<IMPanelElement> list = new ArrayList(2);
        for (Control elt : getElements()) {
            list.add((IMPanelElement) elt);
        }
        return list;
    }

    public IMPanelElement getFirstElement() {
        List<IMPanelElement> list = getChildrenElements();
        if (list.size() >= 1) {
            return (IMPanelElement) list.get(0);
        }
        return null;
    }

    public IMPanelElement getSecondElement() {
        List<IMPanelElement> list = getChildrenElements();
        if (list.size() >= 2) {
            return (IMPanelElement) list.get(1);
        }
        return null;
    }

    public boolean isVertical() {
        return this.sashform.getOrientation() == 512;
    }

    public int getSplitRatio() {
        int[] weights = this.sashform.getWeights();
        if (weights.length == 1) {
            return 100;
        }
        if (weights.length == 2) {
            return (int) (100.0D * (weights[0] / (weights[0] + weights[1])));
        }
        throw new RuntimeException();
    }

    public boolean setSplitRatio(int ratio) {
        int[] weights = this.sashform.getWeights();
        if (weights.length == 1) {
            return false;
        }
        if (weights.length == 2) {
            if ((ratio < 0) || (ratio > 100)) {
                return false;
            }
            weights[0] = (10 * ratio);
            weights[1] = (1000 - weights[0]);
            return true;
        }
        throw new RuntimeException();
    }

    public String toString() {
        return String.format("Panel@%d", new Object[]{Integer.valueOf(this.internalPanelId)});
    }

    public static class SashSelectionFilter
            implements Listener {
        int maxSizeAllowed;

        public SashSelectionFilter(int maxSizeAllowed) {
            this.maxSizeAllowed = maxSizeAllowed;
        }

        public void handleEvent(Event event) {
            if (!(event.widget instanceof Sash)) {
                return;
            }
            Sash s = (Sash) event.widget;
            Rectangle bounds = s.getBounds();
            if ((bounds.x == event.x) && (bounds.y == event.y)) {
                return;
            }
            if (!(s.getParent() instanceof SashForm)) {
                return;
            }
            SashForm sf = (SashForm) s.getParent();
            if (sf.getChildren().length != 3) {
                return;
            }
            boolean horizontal = (sf.getStyle() & 0x100) != 0;
            int maxSize = horizontal ? sf.getSize().x : sf.getSize().y;
            if (this.maxSizeAllowed * 2 + (horizontal ? s.getSize().x : s.getSize().y) > maxSize) {
                event.x = bounds.x;
                event.y = bounds.y;
                return;
            }
            int eventPosition = horizontal ? event.x : event.y;
            int eventEndPosition = horizontal ? event.x + bounds.width : event.y + bounds.height;
            int sashPosition = horizontal ? bounds.x : bounds.y;
            int sashEndPosition = horizontal ? bounds.x + bounds.width : bounds.y + bounds.height;
            if (eventPosition < this.maxSizeAllowed) {
                if (sashPosition < this.maxSizeAllowed) {
                    if (sashPosition >= eventPosition) {
                        updateEvent(horizontal, event, sashPosition);
                    }
                    return;
                }
                updateEvent(horizontal, event, this.maxSizeAllowed);
            } else if (maxSize - eventEndPosition < this.maxSizeAllowed) {
                if (maxSize - sashEndPosition < this.maxSizeAllowed) {
                    if (sashPosition <= eventPosition) {
                        updateEvent(horizontal, event, sashPosition);
                    }
                    return;
                }
                updateEvent(horizontal, event, maxSize - this.maxSizeAllowed);
            }
        }

        private void updateEvent(boolean horizontal, Event event, int sashPosition) {
            if (horizontal) {
                event.x = sashPosition;
            } else {
                event.y = sashPosition;
            }
        }
    }
}


