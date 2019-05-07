package com.pnfsoftware.jeb.rcpclient.util.regex;

public abstract interface IPattern {
    public abstract boolean match(Object paramObject, Object[] paramArrayOfObject);

    public abstract IPattern createInstance(String paramString);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\regex\IPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */