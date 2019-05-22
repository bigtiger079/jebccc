package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.units.code.ISourceUnit;

import java.util.List;

public interface IFileExport<T> {
    List<? extends T> getItems();

    boolean canProcess(T paramT);

    String getFullName(T paramT);

    boolean isAtAddress(T paramT, String paramString);

    List<String> getPath(T paramT);

    String getNameFromSourceUnit(ISourceUnit paramISourceUnit);
}


