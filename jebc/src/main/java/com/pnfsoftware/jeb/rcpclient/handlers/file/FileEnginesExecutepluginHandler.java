package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.IEnginesPlugin;
import com.pnfsoftware.jeb.core.IOptionDefinition;
import com.pnfsoftware.jeb.core.IPluginInformation;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.OptionsForEnginesPluginDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

public class FileEnginesExecutepluginHandler extends JebBaseHandler {
    String name;
    IEnginesPlugin plugin;

    public FileEnginesExecutepluginHandler(String name, IEnginesPlugin plugin) {
        super(null, name, null, null);
        this.plugin = plugin;
    }

    public boolean canExecute() {
        return this.context.getEnginesContext() != null;
    }

    public void execute() {
        IEnginesContext engctx = this.context.getEnginesContext();
        if (engctx == null) {
            return;
        }
        executePlugin(this.shell, this.context, this.plugin);
    }

    public static void executePlugin(Shell shell, RcpClientContext context, IEnginesPlugin plugin) {
        final IEnginesContext engctx = context.getEnginesContext();
        if (engctx == null) {
            return;
        }
        final Map<String, String> options = readUserOptions(shell, context, plugin);
        if (options == null) {
            return;
        }
        IPluginInformation pinfo = plugin.getPluginInformation();
        String pname = Strings.safe2(pinfo == null ? null : pinfo.getName(), plugin.getClass().getName());
        String taskName = String.format("%s: \"%s\"...", S.s(316), pname);
        context.getTelemetry().record("handlerExecuteEnginesPlugin");
        context.executeTask(taskName, new Runnable() {
            public void run() {
                plugin.execute(engctx, options);
            }
        });
    }

    private static Map<String, String> readUserOptions(Shell shell, RcpClientContext context, IEnginesPlugin plugin) {
        List<? extends IOptionDefinition> optlist = plugin.getExecutionOptionDefinitions();
        if ((optlist != null) && (!optlist.isEmpty())) {
            OptionsForEnginesPluginDialog dlg = new OptionsForEnginesPluginDialog(shell, plugin);
            return dlg.open();
        }
        return new HashMap();
    }
}


