package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

public abstract interface IMDock extends IMElement {
    public abstract IMFolder getInitialFolder();

    public abstract IMPanel getPanelElement();
}


