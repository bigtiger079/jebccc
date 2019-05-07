/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.ICodeClass;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodePackage;
/*    */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.apache.commons.lang3.StringUtils;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class FileExportCodeClass
        /*    */ implements IFileExport<ICodeClass>
        /*    */ {
    /*    */   private List<? extends ICodeClass> allClasses;

    /*    */
    /*    */
    public FileExportCodeClass(List<? extends ICodeClass> allClasses)
    /*    */ {
        /* 33 */
        this.allClasses = allClasses;
        /*    */
    }

    /*    */
    /*    */
    public List<? extends ICodeClass> getItems()
    /*    */ {
        /* 38 */
        return this.allClasses;
        /*    */
    }

    /*    */
    /*    */
    public boolean canProcess(ICodeClass item)
    /*    */ {
        /* 43 */
        return (item.getGenericFlags() & 0x100000) == 0;
        /*    */
    }

    /*    */
    /*    */
    public List<String> getPath(ICodeClass c)
    /*    */ {
        /* 48 */
        List<String> packages = new ArrayList();
        /* 49 */
        ICodePackage p = c.getPackage();
        /* 50 */
        while (p != null) {
            /* 51 */
            if (p.getName(true) != null) {
                /* 52 */
                packages.add(0, p.getName(true));
                /*    */
            }
            /* 54 */
            p = p.getParentPackage();
            /*    */
        }
        /* 56 */
        return packages;
        /*    */
    }

    /*    */
    /*    */
    public String getFullName(ICodeClass c)
    /*    */ {
        /* 61 */
        return getFullPackage(getPath(c)) + "." + c.getName(true);
        /*    */
    }

    /*    */
    /*    */
    public String getNameFromSourceUnit(ISourceUnit sourceUnit)
    /*    */ {
        /* 66 */
        String fullName = sourceUnit.getFullyQualifiedName();
        /* 67 */
        return fullName.substring(1, fullName.length() - 1).replace('/', '.');
        /*    */
    }

    /*    */
    /*    */
    public static String getFullPackage(List<String> p) {
        /* 71 */
        return StringUtils.join(p, '.');
        /*    */
    }

    /*    */
    /*    */
    public boolean isAtAddress(ICodeClass c, String address)
    /*    */ {
        /* 76 */
        return address.startsWith(c.getAddress().substring(0, c.getAddress().lastIndexOf(';')));
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\export\FileExportCodeClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */