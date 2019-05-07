
package com.pnfsoftware.jeb.rcpclient.extensions;


import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class UIExecutor {
    private static UIExecutor instance = new UIExecutor();
    private static AtomicInteger synccnt = new AtomicInteger();
    private static AtomicInteger executingSyncCnt = new AtomicInteger();

    private static final Object alock = new Object();
    private static AtomicInteger asynccnt = new AtomicInteger();
    private static int asynccntExec;
    private static Map<String, Integer> asyncCallers = Collections.synchronizedMap(new HashMap());
    private static long asyncBestWaitTime = Long.MAX_VALUE;
    private static long asyncWorstWaitTime = 0L;

    private static long asyncAvgWaitTime;

    static synchronized int access$108() {
        int i = asynccntExec;
        asynccntExec = i + 1;
        return i;
    }

    public static UIExecutor getInstance() {
        return instance;
    }

    public static void async(Widget w, UIRunnable runnable) {
        asyncInternal(w.getDisplay(), runnable);
    }


    public static void async(Display d, UIRunnable runnable) {

        asyncInternal(d, runnable);

    }


    public static void asyncIfNotOnUIThread(Display d, UIRunnable runnable) {

        asyncInternal(d, runnable, true);

    }


    public static void sync(Widget w, Runnable runnable) {

        sync(w.getDisplay(), runnable);

    }


    public static void sync(Display d, Runnable runnable) {

        if (d.isDisposed()) {

            return;

        }


        if (d.getThread() == Thread.currentThread()) {

            runnable.run();

            return;

        }


        executingSyncCnt.incrementAndGet();


        synccnt.incrementAndGet();

        d.syncExec(runnable);


        executingSyncCnt.decrementAndGet();

    }


    private static void asyncInternal(Display d, UIRunnable runnable) {

        asyncInternal(d, runnable, false);

    }


    private static void asyncInternal(Display d, UIRunnable runnable, boolean bypassQueue) {

        if (d.isDisposed()) {

            return;

        }


        if ((bypassQueue) && (d.getThread() == Thread.currentThread())) {

            runnable.run();

            return;

        }


        boolean debug = Licensing.isDebugBuild();

        if (debug) {

            StackTraceElement[] elts = Thread.currentThread().getStackTrace();

            String caller = "unknown";

            if (elts.length >= 4) {

                caller = elts[3].toString();

            }

            synchronized (asyncCallers) {

                Integer cnt = (Integer) asyncCallers.get(caller);

                if (cnt == null) {

                    asyncCallers.put(caller, Integer.valueOf(1));

                } else {

                    asyncCallers.put(caller, Integer.valueOf(cnt.intValue() + 1));

                }

            }

        }


        asynccnt.incrementAndGet();

        d.asyncExec(runnable);


        if (debug) {

            monitorUITask(runnable);

        }

    }


    private static void monitorUITask(UIRunnable task) {
        String threadName = "monitorUITask: " + getCaller();
        ThreadUtil.start(threadName, new Runnable() {
            public void run() {
                while (!task.isDone()) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        return;
                    }

                }

                synchronized (UIExecutor.alock) {
                    UIExecutor.access$108();
                    long waitTime = task.getExecStartTs() - task.getCreatedTs();
                    if (waitTime < UIExecutor.asyncBestWaitTime) {
                        UIExecutor.asyncBestWaitTime = waitTime;
                    }
                    if (waitTime > UIExecutor.asyncWorstWaitTime) {
                        UIExecutor.asyncWorstWaitTime = waitTime;
                    }
                    if (UIExecutor.asynccntExec == 1) {
                        UIExecutor.asyncAvgWaitTime = waitTime;
                    } else {
                        UIExecutor.asyncAvgWaitTime = ((UIExecutor.asyncAvgWaitTime * (((long) UIExecutor.asynccntExec) - 1)) + waitTime) / ((long) UIExecutor.asynccntExec);
                    }

                }
            }
        });
    }


    private static String getCaller() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stack.length; i++) {
            if ((!stack[i].getClassName().startsWith(UIExecutor.class.getName())) &&
                    (!stack[i].getClassName().contains(".Abstract"))) {
                return stack[i].getClassName();
            }
        }
        return "";
    }

    public static String format() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Map.Entry<String, Integer>> list;
        synchronized (asyncCallers) {
            list = new ArrayList(asyncCallers.entrySet());
        }

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return -Integer.compare(o1.getValue(), o2.getValue());
            }
        });

        sb.append(String.format("=> %d sync executed (currently executing: %d)\n", synccnt.get(),
                executingSyncCnt.get()));

        sb.append(String.format("=> %d/%d async executed (wait times: avg=%dms best=%dms worst=%dms)\n", asynccntExec,
                asynccnt.get(), asyncAvgWaitTime, asyncBestWaitTime, asyncWorstWaitTime));


        sb.append("Top callers:\n");

        int i = 0;

        for (Map.Entry<String, Integer> e : list) {

            if (i >= 10) {

                break;

            }

            sb.append(String.format("- %4d %s\n", new Object[]{e.getValue(), e.getKey()}));

            i++;

        }


        return sb.toString().trim();

    }


    public String toString() {

        return format();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\UIExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */