package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.List;

public interface IMFolder extends IMPanelElement {
    int getPartsCount();

    List<? extends IMPart> getParts();

    IMPart addPart();

    IMPart addPart(int paramInt);

    boolean isCloseOnEmpty();

    void setCloseOnEmpty(boolean paramBoolean);
}


