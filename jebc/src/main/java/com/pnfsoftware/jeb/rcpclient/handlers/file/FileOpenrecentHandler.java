/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class FileOpenrecentHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */ String path;

    /*    */
    /*    */
    public FileOpenrecentHandler()
    /*    */ {
        /* 21 */
        super(null, "Clear the recent files list", null, null);
        /*    */
    }

    /*    */
    /*    */
    public FileOpenrecentHandler(String path) {
        /* 25 */
        super(null, path, null, null);
        /* 26 */
        this.path = path;
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 31 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 36 */
        if (this.path != null)
            /*    */ {
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /* 46 */
            this.context.loadInputAsProject(this.shell, this.path);
            /*    */
        }
        /*    */
        else {
            /* 49 */
            this.context.clearRecentlyOpenedFiles();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileOpenrecentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */