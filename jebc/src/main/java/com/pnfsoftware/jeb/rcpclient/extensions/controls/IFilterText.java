package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;

public interface IFilterText {
    void addFocusListener(FocusListener paramFocusListener);

    void addKeyListener(KeyListener paramKeyListener);

    void setText(String paramString);

    String getText();

    void submitText(String paramString);

    void setStatus(Boolean paramBoolean);
}


