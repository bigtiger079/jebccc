/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import org.eclipse.swt.custom.CTabFolder;
/*    */ import org.eclipse.swt.graphics.Point;
/*    */ import org.eclipse.swt.widgets.ToolBar;
/*    */ import org.eclipse.swt.widgets.ToolItem;

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
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class CTabFolderUtils
        /*    */ {
    /*    */
    public static void setCTabFolderHeight(CTabFolder folderWidget, int height)
    /*    */ {
        /*    */
        try
            /*    */ {
            /* 34 */
            folderWidget.setRedraw(false);
            /* 35 */
            ToolBar tb = new ToolBar(folderWidget, 8388608);
            /* 36 */
            ToolItem item = new ToolItem(tb, 8);
            /* 37 */
            item.setText(" ");
            /*    */
            /* 39 */
            Point minSize = tb.computeSize(-1, -1);
            /* 40 */
            tb.dispose();
            /* 41 */
            folderWidget.setTabHeight(height > minSize.y ? height : minSize.y);
            /*    */
        }
        /*    */ finally {
            /* 44 */
            folderWidget.setRedraw(true);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\CTabFolderUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */