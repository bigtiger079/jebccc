package com.pnfsoftware.jeb.rcpclient.util;

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

}
