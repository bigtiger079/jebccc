
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;

import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationAccessExtension2;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class StandardAnnotationAccess
        implements IAnnotationAccess, IAnnotationAccessExtension, IAnnotationAccessExtension2 {
    public Object getType(Annotation annotation) {
        return annotation.getType();
    }

    public boolean isMultiLine(Annotation annotation) {
        return false;
    }

    public boolean isTemporary(Annotation annotation) {
        return !annotation.isPersistent();
    }

    public String getTypeLabel(Annotation annotation) {
        return annotation.getType().toString();
    }

    public int getLayer(Annotation annotation) {
        if ((annotation instanceof TextAnnotation)) {
            ((TextAnnotation) annotation).getLayer();
        }
        return 0;
    }

    public void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds) {
        if ((annotation instanceof TextAnnotation)) {
            Image img = ((TextAnnotation) annotation).getImage();
            if (img != null) {
                ImageUtilities.drawImage(((TextAnnotation) annotation).getImage(), gc, canvas, bounds, 16777216, 128);
            }
        }
    }

    public boolean isPaintable(Annotation annotation) {
        if ((annotation instanceof TextAnnotation)) {
            return ((TextAnnotation) annotation).getImage() != null;
        }
        return false;
    }

    public boolean isSubtype(Object annotationType, Object potentialSupertype) {
        String sub = Strings.toString(annotationType);
        String sup = Strings.toString(potentialSupertype);
        return sub.startsWith(sup + ".");
    }

    public Object[] getSupertypes(Object annotationType) {
        String s = Strings.toString(annotationType);
        int pos = s.lastIndexOf('.');
        if (pos < 0) {
            return new Object[0];
        }
        return new Object[]{s.substring(0, pos)};
    }

    public void setQuickAssistAssistant(IQuickAssistAssistant assistant) {
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\StandardAnnotationAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */