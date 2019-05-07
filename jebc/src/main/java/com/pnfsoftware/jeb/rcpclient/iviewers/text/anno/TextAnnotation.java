/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*    */ import org.eclipse.jface.text.source.Annotation;
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
/*    */ public class TextAnnotation
        /*    */ extends Annotation
        /*    */ {
    /*    */   public static final String BASETYPE = "com.pnfsoftware.jeb.rcpclient.textAnno";
    /*    */   protected ICoordinates coordinates;
    /*    */   protected int layer;
    /*    */   protected Image image;

    /*    */
    /*    */   TextAnnotation(String type, ICoordinates coordinates, int layer, Image image)
    /*    */ {
        /* 28 */
        super(type, true, null);
        /* 29 */
        this.coordinates = coordinates;
        /* 30 */
        this.layer = layer;
        /* 31 */
        this.image = image;
        /*    */
    }

    /*    */
    /*    */
    public ICoordinates getCoordinates() {
        /* 35 */
        return this.coordinates;
        /*    */
    }

    /*    */
    /*    */
    public int getLayer() {
        /* 39 */
        return this.layer;
        /*    */
    }

    /*    */
    /*    */
    public Image getImage() {
        /* 43 */
        return this.image;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\TextAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */