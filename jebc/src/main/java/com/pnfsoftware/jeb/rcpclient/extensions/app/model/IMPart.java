package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.Map;

public abstract interface IMPart
        extends IMElementWithLabel {
    public abstract IMFolder getParentElement();

    public abstract Map<String, Object> getData();

    public abstract boolean isCloseOnHide();

    public abstract void setCloseOnHide(boolean paramBoolean);

    public abstract boolean isHidden();

    public abstract boolean isHideable();

    public abstract void setHideable(boolean paramBoolean);

    public abstract void setManager(IMPartManager paramIMPartManager);

    public abstract IMPartManager getManager();
}


