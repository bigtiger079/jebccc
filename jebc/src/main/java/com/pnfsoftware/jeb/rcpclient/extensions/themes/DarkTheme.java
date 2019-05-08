
package com.pnfsoftware.jeb.rcpclient.extensions.themes;

import com.pnfsoftware.jeb.rcpclient.UIAssetManager;

public class DarkTheme
        extends Theme {
    private static final UIAssetManager ui = UIAssetManager.getInstance();
    int normal = 3622479;
    int light = 6451579;
    int dark = 1056551;
    int text = 14737632;
    int normal2 = 2503224;
    int light2 = 5200738;
    int dark2 = 2578;
    int text2 = 16119285;

    public DarkTheme() {
        super("theme.dark", "Dark Theme");
        this.cBackground = ui.getColor(this.normal);
        this.cForeground = ui.getColor(this.text);
        this.cBackgroundSelectedTab = ui.getColor(this.light);
        this.cForegroundSelectedTab = ui.getColor(this.text);
        this.cBackgroundTableHeader = ui.getColor(this.normal2);
        this.cForegroundTableHeader = ui.getColor(this.text2);
        this.cBackgroundFilter = ui.getColor(this.light);
        this.cForegroundFilter = ui.getColor(this.text);
        this.cBackgroundReadOnlyText = ui.getColor(this.light);
        this.cForegroundReadOnlyText = ui.getColor(this.text);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\themes\DarkTheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */