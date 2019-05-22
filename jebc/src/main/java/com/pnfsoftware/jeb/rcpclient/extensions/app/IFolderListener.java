package com.pnfsoftware.jeb.rcpclient.extensions.app;

import java.util.EventListener;

public interface IFolderListener extends EventListener {
    void partSelected(Part paramPart);

    void partAdded(Part paramPart);

    void partHidden(Part paramPart);

    void partVisible(Part paramPart);

    void partRemoved(Part paramPart);

    void partMoved(Folder paramFolder, Part paramPart);
}


