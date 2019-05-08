package com.pnfsoftware.jeb.rcpclient.iviewers.tree;

import com.pnfsoftware.jeb.core.output.tree.INode;
import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

class LazyContentProvider implements ILazyTreeContentProvider {
    private static final ILogger logger = GlobalLog.getLogger(LazyContentProvider.class);
    InteractiveTreeViewer iviewer;
    ITreeDocument idoc;
    TreeViewer viewer;

    public LazyContentProvider(InteractiveTreeViewer iviewer) {
        this.iviewer = iviewer;
        this.viewer = iviewer.getViewer();
    }

    public void dispose() {
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        this.idoc = ((ITreeDocument) newInput);
    }

    public Object getParent(Object e) {
        logger.debug("getParent() e=%s", new Object[]{e});
        return null;
    }

    public void updateElement(Object e, int index) {
        logger.debug("updateElement() e=%s index=%d", new Object[]{e, Integer.valueOf(index)});
        if ((e instanceof ITreeDocument)) {
            INode element = (INode) ((ITreeDocument) e).getRoots().get(index);
            this.viewer.replace(e, index, element);
            this.viewer.setChildCount(element, element.getChildren().size());
        } else if ((e instanceof INode)) {
            INode element = (INode) ((INode) e).getChildren().get(index);
            this.viewer.replace(e, index, element);
            this.viewer.setChildCount(element, element.getChildren().size());
        }
    }

    public void updateChildCount(Object e, int currentChildCount) {
        logger.debug("updateChildCount() e=%s currentChildCount=%d", new Object[]{e, Integer.valueOf(currentChildCount)});
        if ((e instanceof ITreeDocument)) {
            int cnt = ((ITreeDocument) e).getRoots().size();
            if (cnt != currentChildCount) {
                this.viewer.setChildCount(e, cnt);
            }
        } else if ((e instanceof INode)) {
            int cnt = ((INode) e).getChildren().size();
            if (cnt != currentChildCount) {
                this.viewer.setChildCount(e, cnt);
            }
        }
    }
}


