
package com.pnfsoftware.jeb.rcpclient.iviewers;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.events.JC;
import com.pnfsoftware.jeb.client.events.JebClientEvent;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.themes.IThemeChangeListener;
import com.pnfsoftware.jeb.rcpclient.extensions.themes.ThemeManager;
import com.pnfsoftware.jeb.util.events.EventSource;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.format.IAsciiable;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class StyleManager
        extends EventSource
        implements IThemeChangeListener, IAsciiable {
    private static final ILogger logger = GlobalLog.getLogger(StyleManager.class);
    private static Display display;
    private static UIAssetManager aman = UIAssetManager.getInstance();
    private static Color black;
    private static Color white;
    private static Color red;
    private static Color green;
    private static Color darkgreen;
    private static Color blue;
    private static Color yellow;
    private static Color darkgray;
    private static Color darkred;
    private static Color cyan;

    static {
        display = aman.getDisplay();
        black = display.getSystemColor(2);
        white = display.getSystemColor(1);
        red = display.getSystemColor(3);
        green = display.getSystemColor(5);
        darkgreen = display.getSystemColor(6);
        blue = display.getSystemColor(9);
        yellow = display.getSystemColor(7);
        darkgray = display.getSystemColor(16);
        darkred = display.getSystemColor(4);
        cyan = display.getSystemColor(13);
        c_lightblue = aman.getColor(230, 230, 250);
        c_yellow = aman.getColor(255, 255, 128);
        c_greenblue = aman.getColor(63, 127, 95);
        c_orange = aman.getColor(255, 153, 0);
        c_darkorange = aman.getColor(255, 102, 0);
        c_darkgreen = aman.getColor(0, 138, 0);
        c_purple = aman.getColor(127, 0, 85);
        c_darkblue = aman.getColor(0, 0, 85);
        c_lightgray = aman.getColor(192, 192, 192);
        c_bluepurple = aman.getColor(140, 100, 255);
        c_lightgreen = aman.getColor(133, 224, 133);
    }

    private static Color c_lightblue;
    private static Color c_yellow;
    private static Color c_greenblue;
    private static Color c_orange;
    private static Color c_darkorange;
    private static Color c_darkgreen;
    private static Color c_brightgreen = aman.getColor(153, 255, 153);
    private static Color c_purple;
    private static Color c_darkblue;
    private static Color c_lightgray;
    private static Color c_bluepurple;
    private static Color c_lightgreen;
    private static Color c_purple2 = aman.getColor(14493799);
    private static Color c_blue2 = aman.getColor(18, 144, 195);
    private static Color c_darkblue2 = aman.getColor(102, 225, 248);
    private static Color solarized_base03 = aman.getColor(11062);
    private static Color solarized_base02 = aman.getColor(472642);
    private static Color solarized_base01 = aman.getColor(5795445);
    private static Color solarized_base00 = aman.getColor(6650755);
    private static Color solarized_base0 = aman.getColor(8623254);
    private static Color solarized_base1 = aman.getColor(9675169);
    private static Color solarized_base2 = aman.getColor(15657173);
    private static Color solarized_base3 = aman.getColor(16643811);
    private static Color solarized_yellow = aman.getColor(11897088);
    private static Color solarized_orange = aman.getColor(13323030);
    private static Color solarized_red = aman.getColor(14430767);
    private static Color solarized_magenta = aman.getColor(13842050);
    private static Color solarized_violet = aman.getColor(7107012);
    private static Color solarized_blue = aman.getColor(2526162);
    private static Color solarized_cyan = aman.getColor(2793880);
    private static Color solarized_green = aman.getColor(8755456);
    public static final String SCHEME_DEFAULT = "default";
    public static final String SCHEME_DARK = "dark";
    private ThemeManager themeManager;

    static class ColorScheme {
        Style defstyle;
        Style defastyle;
        Map<ItemClassIdentifiers, Style> styles;
        Map<ItemClassIdentifiers, Style> astyles;
        Color defaultActiveLineColor;
        Color defaultFontColor;
        Color defaultActiveBgcolor;

        ColorScheme() {
            this.styles = new HashMap();
            this.astyles = new HashMap();
        }

        ColorScheme(ColorScheme s) {
            this.defstyle = s.defstyle;
            this.defastyle = s.defastyle;
            this.styles = new HashMap(s.styles);
            this.astyles = new HashMap(s.astyles);
            this.defaultActiveLineColor = s.defaultActiveLineColor;
            this.defaultFontColor = s.defaultFontColor;
            this.defaultActiveBgcolor = s.defaultActiveBgcolor;
        }
    }

    private Map<String, ColorScheme> schemes = new HashMap();
    private String activeSchemeName = "default";

    public StyleManager(String styleData, ThemeManager themeManager) {
        this.themeManager = themeManager;
        if (themeManager != null) {
            themeManager.addThemeChangeListener(this);
        }
        resetDefaults(false);
        if ((styleData != null) && (!styleData.isEmpty()) && (!styleData.equals("<default>"))) {
            decode(styleData);
        }
    }

    public StyleManager clone() {
        StyleManager dst = new StyleManager();
        dst.themeManager = this.themeManager;
        dst.schemes = new HashMap(this.schemes.size());
        for (Map.Entry<String, ColorScheme> e : this.schemes.entrySet()) {
            dst.schemes.put(e.getKey(), new ColorScheme((ColorScheme) e.getValue()));
        }
        return dst;
    }

    public void restore(StyleManager styleman, boolean notify) {
        this.themeManager = styleman.themeManager;
        this.schemes = new HashMap(styleman.schemes.size());
        for (Map.Entry<String, ColorScheme> e : styleman.schemes.entrySet()) {
            this.schemes.put(e.getKey(), new ColorScheme((ColorScheme) e.getValue()));
        }
        if (notify) {
            onStyleChanged();
        }
    }

    public static Display getDisplay() {
        return display;
    }

    public void onThemeChange(String themeId) {
        if (themeId == null) {
            updateScheme("default");
        } else if (themeId.equals("theme.dark")) {
            updateScheme("dark");
        } else {
            updateScheme("default");
        }
    }

    private boolean updateScheme(String name) {
        if (name == null) {
            name = "default";
        } else if ((!name.equals("default")) && (!name.equals("dark"))) {
            return false;
        }
        if (Strings.equals(name, this.activeSchemeName)) {
            return false;
        }
        ColorScheme scheme = getScheme(name);
        this.activeSchemeName = name;
        return true;
    }

    public List<String> getSchemeNames() {
        return Arrays.asList(new String[]{"default", "dark"});
    }

    private ColorScheme getScheme(String name) {
        ColorScheme themeStyles = (ColorScheme) this.schemes.get(name);
        if (themeStyles == null) {
            themeStyles = new ColorScheme();
            this.schemes.put(name, themeStyles);
        }
        return themeStyles;
    }

    private ColorScheme getActiveScheme() {
        return getScheme(this.activeSchemeName);
    }

    public void resetDefaults(boolean notify) {
        for (String t : getSchemeNames()) {
            ColorScheme ts = getScheme(t);
            ts.defstyle = null;
            ts.defastyle = null;
            ts.styles = new HashMap();
            ts.astyles = new HashMap();
            ts.defaultActiveLineColor = null;
            ts.defaultFontColor = null;
            ts.defaultActiveBgcolor = null;
            if (t.equals("default")) {
                ts.defaultActiveLineColor = aman.getColor(230, 230, 250);
                ts.defaultFontColor = black;
                ts.defaultActiveBgcolor = c_yellow;
                ts.defstyle = new Style(this, ts.defaultFontColor);
                ts.defastyle = new Style(this, ts.defaultFontColor, ts.defaultActiveBgcolor);
                add(ts, ItemClassIdentifiers.DEFAULT, ts.defstyle, ts.defastyle);
                add(ts, ItemClassIdentifiers.ARTIFACT, new Style(this, blue));
                add(ts, ItemClassIdentifiers.INFO_USELESS, new Style(this, white, c_lightgray));
                add(ts, ItemClassIdentifiers.INFO_DEPRECATED, new Style(this, black, c_lightgray));
                add(ts, ItemClassIdentifiers.INFO_DEBUG, new Style(this, black, c_lightblue));
                add(ts, ItemClassIdentifiers.INFO_NORMAL, new Style(this, white, blue));
                add(ts, ItemClassIdentifiers.INFO_WARNING, new Style(this, white, c_darkorange));
                add(ts, ItemClassIdentifiers.INFO_ERROR, new Style(this, white, darkred));
                add(ts, ItemClassIdentifiers.INFO_DANGEROUS, new Style(this, c_yellow, red));
                add(ts, ItemClassIdentifiers.INFO_MALFORMED, new Style(this, white, black));
                add(ts, ItemClassIdentifiers.INFO_CORRUPT, new Style(this, white, black));
                add(ts, ItemClassIdentifiers.RESULT_SUCCESS, new Style(this, black, c_brightgreen));
                add(ts, ItemClassIdentifiers.RESULT_ERROR, new Style(this, black, c_orange));
                add(ts, ItemClassIdentifiers.TYPE_BYTE, null);
                add(ts, ItemClassIdentifiers.TYPE_SHORT, null);
                add(ts, ItemClassIdentifiers.TYPE_INTEGER, null);
                add(ts, ItemClassIdentifiers.TYPE_LONG, null);
                add(ts, ItemClassIdentifiers.TYPE_FLOAT, null);
                add(ts, ItemClassIdentifiers.TYPE_DOUBLE, null);
                add(ts, ItemClassIdentifiers.COMMENT, new Style(this, c_greenblue), new Style(this, c_greenblue));
                add(ts, ItemClassIdentifiers.ADDRESS, new Style(this, darkgray));
                add(ts, ItemClassIdentifiers.ADDRESS_SLACK, new Style(this, c_lightgray));
                add(ts, ItemClassIdentifiers.BYTECODE, new Style(this, darkgray), new Style(this, darkgray));
                add(ts, ItemClassIdentifiers.DIRECTIVE, new Style(this, c_purple, null, true, false));
                add(ts, ItemClassIdentifiers.KEYWORD, new Style(this, c_purple, null, true, false));
                add(ts, ItemClassIdentifiers.MNEMONIC, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.OPCODE, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.LABEL, new Style(this, darkgray));
                add(ts, ItemClassIdentifiers.LABEL_ALTERNATE, new Style(this, c_lightgray));
                add(ts, ItemClassIdentifiers.LABEL_OOR, new Style(this, darkgray, aman.getColor(15374745)));
                add(ts, ItemClassIdentifiers.PARAMETER, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.IDENTIFIER, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.NUMBER, new Style(this, c_darkgreen, null, true, false));
                add(ts, ItemClassIdentifiers.CHARACTER, new Style(this, c_darkorange, null, true, false));
                add(ts, ItemClassIdentifiers.STRING, new Style(this, c_darkorange, null, true, false));
                add(ts, ItemClassIdentifiers.CLASS_NAME, new Style(this, blue));
                add(ts, ItemClassIdentifiers.FIELD_NAME, new Style(this, c_darkblue, null, false, true));
                add(ts, ItemClassIdentifiers.METHOD_NAME, new Style(this, c_darkblue));
                add(ts, ItemClassIdentifiers.EXTERNAL_CLASS_NAME, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.EXTERNAL_FIELD_NAME, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.EXTERNAL_METHOD_NAME, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.PACKAGE_NAME, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.REGISTER, new Style(this, ts.defaultFontColor), new Style(this, black, c_lightgreen));
                add(ts, ItemClassIdentifiers.IMMEDIATE, new Style(this, darkgreen, null, true, false));
                add(ts, ItemClassIdentifiers.CODE_UNKNOWN, new Style(this, c_lightgray));
                add(ts, ItemClassIdentifiers.CODE_INSTRUCTIONS, new Style(this, blue));
                add(ts, ItemClassIdentifiers.CODE_ROUTINE, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.CODE_DATA, new Style(this, c_darkgreen));
                add(ts, ItemClassIdentifiers.CODE_LIBRARY, new Style(this, cyan));
                add(ts, ItemClassIdentifiers.CODE_METADATA, new Style(this, yellow));
                add(ts, ItemClassIdentifiers.CODE_SLACK, new Style(this, black));
                add(ts, ItemClassIdentifiers.MARKUP_ELEMENT, new Style(this, blue));
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_NAME, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_VALUE, new Style(this, c_darkorange));
                add(ts, ItemClassIdentifiers.MARKUP_TEXT, new Style(this, c_greenblue));
                add(ts, ItemClassIdentifiers.MARKUP_COMMENT, new Style(this, c_greenblue));
            } else if (t.equalsIgnoreCase("dark")) {
                ts.defaultActiveLineColor = solarized_base02;
                ts.defaultFontColor = solarized_base2;
                ts.defaultActiveBgcolor = solarized_base03;
                ts.defstyle = new Style(this, ts.defaultFontColor);
                ts.defastyle = new Style(this, ts.defaultFontColor, ts.defaultActiveBgcolor);
                add(ts, ItemClassIdentifiers.DEFAULT, ts.defstyle, ts.defastyle);
                add(ts, ItemClassIdentifiers.ARTIFACT, new Style(this, c_blue2));
                add(ts, ItemClassIdentifiers.INFO_USELESS, new Style(this, white, c_lightgray));
                add(ts, ItemClassIdentifiers.INFO_DEPRECATED, new Style(this, ts.defaultFontColor, c_lightgray));
                add(ts, ItemClassIdentifiers.INFO_DEBUG, new Style(this, ts.defaultFontColor, c_lightblue));
                add(ts, ItemClassIdentifiers.INFO_NORMAL, new Style(this, white, c_blue2));
                add(ts, ItemClassIdentifiers.INFO_WARNING, new Style(this, white, c_darkorange));
                add(ts, ItemClassIdentifiers.INFO_ERROR, new Style(this, white, darkred));
                add(ts, ItemClassIdentifiers.INFO_DANGEROUS, new Style(this, c_yellow, red));
                add(ts, ItemClassIdentifiers.INFO_MALFORMED, new Style(this, white, black));
                add(ts, ItemClassIdentifiers.INFO_CORRUPT, new Style(this, white, black));
                add(ts, ItemClassIdentifiers.RESULT_SUCCESS, new Style(this, white, c_darkgreen));
                add(ts, ItemClassIdentifiers.RESULT_ERROR, new Style(this, white, c_darkorange));
                add(ts, ItemClassIdentifiers.TYPE_BYTE, null);
                add(ts, ItemClassIdentifiers.TYPE_SHORT, null);
                add(ts, ItemClassIdentifiers.TYPE_INTEGER, null);
                add(ts, ItemClassIdentifiers.TYPE_LONG, null);
                add(ts, ItemClassIdentifiers.TYPE_FLOAT, null);
                add(ts, ItemClassIdentifiers.TYPE_DOUBLE, null);
                add(ts, ItemClassIdentifiers.ADDRESS, new Style(this, solarized_base01));
                add(ts, ItemClassIdentifiers.ADDRESS_SLACK, new Style(this, c_lightgray));
                add(ts, ItemClassIdentifiers.COMMENT, new Style(this, darkgray));
                add(ts, ItemClassIdentifiers.BYTECODE, new Style(this, darkgray), new Style(this, darkgray));
                add(ts, ItemClassIdentifiers.DIRECTIVE, new Style(this, c_purple2, null, true, false));
                add(ts, ItemClassIdentifiers.KEYWORD, new Style(this, solarized_magenta, null, false, true));
                add(ts, ItemClassIdentifiers.MNEMONIC, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.OPCODE, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.LABEL, new Style(this, solarized_green, null, false, true));
                add(ts, ItemClassIdentifiers.LABEL_ALTERNATE, new Style(this, c_lightgray));
                add(ts, ItemClassIdentifiers.LABEL_OOR, new Style(this, darkgray, aman.getColor(15374745)));
                add(ts, ItemClassIdentifiers.PARAMETER, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.IDENTIFIER, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.NUMBER, new Style(this, solarized_yellow, null, false, false));
                add(ts, ItemClassIdentifiers.CHARACTER, new Style(this, c_darkorange, null, true, false));
                add(ts, ItemClassIdentifiers.STRING, new Style(this, c_darkorange, null, true, false));
                add(ts, ItemClassIdentifiers.CLASS_NAME, new Style(this, solarized_base2, null, false, false));
                add(ts, ItemClassIdentifiers.FIELD_NAME, new Style(this, solarized_violet, null, false, false));
                add(ts, ItemClassIdentifiers.METHOD_NAME, new Style(this, solarized_green, null, false, false));
                add(ts, ItemClassIdentifiers.EXTERNAL_CLASS_NAME, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.EXTERNAL_FIELD_NAME, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.EXTERNAL_METHOD_NAME, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.PACKAGE_NAME, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.REGISTER, new Style(this, solarized_base2), new Style(this, ts.defaultFontColor, solarized_blue));
                add(ts, ItemClassIdentifiers.IMMEDIATE, new Style(this, solarized_yellow, null, false, false));
                add(ts, ItemClassIdentifiers.CODE_UNKNOWN, new Style(this, solarized_base2));
                add(ts, ItemClassIdentifiers.CODE_INSTRUCTIONS, new Style(this, solarized_cyan));
                add(ts, ItemClassIdentifiers.CODE_ROUTINE, new Style(this, c_bluepurple));
                add(ts, ItemClassIdentifiers.CODE_DATA, new Style(this, solarized_green));
                add(ts, ItemClassIdentifiers.CODE_LIBRARY, new Style(this, cyan));
                add(ts, ItemClassIdentifiers.CODE_METADATA, new Style(this, yellow));
                add(ts, ItemClassIdentifiers.CODE_SLACK, new Style(this, solarized_base02));
                add(ts, ItemClassIdentifiers.MARKUP_ELEMENT, new Style(this, c_blue2));
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_NAME, new Style(this, ts.defaultFontColor));
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_VALUE, new Style(this, c_darkorange));
                add(ts, ItemClassIdentifiers.MARKUP_TEXT, new Style(this, c_greenblue));
                add(ts, ItemClassIdentifiers.MARKUP_COMMENT, new Style(this, c_greenblue));
            }
        }
        if (notify) {
            onStyleChanged();
        }
    }

    void onStyleChanged() {
        notifyListeners(new JebClientEvent(JC.ItemStyleChanged));
    }

    private void add(ColorScheme currentThemeStyles, ItemClassIdentifiers classId, Style style, Style activeStyle) {
        currentThemeStyles.styles.put(classId, style);
        currentThemeStyles.astyles.put(classId, activeStyle);
    }

    private void add(ColorScheme currentThemeStyles, ItemClassIdentifiers classId, Style style) {
        Style activeStyle = null;
        if (style != null) {
            activeStyle = new Style(style);
            if (activeStyle.getBackgroungColor() == null) {
                activeStyle.bgcolor = currentThemeStyles.defaultActiveBgcolor;
            }
            if (!activeStyle.bold) {
                activeStyle.bold = style.bold;
            }
            if (!activeStyle.italic) {
                activeStyle.italic = style.italic;
            }
        }
        add(currentThemeStyles, classId, style, activeStyle);
    }

    public Style getStyle(ColorScheme scheme, ItemClassIdentifiers classId, boolean active) {
        return active ? getActiveStyle(scheme, classId) : getNormalStyle(scheme, classId);
    }

    public Style getNormalStyle(ColorScheme scheme, ItemClassIdentifiers classId) {
        Style r = (Style) scheme.styles.get(classId);
        if (r == null) {
            r = scheme.defstyle;
        }
        return r;
    }

    public Style getActiveStyle(ColorScheme scheme, ItemClassIdentifiers classId) {
        Style r = (Style) scheme.astyles.get(classId);
        if (r == null) {
            r = scheme.defastyle;
        }
        return r;
    }

    public Style getStyle(ItemClassIdentifiers classId, boolean active) {
        return getStyle(getActiveScheme(), classId, active);
    }

    public Style getNormalStyle(ItemClassIdentifiers classId) {
        return getNormalStyle(getActiveScheme(), classId);
    }

    public Style getActiveStyle(ItemClassIdentifiers classId) {
        return getActiveStyle(getActiveScheme(), classId);
    }

    public Color getOnCaretBackground() {
        return getActiveScheme().defaultActiveLineColor;
    }

    private void decode(String s) {
        Map<String, String> map = Strings.decodeMap(s);
        for (String themeName : map.keySet()) {
            String themeStylesData = (String) map.get(themeName);
            ColorScheme r = decodeScheme(themeStylesData);
            this.schemes.put(themeName, r);
        }
    }

    private ColorScheme decodeScheme(String s) {
        ColorScheme themeStyles = new ColorScheme();
        for (String item : s.split("\\|")) {
            String[] tmp = item.split("=");
            if (tmp.length != 2) {
                logger.warn("%s: %s", new Object[]{S.s(403), Formatter.escapeString(item, true)});
            } else if (tmp[0].equals("CURRENT_LINE_BGCOLOR")) {
                themeStyles.defaultActiveLineColor = Style.parseColor(display, tmp[1]);
            } else if (tmp[0].equals("DEFAULT_FONT_COLOR")) {
                themeStyles.defaultFontColor = Style.parseColor(display, tmp[1]);
            } else if (tmp[0].equals("DEFAULT_ACTIVE_BGCOLOR")) {
                themeStyles.defaultActiveBgcolor = Style.parseColor(display, tmp[1]);
            } else {
                ItemClassIdentifiers t = null;
                try {
                    t = ItemClassIdentifiers.valueOf(tmp[0]);
                } catch (Exception e) {
                    logger.warn("%s: %s", new Object[]{S.s(404), Formatter.escapeString(tmp[0], true)});
                    continue;
                }
                String[] data = tmp[1].split(";");
                if (data.length != 2) {
                    logger.warn("%s: %s", new Object[]{S.s(403), Formatter.escapeString(tmp[1], true)});
                } else {
                    themeStyles.styles.put(t, new Style(this, data[0]));
                    themeStyles.astyles.put(t, new Style(this, data[1]));
                }
            }
        }
        themeStyles.defstyle = ((Style) themeStyles.styles.get(ItemClassIdentifiers.DEFAULT));
        if (themeStyles.defstyle == null) {
            themeStyles.defstyle = new Style(this, display.getSystemColor(2));
            themeStyles.styles.put(ItemClassIdentifiers.DEFAULT, themeStyles.defstyle);
        }
        themeStyles.defastyle = ((Style) themeStyles.astyles.get(ItemClassIdentifiers.DEFAULT));
        if (themeStyles.defastyle == null) {
            themeStyles.defastyle = new Style(this, display.getSystemColor(2));
            themeStyles.astyles.put(ItemClassIdentifiers.DEFAULT, themeStyles.defastyle);
        }
        return themeStyles;
    }

    public String encode() {
        Map<String, String> map = new LinkedHashMap();
        for (Map.Entry<String, ColorScheme> e : this.schemes.entrySet()) {
            ColorScheme themeStyles = (ColorScheme) e.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append(
                    String.format("CURRENT_LINE_BGCOLOR=%s|", new Object[]{Style.colorToString(themeStyles.defaultActiveLineColor)}));
            sb.append(String.format("DEFAULT_FONT_COLOR=%s|", new Object[]{Style.colorToString(themeStyles.defaultFontColor)}));
            sb.append(
                    String.format("DEFAULT_ACTIVE_BGCOLOR=%s|", new Object[]{Style.colorToString(themeStyles.defaultActiveBgcolor)}));
            for (ItemClassIdentifiers t : ItemClassIdentifiers.values()) {
                sb.append(
                        String.format("%s=%s;%s|", new Object[]{t, getNormalStyle(themeStyles, t), getActiveStyle(themeStyles, t)}));
            }
            map.put(e.getKey(), sb.toString());
        }
        return Strings.encodeMap(map);
    }

    private StyleManager() {
    }
}


