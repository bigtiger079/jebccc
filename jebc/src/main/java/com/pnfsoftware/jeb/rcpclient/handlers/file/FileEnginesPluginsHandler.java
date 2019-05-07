/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.IEnginesContext;
/*    */ import com.pnfsoftware.jeb.core.IEnginesPlugin;
/*    */ import com.pnfsoftware.jeb.core.IPluginInformation;
/*    */ import com.pnfsoftware.jeb.core.Version;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.DataFrameDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
/*    */ import java.util.List;

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
/*    */ public class FileEnginesPluginsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileEnginesPluginsHandler()
    /*    */ {
        /* 28 */
        super(null, "Plugins...", null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 33 */
        return this.context.getEnginesContext() != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 38 */
        IEnginesContext engctx = this.context.getEnginesContext();
        /* 39 */
        if (engctx == null) {
            /* 40 */
            return;
            /*    */
        }
        /*    */
        /* 43 */
        List<IEnginesPlugin> plugins = engctx.getEnginesPlugins();
        /* 44 */
        DataFrame df = new DataFrame(new String[]{S.s(591), S.s(268), S.s(818), S.s(86)});
        /* 45 */
        for (IEnginesPlugin p : plugins) {
            /* 46 */
            String name = p.getClass().getName();
            /* 47 */
            String description = null;
            /* 48 */
            String author = null;
            /* 49 */
            String version = null;
            /*    */
            /* 51 */
            IPluginInformation pi = p.getPluginInformation();
            /* 52 */
            if (pi != null) {
                /* 53 */
                name = pi.getName();
                /* 54 */
                description = pi.getDescription();
                /* 55 */
                author = pi.getAuthor();
                /* 56 */
                version = pi.getVersion().toString();
                /*    */
            }
            /*    */
            /* 59 */
            df.addRow(new Object[]{name, description, version, author});
            /*    */
        }
        /*    */
        /* 62 */
        DataFrameDialog dlg = new DataFrameDialog(this.shell, S.s(648), true, "enginesPluginsDialog");
        /* 63 */
        dlg.setDataFrame(df);
        /* 64 */
        int index = dlg.open().intValue();
        /* 65 */
        if ((index >= 0) && (index < plugins.size())) {
            /* 66 */
            IEnginesPlugin plugin = (IEnginesPlugin) plugins.get(index);
            /* 67 */
            FileEnginesExecutepluginHandler.executePlugin(this.shell, this.context, plugin);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesPluginsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */