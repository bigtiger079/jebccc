package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import org.eclipse.swt.widgets.Composite;

public interface IMPartManager {
    void createView(Composite paramComposite, IMPart paramIMPart);

    void deleteView();

    void setFocus();
}


