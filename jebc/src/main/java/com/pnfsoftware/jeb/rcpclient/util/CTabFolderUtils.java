
package com.pnfsoftware.jeb.rcpclient.util;


import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class CTabFolderUtils {

    public static void setCTabFolderHeight(CTabFolder folderWidget, int height) {

        try {

            folderWidget.setRedraw(false);

            ToolBar tb = new ToolBar(folderWidget, 8388608);

            ToolItem item = new ToolItem(tb, 8);

            item.setText(" ");


            Point minSize = tb.computeSize(-1, -1);

            tb.dispose();

            folderWidget.setTabHeight(height > minSize.y ? height : minSize.y);

        } finally {

            folderWidget.setRedraw(true);

        }

    }

}


