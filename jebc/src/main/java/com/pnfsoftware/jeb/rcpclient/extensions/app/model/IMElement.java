package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.List;

public interface IMElement {
    String getElementId();

    void setElementId(String paramString);

    IMElement getParentElement();

    List<? extends IMElement> getChildrenElements();
}


