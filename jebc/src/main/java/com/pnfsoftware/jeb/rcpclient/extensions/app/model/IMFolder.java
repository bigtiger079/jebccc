package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.List;

public abstract interface IMFolder
        extends IMPanelElement {
    public abstract int getPartsCount();

    public abstract List<? extends IMPart> getParts();

    public abstract IMPart addPart();

    public abstract IMPart addPart(int paramInt);

    public abstract boolean isCloseOnEmpty();

    public abstract void setCloseOnEmpty(boolean paramBoolean);
}


