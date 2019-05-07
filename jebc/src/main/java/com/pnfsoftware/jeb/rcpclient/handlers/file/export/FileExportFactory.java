/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.ICodeClass;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
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
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class FileExportFactory
        /*    */ {
    /*    */
    public static IFileExport<? extends ICodeItem> get(ICodeUnit codeUnit)
    /*    */ {
        /* 33 */
        List<? extends ICodeClass> allClasses = codeUnit.getClasses();
        /* 34 */
        if ((allClasses != null) && (!allClasses.isEmpty())) {
            /* 35 */
            return new FileExportCodeClass(allClasses);
            /*    */
        }
        /*    */
        /*    */
        /* 39 */
        List<? extends ICodeMethod> allMethods = null;
        /* 40 */
        allMethods = codeUnit.getMethods();
        /* 41 */
        if ((allMethods != null) && (!allMethods.isEmpty())) {
            /* 42 */
            return new FileExportCodeMethod(allMethods);
            /*    */
        }
        /* 44 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\export\FileExportFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */