package com.pnfsoftware.jeb.rcpclient.extensions.app;

import java.util.EventListener;

public interface IDockListener extends EventListener {
    void folderAdded(Folder paramFolder);

    void folderRemoving(Folder paramFolder);
}


