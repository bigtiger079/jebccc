package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

import org.eclipse.swt.widgets.Composite;

public interface IHoverableWidget {
    void buildWidget(Composite paramComposite);

    void setInput(Object paramObject);

    boolean hasContents();
}


