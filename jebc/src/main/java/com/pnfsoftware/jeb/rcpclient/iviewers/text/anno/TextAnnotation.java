package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;

public class TextAnnotation extends Annotation {
    public static final String BASETYPE = "com.pnfsoftware.jeb.rcpclient.textAnno";
    protected ICoordinates coordinates;
    protected int layer;
    protected Image image;

    TextAnnotation(String type, ICoordinates coordinates, int layer, Image image) {
        super(type, true, null);
        this.coordinates = coordinates;
        this.layer = layer;
        this.image = image;
    }

    public ICoordinates getCoordinates() {
        return this.coordinates;
    }

    public int getLayer() {
        return this.layer;
    }

    public Image getImage() {
        return this.image;
    }
}


