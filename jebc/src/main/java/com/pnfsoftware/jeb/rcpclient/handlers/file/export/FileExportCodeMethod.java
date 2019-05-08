package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;

import java.util.ArrayList;
import java.util.List;

public class FileExportCodeMethod implements IFileExport<ICodeMethod> {
    private List<? extends ICodeMethod> allMethods;

    public FileExportCodeMethod(List<? extends ICodeMethod> allMethods) {
        this.allMethods = allMethods;
    }

    public List<? extends ICodeMethod> getItems() {
        return this.allMethods;
    }

    public boolean canProcess(ICodeMethod item) {
        return true;
    }

    public List<String> getPath(ICodeMethod item) {
        return new ArrayList();
    }

    public String getFullName(ICodeMethod c) {
        return c.getName(true);
    }

    public boolean isAtAddress(ICodeMethod item, String address) {
        return address.equals(item.getAddress());
    }

    public String getNameFromSourceUnit(ISourceUnit sourceUnit) {
        return sourceUnit.getName();
    }
}


