package com.pnfsoftware.jeb.rcpclient.util.regex;

public abstract interface IValueProvider {
    public abstract String getString(Object paramObject);

    public abstract String getStringAt(Object paramObject, int paramInt);
}

