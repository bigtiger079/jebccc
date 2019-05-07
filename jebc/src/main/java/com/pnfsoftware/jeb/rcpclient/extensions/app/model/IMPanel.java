package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

public abstract interface IMPanel
        extends IMPanelElement {
    public abstract IMPanelElement getFirstElement();

    public abstract IMPanelElement getSecondElement();

    public abstract boolean isVertical();

    public abstract int getSplitRatio();

    public abstract boolean setSplitRatio(int paramInt);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\model\IMPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */