package com.pnfsoftware.jeb.rcpclient;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public interface IWidgetManager {
    void setShouldShowDialog(String paramString, boolean paramBoolean);

    boolean getShouldShowDialog(String paramString);

    void setRecordedBounds(int paramInt, Rectangle paramRectangle);

    Rectangle getRecordedBounds(int paramInt);

    void wrapWidget(Control paramControl, String paramString);
}


