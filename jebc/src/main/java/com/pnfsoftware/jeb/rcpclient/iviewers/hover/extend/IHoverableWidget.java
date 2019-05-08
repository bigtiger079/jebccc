package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

import org.eclipse.swt.widgets.Composite;

public abstract interface IHoverableWidget {
    public abstract void buildWidget(Composite paramComposite);

    public abstract void setInput(Object paramObject);

    public abstract boolean hasContents();
}


