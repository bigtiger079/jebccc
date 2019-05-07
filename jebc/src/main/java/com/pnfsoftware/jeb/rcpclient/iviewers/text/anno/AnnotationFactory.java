/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*    */ import org.eclipse.swt.graphics.Color;
/*    */ import org.eclipse.swt.graphics.Image;

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
/*    */ public class AnnotationFactory
        /*    */ {
    /*    */   private String type;
    /*    */   private Image image;
    /*    */   private Color hlColor;

    /*    */
    /*    */   AnnotationFactory(String type, Image image, Color hlColor)
    /*    */ {
        /* 27 */
        this.type = type;
        /* 28 */
        this.image = image;
        /* 29 */
        this.hlColor = hlColor;
        /*    */
    }

    /*    */
    /*    */
    public String getType() {
        /* 33 */
        return this.type;
        /*    */
    }

    /*    */
    /*    */
    public Image getImage() {
        /* 37 */
        return this.image;
        /*    */
    }

    /*    */
    /*    */
    public Color getHighlightingColor() {
        /* 41 */
        return this.hlColor;
        /*    */
    }

    /*    */
    /*    */
    public TextAnnotation create(ICoordinates coordinates) {
        /* 45 */
        return new TextAnnotation(this.type, coordinates, 1, this.image);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\AnnotationFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */