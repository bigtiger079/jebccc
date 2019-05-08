package com.pnfsoftware.jeb.rcpclient.extensions.app;

import java.util.EventListener;

public abstract interface IDockListener extends EventListener {
    public abstract void folderAdded(Folder paramFolder);

    public abstract void folderRemoving(Folder paramFolder);
}


