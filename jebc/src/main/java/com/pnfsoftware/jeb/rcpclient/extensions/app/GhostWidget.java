package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.base.Couple;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class GhostWidget {
    private static final ILogger logger = GlobalLog.getLogger(GhostWidget.class);
    GhostStyleData styleData = GhostStyleData.buildDefault();
    Display display;
    Control topLevelContainer;
    Rectangle topRectangle;
    List<Couple<Control, PaintListener>> paintListeners = new ArrayList<>();
    int posDeltaX;
    int posDeltaY;
    List<DropZone> dropzones = new ArrayList<>();

    public GhostWidget(Control topLevelContainer, Display display) {
        if ((topLevelContainer != null) && (display != null) && (topLevelContainer.getDisplay() != display)) {
            throw new IllegalArgumentException();
        }
        this.topLevelContainer = topLevelContainer;
        this.display = display;
        if (topLevelContainer != null) {
            prepare(topLevelContainer);
        } else {
            for (Shell shell : display.getShells()) {
                prepare(shell);
            }
        }
    }

    public void setStyleData(GhostStyleData styleData) {
        this.styleData = styleData;
    }

    public GhostStyleData getStyleData() {
        return this.styleData;
    }

    public Display getDisplay() {
        return this.display;
    }

    public Control getTopLevelContainer() {
        return this.topLevelContainer;
    }

    public void setRectangle(Rectangle r) {
        this.topRectangle = r;
    }

    public Rectangle getRectangle() {
        return this.topRectangle;
    }

    public void setPositionDelta(Point posDelta) {
        this.posDeltaX = posDelta.x;
        this.posDeltaY = posDelta.y;
    }

    public void updatePosition(Point p) {
        this.topRectangle.x = (p.x - this.posDeltaX);
        this.topRectangle.y = (p.y - this.posDeltaY);
        for (DropZone dropzone : this.dropzones) {
            dropzone.activated = UIUtil.isContained(p, dropzone.rectangle);
        }
        redrawAll();
    }

    final PaintListener paintListener = new PaintListener() {
        public void paintControl(PaintEvent e) {
            if (!(e.widget instanceof Control)) {
                return;
            }
            Control ctl = (Control) e.widget;
            GC gc = e.gc;
            gc.setLineWidth(2);
            Rectangle r = e.display.map(GhostWidget.this.topLevelContainer, ctl, GhostWidget.this.topRectangle);
            gc.setForeground(GhostWidget.this.styleData.cGhostForeground);
            gc.setLineStyle(3);
            gc.drawRectangle(r);
            for (DropZone dropzone : GhostWidget.this.dropzones) {
                r = e.display.map(GhostWidget.this.topLevelContainer, ctl, dropzone.rectangle);
                gc.setForeground(GhostWidget.this.styleData.cDropzoneForeground);
                gc.setLineStyle(3);
                gc.drawRoundRectangle(r.x, r.y, r.width, r.height, 5, 5);
                if (dropzone.activated) {
                    gc.setBackground(GhostWidget.this.styleData.cDropzoneActiveBackground);
                    gc.fillRoundRectangle(r.x, r.y, r.width, r.height, 5, 5);
                }
            }
        }
    };

    private void prepare(Control ctl) {
        ctl.addPaintListener(this.paintListener);
        this.paintListeners.add(new Couple(ctl, this.paintListener));
        if ((ctl instanceof Composite)) {
            for (Control c : ((Composite) ctl).getChildren()) {
                prepare(c);
            }
        }
    }

    private void redrawAll() {
        if (this.topLevelContainer != null) {
            redraw(this.topLevelContainer);
        } else {
            for (Shell shell : this.display.getShells()) {
                redraw(shell);
            }
        }
    }

    private void redraw(Control ctl) {
        Point size = ctl.getSize();
        ctl.redraw(0, 0, size.x, size.y, true);
        ctl.update();
    }

    public void dispose() {
        for (Couple<Control, PaintListener> e : this.paintListeners) {
            Control ctl = (Control) e.getFirst();
            PaintListener listener = (PaintListener) e.getSecond();
            if (!ctl.isDisposed()) {
                ctl.removePaintListener(listener);
            }
        }
        redrawAll();
    }

    public void registerDropZones(List<DropZone> dropzones) {
        for (DropZone dropzone : dropzones) {
            if (dropzone.refctl != this.topLevelContainer) {
                if (this.topLevelContainer == null) {
                    dropzone.rectangle = this.display.map(dropzone.refctl, null, dropzone.rectangle);
                } else {
                    dropzone.rectangle = this.display.map(dropzone.refctl, this.topLevelContainer, dropzone.rectangle);
                }
            }
        }
        this.dropzones.addAll(dropzones);
    }

    public DropZone getActiveDropZone() {
        for (DropZone dropzone : this.dropzones) {
            if (dropzone.activated) {
                return dropzone;
            }
        }
        return null;
    }

    public boolean inCandidateArea(Point p) {
        if (this.topLevelContainer != null) {
            return UIUtil.isContained(p, this.topLevelContainer.getBounds());
        }
        for (Shell shell : this.display.getShells()) {
            if (UIUtil.isContained(p, shell.getBounds())) {
                return true;
            }
        }
        return false;
    }
}


