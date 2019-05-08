package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.rcpclient.IWidgetManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ShellWrapper {
    private Shell shell;
    private IWidgetManager widgetManager;
    private int widgetId;
    private Listener closeListener;
    private Rectangle recordedBounds;

    public static enum BoundsRestorationType {
        NONE, POSITION, SIZE, SIZE_AND_POSITION;

        private BoundsRestorationType() {
        }
    }

    public static ShellWrapper wrap(Shell shell, IWidgetManager widgetManager) {
        int widgetId = UIUtil.getWidgetId(shell);
        if (widgetId == 0) {
            return null;
        }
        return new ShellWrapper(shell, widgetManager, widgetId);
    }

    private ShellWrapper(Shell shell, IWidgetManager widgetManager, int widgetId) {
        this.shell = shell;
        this.widgetManager = widgetManager;
        this.widgetId = widgetId;
        setup();
    }

    public boolean hasRecordedBounds() {
        return this.recordedBounds != null;
    }

    public Rectangle getRecordedBounds() {
        return this.recordedBounds;
    }

    public Point getRecordedSize() {
        if (this.recordedBounds == null) {
            return null;
        }
        return new Point(this.recordedBounds.width, this.recordedBounds.height);
    }

    public Point getRecordedPosition() {
        if (this.recordedBounds == null) {
            return null;
        }
        return new Point(this.recordedBounds.x, this.recordedBounds.y);
    }

    private void setup() {
        if ((this.widgetId != 0) && (this.widgetManager != null)) {
            this.recordedBounds = this.widgetManager.getRecordedBounds(this.widgetId);
            this.shell.addListener(21, this.closeListener = new Listener() {
                public void handleEvent(Event event) {
                    ShellWrapper.this.widgetManager.setRecordedBounds(ShellWrapper.this.widgetId, ShellWrapper.this.shell.getBounds());
                }
            });
            this.shell.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    if (ShellWrapper.this.closeListener != null) {
                        ShellWrapper.this.shell.removeListener(21, ShellWrapper.this.closeListener);
                    }
                }
            });
            if (this.recordedBounds != null) {
                this.shell.setBounds(this.recordedBounds);
            }
        }
    }
}


