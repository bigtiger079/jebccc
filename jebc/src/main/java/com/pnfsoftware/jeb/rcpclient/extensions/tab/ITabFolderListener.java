package com.pnfsoftware.jeb.rcpclient.extensions.tab;

public abstract interface ITabFolderListener {
    public abstract void tabAdded(TabFolderEvent paramTabFolderEvent);

    public abstract void tabRemoved(TabFolderEvent paramTabFolderEvent);
}


