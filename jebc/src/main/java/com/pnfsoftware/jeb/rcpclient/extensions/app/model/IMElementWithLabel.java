package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import org.eclipse.swt.graphics.Image;

public interface IMElementWithLabel extends IMElement {
    String getLabel();

    void setLabel(String paramString);

    String getTooltip();

    void setTooltip(String paramString);

    Image getIcon();

    void setIcon(Image paramImage);
}


