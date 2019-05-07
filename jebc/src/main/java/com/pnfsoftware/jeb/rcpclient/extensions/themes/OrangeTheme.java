
package com.pnfsoftware.jeb.rcpclient.extensions.themes;


import com.pnfsoftware.jeb.rcpclient.UIAssetManager;


public class OrangeTheme
        extends Theme {
    private static final UIAssetManager ui = UIAssetManager.getInstance();

    int normal = 12531212;
    int light = 16345146;
    int dark = 8847360;
    int text = 16119285;

    int normal2 = 16088064;
    int light2 = 16756034;
    int dark2 = 12274944;
    int text2 = 16119285;


    public OrangeTheme() {

        super("theme.orange", "Orange Theme");


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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\themes\OrangeTheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */