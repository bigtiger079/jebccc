package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
import org.eclipse.swt.widgets.Control;

public interface IRcpUnitFragment extends IOperable, IUnitFragment {
    void setViewNavigatorHelper(IViewNavigator paramIViewNavigator);

    IViewNavigator getViewNavigatorHelper();

    Control getFragmentControl();

    IStatusIndicator getStatusIndicator();

    IViewManager getViewManager();

    boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);

    Position getActivePosition();

    String getComment();

    boolean isDefaultFragment();

    int getFocusPriority();

    boolean isValidActiveAddress(String paramString, Object paramObject);
}


