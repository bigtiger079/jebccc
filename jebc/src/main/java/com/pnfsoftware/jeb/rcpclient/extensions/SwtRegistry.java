package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class SwtRegistry {
    private static final ILogger logger = GlobalLog.getLogger(SwtRegistry.class);
    private static SwtRegistry manager = null;
    protected Display display;

    public static SwtRegistry getInstance() {
        if (manager == null) {
            manager = new SwtRegistry();
        }
        return manager;
    }

    protected Map<RGB, Color> colors = new HashMap();
    protected Map<FontDescriptor, Font> fonts = new HashMap();

    protected SwtRegistry() {
        this(Display.getCurrent());
    }

    protected SwtRegistry(Display display) {
        if (display == null) {
            throw new IllegalArgumentException("Needs a display");
        }
        this.display = display;
    }

    public Display getDisplay() {
        return this.display;
    }

    public Color getColor(RGB rgb) {
        Color color = (Color) this.colors.get(rgb);
        if (color == null) {
            color = new Color(this.display, rgb);
            this.colors.put(rgb, color);
        }
        return color;
    }

    public Color getColor(int rgb) {
        return getColor(new RGB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF));
    }

    public Color getColor(int r, int g, int b) {
        return getColor(new RGB(r, g, b));
    }

    public Color getSystemColor(int id) {
        return this.display.getSystemColor(id);
    }

    public Font getFont(FontDescriptor desc) {
        Font font = (Font) this.fonts.get(desc);
        if (font == null) {
            font = desc.createFont(this.display);
            this.fonts.put(desc, font);
        }
        return font;
    }

    public Font getFont(FontData data) {
        FontDescriptor desc = FontDescriptor.createFrom(data);
        return getFont(desc);
    }

    public Font getFont(FontData[] data) {
        FontDescriptor desc = FontDescriptor.createFrom(data);
        return getFont(desc);
    }

    public Font getFont(String name, int height, int style) {
        return getFont(new FontData(name, height, style));
    }

    public Font getFont(Font basefont, Integer height, Integer style) {
        FontDescriptor desc = FontDescriptor.createFrom(basefont);
        if (height != null) {
            desc = desc.setHeight(height.intValue());
        }
        if (style != null) {
            desc = desc.setStyle(style.intValue());
        }
        return getFont(desc);
    }

    public void dispose() {
        for (Color color : this.colors.values()) {
            color.dispose();
        }
        for (Font font : this.fonts.values()) {
            font.dispose();
        }
    }
}


