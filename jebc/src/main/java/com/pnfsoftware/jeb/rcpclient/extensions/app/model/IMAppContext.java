package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import com.pnfsoftware.jeb.rcpclient.extensions.app.App;

public interface IMAppContext {
    void onApplicationBuilt(App paramApp);

    void onApplicationReady(App paramApp);

    boolean onApplicationException(Exception paramException);

    boolean onApplicationCloseAttempt(App paramApp);

    void onApplicationClose(App paramApp);
}