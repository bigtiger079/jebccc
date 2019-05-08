
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class AnnotationFactory {
    private String type;
    private Image image;
    private Color hlColor;

    AnnotationFactory(String type, Image image, Color hlColor) {
        this.type = type;
        this.image = image;
        this.hlColor = hlColor;
    }

    public String getType() {
        return this.type;
    }

    public Image getImage() {
        return this.image;
    }

    public Color getHighlightingColor() {
        return this.hlColor;
    }

    public TextAnnotation create(ICoordinates coordinates) {
        return new TextAnnotation(this.type, coordinates, 1, this.image);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\AnnotationFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */