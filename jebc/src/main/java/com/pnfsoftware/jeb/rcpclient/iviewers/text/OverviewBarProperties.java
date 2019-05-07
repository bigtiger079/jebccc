/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*    */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*    */ import com.pnfsoftware.jeb.core.units.IMetadataManager;

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
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class OverviewBarProperties
        /*    */ {
    /*    */   public static final int defaultPosition = 128;
    /*    */   public static final int defaultThickness = 18;
    /*    */ ITextDocument doc;
    /*    */ IMetadataManager mm;
    /*    */ int position;
    /*    */ int thickness;

    /*    */
    /*    */
    public OverviewBarProperties(ITextDocument doc, IMetadataManager mm, int position, int thickness)
    /*    */ {
        /* 38 */
        if (doc == null) {
            /* 39 */
            throw new IllegalArgumentException();
            /*    */
        }
        /*    */
        /* 42 */
        if ((position != 128) && (position != 1024) && (position != 16384) && (position != 131072)) {
            /* 43 */
            position = 128;
            /*    */
        }
        /*    */
        /* 46 */
        if (thickness <= 0) {
            /* 47 */
            thickness = 18;
            /*    */
        }
        /*    */
        /* 50 */
        this.doc = doc;
        /* 51 */
        this.mm = mm;
        /* 52 */
        this.position = position;
        /* 53 */
        this.thickness = thickness;
        /*    */
    }

    /*    */
    /*    */
    public OverviewBarProperties(ITextDocument doc, IMetadataManager mm) {
        /* 57 */
        this(doc, mm, 128, 18);
        /*    */
    }

    /*    */
    /*    */
    public int getPosition() {
        /* 61 */
        return this.position;
        /*    */
    }

    /*    */
    /*    */
    public int getThickness() {
        /* 65 */
        return this.thickness;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    protected static OverviewBarProperties buildOverviewBarProperties(IPropertyManager pm, ITextDocument doc, IMetadataManager mm)
    /*    */ {
        /*    */
        int position;
        /*    */
        /*    */
        int position;
        /*    */
        /*    */
        int position;
        /*    */
        /*    */
        int position;
        /*    */
        /* 79 */
        switch (pm.getInteger(".ui.NavigationBarPosition")) {
            /*    */
            case 1:
                /* 81 */
                position = 128;
                /* 82 */
                break;
            /*    */
            case 2:
                /* 84 */
                position = 131072;
                /* 85 */
                break;
            /*    */
            case 3:
                /* 87 */
                position = 1024;
                /* 88 */
                break;
            /*    */
            case 4:
                /* 90 */
                position = 16384;
                /* 91 */
                break;
            /*    */
            default:
                /* 93 */
                return null;
            /*    */
        }
        /*    */
        int position;
        /* 96 */
        int thickness = pm.getInteger(".ui.NavigationBarThickness");
        /*    */
        /* 98 */
        return new OverviewBarProperties(doc, mm, position, thickness);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\OverviewBarProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */