
package com.pnfsoftware.jeb.rcpclient;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.ICoreContext;
import com.pnfsoftware.jeb.core.events.ClientNotification;
import com.pnfsoftware.jeb.core.events.ControllerNotification;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.events.JebEvent;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;


public class CoreListener
        implements IEventListener {
    private static final ILogger logger = GlobalLog.getLogger(CoreListener.class);
    private static CoreListener instance;
    private RcpClientContext context;


    public static synchronized CoreListener initialize(RcpClientContext context, ICoreContext core) {

        if (instance != null) {

            throw new RuntimeException("The context's core listener was already initialized");

        }


        instance = new CoreListener(context);

        core.addListener(instance);

        return instance;

    }


    private CoreListener(RcpClientContext context) {

        this.context = context;

    }


    public void onEvent(IEvent e) {

        if (!(e instanceof JebEvent)) {

            return;

        }

        J type = ((JebEvent) e).getType();


        if (type == J.CoreError) {


            String message = (String) e.getData();

            if (Strings.isBlank(message)) {

                message = "Unknown status message";

            }

            String text = String.format("%s:\n\n%s", new Object[]{S.s(309), message});

            UI.error(text);

        } else if (type == J.Notification) {

            if (!(e.getData() instanceof ClientNotification)) {

                return;

            }

            EnginesListener.processNotification(this.context, e.getSource(), (ClientNotification) e.getData());

        } else if (type == J.FloatingNotification) {

            if (!(e.getData() instanceof ControllerNotification)) {

                return;

            }

            this.context.notifyFloatingClient((ControllerNotification) e.getData());

        }

    }

}


