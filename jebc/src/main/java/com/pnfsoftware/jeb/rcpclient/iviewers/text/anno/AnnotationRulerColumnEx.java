/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;
/*    */
/*    */

import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import org.eclipse.jface.text.BadLocationException;
/*    */ import org.eclipse.jface.text.IDocument;
/*    */ import org.eclipse.jface.text.source.AnnotationRulerColumn;
/*    */ import org.eclipse.jface.text.source.IAnnotationAccess;
/*    */ import org.eclipse.jface.text.source.IAnnotationModel;
/*    */ import org.eclipse.jface.text.source.SourceViewer;

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
/*    */ public class AnnotationRulerColumnEx
        /*    */ extends AnnotationRulerColumn
        /*    */ {
    /* 25 */   private static final ILogger logger = GlobalLog.getLogger(AnnotationRulerColumnEx.class);
    /*    */   private SourceViewer viewer;

    /*    */
    /*    */
    public AnnotationRulerColumnEx(IAnnotationModel model, int width, IAnnotationAccess annotationAccess)
    /*    */ {
        /* 30 */
        super(model, width, annotationAccess);
        /*    */
    }

    /*    */
    /*    */
    public void setViewer(SourceViewer viewer) {
        /* 34 */
        this.viewer = viewer;
        /*    */
    }

    /*    */
    /*    */
    protected void mouseDoubleClicked(int rulerLine)
    /*    */ {
        /* 39 */
        logger.i("Double clicked on line: " + rulerLine, new Object[0]);
        /*    */
        /*    */
        try
            /*    */ {
            /* 43 */
            offset = this.viewer.getDocument().getLineOffset(rulerLine);
            /*    */
        } catch (BadLocationException e) {
            /*    */
            int offset;
            /* 46 */
            logger.i("Invalid location", new Object[0]);
            return;
            /*    */
        }
        /*    */
        /*    */
        int offset;
        /* 50 */
        logger.i("  offset in widget: " + offset, new Object[0]);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\AnnotationRulerColumnEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */