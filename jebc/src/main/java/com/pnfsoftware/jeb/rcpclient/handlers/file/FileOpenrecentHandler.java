package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

public class FileOpenrecentHandler extends JebBaseHandler {
    String path;

    public FileOpenrecentHandler() {
        super(null, "Clear the recent files list", null, null);
    }

    public FileOpenrecentHandler(String path) {
        super(null, path, null, null);
        this.path = path;
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        if (this.path != null) {
            this.context.loadInputAsProject(this.shell, this.path);
        } else {
            this.context.clearRecentlyOpenedFiles();
        }
    }
}


