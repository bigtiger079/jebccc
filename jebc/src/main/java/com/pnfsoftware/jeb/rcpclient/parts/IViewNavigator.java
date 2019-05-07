package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.rcpclient.IViewManager;

public abstract interface IViewNavigator {
    public abstract boolean canHandleAddress(String paramString);

    public abstract boolean navigateTo(String paramString, IViewManager paramIViewManager, boolean paramBoolean);

    public abstract boolean navigateTo(IActionableItem paramIActionableItem, IViewManager paramIViewManager, boolean paramBoolean);
}


