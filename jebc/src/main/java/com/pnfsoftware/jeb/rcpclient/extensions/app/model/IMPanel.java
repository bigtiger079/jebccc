package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

public abstract interface IMPanel
        extends IMPanelElement {
    public abstract IMPanelElement getFirstElement();

    public abstract IMPanelElement getSecondElement();

    public abstract boolean isVertical();

    public abstract int getSplitRatio();

    public abstract boolean setSplitRatio(int paramInt);
}


