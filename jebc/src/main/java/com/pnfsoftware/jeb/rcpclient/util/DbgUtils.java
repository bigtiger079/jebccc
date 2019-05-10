package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.units.code.java.IJavaMethod;
import com.pnfsoftware.jeb.core.units.code.java.IJavaSourceUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Part;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbgUtils {
    private static final ILogger logger = GlobalLog.getLogger(DbgUtils.class);
    public static String getStackTrace(Thread thread) {
        StackTraceElement[] stackTrace = thread.getStackTrace();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < stackTrace.length; i++) {
            if (i > 2) {
                buffer.append(stackTrace[i].getClassName()).append(".").append(stackTrace[i].getMethodName()).append("\r");
            }
        }

        return buffer.toString();
    }

    public static String getStackTrace() {
        return getStackTrace(Thread.currentThread());
    }

    public static boolean isMethodDefineLine(ILine line) {
        String lineText = line.getText().toString().trim();
        if (lineText.isEmpty()) {
            return false;
        }

        return lineText.matches("(public\\s|protected\\s|private\\s)?(static\\s)?(final\\s)?(void\\s|.+?\\s)+(.+?\\().*?\\)(\\s)*\\{");
    }

    public static Pair<String, List<Pair<String, String>>> getMethodInfo(ILine line) {
        if (!isMethodDefineLine(line)) {
            return null;
        }
        Pattern compile = Pattern.compile("(public\\s|protected\\s|private\\s)?(native\\s|static\\s|final\\s|synchronize\\s){0,4}(void\\s|.+?\\s)([^\\s(]+)\\(([^=!><?]*)\\)(\\s)*\\{$");
        Matcher matcher = compile.matcher(line.getText().toString().trim());
        if (matcher.find()) {
            String methodName = matcher.group(4);
            String args = matcher.group(5);
            List<Pair<String, String>> params = new ArrayList<>();
            String[] split = args.split(",");
            if (split.length > 0) {
                for (String s : split) {
                    String[] p = s.trim().split("\\s+");
                    if (p.length != 2) {
                        logger.error("ERROR: on parse args [%s] of method %s ", s, methodName);
                    }
                    params.add(Pair.of(p[0], p[1]));
                }
            }
            return Pair.of(methodName, params);
        }
        return null;
    }
}
