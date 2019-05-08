
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.units.code.ICodeClass;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;

import java.util.List;

public class FileExportFactory {
    public static IFileExport<? extends ICodeItem> get(ICodeUnit codeUnit) {
        List<? extends ICodeClass> allClasses = codeUnit.getClasses();
        if ((allClasses != null) && (!allClasses.isEmpty())) {
            return new FileExportCodeClass(allClasses);
        }
        List<? extends ICodeMethod> allMethods = null;
        allMethods = codeUnit.getMethods();
        if ((allMethods != null) && (!allMethods.isEmpty())) {
            return new FileExportCodeMethod(allMethods);
        }
        return null;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\export\FileExportFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */