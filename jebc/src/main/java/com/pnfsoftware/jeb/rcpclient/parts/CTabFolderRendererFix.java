
package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class CTabFolderRendererFix
        extends CTabFolderRenderer {
    private static final ILogger logger = GlobalLog.getLogger(CTabFolderRendererFix.class);

    public CTabFolderRendererFix(CTabFolder parent) {
        super(parent);
    }

    protected void draw(int part, int state, Rectangle bounds, GC gc) {
        try {
            super.draw(part, state, bounds, gc);
        } catch (Exception e) {
            logger.catching(e);
        }
    }
}


