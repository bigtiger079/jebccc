package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.util.concurrent.AbstractThreadManager;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class ThreadManager extends AbstractThreadManager {
    private static final ILogger logger = GlobalLog.getLogger(ThreadManager.class);
    private RcpErrorHandler err;

    public ThreadManager(RcpErrorHandler err) {
        if (err == null) {
            throw new NullPointerException();
        }
        this.err = err;
    }

    public Thread create(final Runnable r) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    ThreadManager.logger.catching(t);
                    ThreadManager.this.err.processThrowableSilent(t);
                }
            }
        });
        t.setDaemon(true);
        return t;
    }
}


