
package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.rcpclient.extensions.controls.ImageView;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class ImageViewer
        extends Viewer {
    private static final ILogger logger = GlobalLog.getLogger(ImageViewer.class);
    private IImageDocument input;
    private IEventListener inputListener;
    private Composite container;
    private ImageView widget;
    private ViewerRefresher refresher = null;

    public ImageViewer(Composite parent) {
        this.container = parent;
    }

    public ImageView getControl() {
        return this.widget;
    }

    public IImageDocument getInput() {
        return this.input;
    }

    public void refresh() {
        if (this.widget != null) {
            this.widget.dispose();
            this.widget = null;
        }
        if (this.input != null) {
            this.widget = new ImageView(this.container, this.input.getImage());
            this.container.layout();
        }
    }

    public void setInput(Object input) {
        if (this.inputListener != null) {
            this.input.removeListener(this.inputListener);
            this.inputListener = null;
        }
        if (this.refresher == null) {
            this.refresher = new ViewerRefresher(this.container.getDisplay(), this);
        }
        this.input = ((IImageDocument) input);
        this.input.addListener(this.inputListener = new IEventListener() {
            public void onEvent(IEvent e) {
                ImageViewer.logger.i("Event received: %s", new Object[]{e});
                ImageViewer.this.refresher.request();
            }
        });
        refresh();
    }

    public void setSelection(ISelection selection, boolean reveal) {
    }

    public ISelection getSelection() {
        return null;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\ImageViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */