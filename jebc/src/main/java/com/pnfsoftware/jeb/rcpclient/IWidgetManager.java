package com.pnfsoftware.jeb.rcpclient;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public abstract interface IWidgetManager {
    public abstract void setShouldShowDialog(String paramString, boolean paramBoolean);

    public abstract boolean getShouldShowDialog(String paramString);

    public abstract void setRecordedBounds(int paramInt, Rectangle paramRectangle);

    public abstract Rectangle getRecordedBounds(int paramInt);

    public abstract void wrapWidget(Control paramControl, String paramString);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\IWidgetManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */