package com.pnfsoftware.jeb.rcpclient.extensions.app;

import java.util.EventListener;

public abstract interface IDockListener
        extends EventListener {
    public abstract void folderAdded(Folder paramFolder);

    public abstract void folderRemoving(Folder paramFolder);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\IDockListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */