package com.pnfsoftware.jeb.rcpclient.util.regex;

public interface IPattern {
    public abstract boolean match(Object paramObject, Object[] paramArrayOfObject);

    public abstract IPattern createInstance(String paramString);
}


