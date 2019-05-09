package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.Map;

public interface IMPart extends IMElementWithLabel {
    IMFolder getParentElement();

    Map<String, Object> getData();

    boolean isCloseOnHide();

    void setCloseOnHide(boolean paramBoolean);

    boolean isHidden();

    boolean isHideable();

    void setHideable(boolean paramBoolean);

    void setManager(IMPartManager paramIMPartManager);

    IMPartManager getManager();
}


