/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import com.pnfsoftware.jeb.util.format.Strings;

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
/*    */ public class Extensions
        /*    */ {
    /*    */
    public static boolean hasExtension(String name)
    /*    */ {
        /* 20 */
        return !getExtension(name).isEmpty();
        /*    */
    }

    /*    */
    /*    */
    public static String getExtension(String name) {
        /* 24 */
        int pos = name.lastIndexOf('.');
        /* 25 */
        if (pos < 0) {
            /* 26 */
            return "";
            /*    */
        }
        /* 28 */
        return name.substring(pos + 1);
        /*    */
    }

    /*    */
    /* 31 */   private static final String[] textFileExtList = {"log", "txt", "cfg", "mf", "properties", "cmd", "bat", "xml", "json", "yaml", "c", "cpp", "h", "hpp", "py", "js", "java", "pl", "sh", "html", "htm", "xhtml", "css", "php", "asm", "rb", "md", "classpath", "project"};

    /*    */
    /*    */
    /*    */
    public static boolean hasKnownTextDocumentExtension(String name)
    /*    */ {
        /* 36 */
        String ext = getExtension(name).toLowerCase();
        /* 37 */
        return Strings.isContainedIn(ext, textFileExtList);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\Extensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */