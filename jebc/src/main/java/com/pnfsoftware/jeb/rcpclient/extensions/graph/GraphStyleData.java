package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.rcpclient.util.ColorsGradient;
import org.eclipse.swt.graphics.Color;

public class GraphStyleData {
    public Color cCanvas;
    public Color cEdge;
    public Color cNode;
    public Color cActiveNode;
    public Color cBorder;
    public Color cActiveBorder;
    public Color cBorderShade;
    public Color cActiveBorderShade;

    public static GraphStyleData buildDefault() {
        GraphStyleData r = new GraphStyleData();
        r.cCanvas = get("rosybrown 3");
        r.cEdge = get("indianred");
        r.cNode = get("rosybrown 1");
        r.cActiveNode = get("cadmiumorange");
        r.cBorder = get("royalblue 4");
        r.cActiveBorder = r.cActiveNode;
        r.cBorderShade = get("black");
        r.cActiveBorderShade = r.cBorderShade;
        return r;
    }

    private static Color get(String name) {
        return SwtRegistry.getInstance().getColor(ColorsGradient.get(name));
    }
}


