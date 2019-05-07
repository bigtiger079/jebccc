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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\model\IMFolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */