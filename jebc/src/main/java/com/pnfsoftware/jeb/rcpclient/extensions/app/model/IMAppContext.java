package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import com.pnfsoftware.jeb.rcpclient.extensions.app.App;

public abstract interface IMAppContext {
    public abstract void onApplicationBuilt(App paramApp);

    public abstract void onApplicationReady(App paramApp);

    public abstract boolean onApplicationException(Exception paramException);

    public abstract boolean onApplicationCloseAttempt(App paramApp);

    public abstract void onApplicationClose(App paramApp);
}