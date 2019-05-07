/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.IEnginesContext;
/*    */ import com.pnfsoftware.jeb.core.IEnginesPlugin;
/*    */ import com.pnfsoftware.jeb.core.IOptionDefinition;
/*    */ import com.pnfsoftware.jeb.core.IPluginInformation;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.OptionsForEnginesPluginDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.eclipse.swt.widgets.Shell;

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
/*    */ public class FileEnginesExecutepluginHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */ String name;
    /*    */ IEnginesPlugin plugin;

    /*    */
    /*    */
    public FileEnginesExecutepluginHandler(String name, IEnginesPlugin plugin)
    /*    */ {
        /* 36 */
        super(null, name, null, null);
        /* 37 */
        this.plugin = plugin;
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 42 */
        return this.context.getEnginesContext() != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 47 */
        IEnginesContext engctx = this.context.getEnginesContext();
        /* 48 */
        if (engctx == null) {
            /* 49 */
            return;
            /*    */
        }
        /* 51 */
        executePlugin(this.shell, this.context, this.plugin);
        /*    */
    }

    /*    */
    /*    */
    public static void executePlugin(Shell shell, RcpClientContext context, IEnginesPlugin plugin) {
        /* 55 */
        final IEnginesContext engctx = context.getEnginesContext();
        /* 56 */
        if (engctx == null) {
            /* 57 */
            return;
            /*    */
        }
        /*    */
        /* 60 */
        final Map<String, String> options = readUserOptions(shell, context, plugin);
        /* 61 */
        if (options == null) {
            /* 62 */
            return;
            /*    */
        }
        /*    */
        /* 65 */
        IPluginInformation pinfo = plugin.getPluginInformation();
        /* 66 */
        String pname = Strings.safe2(pinfo == null ? null : pinfo.getName(), plugin.getClass().getName());
        /* 67 */
        String taskName = String.format("%s: \"%s\"...", new Object[]{S.s(316), pname});
        /*    */
        /* 69 */
        context.getTelemetry().record("handlerExecuteEnginesPlugin");
        /*    */
        /* 71 */
        context.executeTask(taskName, new Runnable()
                /*    */ {
            /*    */
            public void run() {
                /* 74 */
                this.val$plugin.execute(engctx, options);
                /*    */
            }
            /*    */
        });
        /*    */
    }

    /*    */
    /*    */
    private static Map<String, String> readUserOptions(Shell shell, RcpClientContext context, IEnginesPlugin plugin)
    /*    */ {
        /* 81 */
        List<? extends IOptionDefinition> optlist = plugin.getExecutionOptionDefinitions();
        /* 82 */
        if ((optlist != null) && (!optlist.isEmpty())) {
            /* 83 */
            OptionsForEnginesPluginDialog dlg = new OptionsForEnginesPluginDialog(shell, plugin);
            /* 84 */
            return dlg.open();
            /*    */
        }
        /*    */
        /* 87 */
        return new HashMap();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileEnginesExecutepluginHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */