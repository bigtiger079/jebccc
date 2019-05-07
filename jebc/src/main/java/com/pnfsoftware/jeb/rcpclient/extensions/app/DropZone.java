
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

        return String.format("Target=%s(%d)", new Object[]{this.ctl, Integer.valueOf(this.index)});

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\DropZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */