package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IMetadataManager;

public class OverviewBarProperties {
    public static final int defaultPosition = 128;
    public static final int defaultThickness = 18;
    ITextDocument doc;
    IMetadataManager mm;
    int position;
    int thickness;

    public OverviewBarProperties(ITextDocument doc, IMetadataManager mm, int position, int thickness) {
        if (doc == null) {
            throw new IllegalArgumentException();
        }
        if ((position != 128) && (position != 1024) && (position != 16384) && (position != 131072)) {
            position = 128;
        }
        if (thickness <= 0) {
            thickness = 18;
        }
        this.doc = doc;
        this.mm = mm;
        this.position = position;
        this.thickness = thickness;
    }

    public OverviewBarProperties(ITextDocument doc, IMetadataManager mm) {
        this(doc, mm, 128, 18);
    }

    public int getPosition() {
        return this.position;
    }

    public int getThickness() {
        return this.thickness;
    }

    protected static OverviewBarProperties buildOverviewBarProperties(IPropertyManager pm, ITextDocument doc, IMetadataManager mm) {
        int position;
        switch (pm.getInteger(".ui.NavigationBarPosition")) {
            case 1:
                position = 128;
                break;
            case 2:
                position = 131072;
                break;
            case 3:
                position = 1024;
                break;
            case 4:
                position = 16384;
                break;
            default:
                return null;
        }
        int thickness = pm.getInteger(".ui.NavigationBarThickness");
        return new OverviewBarProperties(doc, mm, position, thickness);
    }
}


