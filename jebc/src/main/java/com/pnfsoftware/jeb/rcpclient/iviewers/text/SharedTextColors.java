
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

class SharedTextColors
        implements ISharedTextColors {
    public Color getColor(RGB rgb) {
        return UIAssetManager.getInstance().getColor(rgb);
    }

    public void dispose() {
    }
}


