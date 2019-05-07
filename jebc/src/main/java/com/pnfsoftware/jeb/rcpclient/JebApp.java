/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import com.pnfsoftware.jeb.client.AbstractContext;
/*    */ import com.pnfsoftware.jeb.core.Version;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
/*    */ import com.pnfsoftware.jeb.util.base.OSType;
/*    */ import org.eclipse.jface.action.MenuManager;
/*    */ import org.eclipse.jface.action.ToolBarManager;
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
/*    */ public class JebApp
        /*    */ extends App
        /*    */ {
    /*    */ String[] args;
    /*    */ RcpClientContext context;
    /*    */   public Folder folderWorkspace;
    /*    */   public Folder folderProject;
    /*    */   public Folder folderConsoles;

    /*    */
    /*    */
    public JebApp(String[] args)
    /*    */ {
        /* 33 */
        super("JEB", 4);
        /*    */
        /* 35 */
        this.args = args;
        /* 36 */
        this.context = new RcpClientContext();
        /*    */
        /* 38 */
        if (OSType.determine().isMac()) {
            /*    */
            try
                /*    */ {
                /* 41 */
                new CocoaUIEnhancer("JEB").hookApplicationMenu(this.context);
                /*    */
            }
            /*    */ catch (Exception e) {
                /* 44 */
                e.printStackTrace();
                /*    */
            }
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    /*    */
    protected void onPreDisplayCreation(String name, String version)
    /*    */ {
        /* 52 */
        super.onPreDisplayCreation("JEB", AbstractContext.app_ver.toString());
        /*    */
    }

    /*    */
    /*    */
    /*    */
    protected void customizeShell(Shell shell) {
    }

    /*    */
    /*    */
    /*    */
    public String[] getArguments()
    /*    */ {
        /* 61 */
        return this.args;
        /*    */
    }

    /*    */
    /*    */
    public void onApplicationBuilt()
    /*    */ {
        /* 66 */
        this.context.onApplicationBuilt(this);
        /*    */
    }

    /*    */
    /*    */
    public void onApplicationReady()
    /*    */ {
        /* 71 */
        this.context.onApplicationReady(this);
        /*    */
    }

    /*    */
    /*    */
    public boolean onApplicationException(Exception e)
    /*    */ {
        /* 76 */
        return this.context.onApplicationException(e);
        /*    */
    }

    /*    */
    /*    */
    public void onApplicationClose()
    /*    */ {
        /* 81 */
        this.context.onApplicationClose(this);
        /*    */
    }

    /*    */
    /*    */
    public boolean onApplicationCloseAttempt()
    /*    */ {
        /* 86 */
        return this.context.onApplicationCloseAttempt(this);
        /*    */
    }

    /*    */
    /*    */
    protected void buildShell(Shell shell) {
    }

    /*    */
    /*    */
    protected void buildMenu(MenuManager menuManager) {
    }

    /*    */
    /*    */
    protected void buildToolbar(ToolBarManager toolbarManager) {
    }

    /*    */
    /*    */
    protected void buildDock(Dock dock) {
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\JebApp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */