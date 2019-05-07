/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;
/*    */
/*    */

import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
/*    */ import org.eclipse.jface.text.source.Annotation;
/*    */ import org.eclipse.jface.text.source.IAnnotationAccess;
/*    */ import org.eclipse.jface.text.source.IAnnotationAccessExtension;
/*    */ import org.eclipse.jface.text.source.IAnnotationAccessExtension2;
/*    */ import org.eclipse.jface.text.source.ImageUtilities;
/*    */ import org.eclipse.swt.graphics.GC;
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ import org.eclipse.swt.graphics.Rectangle;
/*    */ import org.eclipse.swt.widgets.Canvas;

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
/*    */ public class StandardAnnotationAccess
        /*    */ implements IAnnotationAccess, IAnnotationAccessExtension, IAnnotationAccessExtension2
        /*    */ {
    /*    */
    public Object getType(Annotation annotation)
    /*    */ {
        /* 34 */
        return annotation.getType();
        /*    */
    }

    /*    */
    /*    */
    public boolean isMultiLine(Annotation annotation)
    /*    */ {
        /* 39 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public boolean isTemporary(Annotation annotation)
    /*    */ {
        /* 44 */
        return !annotation.isPersistent();
        /*    */
    }

    /*    */
    /*    */
    public String getTypeLabel(Annotation annotation)
    /*    */ {
        /* 49 */
        return annotation.getType().toString();
        /*    */
    }

    /*    */
    /*    */
    public int getLayer(Annotation annotation)
    /*    */ {
        /* 54 */
        if ((annotation instanceof TextAnnotation)) {
            /* 55 */
            ((TextAnnotation) annotation).getLayer();
            /*    */
        }
        /* 57 */
        return 0;
        /*    */
    }

    /*    */
    /*    */
    public void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds)
    /*    */ {
        /* 62 */
        if ((annotation instanceof TextAnnotation)) {
            /* 63 */
            Image img = ((TextAnnotation) annotation).getImage();
            /* 64 */
            if (img != null) {
                /* 65 */
                ImageUtilities.drawImage(((TextAnnotation) annotation).getImage(), gc, canvas, bounds, 16777216, 128);
                /*    */
            }
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public boolean isPaintable(Annotation annotation)
    /*    */ {
        /* 73 */
        if ((annotation instanceof TextAnnotation)) {
            /* 74 */
            return ((TextAnnotation) annotation).getImage() != null;
            /*    */
        }
        /* 76 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public boolean isSubtype(Object annotationType, Object potentialSupertype)
    /*    */ {
        /* 81 */
        String sub = Strings.toString(annotationType);
        /* 82 */
        String sup = Strings.toString(potentialSupertype);
        /* 83 */
        return sub.startsWith(sup + ".");
        /*    */
    }

    /*    */
    /*    */
    public Object[] getSupertypes(Object annotationType)
    /*    */ {
        /* 88 */
        String s = Strings.toString(annotationType);
        /* 89 */
        int pos = s.lastIndexOf('.');
        /* 90 */
        if (pos < 0) {
            /* 91 */
            return new Object[0];
            /*    */
        }
        /* 93 */
        return new Object[]{s.substring(0, pos)};
        /*    */
    }

    /*    */
    /*    */
    public void setQuickAssistAssistant(IQuickAssistAssistant assistant) {
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\StandardAnnotationAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */