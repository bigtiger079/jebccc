package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class DndDragSource implements DragSourceListener {
    private static final ILogger logger = GlobalLog.getLogger(DndDragSource.class);
    private IDndProvider dndProvider;
    String dragData = null;

    public DndDragSource(IDndProvider dndProvider) {
        this.dndProvider = dndProvider;
    }

    public void dragStart(DragSourceEvent event) {
        Object data = this.dndProvider.getSelectedElements();
        event.doit = this.dndProvider.canDrag(data);
        if (event.doit) {
            this.dragData = this.dndProvider.getDragData();
            if (this.dragData == null) {
                logger.i("Invalid Drag %s", data);
                event.doit = false;
            }
        }
        logger.i("can Drag %b %s", event.doit, data);
    }

    public void dragSetData(DragSourceEvent event) {
        event.data = this.dndProvider.getDragData();
        logger.i("dragSetData %s", event.data);
    }

    public void dragFinished(DragSourceEvent event) {
        logger.i("dragFinished");
    }
}


