package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.units.code.java.IJavaMethod;
import com.pnfsoftware.jeb.core.units.code.java.IJavaSourceUnit;

import java.util.List;

public class DbgUtils {

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
        String lineText = line.getText().toString();
        if (lineText.isEmpty()) {
            return false;
        }
        lineText.matches("(?<=(public\\s|protected\\s|private\\s)?(static\\s)?(final/s)?(void/s|(.+)?\\s)?).+?\\(.*?\\)(\\s)*\\{");

        return lineText.matches("(public\\s|protected\\s|private\\s)?(static\\s)?(final/s)?(void/s|.+?\\s)?.+?\\(.*?\\)(\\s)*\\{");
    }

    public static void getMethodInfo(ILine line, IJavaSourceUnit unit) {
        if (isMethodDefineLine(line)) {
            List<? extends IJavaMethod> methods = unit.getClassElement().getMethods();
            IJavaMethod iJavaMethod = methods.get(0);

        }
    }
}
