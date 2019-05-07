package com.pnfsoftware.jeb.rcpclient.util;

public abstract interface IPersistenceProvider {
    public abstract void save(String paramString1, String paramString2);

    public abstract String load(String paramString);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\IPersistenceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */