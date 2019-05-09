package com.pnfsoftware.jeb.rcpclient.util;

public class DbgUtils {

    public static String getStackTrace(Thread thread) {
        StackTraceElement[] stackTrace = thread.getStackTrace();
        StringBuffer buffer = new StringBuffer();
        for (StackTraceElement stackTraceElement : stackTrace) {
            buffer.append(stackTraceElement.getLineNumber()).append(" : ")
                    .append(stackTraceElement.getClassName())
                    .append(".")
                    .append(stackTraceElement.getMethodName())
                    .append("\r");
        }

        return buffer.toString();
    }

    public static String getStackTrace() {
        return getStackTrace(Thread.currentThread());
    }

}
