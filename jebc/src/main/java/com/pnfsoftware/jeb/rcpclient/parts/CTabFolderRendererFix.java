/*    */
package com.pnfsoftware.jeb.rcpclient.parts;
/*    */
/*    */

import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import org.eclipse.swt.custom.CTabFolder;
/*    */ import org.eclipse.swt.custom.CTabFolderRenderer;
/*    */ import org.eclipse.swt.graphics.GC;
/*    */ import org.eclipse.swt.graphics.Rectangle;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class CTabFolderRendererFix
        /*    */ extends CTabFolderRenderer
        /*    */ {
    /* 24 */   private static final ILogger logger = GlobalLog.getLogger(CTabFolderRendererFix.class);

    /*    */
    /*    */
    public CTabFolderRendererFix(CTabFolder parent) {
        /* 27 */
        super(parent);
        /*    */
    }

    /*    */
    /*    */
    protected void draw(int part, int state, Rectangle bounds, GC gc)
    /*    */ {
        /*    */
        try {
            /* 33 */
            super.draw(part, state, bounds, gc);
            /*    */
        }
        /*    */ catch (Exception e) {
            /* 36 */
            logger.catching(e);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\CTabFolderRendererFix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */