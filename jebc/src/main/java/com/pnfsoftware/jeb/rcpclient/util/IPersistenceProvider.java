package com.pnfsoftware.jeb.rcpclient.util;

public interface IPersistenceProvider {
    void save(String paramString1, String paramString2);

    String load(String paramString);
}


