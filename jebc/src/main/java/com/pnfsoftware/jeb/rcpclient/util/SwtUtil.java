
package com.pnfsoftware.jeb.rcpclient.util;


import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public class SwtUtil {
    private static final ILogger logger = GlobalLog.getLogger(SwtUtil.class);


    public static boolean isFocusContainer(Control container) {

        Control ctl = container.getDisplay().getFocusControl();

        do {

            if (ctl == container) {

                return true;

            }

            ctl = ctl.getParent();

        } while ((ctl != null) && (ctl != container));

        return false;

    }


    public static void sleep(Display display) {

        try {

            display.sleep();

        } catch (NullPointerException e) {

            logger.catchingSilent(e);

        }

    }

}


