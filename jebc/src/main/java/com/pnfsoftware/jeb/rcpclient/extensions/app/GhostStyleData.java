
package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.rcpclient.util.ColorsGradient;
import org.eclipse.swt.graphics.Color;

public class GhostStyleData {
    public Color cGhostForeground;
    public Color cDropzoneActiveBackground;
    public Color cDropzoneForeground;

    public static GhostStyleData buildDefault() {
        GhostStyleData r = new GhostStyleData();
        r.cGhostForeground = get("gray 31");
        r.cDropzoneActiveBackground = get("peachpuff 1 (peachpuff)");
        r.cDropzoneForeground = get("orange");
        return r;
    }

    private static Color get(String name) {
        return SwtRegistry.getInstance().getColor(ColorsGradient.get(name));
    }
}


