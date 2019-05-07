package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

import org.eclipse.swt.widgets.Composite;

public abstract interface IHoverableWidget {
    public abstract void buildWidget(Composite paramComposite);

    public abstract void setInput(Object paramObject);

    public abstract boolean hasContents();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\hover\extend\IHoverableWidget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */