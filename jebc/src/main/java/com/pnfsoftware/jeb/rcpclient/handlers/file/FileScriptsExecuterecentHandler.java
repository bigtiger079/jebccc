
package com.pnfsoftware.jeb.rcpclient.handlers.file;


import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;


public class FileScriptsExecuterecentHandler
        extends JebBaseHandler {
    String path;


    public FileScriptsExecuterecentHandler(String path) {

        super(null, path, null, null);

        this.path = path;

    }


    public boolean canExecute() {

        return this.context.getEnginesContext() != null;

    }


    public void execute() {

        this.context.executeScript(this.shell, this.path);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileScriptsExecuterecentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */