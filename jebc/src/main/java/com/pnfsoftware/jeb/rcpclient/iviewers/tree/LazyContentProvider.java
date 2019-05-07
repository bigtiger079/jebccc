/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.tree.INode;
/*    */ import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.util.List;
/*    */ import org.eclipse.jface.viewers.ILazyTreeContentProvider;
/*    */ import org.eclipse.jface.viewers.TreeViewer;
/*    */ import org.eclipse.jface.viewers.Viewer;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ class LazyContentProvider
        /*    */ implements ILazyTreeContentProvider
        /*    */ {
    /* 31 */   private static final ILogger logger = GlobalLog.getLogger(LazyContentProvider.class);
    /*    */ InteractiveTreeViewer iviewer;
    /*    */ ITreeDocument idoc;
    /*    */ TreeViewer viewer;

    /*    */
    /*    */
    public LazyContentProvider(InteractiveTreeViewer iviewer)
    /*    */ {
        /* 38 */
        this.iviewer = iviewer;
        /* 39 */
        this.viewer = iviewer.getViewer();
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void dispose() {
    }

    /*    */
    /*    */
    /*    */
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    /*    */ {
        /* 48 */
        this.idoc = ((ITreeDocument) newInput);
        /*    */
    }

    /*    */
    /*    */
    public Object getParent(Object e)
    /*    */ {
        /* 53 */
        logger.debug("getParent() e=%s", new Object[]{e});
        /* 54 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public void updateElement(Object e, int index)
    /*    */ {
        /* 59 */
        logger.debug("updateElement() e=%s index=%d", new Object[]{e, Integer.valueOf(index)});
        /*    */
        /* 61 */
        if ((e instanceof ITreeDocument)) {
            /* 62 */
            INode element = (INode) ((ITreeDocument) e).getRoots().get(index);
            /* 63 */
            this.viewer.replace(e, index, element);
            /*    */
            /* 65 */
            this.viewer.setChildCount(element, element.getChildren().size());
            /*    */
            /*    */
        }
        /* 68 */
        else if ((e instanceof INode)) {
            /* 69 */
            INode element = (INode) ((INode) e).getChildren().get(index);
            /* 70 */
            this.viewer.replace(e, index, element);
            /*    */
            /* 72 */
            this.viewer.setChildCount(element, element.getChildren().size());
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void updateChildCount(Object e, int currentChildCount)
    /*    */ {
        /* 79 */
        logger.debug("updateChildCount() e=%s currentChildCount=%d", new Object[]{e, Integer.valueOf(currentChildCount)});
        /*    */
        /* 81 */
        if ((e instanceof ITreeDocument)) {
            /* 82 */
            int cnt = ((ITreeDocument) e).getRoots().size();
            /* 83 */
            if (cnt != currentChildCount) {
                /* 84 */
                this.viewer.setChildCount(e, cnt);
                /*    */
            }
            /*    */
        }
        /* 87 */
        else if ((e instanceof INode)) {
            /* 88 */
            int cnt = ((INode) e).getChildren().size();
            /* 89 */
            if (cnt != currentChildCount) {
                /* 90 */
                this.viewer.setChildCount(e, cnt);
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\LazyContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */