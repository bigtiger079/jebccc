package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

public interface IMPanel extends IMPanelElement {
    IMPanelElement getFirstElement();

    IMPanelElement getSecondElement();

    boolean isVertical();

    int getSplitRatio();

    boolean setSplitRatio(int paramInt);
}


