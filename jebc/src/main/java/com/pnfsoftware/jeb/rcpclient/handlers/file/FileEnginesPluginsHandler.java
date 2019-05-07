
package com.pnfsoftware.jeb.rcpclient.handlers.file;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.IEnginesPlugin;
import com.pnfsoftware.jeb.core.IPluginInformation;
import com.pnfsoftware.jeb.core.Version;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.DataFrameDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;

import java.util.List;


public class FileEnginesPluginsHandler
        extends JebBaseHandler {

    public FileEnginesPluginsHandler() {

        super(null, "Plugins...", null, null);

    }


    public boolean canExecute() {

        return this.context.getEnginesContext() != null;

    }


    public void execute() {

        IEnginesContext engctx = this.context.getEnginesContext();

        if (engctx == null) {

            return;

        }


        List<IEnginesPlugin> plugins = engctx.getEnginesPlugins();

        DataFrame df = new DataFrame(new String[]{S.s(591), S.s(268), S.s(818), S.s(86)});

        for (IEnginesPlugin p : plugins) {

            String name = p.getClass().getName();

            String description = null;

            String author = null;

            String version = null;


            IPluginInformation pi = p.getPluginInformation();

            if (pi != null) {

                name = pi.getName();

                description = pi.getDescription();

                author = pi.getAuthor();

                version = pi.getVersion().toString();

            }


            df.addRow(new Object[]{name, description, version, author});

        }


        DataFrameDialog dlg = new DataFrameDialog(this.shell, S.s(648), true, "enginesPluginsDialog");

        dlg.setDataFrame(df);

        int index = dlg.open().intValue();

        if ((index >= 0) && (index < plugins.size())) {

            IEnginesPlugin plugin = (IEnginesPlugin) plugins.get(index);

            FileEnginesExecutepluginHandler.executePlugin(this.shell, this.context, plugin);

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesPluginsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */