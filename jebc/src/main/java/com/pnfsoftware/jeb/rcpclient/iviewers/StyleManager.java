/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.client.events.JC;
/*     */ import com.pnfsoftware.jeb.client.events.JebClientEvent;
/*     */ import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.themes.IThemeChangeListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.themes.ThemeManager;
/*     */ import com.pnfsoftware.jeb.util.events.EventSource;
/*     */ import com.pnfsoftware.jeb.util.format.Formatter;
/*     */ import com.pnfsoftware.jeb.util.format.IAsciiable;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ public class StyleManager
        /*     */ extends EventSource
        /*     */ implements IThemeChangeListener, IAsciiable
        /*     */ {
    /*  43 */   private static final ILogger logger = GlobalLog.getLogger(StyleManager.class);
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   private static Display display;
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*  57 */   private static UIAssetManager aman = UIAssetManager.getInstance();
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

    /*  58 */   static {
        display = aman.getDisplay();
        /*     */
        /*     */
        /*  61 */
        black = display.getSystemColor(2);
        /*  62 */
        white = display.getSystemColor(1);
        /*  63 */
        red = display.getSystemColor(3);
        /*  64 */
        green = display.getSystemColor(5);
        /*  65 */
        darkgreen = display.getSystemColor(6);
        /*  66 */
        blue = display.getSystemColor(9);
        /*  67 */
        yellow = display.getSystemColor(7);
        /*  68 */
        darkgray = display.getSystemColor(16);
        /*  69 */
        darkred = display.getSystemColor(4);
        /*  70 */
        cyan = display.getSystemColor(13);
        /*     */
        /*     */
        /*  73 */
        c_lightblue = aman.getColor(230, 230, 250);
        /*  74 */
        c_yellow = aman.getColor(255, 255, 128);
        /*  75 */
        c_greenblue = aman.getColor(63, 127, 95);
        /*  76 */
        c_orange = aman.getColor(255, 153, 0);
        /*  77 */
        c_darkorange = aman.getColor(255, 102, 0);
        /*  78 */
        c_darkgreen = aman.getColor(0, 138, 0);
        /*  79 */
        c_purple = aman.getColor(127, 0, 85);
        /*  80 */
        c_darkblue = aman.getColor(0, 0, 85);
        /*  81 */
        c_lightgray = aman.getColor(192, 192, 192);
        /*  82 */
        c_bluepurple = aman.getColor(140, 100, 255);
        /*  83 */
        c_lightgreen = aman.getColor(133, 224, 133);
    }

    /*  84 */   private static Color c_lightblue;
    private static Color c_yellow;
    private static Color c_greenblue;
    private static Color c_orange;
    private static Color c_darkorange;
    private static Color c_darkgreen;
    private static Color c_brightgreen = aman.getColor(153, 255, 153);
    /*  85 */   private static Color c_purple;
    private static Color c_darkblue;
    private static Color c_lightgray;
    private static Color c_bluepurple;
    private static Color c_lightgreen;
    private static Color c_purple2 = aman.getColor(14493799);
    /*  86 */   private static Color c_blue2 = aman.getColor(18, 144, 195);
    /*  87 */   private static Color c_darkblue2 = aman.getColor(102, 225, 248);
    /*     */
    /*     */
    /*     */
    /*  91 */   private static Color solarized_base03 = aman.getColor(11062);
    /*  92 */   private static Color solarized_base02 = aman.getColor(472642);
    /*  93 */   private static Color solarized_base01 = aman.getColor(5795445);
    /*  94 */   private static Color solarized_base00 = aman.getColor(6650755);
    /*  95 */   private static Color solarized_base0 = aman.getColor(8623254);
    /*  96 */   private static Color solarized_base1 = aman.getColor(9675169);
    /*  97 */   private static Color solarized_base2 = aman.getColor(15657173);
    /*  98 */   private static Color solarized_base3 = aman.getColor(16643811);
    /*  99 */   private static Color solarized_yellow = aman.getColor(11897088);
    /* 100 */   private static Color solarized_orange = aman.getColor(13323030);
    /* 101 */   private static Color solarized_red = aman.getColor(14430767);
    /* 102 */   private static Color solarized_magenta = aman.getColor(13842050);
    /* 103 */   private static Color solarized_violet = aman.getColor(7107012);
    /* 104 */   private static Color solarized_blue = aman.getColor(2526162);
    /* 105 */   private static Color solarized_cyan = aman.getColor(2793880);
    /* 106 */   private static Color solarized_green = aman.getColor(8755456);
    /*     */   public static final String SCHEME_DEFAULT = "default";
    /*     */   public static final String SCHEME_DARK = "dark";
    /*     */   private ThemeManager themeManager;

    /*     */
    /*     */   static class ColorScheme {
        Style defstyle;
        /*     */ Style defastyle;
        /*     */ Map<ItemClassIdentifiers, Style> styles;
        /*     */ Map<ItemClassIdentifiers, Style> astyles;
        /*     */ Color defaultActiveLineColor;
        /*     */ Color defaultFontColor;
        /*     */ Color defaultActiveBgcolor;

        /*     */
        /* 119 */     ColorScheme() {
            this.styles = new HashMap();
            /* 120 */
            this.astyles = new HashMap();
            /*     */
        }

        /*     */
        /*     */     ColorScheme(ColorScheme s) {
            /* 124 */
            this.defstyle = s.defstyle;
            /* 125 */
            this.defastyle = s.defastyle;
            /* 126 */
            this.styles = new HashMap(s.styles);
            /* 127 */
            this.astyles = new HashMap(s.astyles);
            /* 128 */
            this.defaultActiveLineColor = s.defaultActiveLineColor;
            /* 129 */
            this.defaultFontColor = s.defaultFontColor;
            /* 130 */
            this.defaultActiveBgcolor = s.defaultActiveBgcolor;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /* 139 */   private Map<String, ColorScheme> schemes = new HashMap();
    /* 140 */   private String activeSchemeName = "default";

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public StyleManager(String styleData, ThemeManager themeManager)
    /*     */ {
        /* 149 */
        this.themeManager = themeManager;
        /* 150 */
        if (themeManager != null) {
            /* 151 */
            themeManager.addThemeChangeListener(this);
            /*     */
        }
        /*     */
        /* 154 */
        resetDefaults(false);
        /*     */
        /* 156 */
        if ((styleData != null) && (!styleData.isEmpty()) && (!styleData.equals("<default>"))) {
            /* 157 */
            decode(styleData);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public StyleManager clone()
    /*     */ {
        /* 166 */
        StyleManager dst = new StyleManager();
        /* 167 */
        dst.themeManager = this.themeManager;
        /* 168 */
        dst.schemes = new HashMap(this.schemes.size());
        /* 169 */
        for (Map.Entry<String, ColorScheme> e : this.schemes.entrySet()) {
            /* 170 */
            dst.schemes.put(e.getKey(), new ColorScheme((ColorScheme) e.getValue()));
            /*     */
        }
        /* 172 */
        return dst;
        /*     */
    }

    /*     */
    /*     */
    public void restore(StyleManager styleman, boolean notify) {
        /* 176 */
        this.themeManager = styleman.themeManager;
        /* 177 */
        this.schemes = new HashMap(styleman.schemes.size());
        /* 178 */
        for (Map.Entry<String, ColorScheme> e : styleman.schemes.entrySet()) {
            /* 179 */
            this.schemes.put(e.getKey(), new ColorScheme((ColorScheme) e.getValue()));
            /*     */
        }
        /*     */
        /*     */
        /* 183 */
        if (notify) {
            /* 184 */
            onStyleChanged();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public static Display getDisplay() {
        /* 189 */
        return display;
        /*     */
    }

    /*     */
    /*     */
    public void onThemeChange(String themeId)
    /*     */ {
        /* 194 */
        if (themeId == null) {
            /* 195 */
            updateScheme("default");
            /*     */
        }
        /* 197 */
        else if (themeId.equals("theme.dark")) {
            /* 198 */
            updateScheme("dark");
            /*     */
        }
        /*     */
        else {
            /* 201 */
            updateScheme("default");
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private boolean updateScheme(String name) {
        /* 206 */
        if (name == null) {
            /* 207 */
            name = "default";
            /*     */
        }
        /* 209 */
        else if ((!name.equals("default")) && (!name.equals("dark"))) {
            /* 210 */
            return false;
            /*     */
        }
        /*     */
        /* 213 */
        if (Strings.equals(name, this.activeSchemeName)) {
            /* 214 */
            return false;
            /*     */
        }
        /*     */
        /* 217 */
        ColorScheme scheme = getScheme(name);
        /* 218 */
        this.activeSchemeName = name;
        /* 219 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public List<String> getSchemeNames() {
        /* 223 */
        return Arrays.asList(new String[]{"default", "dark"});
        /*     */
    }

    /*     */
    /*     */
    private ColorScheme getScheme(String name) {
        /* 227 */
        ColorScheme themeStyles = (ColorScheme) this.schemes.get(name);
        /* 228 */
        if (themeStyles == null) {
            /* 229 */
            themeStyles = new ColorScheme();
            /* 230 */
            this.schemes.put(name, themeStyles);
            /*     */
        }
        /* 232 */
        return themeStyles;
        /*     */
    }

    /*     */
    /*     */
    private ColorScheme getActiveScheme() {
        /* 236 */
        return getScheme(this.activeSchemeName);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void resetDefaults(boolean notify)
    /*     */ {
        /* 242 */
        for (String t : getSchemeNames()) {
            /* 243 */
            ColorScheme ts = getScheme(t);
            /*     */
            /* 245 */
            ts.defstyle = null;
            /* 246 */
            ts.defastyle = null;
            /* 247 */
            ts.styles = new HashMap();
            /* 248 */
            ts.astyles = new HashMap();
            /* 249 */
            ts.defaultActiveLineColor = null;
            /* 250 */
            ts.defaultFontColor = null;
            /* 251 */
            ts.defaultActiveBgcolor = null;
            /*     */
            /* 253 */
            if (t.equals("default")) {
                /* 254 */
                ts.defaultActiveLineColor = aman.getColor(230, 230, 250);
                /* 255 */
                ts.defaultFontColor = black;
                /* 256 */
                ts.defaultActiveBgcolor = c_yellow;
                /*     */
                /*     */
                /* 259 */
                ts.defstyle = new Style(this, ts.defaultFontColor);
                /* 260 */
                ts.defastyle = new Style(this, ts.defaultFontColor, ts.defaultActiveBgcolor);
                /* 261 */
                add(ts, ItemClassIdentifiers.DEFAULT, ts.defstyle, ts.defastyle);
                /*     */
                /*     */
                /* 264 */
                add(ts, ItemClassIdentifiers.ARTIFACT, new Style(this, blue));
                /*     */
                /* 266 */
                add(ts, ItemClassIdentifiers.INFO_USELESS, new Style(this, white, c_lightgray));
                /* 267 */
                add(ts, ItemClassIdentifiers.INFO_DEPRECATED, new Style(this, black, c_lightgray));
                /* 268 */
                add(ts, ItemClassIdentifiers.INFO_DEBUG, new Style(this, black, c_lightblue));
                /* 269 */
                add(ts, ItemClassIdentifiers.INFO_NORMAL, new Style(this, white, blue));
                /* 270 */
                add(ts, ItemClassIdentifiers.INFO_WARNING, new Style(this, white, c_darkorange));
                /* 271 */
                add(ts, ItemClassIdentifiers.INFO_ERROR, new Style(this, white, darkred));
                /* 272 */
                add(ts, ItemClassIdentifiers.INFO_DANGEROUS, new Style(this, c_yellow, red));
                /* 273 */
                add(ts, ItemClassIdentifiers.INFO_MALFORMED, new Style(this, white, black));
                /* 274 */
                add(ts, ItemClassIdentifiers.INFO_CORRUPT, new Style(this, white, black));
                /*     */
                /* 276 */
                add(ts, ItemClassIdentifiers.RESULT_SUCCESS, new Style(this, black, c_brightgreen));
                /* 277 */
                add(ts, ItemClassIdentifiers.RESULT_ERROR, new Style(this, black, c_orange));
                /*     */
                /* 279 */
                add(ts, ItemClassIdentifiers.TYPE_BYTE, null);
                /* 280 */
                add(ts, ItemClassIdentifiers.TYPE_SHORT, null);
                /* 281 */
                add(ts, ItemClassIdentifiers.TYPE_INTEGER, null);
                /* 282 */
                add(ts, ItemClassIdentifiers.TYPE_LONG, null);
                /* 283 */
                add(ts, ItemClassIdentifiers.TYPE_FLOAT, null);
                /* 284 */
                add(ts, ItemClassIdentifiers.TYPE_DOUBLE, null);
                /*     */
                /* 286 */
                add(ts, ItemClassIdentifiers.COMMENT, new Style(this, c_greenblue), new Style(this, c_greenblue));
                /*     */
                /* 288 */
                add(ts, ItemClassIdentifiers.ADDRESS, new Style(this, darkgray));
                /* 289 */
                add(ts, ItemClassIdentifiers.ADDRESS_SLACK, new Style(this, c_lightgray));
                /*     */
                /*     */
                /*     */
                /* 293 */
                add(ts, ItemClassIdentifiers.BYTECODE, new Style(this, darkgray), new Style(this, darkgray));
                /* 294 */
                add(ts, ItemClassIdentifiers.DIRECTIVE, new Style(this, c_purple, null, true, false));
                /* 295 */
                add(ts, ItemClassIdentifiers.KEYWORD, new Style(this, c_purple, null, true, false));
                /* 296 */
                add(ts, ItemClassIdentifiers.MNEMONIC, new Style(this, ts.defaultFontColor));
                /* 297 */
                add(ts, ItemClassIdentifiers.OPCODE, new Style(this, ts.defaultFontColor));
                /* 298 */
                add(ts, ItemClassIdentifiers.LABEL, new Style(this, darkgray));
                /* 299 */
                add(ts, ItemClassIdentifiers.LABEL_ALTERNATE, new Style(this, c_lightgray));
                /* 300 */
                add(ts, ItemClassIdentifiers.LABEL_OOR, new Style(this, darkgray, aman.getColor(15374745)));
                /* 301 */
                add(ts, ItemClassIdentifiers.PARAMETER, new Style(this, ts.defaultFontColor));
                /* 302 */
                add(ts, ItemClassIdentifiers.IDENTIFIER, new Style(this, ts.defaultFontColor));
                /* 303 */
                add(ts, ItemClassIdentifiers.NUMBER, new Style(this, c_darkgreen, null, true, false));
                /* 304 */
                add(ts, ItemClassIdentifiers.CHARACTER, new Style(this, c_darkorange, null, true, false));
                /* 305 */
                add(ts, ItemClassIdentifiers.STRING, new Style(this, c_darkorange, null, true, false));
                /* 306 */
                add(ts, ItemClassIdentifiers.CLASS_NAME, new Style(this, blue));
                /* 307 */
                add(ts, ItemClassIdentifiers.FIELD_NAME, new Style(this, c_darkblue, null, false, true));
                /* 308 */
                add(ts, ItemClassIdentifiers.METHOD_NAME, new Style(this, c_darkblue));
                /* 309 */
                add(ts, ItemClassIdentifiers.EXTERNAL_CLASS_NAME, new Style(this, c_bluepurple));
                /* 310 */
                add(ts, ItemClassIdentifiers.EXTERNAL_FIELD_NAME, new Style(this, c_bluepurple));
                /* 311 */
                add(ts, ItemClassIdentifiers.EXTERNAL_METHOD_NAME, new Style(this, c_bluepurple));
                /* 312 */
                add(ts, ItemClassIdentifiers.PACKAGE_NAME, new Style(this, ts.defaultFontColor));
                /* 313 */
                add(ts, ItemClassIdentifiers.REGISTER, new Style(this, ts.defaultFontColor), new Style(this, black, c_lightgreen));
                /*     */
                /* 315 */
                add(ts, ItemClassIdentifiers.IMMEDIATE, new Style(this, darkgreen, null, true, false));
                /*     */
                /*     */
                /* 318 */
                add(ts, ItemClassIdentifiers.CODE_UNKNOWN, new Style(this, c_lightgray));
                /* 319 */
                add(ts, ItemClassIdentifiers.CODE_INSTRUCTIONS, new Style(this, blue));
                /* 320 */
                add(ts, ItemClassIdentifiers.CODE_ROUTINE, new Style(this, c_bluepurple));
                /* 321 */
                add(ts, ItemClassIdentifiers.CODE_DATA, new Style(this, c_darkgreen));
                /* 322 */
                add(ts, ItemClassIdentifiers.CODE_LIBRARY, new Style(this, cyan));
                /* 323 */
                add(ts, ItemClassIdentifiers.CODE_METADATA, new Style(this, yellow));
                /* 324 */
                add(ts, ItemClassIdentifiers.CODE_SLACK, new Style(this, black));
                /*     */
                /* 326 */
                add(ts, ItemClassIdentifiers.MARKUP_ELEMENT, new Style(this, blue));
                /* 327 */
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_NAME, new Style(this, ts.defaultFontColor));
                /* 328 */
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_VALUE, new Style(this, c_darkorange));
                /* 329 */
                add(ts, ItemClassIdentifiers.MARKUP_TEXT, new Style(this, c_greenblue));
                /* 330 */
                add(ts, ItemClassIdentifiers.MARKUP_COMMENT, new Style(this, c_greenblue));
                /*     */
            }
            /* 332 */
            else if (t.equalsIgnoreCase("dark")) {
                /* 333 */
                ts.defaultActiveLineColor = solarized_base02;
                /* 334 */
                ts.defaultFontColor = solarized_base2;
                /* 335 */
                ts.defaultActiveBgcolor = solarized_base03;
                /*     */
                /*     */
                /* 338 */
                ts.defstyle = new Style(this, ts.defaultFontColor);
                /* 339 */
                ts.defastyle = new Style(this, ts.defaultFontColor, ts.defaultActiveBgcolor);
                /* 340 */
                add(ts, ItemClassIdentifiers.DEFAULT, ts.defstyle, ts.defastyle);
                /*     */
                /*     */
                /* 343 */
                add(ts, ItemClassIdentifiers.ARTIFACT, new Style(this, c_blue2));
                /*     */
                /* 345 */
                add(ts, ItemClassIdentifiers.INFO_USELESS, new Style(this, white, c_lightgray));
                /* 346 */
                add(ts, ItemClassIdentifiers.INFO_DEPRECATED, new Style(this, ts.defaultFontColor, c_lightgray));
                /* 347 */
                add(ts, ItemClassIdentifiers.INFO_DEBUG, new Style(this, ts.defaultFontColor, c_lightblue));
                /* 348 */
                add(ts, ItemClassIdentifiers.INFO_NORMAL, new Style(this, white, c_blue2));
                /* 349 */
                add(ts, ItemClassIdentifiers.INFO_WARNING, new Style(this, white, c_darkorange));
                /* 350 */
                add(ts, ItemClassIdentifiers.INFO_ERROR, new Style(this, white, darkred));
                /* 351 */
                add(ts, ItemClassIdentifiers.INFO_DANGEROUS, new Style(this, c_yellow, red));
                /* 352 */
                add(ts, ItemClassIdentifiers.INFO_MALFORMED, new Style(this, white, black));
                /* 353 */
                add(ts, ItemClassIdentifiers.INFO_CORRUPT, new Style(this, white, black));
                /*     */
                /* 355 */
                add(ts, ItemClassIdentifiers.RESULT_SUCCESS, new Style(this, white, c_darkgreen));
                /* 356 */
                add(ts, ItemClassIdentifiers.RESULT_ERROR, new Style(this, white, c_darkorange));
                /*     */
                /* 358 */
                add(ts, ItemClassIdentifiers.TYPE_BYTE, null);
                /* 359 */
                add(ts, ItemClassIdentifiers.TYPE_SHORT, null);
                /* 360 */
                add(ts, ItemClassIdentifiers.TYPE_INTEGER, null);
                /* 361 */
                add(ts, ItemClassIdentifiers.TYPE_LONG, null);
                /* 362 */
                add(ts, ItemClassIdentifiers.TYPE_FLOAT, null);
                /* 363 */
                add(ts, ItemClassIdentifiers.TYPE_DOUBLE, null);
                /*     */
                /* 365 */
                add(ts, ItemClassIdentifiers.ADDRESS, new Style(this, solarized_base01));
                /* 366 */
                add(ts, ItemClassIdentifiers.ADDRESS_SLACK, new Style(this, c_lightgray));
                /*     */
                /* 368 */
                add(ts, ItemClassIdentifiers.COMMENT, new Style(this, darkgray));
                /* 369 */
                add(ts, ItemClassIdentifiers.BYTECODE, new Style(this, darkgray), new Style(this, darkgray));
                /* 370 */
                add(ts, ItemClassIdentifiers.DIRECTIVE, new Style(this, c_purple2, null, true, false));
                /* 371 */
                add(ts, ItemClassIdentifiers.KEYWORD, new Style(this, solarized_magenta, null, false, true));
                /* 372 */
                add(ts, ItemClassIdentifiers.MNEMONIC, new Style(this, ts.defaultFontColor));
                /* 373 */
                add(ts, ItemClassIdentifiers.OPCODE, new Style(this, ts.defaultFontColor));
                /* 374 */
                add(ts, ItemClassIdentifiers.LABEL, new Style(this, solarized_green, null, false, true));
                /* 375 */
                add(ts, ItemClassIdentifiers.LABEL_ALTERNATE, new Style(this, c_lightgray));
                /* 376 */
                add(ts, ItemClassIdentifiers.LABEL_OOR, new Style(this, darkgray, aman.getColor(15374745)));
                /* 377 */
                add(ts, ItemClassIdentifiers.PARAMETER, new Style(this, ts.defaultFontColor));
                /* 378 */
                add(ts, ItemClassIdentifiers.IDENTIFIER, new Style(this, ts.defaultFontColor));
                /* 379 */
                add(ts, ItemClassIdentifiers.NUMBER, new Style(this, solarized_yellow, null, false, false));
                /* 380 */
                add(ts, ItemClassIdentifiers.CHARACTER, new Style(this, c_darkorange, null, true, false));
                /* 381 */
                add(ts, ItemClassIdentifiers.STRING, new Style(this, c_darkorange, null, true, false));
                /* 382 */
                add(ts, ItemClassIdentifiers.CLASS_NAME, new Style(this, solarized_base2, null, false, false));
                /* 383 */
                add(ts, ItemClassIdentifiers.FIELD_NAME, new Style(this, solarized_violet, null, false, false));
                /* 384 */
                add(ts, ItemClassIdentifiers.METHOD_NAME, new Style(this, solarized_green, null, false, false));
                /* 385 */
                add(ts, ItemClassIdentifiers.EXTERNAL_CLASS_NAME, new Style(this, c_bluepurple));
                /* 386 */
                add(ts, ItemClassIdentifiers.EXTERNAL_FIELD_NAME, new Style(this, c_bluepurple));
                /* 387 */
                add(ts, ItemClassIdentifiers.EXTERNAL_METHOD_NAME, new Style(this, c_bluepurple));
                /* 388 */
                add(ts, ItemClassIdentifiers.PACKAGE_NAME, new Style(this, ts.defaultFontColor));
                /* 389 */
                add(ts, ItemClassIdentifiers.REGISTER, new Style(this, solarized_base2), new Style(this, ts.defaultFontColor, solarized_blue));
                /*     */
                /* 391 */
                add(ts, ItemClassIdentifiers.IMMEDIATE, new Style(this, solarized_yellow, null, false, false));
                /*     */
                /*     */
                /* 394 */
                add(ts, ItemClassIdentifiers.CODE_UNKNOWN, new Style(this, solarized_base2));
                /* 395 */
                add(ts, ItemClassIdentifiers.CODE_INSTRUCTIONS, new Style(this, solarized_cyan));
                /* 396 */
                add(ts, ItemClassIdentifiers.CODE_ROUTINE, new Style(this, c_bluepurple));
                /* 397 */
                add(ts, ItemClassIdentifiers.CODE_DATA, new Style(this, solarized_green));
                /* 398 */
                add(ts, ItemClassIdentifiers.CODE_LIBRARY, new Style(this, cyan));
                /* 399 */
                add(ts, ItemClassIdentifiers.CODE_METADATA, new Style(this, yellow));
                /* 400 */
                add(ts, ItemClassIdentifiers.CODE_SLACK, new Style(this, solarized_base02));
                /*     */
                /* 402 */
                add(ts, ItemClassIdentifiers.MARKUP_ELEMENT, new Style(this, c_blue2));
                /* 403 */
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_NAME, new Style(this, ts.defaultFontColor));
                /* 404 */
                add(ts, ItemClassIdentifiers.MARKUP_ATTRIBUTE_VALUE, new Style(this, c_darkorange));
                /* 405 */
                add(ts, ItemClassIdentifiers.MARKUP_TEXT, new Style(this, c_greenblue));
                /* 406 */
                add(ts, ItemClassIdentifiers.MARKUP_COMMENT, new Style(this, c_greenblue));
                /*     */
            }
            /*     */
        }
        /*     */
        /* 410 */
        if (notify) {
            /* 411 */
            onStyleChanged();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   void onStyleChanged() {
        /* 416 */
        notifyListeners(new JebClientEvent(JC.ItemStyleChanged));
        /*     */
    }

    /*     */
    /*     */
    private void add(ColorScheme currentThemeStyles, ItemClassIdentifiers classId, Style style, Style activeStyle) {
        /* 420 */
        currentThemeStyles.styles.put(classId, style);
        /* 421 */
        currentThemeStyles.astyles.put(classId, activeStyle);
        /*     */
    }

    /*     */
    /*     */
    private void add(ColorScheme currentThemeStyles, ItemClassIdentifiers classId, Style style) {
        /* 425 */
        Style activeStyle = null;
        /* 426 */
        if (style != null) {
            /* 427 */
            activeStyle = new Style(style);
            /* 428 */
            if (activeStyle.getBackgroungColor() == null) {
                /* 429 */
                activeStyle.bgcolor = currentThemeStyles.defaultActiveBgcolor;
                /*     */
            }
            /* 431 */
            if (!activeStyle.bold) {
                /* 432 */
                activeStyle.bold = style.bold;
                /*     */
            }
            /* 434 */
            if (!activeStyle.italic) {
                /* 435 */
                activeStyle.italic = style.italic;
                /*     */
            }
            /*     */
        }
        /* 438 */
        add(currentThemeStyles, classId, style, activeStyle);
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(ColorScheme scheme, ItemClassIdentifiers classId, boolean active) {
        /* 442 */
        return active ? getActiveStyle(scheme, classId) : getNormalStyle(scheme, classId);
        /*     */
    }

    /*     */
    /*     */
    public Style getNormalStyle(ColorScheme scheme, ItemClassIdentifiers classId) {
        /* 446 */
        Style r = (Style) scheme.styles.get(classId);
        /* 447 */
        if (r == null) {
            /* 448 */
            r = scheme.defstyle;
            /*     */
        }
        /* 450 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public Style getActiveStyle(ColorScheme scheme, ItemClassIdentifiers classId) {
        /* 454 */
        Style r = (Style) scheme.astyles.get(classId);
        /* 455 */
        if (r == null) {
            /* 456 */
            r = scheme.defastyle;
            /*     */
        }
        /* 458 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(ItemClassIdentifiers classId, boolean active) {
        /* 462 */
        return getStyle(getActiveScheme(), classId, active);
        /*     */
    }

    /*     */
    /*     */
    public Style getNormalStyle(ItemClassIdentifiers classId) {
        /* 466 */
        return getNormalStyle(getActiveScheme(), classId);
        /*     */
    }

    /*     */
    /*     */
    public Style getActiveStyle(ItemClassIdentifiers classId) {
        /* 470 */
        return getActiveStyle(getActiveScheme(), classId);
        /*     */
    }

    /*     */
    /*     */
    public Color getOnCaretBackground() {
        /* 474 */
        return getActiveScheme().defaultActiveLineColor;
        /*     */
    }

    /*     */
    /*     */
    private void decode(String s) {
        /* 478 */
        Map<String, String> map = Strings.decodeMap(s);
        /* 479 */
        for (String themeName : map.keySet()) {
            /* 480 */
            String themeStylesData = (String) map.get(themeName);
            /* 481 */
            ColorScheme r = decodeScheme(themeStylesData);
            /* 482 */
            this.schemes.put(themeName, r);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private ColorScheme decodeScheme(String s) {
        /* 487 */
        ColorScheme themeStyles = new ColorScheme();
        /*     */
        /* 489 */
        for (String item : s.split("\\|")) {
            /* 490 */
            String[] tmp = item.split("=");
            /* 491 */
            if (tmp.length != 2) {
                /* 492 */
                logger.warn("%s: %s", new Object[]{S.s(403), Formatter.escapeString(item, true)});
                /*     */
                /*     */
                /*     */
            }
            /* 496 */
            else if (tmp[0].equals("CURRENT_LINE_BGCOLOR")) {
                /* 497 */
                themeStyles.defaultActiveLineColor = Style.parseColor(display, tmp[1]);
                /*     */
            }
            /* 499 */
            else if (tmp[0].equals("DEFAULT_FONT_COLOR")) {
                /* 500 */
                themeStyles.defaultFontColor = Style.parseColor(display, tmp[1]);
                /*     */
            }
            /* 502 */
            else if (tmp[0].equals("DEFAULT_ACTIVE_BGCOLOR")) {
                /* 503 */
                themeStyles.defaultActiveBgcolor = Style.parseColor(display, tmp[1]);
                /*     */
            }
            /*     */
            else {
                /* 506 */
                ItemClassIdentifiers t = null;
                /*     */
                try {
                    /* 508 */
                    t = ItemClassIdentifiers.valueOf(tmp[0]);
                    /*     */
                }
                /*     */ catch (Exception e) {
                    /* 511 */
                    logger.warn("%s: %s", new Object[]{S.s(404), Formatter.escapeString(tmp[0], true)});
                    /* 512 */
                    continue;
                    /*     */
                }
                /*     */
                /* 515 */
                String[] data = tmp[1].split(";");
                /* 516 */
                if (data.length != 2) {
                    /* 517 */
                    logger.warn("%s: %s", new Object[]{S.s(403), Formatter.escapeString(tmp[1], true)});
                    /*     */
                }
                /*     */
                else
                    /*     */ {
                    /* 521 */
                    themeStyles.styles.put(t, new Style(this, data[0]));
                    /* 522 */
                    themeStyles.astyles.put(t, new Style(this, data[1]));
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 527 */
        themeStyles.defstyle = ((Style) themeStyles.styles.get(ItemClassIdentifiers.DEFAULT));
        /* 528 */
        if (themeStyles.defstyle == null) {
            /* 529 */
            themeStyles.defstyle = new Style(this, display.getSystemColor(2));
            /* 530 */
            themeStyles.styles.put(ItemClassIdentifiers.DEFAULT, themeStyles.defstyle);
            /*     */
        }
        /* 532 */
        themeStyles.defastyle = ((Style) themeStyles.astyles.get(ItemClassIdentifiers.DEFAULT));
        /* 533 */
        if (themeStyles.defastyle == null) {
            /* 534 */
            themeStyles.defastyle = new Style(this, display.getSystemColor(2));
            /* 535 */
            themeStyles.astyles.put(ItemClassIdentifiers.DEFAULT, themeStyles.defastyle);
            /*     */
        }
        /*     */
        /* 538 */
        return themeStyles;
        /*     */
    }

    /*     */
    /*     */
    public String encode()
    /*     */ {
        /* 543 */
        Map<String, String> map = new LinkedHashMap();
        /* 544 */
        for (Map.Entry<String, ColorScheme> e : this.schemes.entrySet()) {
            /* 545 */
            ColorScheme themeStyles = (ColorScheme) e.getValue();
            /* 546 */
            StringBuilder sb = new StringBuilder();
            /* 547 */
            sb.append(
                    /* 548 */         String.format("CURRENT_LINE_BGCOLOR=%s|", new Object[]{Style.colorToString(themeStyles.defaultActiveLineColor)}));
            /* 549 */
            sb.append(String.format("DEFAULT_FONT_COLOR=%s|", new Object[]{Style.colorToString(themeStyles.defaultFontColor)}));
            /* 550 */
            sb.append(
                    /* 551 */         String.format("DEFAULT_ACTIVE_BGCOLOR=%s|", new Object[]{Style.colorToString(themeStyles.defaultActiveBgcolor)}));
            /* 552 */
            for (ItemClassIdentifiers t : ItemClassIdentifiers.values()) {
                /* 553 */
                sb.append(
                        /* 554 */           String.format("%s=%s;%s|", new Object[]{t, getNormalStyle(themeStyles, t), getActiveStyle(themeStyles, t)}));
                /*     */
            }
            /* 556 */
            map.put(e.getKey(), sb.toString());
            /*     */
        }
        /* 558 */
        return Strings.encodeMap(map);
        /*     */
    }

    /*     */
    /*     */
    private StyleManager() {
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\StyleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */