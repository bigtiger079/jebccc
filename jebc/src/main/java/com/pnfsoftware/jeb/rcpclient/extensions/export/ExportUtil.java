
package com.pnfsoftware.jeb.rcpclient.extensions.export;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;

import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

public class ExportUtil {
    public static boolean copyLinesToClipboard(IExportableData exportTable, List<?> list) {
        String export = exportLines(exportTable, list);
        if (export == null) {
            return false;
        }
        UIUtil.copyTextToClipboard(export);
        return true;
    }

    public static String exportLines(IExportableData exportTable, List<?> list) {
        if (list != null) {
            StringBuilder stb = new StringBuilder();
            for (Object o : list) {
                String s = exportTable.exportElementToString(o);
                if (s != null) {
                    stb.append(s).append('\n');
                }
            }
            if (stb.length() > 0) {
                stb.deleteCharAt(stb.length() - 1);
                return stb.toString();
            }
        }
        return null;
    }

    public static String buildCsvLine(IValueProvider labelProvider, Object obj, int length) {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String cell = StringEscapeUtils.escapeCsv(labelProvider.getStringAt(obj, i));
            stb.append(cell == null ? "" : cell).append(",");
        }
        if (stb.length() > 0) {
            stb.deleteCharAt(stb.length() - 1);
        }
        return stb.toString();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\export\ExportUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */