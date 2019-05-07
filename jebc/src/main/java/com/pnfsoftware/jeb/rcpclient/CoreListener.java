/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.ICoreContext;
/*    */ import com.pnfsoftware.jeb.core.events.ClientNotification;
/*    */ import com.pnfsoftware.jeb.core.events.ControllerNotification;
/*    */ import com.pnfsoftware.jeb.core.events.J;
/*    */ import com.pnfsoftware.jeb.core.events.JebEvent;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import com.pnfsoftware.jeb.util.events.IEvent;
/*    */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
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
/*    */ public class CoreListener
        /*    */ implements IEventListener
        /*    */ {
    /* 30 */   private static final ILogger logger = GlobalLog.getLogger(CoreListener.class);
    /*    */   private static CoreListener instance;
    /*    */   private RcpClientContext context;

    /*    */
    /*    */
    public static synchronized CoreListener initialize(RcpClientContext context, ICoreContext core) {
        /* 35 */
        if (instance != null) {
            /* 36 */
            throw new RuntimeException("The context's core listener was already initialized");
            /*    */
        }
        /*    */
        /* 39 */
        instance = new CoreListener(context);
        /* 40 */
        core.addListener(instance);
        /* 41 */
        return instance;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    private CoreListener(RcpClientContext context)
    /*    */ {
        /* 47 */
        this.context = context;
        /*    */
    }

    /*    */
    /*    */
    public void onEvent(IEvent e)
    /*    */ {
        /* 52 */
        if (!(e instanceof JebEvent)) {
            /* 53 */
            return;
            /*    */
        }
        /* 55 */
        J type = ((JebEvent) e).getType();
        /*    */
        /* 57 */
        if (type == J.CoreError)
            /*    */ {
            /*    */
            /* 60 */
            String message = (String) e.getData();
            /* 61 */
            if (Strings.isBlank(message)) {
                /* 62 */
                message = "Unknown status message";
                /*    */
            }
            /* 64 */
            String text = String.format("%s:\n\n%s", new Object[]{S.s(309), message});
            /* 65 */
            UI.error(text);
            /*    */
        }
        /* 67 */
        else if (type == J.Notification) {
            /* 68 */
            if (!(e.getData() instanceof ClientNotification)) {
                /* 69 */
                return;
                /*    */
            }
            /* 71 */
            EnginesListener.processNotification(this.context, e.getSource(), (ClientNotification) e.getData());
            /*    */
        }
        /* 73 */
        else if (type == J.FloatingNotification) {
            /* 74 */
            if (!(e.getData() instanceof ControllerNotification)) {
                /* 75 */
                return;
                /*    */
            }
            /* 77 */
            this.context.notifyFloatingClient((ControllerNotification) e.getData());
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\CoreListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */