package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.rcpclient.IViewManager;

public interface IViewNavigator {
    boolean canHandleAddress(String paramString);

    boolean navigateTo(String paramString, IViewManager paramIViewManager, boolean paramBoolean);

    boolean navigateTo(IActionableItem paramIActionableItem, IViewManager paramIViewManager, boolean paramBoolean);
}


