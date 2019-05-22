package com.pnfsoftware.jeb.rcpclient.iviewers;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Style {
    private static final ILogger logger = GlobalLog.getLogger(Style.class);
    StyleManager styleman;
    Color color;
    Color bgcolor;
    boolean bold;
    boolean italic;

    public Style(Style s) {
        this.styleman = s.styleman;
        this.color = s.color;
        this.bgcolor = s.bgcolor;
        this.bold = s.bold;
        this.italic = s.italic;
    }

    public Style(StyleManager styleman, String s) {
        String[] elts = s.split(",");
        if (elts.length != 4) {
            logger.warn(String.format("%s: %s", S.s(403), Formatter.escapeString(s, true)));
            return;
        }
        this.styleman = styleman;
        this.color = parseColor(StyleManager.getDisplay(), elts[0]);
        this.bgcolor = parseColor(StyleManager.getDisplay(), elts[1]);
        this.bold = Boolean.parseBoolean(elts[2]);
        this.italic = Boolean.parseBoolean(elts[3]);
    }

    public Style(StyleManager styleman, Color color, Color bgcolor, boolean bold, boolean italic) {
        this.styleman = styleman;
        this.color = color;
        this.bgcolor = bgcolor;
        this.bold = bold;
        this.italic = italic;
    }

    public Style(StyleManager styleman, Color color, Color bgcolor) {
        this.styleman = styleman;
        this.color = color;
        this.bgcolor = bgcolor;
        this.bold = false;
        this.italic = false;
    }

    public Style(StyleManager styleman, Color color) {
        this.styleman = styleman;
        this.color = color;
        this.bgcolor = null;
        this.bold = false;
        this.italic = false;
    }

    private void notifyChange() {
        if (this.styleman != null) {
            this.styleman.onStyleChanged();
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        notifyChange();
    }

    public Color getBackgroungColor() {
        return this.bgcolor;
    }

    public void setBackgroundColor(Color bgcolor) {
        this.bgcolor = bgcolor;
        notifyChange();
    }

    public boolean isItalic() {
        return this.italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
        notifyChange();
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
        notifyChange();
    }

    public String toString() {
        return String.format("%s,%s,%s,%s", colorToString(this.color), colorToString(this.bgcolor), this.bold, this.italic);
    }

    public static String colorToString(Color color) {
        if (color == null) {
            return "-1";
        }
        return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color parseColor(Display display, String s) {
        int rgb;
        try {
            rgb = Integer.parseInt(s, 16);
        } catch (NumberFormatException e) {
            logger.warn(String.format("%s: %s", S.s(397), Formatter.escapeString(s, true)));
            return null;
        }
        if (rgb < 0) {
            return null;
        }
        return UIAssetManager.getInstance().getColor(rgb);
    }
}


