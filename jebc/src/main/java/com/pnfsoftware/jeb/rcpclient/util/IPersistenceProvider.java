package com.pnfsoftware.jeb.rcpclient.util;

public interface IPersistenceProvider {
    public abstract void save(String paramString1, String paramString2);

    public abstract String load(String paramString);
}


