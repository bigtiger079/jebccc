package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.client.AbstractContext;
import com.pnfsoftware.jeb.core.Version;
import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
import com.pnfsoftware.jeb.util.base.OSType;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Shell;

public class JebApp extends App {
    String[] args;
    RcpClientContext context;
    public Folder folderWorkspace;
    public Folder folderProject;
    public Folder folderConsoles;

    public JebApp(String[] args) {
        super("JEB", 4);
        this.args = args;
        this.context = new RcpClientContext();
        if (OSType.determine().isMac()) {
            try {
                new CocoaUIEnhancer("JEB").hookApplicationMenu(this.context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onPreDisplayCreation(String name, String version) {
        super.onPreDisplayCreation("JEB", AbstractContext.app_ver.toString());
    }

    protected void customizeShell(Shell shell) {
    }

    public String[] getArguments() {
        return this.args;
    }

    public void onApplicationBuilt() {
        this.context.onApplicationBuilt(this);
    }

    public void onApplicationReady() {
        this.context.onApplicationReady(this);
    }

    public boolean onApplicationException(Exception e) {
        return this.context.onApplicationException(e);
    }

    public void onApplicationClose() {
        this.context.onApplicationClose(this);
    }

    public boolean onApplicationCloseAttempt() {
        return this.context.onApplicationCloseAttempt(this);
    }

    protected void buildShell(Shell shell) {
    }

    protected void buildMenu(MenuManager menuManager) {
    }

    protected void buildToolbar(ToolBarManager toolbarManager) {
    }

    protected void buildDock(Dock dock) {
    }
}


