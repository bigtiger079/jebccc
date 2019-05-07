/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Display;

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
/*    */
/*    */
/*    */
/*    */ public class SwtUtil
        /*    */ {
    /* 24 */   private static final ILogger logger = GlobalLog.getLogger(SwtUtil.class);

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public static boolean isFocusContainer(Control container)
    /*    */ {
        /* 33 */
        Control ctl = container.getDisplay().getFocusControl();
        /*    */
        do {
            /* 35 */
            if (ctl == container) {
                /* 36 */
                return true;
                /*    */
            }
            /* 38 */
            ctl = ctl.getParent();
            /* 39 */
        } while ((ctl != null) && (ctl != container));
        /* 40 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public static void sleep(Display display)
    /*    */ {
        /*    */
        try
            /*    */ {
            /* 52 */
            display.sleep();
            /*    */
        }
        /*    */ catch (NullPointerException e)
            /*    */ {
            /* 56 */
            logger.catchingSilent(e);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\SwtUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */