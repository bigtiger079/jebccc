package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
import org.eclipse.swt.widgets.Control;

public interface IRcpUnitFragment extends IOperable, IUnitFragment {
    public abstract void setViewNavigatorHelper(IViewNavigator paramIViewNavigator);

    public abstract IViewNavigator getViewNavigatorHelper();

    public abstract Control getFragmentControl();

    public abstract IStatusIndicator getStatusIndicator();

    public abstract IViewManager getViewManager();

    public abstract boolean setActiveAddress(String paramString, Object paramObject, boolean paramBoolean);

    public abstract Position getActivePosition();

    public abstract String getComment();

    public abstract boolean isDefaultFragment();

    public abstract int getFocusPriority();

    public abstract boolean isValidActiveAddress(String paramString, Object paramObject);
}


