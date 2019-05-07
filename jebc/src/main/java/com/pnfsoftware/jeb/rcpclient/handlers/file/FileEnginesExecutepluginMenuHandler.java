/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.core.IEnginesContext;
/*    */ import com.pnfsoftware.jeb.core.IEnginesPlugin;
/*    */ import com.pnfsoftware.jeb.core.IPluginInformation;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.AbstractDynamicMenuHandler;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import java.util.List;
/*    */ import org.eclipse.jface.action.IMenuManager;

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
/*    */ public class FileEnginesExecutepluginMenuHandler
        /*    */ extends AbstractDynamicMenuHandler
        /*    */ {
    /*    */
    public void menuAboutToShow(IMenuManager manager)
    /*    */ {
        /* 32 */
        if (!canExecute()) {
            /* 33 */
            return;
            /*    */
        }
        /*    */
        /* 36 */
        IEnginesContext engctx = this.context.getEnginesContext();
        /* 37 */
        if (engctx == null) {
            /* 38 */
            return;
            /*    */
        }
        /*    */
        /* 41 */
        List<IEnginesPlugin> enginePlugins = engctx.getEnginesPlugins();
        /* 42 */
        for (IEnginesPlugin p : enginePlugins) {
            /* 43 */
            String classname = p.getClass().getName();
            /* 44 */
            String name = null;
            /* 45 */
            if (p.getPluginInformation() != null) {
                /* 46 */
                name = p.getPluginInformation().getName();
                /*    */
            }
            /* 48 */
            name = Strings.safe(name, classname);
            /* 49 */
            manager.add(new FileEnginesExecutepluginHandler(name, p));
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesExecutepluginMenuHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */