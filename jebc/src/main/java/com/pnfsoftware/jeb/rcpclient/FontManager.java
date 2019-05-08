package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.client.events.JC;
import com.pnfsoftware.jeb.client.events.JebClientEvent;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.util.base.OSType;
import com.pnfsoftware.jeb.util.events.EventSource;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class FontManager extends EventSource {
    private static final ILogger logger = GlobalLog.getLogger(FontManager.class);
    private Display display = Display.getCurrent();
    private IPropertyManager propertyManager;
    private Font codeFont;

    public FontManager(IPropertyManager pm) {
        this.propertyManager = pm;
        resetDefaults(false, false);
    }

    private FontManager() {
    }

    public FontManager clone() {
        FontManager dst = new FontManager();
        dst.display = this.display;
        dst.propertyManager = this.propertyManager;
        dst.codeFont = this.codeFont;
        return dst;
    }

    public void resetDefaults(boolean forceDefault, boolean notify) {
        this.codeFont = loadCodeFont(forceDefault);
        if (notify) {
            onFontChanged();
        }
    }

    public void restore(FontManager src, boolean notify) {
        this.propertyManager = src.propertyManager;
        this.display = src.display;
        this.codeFont = src.codeFont;
        if (notify) {
            onFontChanged();
        }
    }

    public Font getCodeFont() {
        return this.codeFont;
    }

    public void setCodeFont(Font codeFont) {
        saveCodeFont(codeFont);
        this.codeFont = codeFont;
        onFontChanged();
    }

    private void onFontChanged() {
        notifyListeners(new JebClientEvent(JC.CodeFontChanged, this.codeFont));
    }

    private void saveCodeFont(Font codeFontToSave) {
        if (this.propertyManager != null) {
            String s = "";
            if (codeFontToSave != null) {
                FontData[] fdlist = codeFontToSave.getFontData();
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (FontData fd : fdlist) {
                    if (i >= 1) {
                        sb.append(",");
                    }
                    sb.append(fd.toString());
                    i++;
                }
                s = sb.toString();
            }
            this.propertyManager.setString(".ui.CodeFont", s);
        }
    }

    private Font loadCodeFont(boolean ignoreSettings) {
        Font newCodeFont = null;
        FontData[] fdlist;
        int i;
        if ((!ignoreSettings) && (this.propertyManager != null)) {
            try {
                String s = this.propertyManager.getString(".ui.CodeFont");
                if ((s != null) && (!s.isEmpty())) {
                    String[] elts = s.split(",");
                    fdlist = new FontData[elts.length];
                    i = 0;
                    for (String elt : elts) {
                        fdlist[i] = new FontData(elt);
                        i++;
                    }
                    newCodeFont = new Font(this.display, fdlist);
                }
            } catch (Exception e) {
                logger.catching(e);
            }
        }
        fdlist = this.display.getFontList(null, true);
        Map<String, FontData> fontmap = new HashMap();
        for (FontData fd : fdlist) {
            if (fd.getStyle() == 0) {
                fontmap.put(fd.getName(), fd);
            }
        }
        if (newCodeFont != null) {
            FontData fd = newCodeFont.getFontData()[0];
            if (!fontmap.containsKey(fd.getName())) {
                newCodeFont = null;
            }
        }
        if (newCodeFont == null) {
            OSType ostype = OSType.determine();
            List<String> preferredNames = new ArrayList();
            if (ostype.isWindows()) {
                preferredNames.add("Consolas");
            } else if (ostype.isMac()) {
                preferredNames.add("Menlo");
                preferredNames.add("Monaco");
            } else if (ostype.isLinux()) {
                preferredNames.add("DejaVu Sans Mono");
            }
            for (String preferredName : preferredNames) {
                if (fontmap.containsKey(preferredName)) {
                    FontData fd = (FontData) fontmap.get(preferredName);
                    fd.setHeight(8);
                    newCodeFont = new Font(this.display, fd);
                    break;
                }
            }
            if (newCodeFont == null) {
                Font font = JFaceResources.getFont("org.eclipse.jface.textfont");
                fdlist = font.getFontData();
                FontDescriptor fd = FontDescriptor.createFrom(fdlist);
                fd = fd.increaseHeight(-1);
                newCodeFont = fd.createFont(this.display);
            }
        }
        return newCodeFont;
    }

    public void registerWidget(final Control ctl) {
        ctl.setFont(getCodeFont());
        final IEventListener fontChangeListener = new IEventListener() {
            public void onEvent(IEvent e) {
                ctl.setFont((Font) e.getData());
            }
        };
        addListener(fontChangeListener);
        ctl.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                FontManager.this.removeListener(fontChangeListener);
            }
        });
    }
}


