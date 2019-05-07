package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.List;

public abstract interface IMElement {
    public abstract String getElementId();

    public abstract void setElementId(String paramString);

    public abstract IMElement getParentElement();

    public abstract List<? extends IMElement> getChildrenElements();
}


