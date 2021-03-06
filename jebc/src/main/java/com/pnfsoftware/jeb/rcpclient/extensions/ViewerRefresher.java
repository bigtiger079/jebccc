package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

public class ViewerRefresher extends AbstractRefresher {
    private static final ILogger logger = GlobalLog.getLogger(ViewerRefresher.class);
    private Viewer viewer;

    public ViewerRefresher(Display display, Viewer viewer) {
        super(display, viewer.getClass().getSimpleName());
        this.viewer = viewer;
    }

    public ViewerRefresher(Viewer viewer) {
        super(viewer.getControl().getDisplay(), viewer.getClass().getSimpleName());
        this.viewer = viewer;
    }

    protected boolean shouldPerformRefresh() {
        return (this.viewer.getControl() != null) && (!this.viewer.getControl().isDisposed());
    }

    protected void performRefresh() {
        this.viewer.refresh();
    }
}


