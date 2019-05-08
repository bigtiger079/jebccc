package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;

public abstract interface IFilterText {
    public abstract void addFocusListener(FocusListener paramFocusListener);

    public abstract void addKeyListener(KeyListener paramKeyListener);

    public abstract void setText(String paramString);

    public abstract String getText();

    public abstract void submitText(String paramString);

    public abstract void setStatus(Boolean paramBoolean);
}


