package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import org.eclipse.swt.widgets.Composite;

public abstract interface IMPartManager {
    public abstract void createView(Composite paramComposite, IMPart paramIMPart);

    public abstract void deleteView();

    public abstract void setFocus();
}


