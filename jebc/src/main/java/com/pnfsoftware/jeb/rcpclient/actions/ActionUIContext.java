package com.pnfsoftware.jeb.rcpclient.actions;

import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.core.actions.ActionContext;

public class ActionUIContext {
    private ActionContext actionContext;
    private IUnitFragment fragment;

    public ActionUIContext(ActionContext actionContext, IUnitFragment fragment) {
        this.actionContext = actionContext;
        this.fragment = fragment;
    }

    public ActionContext getActionContext() {
        return this.actionContext;
    }

    public IUnitFragment getFragment() {
        return this.fragment;
    }

    public String toString() {
        return String.format("%s{%s}", new Object[]{getActionContext(), getFragment()});
    }
}
