/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.client.events.JC;
/*     */ import com.pnfsoftware.jeb.client.events.JebClientEvent;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.util.base.OSType;
/*     */ import com.pnfsoftware.jeb.util.events.EventSource;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jface.resource.FontDescriptor;
/*     */ import org.eclipse.jface.resource.JFaceResources;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.widgets.Control;
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
/*     */ public class FontManager
        /*     */ extends EventSource
        /*     */ {
    /*  41 */   private static final ILogger logger = GlobalLog.getLogger(FontManager.class);
    /*     */
    /*  43 */   private Display display = Display.getCurrent();
    /*     */
    /*     */   private IPropertyManager propertyManager;
    /*     */
    /*     */   private Font codeFont;

    /*     */
    /*     */
    /*     */
    public FontManager(IPropertyManager pm)
    /*     */ {
        /*  52 */
        this.propertyManager = pm;
        /*  53 */
        resetDefaults(false, false);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    private FontManager() {
    }

    /*     */
    /*     */
    public FontManager clone()
    /*     */ {
        /*  61 */
        FontManager dst = new FontManager();
        /*  62 */
        dst.display = this.display;
        /*  63 */
        dst.propertyManager = this.propertyManager;
        /*  64 */
        dst.codeFont = this.codeFont;
        /*  65 */
        return dst;
        /*     */
    }

    /*     */
    /*     */
    public void resetDefaults(boolean forceDefault, boolean notify) {
        /*  69 */
        this.codeFont = loadCodeFont(forceDefault);
        /*  70 */
        if (notify) {
            /*  71 */
            onFontChanged();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void restore(FontManager src, boolean notify) {
        /*  76 */
        this.propertyManager = src.propertyManager;
        /*  77 */
        this.display = src.display;
        /*  78 */
        this.codeFont = src.codeFont;
        /*  79 */
        if (notify) {
            /*  80 */
            onFontChanged();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public Font getCodeFont() {
        /*  85 */
        return this.codeFont;
        /*     */
    }

    /*     */
    /*     */
    public void setCodeFont(Font codeFont) {
        /*  89 */
        saveCodeFont(codeFont);
        /*  90 */
        this.codeFont = codeFont;
        /*  91 */
        onFontChanged();
        /*     */
    }

    /*     */
    /*     */
    private void onFontChanged() {
        /*  95 */
        notifyListeners(new JebClientEvent(JC.CodeFontChanged, this.codeFont));
        /*     */
    }

    /*     */
    /*     */
    private void saveCodeFont(Font codeFontToSave)
    /*     */ {
        /* 100 */
        if (this.propertyManager != null) {
            /* 101 */
            String s = "";
            /* 102 */
            if (codeFontToSave != null) {
                /* 103 */
                FontData[] fdlist = codeFontToSave.getFontData();
                /* 104 */
                StringBuilder sb = new StringBuilder();
                /* 105 */
                int i = 0;
                /* 106 */
                for (FontData fd : fdlist) {
                    /* 107 */
                    if (i >= 1) {
                        /* 108 */
                        sb.append(",");
                        /*     */
                    }
                    /* 110 */
                    sb.append(fd.toString());
                    /* 111 */
                    i++;
                    /*     */
                }
                /* 113 */
                s = sb.toString();
                /*     */
            }
            /* 115 */
            this.propertyManager.setString(".ui.CodeFont", s);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private Font loadCodeFont(boolean ignoreSettings)
    /*     */ {
        /* 121 */
        Font newCodeFont = null;
        /* 122 */
        FontData[] fdlist;
        int i;
        if ((!ignoreSettings) && (this.propertyManager != null)) {
            /*     */
            try
                /*     */ {
                /* 125 */
                String s = this.propertyManager.getString(".ui.CodeFont");
                /* 126 */
                if ((s != null) && (!s.isEmpty())) {
                    /* 127 */
                    String[] elts = s.split(",");
                    /* 128 */
                    fdlist = new FontData[elts.length];
                    /* 129 */
                    i = 0;
                    /* 130 */
                    for (String elt : elts) {
                        /* 131 */
                        fdlist[i] = new FontData(elt);
                        /* 132 */
                        i++;
                        /*     */
                    }
                    /* 134 */
                    newCodeFont = new Font(this.display, fdlist);
                    /*     */
                }
                /*     */
            }
            /*     */ catch (Exception e) {
                /* 138 */
                logger.catching(e);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 143 */
        FontData[] fdlist = this.display.getFontList(null, true);
        /*     */
        /* 145 */
        Map<String, FontData> fontmap = new HashMap();
        /* 146 */
        for (FontData fd : fdlist)
            /*     */ {
            /* 148 */
            if (fd.getStyle() == 0) {
                /* 149 */
                fontmap.put(fd.getName(), fd);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 154 */
        if (newCodeFont != null) {
            /* 155 */
            FontData fd = newCodeFont.getFontData()[0];
            /* 156 */
            if (!fontmap.containsKey(fd.getName())) {
                /* 157 */
                newCodeFont = null;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 162 */
        if (newCodeFont == null) {
            /* 163 */
            OSType ostype = OSType.determine();
            /* 164 */
            List<String> preferredNames = new ArrayList();
            /* 165 */
            if (ostype.isWindows()) {
                /* 166 */
                preferredNames.add("Consolas");
                /*     */
            }
            /* 168 */
            else if (ostype.isMac()) {
                /* 169 */
                preferredNames.add("Menlo");
                /* 170 */
                preferredNames.add("Monaco");
                /*     */
            }
            /* 172 */
            else if (ostype.isLinux()) {
                /* 173 */
                preferredNames.add("DejaVu Sans Mono");
                /*     */
            }
            /*     */
            /*     */
            /* 177 */
            for (String preferredName : preferredNames) {
                /* 178 */
                if (fontmap.containsKey(preferredName)) {
                    /* 179 */
                    FontData fd = (FontData) fontmap.get(preferredName);
                    /* 180 */
                    fd.setHeight(8);
                    /* 181 */
                    newCodeFont = new Font(this.display, fd);
                    /* 182 */
                    break;
                    /*     */
                }
                /*     */
            }
            /*     */
            /*     */
            /* 187 */
            if (newCodeFont == null) {
                /* 188 */
                Font font = JFaceResources.getFont("org.eclipse.jface.textfont");
                /* 189 */
                fdlist = font.getFontData();
                /* 190 */
                FontDescriptor fd = FontDescriptor.createFrom(fdlist);
                /* 191 */
                fd = fd.increaseHeight(-1);
                /* 192 */
                newCodeFont = fd.createFont(this.display);
                /*     */
            }
            /*     */
        }
        /*     */
        /* 196 */
        return newCodeFont;
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
    /*     */
    public void registerWidget(final Control ctl)
    /*     */ {
        /* 208 */
        ctl.setFont(getCodeFont());
        /*     */
        /* 210 */
        final IEventListener fontChangeListener = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /* 213 */
                ctl.setFont((Font) e.getData());
                /*     */
            }
            /*     */
            /* 216 */
        };
        /* 217 */
        addListener(fontChangeListener);
        /*     */
        /* 219 */
        ctl.addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /* 222 */
                FontManager.this.removeListener(fontChangeListener);
                /*     */
            }
            /*     */
        });
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\FontManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */