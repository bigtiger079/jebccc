package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.units.code.ISourceUnit;

import java.util.List;

public abstract interface IFileExport<T> {
    public abstract List<? extends T> getItems();

    public abstract boolean canProcess(T paramT);

    public abstract String getFullName(T paramT);

    public abstract boolean isAtAddress(T paramT, String paramString);

    public abstract List<String> getPath(T paramT);

    public abstract String getNameFromSourceUnit(ISourceUnit paramISourceUnit);
}


