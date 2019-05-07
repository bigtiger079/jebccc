
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;


import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;


public class DndDropTarget
        extends ViewerDropAdapter {
    private static final ILogger logger = GlobalLog.getLogger(DndDropTarget.class);
    private IDndProvider dndProvider;
    private DndDragSource dragSource;
    boolean allowInsertBeforeOrAfter = false;


    public DndDropTarget(Viewer viewer, IDndProvider dndProvider, DndDragSource dragSource) {

        super(viewer);

        this.dndProvider = dndProvider;

        this.dragSource = dragSource;


        setFeedbackEnabled(false);


        setExpandEnabled(false);

    }


    protected int determineLocation(DropTargetEvent event) {

        int location = super.determineLocation(event);

        if ((!this.allowInsertBeforeOrAfter) && ((location == 1) || (location == 2))) {

            return 3;

        }

        return location;

    }


    public void drop(DropTargetEvent event) {

        int location = determineLocation(event);

        Object target = determineTarget(event);

        if (this.dndProvider.performDrop((String) event.data, target, location)) {

            logger.i("drop from %s to %s (%d)", new Object[]{event.data, target, Integer.valueOf(location)});

            super.drop(event);

        } else {

            logger.i("cannotDrop from %s to %s (%d)", new Object[]{event.data, target, Integer.valueOf(location)});

        }

    }


    public boolean performDrop(Object data) {

        return true;

    }


    public boolean validateDrop(Object target, int operation, TransferData transferType) {

        boolean canDrop = false;

        if ((this.dragSource.dragData != null) && (target != null)) {

            canDrop = this.dndProvider.canDrop(this.dragSource.dragData, target, 3);

        }

        logger.i("validate drop %s %d %s %b", new Object[]{target, Integer.valueOf(operation), transferType, Boolean.valueOf(canDrop)});

        return canDrop;

    }


    public void dragOver(DropTargetEvent event) {

        event.feedback = 0;

        super.dragOver(event);

        Object target = getCurrentTarget();

        boolean canDrop = false;

        if ((this.dragSource.dragData != null) && (target != null)) {

            canDrop = this.dndProvider.canDrop(this.dragSource.dragData, target, 3);

        }

        setFeedbackSelect(event, canDrop ? 3 : 4);

    }


    private void setFeedbackSelect(DropTargetEvent event, int location) {

        switch (location) {

            case 1:

                event.feedback |= 0x2;

                break;

            case 2:

                event.feedback |= 0x4;

                break;

            case 3:

                event.feedback |= 0x1;

                if (this.dndProvider.shouldExpand(this.dragSource.dragData, getCurrentTarget())) {

                    event.feedback |= 0x10;

                }

                break;

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\DndDropTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */