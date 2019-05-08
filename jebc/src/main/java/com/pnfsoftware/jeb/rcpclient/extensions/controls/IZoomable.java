package com.pnfsoftware.jeb.rcpclient.extensions.controls;

public abstract interface IZoomable {
    public abstract int getZoomLevel();

    public abstract boolean applyZoom(int paramInt, boolean paramBoolean);
}


