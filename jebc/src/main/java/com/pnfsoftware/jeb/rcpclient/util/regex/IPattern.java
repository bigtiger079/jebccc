package com.pnfsoftware.jeb.rcpclient.util.regex;

public interface IPattern {
    boolean match(Object paramObject, Object[] paramArrayOfObject);

    IPattern createInstance(String paramString);
}


