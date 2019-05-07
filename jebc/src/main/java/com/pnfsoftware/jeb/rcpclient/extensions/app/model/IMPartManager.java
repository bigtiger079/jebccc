package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import org.eclipse.swt.widgets.Composite;

public abstract interface IMPartManager {
    public abstract void createView(Composite paramComposite, IMPart paramIMPart);

    public abstract void deleteView();

    public abstract void setFocus();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\model\IMPartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */