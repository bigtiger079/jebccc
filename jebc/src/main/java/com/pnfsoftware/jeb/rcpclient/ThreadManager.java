/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import com.pnfsoftware.jeb.util.concurrent.AbstractThreadManager;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;

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
/*    */ public class ThreadManager
        /*    */ extends AbstractThreadManager
        /*    */ {
    /* 21 */   private static final ILogger logger = GlobalLog.getLogger(ThreadManager.class);
    /*    */   private RcpErrorHandler err;

    /*    */
    /*    */
    public ThreadManager(RcpErrorHandler err)
    /*    */ {
        /* 26 */
        if (err == null) {
            /* 27 */
            throw new NullPointerException();
            /*    */
        }
        /* 29 */
        this.err = err;
        /*    */
    }

    /*    */
    /*    */
    public Thread create(final Runnable r)
    /*    */ {
        /* 34 */
        Thread t = new Thread(new Runnable()
                /*    */ {
            /*    */
            public void run() {
                /*    */
                try {
                    /* 38 */
                    r.run();
                    /*    */
                }
                /*    */ catch (Throwable t) {
                    /* 41 */
                    ThreadManager.logger.catching(t);
                    /* 42 */
                    ThreadManager.this.err.processThrowableSilent(t);
                    /*    */
                }
                /*    */
            }
            /* 45 */
        });
        /* 46 */
        t.setDaemon(true);
        /* 47 */
        return t;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\ThreadManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */