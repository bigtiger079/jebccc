package com.pnfsoftware.jeb.rcpclient.extensions.tab;

public class TabFolderEvent {
    public TabFolderView manager;
    public String name;

    public TabFolderEvent(TabFolderView manager, String name) {
        this.manager = manager;
        this.name = name;
    }
}


