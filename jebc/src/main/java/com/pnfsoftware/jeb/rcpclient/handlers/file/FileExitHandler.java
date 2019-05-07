
package com.pnfsoftware.jeb.rcpclient.handlers.file;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;


public class FileExitHandler
        extends JebBaseHandler {

    public FileExitHandler() {

        super(null, S.s(500), null, null);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        this.context.getApp().close();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileExitHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */