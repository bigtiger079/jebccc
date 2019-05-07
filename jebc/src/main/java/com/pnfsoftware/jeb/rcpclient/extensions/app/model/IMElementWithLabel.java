package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import org.eclipse.swt.graphics.Image;

public abstract interface IMElementWithLabel
        extends IMElement {
    public abstract String getLabel();

    public abstract void setLabel(String paramString);

    public abstract String getTooltip();

    public abstract void setTooltip(String paramString);

    public abstract Image getIcon();

    public abstract void setIcon(Image paramImage);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\model\IMElementWithLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */