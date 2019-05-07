/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
/*    */ import org.eclipse.jface.action.IMenuManager;
/*    */ import org.eclipse.jface.action.Separator;

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
/*    */ public class FileOpenrecentMenuHandler
        /*    */ extends AbstractDynamicMenuHandler
        /*    */ {
    /*    */
    public void menuAboutToShow(IMenuManager manager)
    /*    */ {
        /* 28 */
        if (!canExecute()) {
            /* 29 */
            return;
            /*    */
        }
        /*    */
        /* 32 */
        int cnt = 0;
        /* 33 */
        for (String path : this.context.getRecentlyOpenedFiles()) {
            /* 34 */
            if (path.length() != 0)
                /*    */ {
                /*    */
                /* 37 */
                manager.add(new FileOpenrecentHandler(path));
                /* 38 */
                cnt++;
                /*    */
            }
            /*    */
        }
        /* 41 */
        if (cnt > 0) {
            /* 42 */
            manager.add(new Separator());
            /* 43 */
            manager.add(new FileOpenrecentHandler());
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileOpenrecentMenuHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */