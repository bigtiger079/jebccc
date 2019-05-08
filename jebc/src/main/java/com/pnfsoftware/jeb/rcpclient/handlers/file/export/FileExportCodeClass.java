package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.units.code.ICodeClass;
import com.pnfsoftware.jeb.core.units.code.ICodePackage;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FileExportCodeClass implements IFileExport<ICodeClass> {
    private List<? extends ICodeClass> allClasses;

    public FileExportCodeClass(List<? extends ICodeClass> allClasses) {
        this.allClasses = allClasses;
    }

    public List<? extends ICodeClass> getItems() {
        return this.allClasses;
    }

    public boolean canProcess(ICodeClass item) {
        return (item.getGenericFlags() & 0x100000) == 0;
    }

    public List<String> getPath(ICodeClass c) {
        List<String> packages = new ArrayList();
        ICodePackage p = c.getPackage();
        while (p != null) {
            if (p.getName(true) != null) {
                packages.add(0, p.getName(true));
            }
            p = p.getParentPackage();
        }
        return packages;
    }

    public String getFullName(ICodeClass c) {
        return getFullPackage(getPath(c)) + "." + c.getName(true);
    }

    public String getNameFromSourceUnit(ISourceUnit sourceUnit) {
        String fullName = sourceUnit.getFullyQualifiedName();
        return fullName.substring(1, fullName.length() - 1).replace('/', '.');
    }

    public static String getFullPackage(List<String> p) {
        return StringUtils.join(p, '.');
    }

    public boolean isAtAddress(ICodeClass c, String address) {
        return address.startsWith(c.getAddress().substring(0, c.getAddress().lastIndexOf(';')));
    }
}


