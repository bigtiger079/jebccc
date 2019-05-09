package com.pnfsoftware.jeb.rcpclient.extensions.themes;

import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilterText;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

public class ThemeManager {
    private static final ILogger logger = GlobalLog.getLogger(ThemeManager.class);
    public static final String ID_THEME_STANDARD = "theme.standard";
    public static final String ID_THEME_DARK = "theme.dark";
    private static ThemeManager instance = new ThemeManager();
    Display display;
    Listener filter;
    LinkedHashMap<String, Theme> themes = new LinkedHashMap();
    Theme activeTheme;
    List<IThemeChangeListener> listeners = new ArrayList<>();
    WeakIdentityHashMap<Widget, Integer> seen = new WeakIdentityHashMap();
    boolean firstSet;

    public static ThemeManager getInstance() {
        return instance;
    }

    private ThemeManager() {
        this.display = Display.getCurrent();
        if (this.display == null) {
            throw new RuntimeException("The display must be initialized");
        }
        this.themes.put("theme.standard", null);
        registerTheme(new DarkTheme());
        this.filter = new Listener() {
            public void handleEvent(Event event) {
                ThemeManager.this.processControls(event.widget);
            }
        };
        this.display.addFilter(26, this.filter);
        this.display.addFilter(15, this.filter);
        this.display.addFilter(22, this.filter);
    }

    private void registerTheme(Theme theme) {
        if (this.themes.put(theme.getId(), theme) != null) {
            throw new RuntimeException("Attempt to overwrite a theme");
        }
    }

    public Collection<String> getThemes() {
        return Collections.unmodifiableCollection(this.themes.keySet());
    }

    public String getActiveTheme() {
        if (this.activeTheme == null) {
            return "theme.standard";
        }
        return this.activeTheme.getId();
    }

    public boolean isDarkTheme() {
        return (this.activeTheme != null) && ("theme.dark".equals(this.activeTheme.getId()));
    }

    public boolean setNextTheme() {
        List<String> ids = new ArrayList<>(this.themes.keySet());
        if (ids.size() <= 1) {
            return false;
        }
        if (this.activeTheme == null) {
            return setActiveTheme((String) ids.get(1));
        }
        for (int i = 1; i < ids.size(); i++) {
            String id = (String) ids.get(i);
            if (id.equals(this.activeTheme.getId())) {
                String nextId = (String) ids.get((i + 1) % ids.size());
                return setActiveTheme(nextId);
            }
        }
        return false;
    }

    public boolean setActiveTheme(String themeId) {
        Theme wantedTheme;
        if ((themeId == null) || (themeId.equals("theme.standard"))) {
            wantedTheme = null;
        } else {
            wantedTheme = (Theme) this.themes.get(themeId);
            if (wantedTheme == null) {
                return false;
            }
        }
        setThemeInternal(wantedTheme, true);
        return true;
    }

    private void setThemeInternal(Theme theme, boolean notify) {
        if (theme == this.activeTheme) {
            return;
        }
        this.activeTheme = theme;
        this.seen = new WeakIdentityHashMap();
        for (Shell shell : this.display.getShells()) {
            processControls(shell);
        }
        if (notify) {
            notifyThemeChangeListeners(theme == null ? null : theme.getId());
        }
    }

    void processControls(Widget w) {
        if (w == null) {
            return;
        }
        if (this.seen.get(w) != null) {
            return;
        }
        this.seen.put(w, Integer.valueOf(0));
        if (!(w instanceof Control)) {
            return;
        }
        Control ctl = (Control) w;
        int r = processSpecificControl(ctl);
        if ((r & 0x1) == 0) {
            if (ctl.getData("storedOriginalColors") == null) {
                ctl.setData("cBackground", ctl.getBackground());
                ctl.setData("cForeground", ctl.getForeground());
                ctl.setData("storedOriginalColors", Boolean.valueOf(true));
            }
            if (this.activeTheme == null) {
                ctl.setBackground((Color) ctl.getData("cBackground"));
                ctl.setForeground((Color) ctl.getData("cForeground"));
            } else {
                ctl.setBackground(this.activeTheme.cBackground);
                ctl.setForeground(this.activeTheme.cForeground);
            }
        }
        if (((r & 0x2) == 0) && ((ctl instanceof Composite))) {
            for (Control c : ((Composite) ctl).getChildren()) {
                processControls(c);
            }
        }
    }

    public static final int FLAG_PROCEED = 0;
    public static final int FLAG_SKIP_GENERIC = 1;
    public static final int FLAG_SKIP_RECURSION = 2;
    public static final int FLAG_SKIP_ALL = 3;

    private int processSpecificControl(Control ctl) {
        if ((ctl instanceof CTabFolder)) {
            CTabFolder c = (CTabFolder) ctl;
            if (ctl.getData("storedOriginalColors") == null) {
                ctl.setData("cBackgroundSelectedTab", c.getSelectionBackground());
                ctl.setData("cForegroundSelectedTab", c.getSelectionForeground());
                ctl.setData("storedOriginalColors", Boolean.valueOf(true));
            }
            if (this.activeTheme == null) {
                c.setSelectionBackground((Color) ctl.getData("cBackgroundSelectedTab"));
                c.setSelectionForeground((Color) ctl.getData("cForegroundSelectedTab"));
            } else {
                c.setSelectionBackground(this.activeTheme.cBackgroundSelectedTab);
                c.setSelectionForeground(this.activeTheme.cForegroundSelectedTab);
            }
            return 0;
        }
        if ((ctl instanceof Table)) {
            Table c = (Table) ctl;
            if (ctl.getData("storedOriginalColors") == null) {
                ctl.setData("cBackgroundTableHeader", c.getHeaderBackground());
                ctl.setData("cForegroundTableHeader", c.getHeaderForeground());
                ctl.setData("storedOriginalColors", Boolean.valueOf(true));
            }
            if (this.activeTheme == null) {
                c.setHeaderBackground((Color) ctl.getData("cBackgroundTableHeader"));
                c.setHeaderForeground((Color) ctl.getData("cForegroundTableHeader"));
            } else {
                c.setHeaderBackground(this.activeTheme.cBackgroundTableHeader);
                c.setHeaderForeground(this.activeTheme.cForegroundTableHeader);
            }
            return 0;
        }
        if ((ctl instanceof Tree)) {
            Tree c = (Tree) ctl;
            if (ctl.getData("storedOriginalColors") == null) {
                ctl.setData("cBackgroundTableHeader", c.getHeaderBackground());
                ctl.setData("cForegroundTableHeader", c.getHeaderForeground());
                ctl.setData("storedOriginalColors", Boolean.valueOf(true));
            }
            if (this.activeTheme == null) {
                c.setHeaderBackground((Color) ctl.getData("cBackgroundTableHeader"));
                c.setHeaderForeground((Color) ctl.getData("cForegroundTableHeader"));
            } else {
                c.setHeaderBackground(this.activeTheme.cBackgroundTableHeader);
                c.setHeaderForeground(this.activeTheme.cForegroundTableHeader);
            }
            return 0;
        }
        if ((ctl instanceof FilterText)) {
            if (ctl.getData("storedOriginalColors") == null) {
                ctl.setData("cBackgroundFilter", ctl.getBackground());
                ctl.setData("cForegroundFilter", ctl.getForeground());
                ctl.setData("storedOriginalColors", Boolean.valueOf(true));
            }
            if (this.activeTheme == null) {
                ctl.setBackground((Color) ctl.getData("cBackgroundFilter"));
                ctl.setForeground((Color) ctl.getData("cForegroundFilter"));
            } else {
                ctl.setBackground(this.activeTheme.cBackgroundFilter);
                ctl.setForeground(this.activeTheme.cForegroundFilter);
            }
            return 3;
        }
        return 0;
    }

    public void addThemeChangeListener(IThemeChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeThemeChangeListener(IThemeChangeListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyThemeChangeListeners(String themeId) {
        for (IThemeChangeListener listener : this.listeners) {
            listener.onThemeChange(themeId);
        }
    }
}


