package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
import org.eclipse.swt.widgets.Control;

public abstract interface IRcpUnitFragment
        extends IOperable, IUnitFragment {
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\IRcpUnitFragment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */