
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileScriptsExecutelastHandler
        extends JebBaseHandler {
    public FileScriptsExecutelastHandler() {
        super("runLastScript", S.s(553), 0, null, null, 16777227);
    }

    public boolean canExecute() {
        return this.context.getEnginesContext() != null;
    }

    public void execute() {
        String path = this.context.getLastExecutedScript();
        if (path == null) {
            path = FileScriptsExecuteHandler.askForScriptPath(this.shell, this.context);
        }
        if (path != null) {
            this.context.executeScript(this.shell, path);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileScriptsExecutelastHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */