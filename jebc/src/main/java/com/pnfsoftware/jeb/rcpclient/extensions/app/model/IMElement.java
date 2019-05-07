package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.List;

public abstract interface IMElement {
    public abstract String getElementId();

    public abstract void setElementId(String paramString);

    public abstract IMElement getParentElement();

    public abstract List<? extends IMElement> getChildrenElements();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\model\IMElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */