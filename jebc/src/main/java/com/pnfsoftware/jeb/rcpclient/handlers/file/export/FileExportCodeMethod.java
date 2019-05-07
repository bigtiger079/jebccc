/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
/*    */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;

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
/*    */ public class FileExportCodeMethod
        /*    */ implements IFileExport<ICodeMethod>
        /*    */ {
    /*    */   private List<? extends ICodeMethod> allMethods;

    /*    */
    /*    */
    public FileExportCodeMethod(List<? extends ICodeMethod> allMethods)
    /*    */ {
        /* 30 */
        this.allMethods = allMethods;
        /*    */
    }

    /*    */
    /*    */
    public List<? extends ICodeMethod> getItems()
    /*    */ {
        /* 35 */
        return this.allMethods;
        /*    */
    }

    /*    */
    /*    */
    public boolean canProcess(ICodeMethod item)
    /*    */ {
        /* 40 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public List<String> getPath(ICodeMethod item)
    /*    */ {
        /* 45 */
        return new ArrayList();
        /*    */
    }

    /*    */
    /*    */
    public String getFullName(ICodeMethod c)
    /*    */ {
        /* 50 */
        return c.getName(true);
        /*    */
    }

    /*    */
    /*    */
    public boolean isAtAddress(ICodeMethod item, String address)
    /*    */ {
        /* 55 */
        return address.equals(item.getAddress());
        /*    */
    }

    /*    */
    /*    */
    public String getNameFromSourceUnit(ISourceUnit sourceUnit)
    /*    */ {
        /* 60 */
        return sourceUnit.getName();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\export\FileExportCodeMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */