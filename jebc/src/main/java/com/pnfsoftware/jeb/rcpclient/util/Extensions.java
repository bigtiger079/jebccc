
package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.util.format.Strings;

public class Extensions {
    public static boolean hasExtension(String name) {
        return !getExtension(name).isEmpty();
    }

    public static String getExtension(String name) {
        int pos = name.lastIndexOf('.');
        if (pos < 0) {
            return "";
        }
        return name.substring(pos + 1);
    }

    private static final String[] textFileExtList = {"log", "txt", "cfg", "mf", "properties", "cmd", "bat", "xml", "json", "yaml", "c", "cpp", "h", "hpp", "py", "js", "java", "pl", "sh", "html", "htm", "xhtml", "css", "php", "asm", "rb", "md", "classpath", "project"};

    public static boolean hasKnownTextDocumentExtension(String name) {
        String ext = getExtension(name).toLowerCase();
        return Strings.isContainedIn(ext, textFileExtList);
    }
}


