/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.util.format.Formatter;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.widgets.Display;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class Style
        /*     */ {
    /*  27 */   private static final ILogger logger = GlobalLog.getLogger(Style.class);
    /*     */
    /*     */ StyleManager styleman;
    /*     */
    /*     */ Color color;
    /*     */
    /*     */ Color bgcolor;
    /*     */
    /*     */ boolean bold;
    /*     */
    /*     */ boolean italic;

    /*     */
    /*     */
    public Style(Style s)
    /*     */ {
        /*  41 */
        this.styleman = s.styleman;
        /*  42 */
        this.color = s.color;
        /*  43 */
        this.bgcolor = s.bgcolor;
        /*  44 */
        this.bold = s.bold;
        /*  45 */
        this.italic = s.italic;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public Style(StyleManager styleman, String s)
    /*     */ {
        /*  55 */
        String[] elts = s.split(",");
        /*  56 */
        if (elts.length != 4) {
            /*  57 */
            logger.warn(String.format("%s: %s", new Object[]{S.s(403), Formatter.escapeString(s, true)}), new Object[0]);
            /*  58 */
            return;
            /*     */
        }
        /*     */
        /*  61 */
        this.styleman = styleman;
        /*  62 */
        this.color = parseColor(StyleManager.getDisplay(), elts[0]);
        /*  63 */
        this.bgcolor = parseColor(StyleManager.getDisplay(), elts[1]);
        /*  64 */
        this.bold = Boolean.parseBoolean(elts[2]);
        /*  65 */
        this.italic = Boolean.parseBoolean(elts[3]);
        /*     */
    }

    /*     */
    /*     */
    public Style(StyleManager styleman, Color color, Color bgcolor, boolean bold, boolean italic) {
        /*  69 */
        this.styleman = styleman;
        /*  70 */
        this.color = color;
        /*  71 */
        this.bgcolor = bgcolor;
        /*  72 */
        this.bold = bold;
        /*  73 */
        this.italic = italic;
        /*     */
    }

    /*     */
    /*     */
    public Style(StyleManager styleman, Color color, Color bgcolor) {
        /*  77 */
        this.styleman = styleman;
        /*  78 */
        this.color = color;
        /*  79 */
        this.bgcolor = bgcolor;
        /*  80 */
        this.bold = false;
        /*  81 */
        this.italic = false;
        /*     */
    }

    /*     */
    /*     */
    public Style(StyleManager styleman, Color color) {
        /*  85 */
        this.styleman = styleman;
        /*  86 */
        this.color = color;
        /*  87 */
        this.bgcolor = null;
        /*  88 */
        this.bold = false;
        /*  89 */
        this.italic = false;
        /*     */
    }

    /*     */
    /*     */
    private void notifyChange() {
        /*  93 */
        if (this.styleman != null) {
            /*  94 */
            this.styleman.onStyleChanged();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public Color getColor() {
        /*  99 */
        return this.color;
        /*     */
    }

    /*     */
    /*     */
    public void setColor(Color color) {
        /* 103 */
        this.color = color;
        /* 104 */
        notifyChange();
        /*     */
    }

    /*     */
    /*     */
    public Color getBackgroungColor() {
        /* 108 */
        return this.bgcolor;
        /*     */
    }

    /*     */
    /*     */
    public void setBackgroundColor(Color bgcolor) {
        /* 112 */
        this.bgcolor = bgcolor;
        /* 113 */
        notifyChange();
        /*     */
    }

    /*     */
    /*     */
    public boolean isItalic() {
        /* 117 */
        return this.italic;
        /*     */
    }

    /*     */
    /*     */
    public void setItalic(boolean italic) {
        /* 121 */
        this.italic = italic;
        /* 122 */
        notifyChange();
        /*     */
    }

    /*     */
    /*     */
    public boolean isBold() {
        /* 126 */
        return this.bold;
        /*     */
    }

    /*     */
    /*     */
    public void setBold(boolean bold) {
        /* 130 */
        this.bold = bold;
        /* 131 */
        notifyChange();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public String toString()
    /*     */ {
        /* 139 */
        return String.format("%s,%s,%s,%s", new Object[]{colorToString(this.color), colorToString(this.bgcolor), Boolean.valueOf(this.bold), Boolean.valueOf(this.italic)});
        /*     */
    }

    /*     */
    /*     */
    public static String colorToString(Color color) {
        /* 143 */
        if (color == null) {
            /* 144 */
            return "-1";
            /*     */
        }
        /* 146 */
        return String.format("%02X%02X%02X", new Object[]{Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())});
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static Color parseColor(Display display, String s)
    /*     */ {
        /*     */
        try
            /*     */ {
            /* 159 */
            rgb = Integer.parseInt(s, 16);
            /*     */
        } catch (NumberFormatException e) {
            /*     */
            int rgb;
            /* 162 */
            logger.warn(String.format("%s: %s", new Object[]{S.s(397), Formatter.escapeString(s, true)}), new Object[0]);
            /* 163 */
            return null;
            /*     */
        }
        /*     */
        int rgb;
        /* 166 */
        if (rgb < 0) {
            /* 167 */
            return null;
            /*     */
        }
        /* 169 */
        return UIAssetManager.getInstance().getColor(rgb);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\Style.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */