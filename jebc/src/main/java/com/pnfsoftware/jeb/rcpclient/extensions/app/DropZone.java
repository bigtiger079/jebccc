package com.pnfsoftware.jeb.rcpclient.extensions.app;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

class DropZone {
    Control refctl;
    Rectangle rectangle;
    boolean activated;
    Control ctl;
    int index;

    public DropZone(Control refctl, Rectangle rectangle) {
        this.refctl = refctl;
        this.rectangle = rectangle;
    }

    public String toString() {
        return String.format("Target=%s(%d)", this.ctl, this.index);
    }
}


