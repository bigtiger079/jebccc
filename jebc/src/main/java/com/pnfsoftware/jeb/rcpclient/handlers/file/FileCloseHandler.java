
package com.pnfsoftware.jeb.rcpclient.handlers.file;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;


public class FileCloseHandler
        extends JebBaseHandler {

    public FileCloseHandler() {

        super(null, S.s(466), "Close the currently opened project", null);

    }


    public boolean canExecute() {

        return this.context.hasOpenedProject();

    }


    public void execute() {

        this.context.attemptCloseOpenedProject(this.shell);

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileCloseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */