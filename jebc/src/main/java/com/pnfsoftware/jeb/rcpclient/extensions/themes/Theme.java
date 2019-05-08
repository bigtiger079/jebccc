
package com.pnfsoftware.jeb.rcpclient.extensions.themes;

import org.eclipse.swt.graphics.Color;

public abstract class Theme {
    String id;
    String name;
    Color cBackground;
    Color cForeground;
    Color cBackgroundSelectedTab;
    Color cForegroundSelectedTab;
    Color cBackgroundTableHeader;
    Color cForegroundTableHeader;
    Color cBackgroundFilter;
    Color cForegroundFilter;
    Color cBackgroundReadOnlyText;
    Color cForegroundReadOnlyText;

    public Theme(String id, String name) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return String.format("Theme{%s}", new Object[]{this.id});
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\themes\Theme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */