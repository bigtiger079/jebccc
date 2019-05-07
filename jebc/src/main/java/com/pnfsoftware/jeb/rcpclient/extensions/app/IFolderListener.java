package com.pnfsoftware.jeb.rcpclient.extensions.app;

import java.util.EventListener;

public abstract interface IFolderListener
        extends EventListener {
    public abstract void partSelected(Part paramPart);

    public abstract void partAdded(Part paramPart);

    public abstract void partHidden(Part paramPart);

    public abstract void partVisible(Part paramPart);

    public abstract void partRemoved(Part paramPart);

    public abstract void partMoved(Folder paramFolder, Part paramPart);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\IFolderListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */