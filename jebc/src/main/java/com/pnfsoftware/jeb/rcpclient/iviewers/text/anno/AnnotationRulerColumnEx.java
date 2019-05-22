package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;

public class AnnotationRulerColumnEx extends AnnotationRulerColumn {
    private static final ILogger logger = GlobalLog.getLogger(AnnotationRulerColumnEx.class);
    private SourceViewer viewer;

    public AnnotationRulerColumnEx(IAnnotationModel model, int width, IAnnotationAccess annotationAccess) {
        super(model, width, annotationAccess);
    }

    public void setViewer(SourceViewer viewer) {
        this.viewer = viewer;
    }

    protected void mouseDoubleClicked(int rulerLine) {
        logger.i("Double clicked on line: " + rulerLine);
        int offset;
        try {
            offset = this.viewer.getDocument().getLineOffset(rulerLine);
        } catch (BadLocationException e) {
            logger.i("Invalid location");
            return;
        }
        logger.i("  offset in widget: " + offset);
    }
}


