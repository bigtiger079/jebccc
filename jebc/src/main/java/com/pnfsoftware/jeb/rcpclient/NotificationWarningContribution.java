/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import com.pnfsoftware.jeb.core.IRuntimeProject;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.AnimatedGif;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileNotificationsHandler;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import org.eclipse.jface.action.StatusLineContributionItem;
/*    */ import org.eclipse.jface.action.StatusLineLayoutData;
/*    */ import org.eclipse.swt.custom.CLabel;
/*    */ import org.eclipse.swt.events.MouseAdapter;
/*    */ import org.eclipse.swt.events.MouseEvent;
/*    */ import org.eclipse.swt.graphics.Point;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;

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
/*    */ public class NotificationWarningContribution
        /*    */ extends StatusLineContributionItem
        /*    */ {
    /* 36 */   private static final ILogger logger = GlobalLog.getLogger(NotificationWarningContribution.class);
    /*    */
    /*    */   public static final String ID = "contribUnitNotificationWarning";
    /*    */   private RcpClientContext context;

    /*    */
    /*    */
    public NotificationWarningContribution(RcpClientContext context)
    /*    */ {
        /* 43 */
        super("contribUnitNotificationWarning", 5);
        /* 44 */
        this.context = context;
        /*    */
        /*    */
        /*    */
        /*    */
        /* 49 */
        setText(" ");
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void fill(final Composite statusLine)
    /*    */ {
        /*    */
        try
            /*    */ {
            /* 57 */
            gifbytes = Assets.readAsset("warning-animated.gif");
            /*    */
        }
        /*    */ catch (IOException e1)
            /*    */ {
            /*    */
            byte[] gifbytes;
            /* 62 */
            this.context.getErrorHandler().processThrowableSilent(e1);
            return;
            /*    */
        }
        /*    */
        /*    */
        byte[] gifbytes;
        /* 66 */
        Label sep = new Label(statusLine, 2);
        /* 67 */
        StatusLineLayoutData data = new StatusLineLayoutData();
        /* 68 */
        data.heightHint = 16;
        /* 69 */
        sep.setLayoutData(data);
        /*    */
        /* 71 */
        CLabel label = new CLabel(statusLine, 32);
        /* 72 */
        label.setText("");
        /* 73 */
        Point preferredSize = label.computeSize(-1, -1);
        /* 74 */
        int heightHint = preferredSize.y;
        /* 75 */
        data = new StatusLineLayoutData();
        /* 76 */
        data.heightHint = 16;
        /* 77 */
        label.setLayoutData(data);
        /*    */
        /* 79 */
        AnimatedGif gif = new AnimatedGif(statusLine, 0);
        /* 80 */
        gif.setSize(16, 16);
        /* 81 */
        gif.setLocation(0, (heightHint - 16) / 2);
        /* 82 */
        gif.load(new ByteArrayInputStream(gifbytes));
        /* 83 */
        gif.animate();
        /* 84 */
        gif.addMouseListener(new MouseAdapter()
                /*    */ {
            /*    */
            public void mouseDown(MouseEvent e) {
                /* 87 */
                NotificationWarningContribution.this.context.getStatusIndicator().removeContribution(NotificationWarningContribution.this);
                /* 88 */
                IRuntimeProject prj = NotificationWarningContribution.this.context.getOpenedProject();
                /* 89 */
                if (prj != null) {
                    /* 90 */
                    FileNotificationsHandler.showUnitNotificationsDialog(statusLine.getShell(), NotificationWarningContribution.this.context, prj);
                    /*    */
                }
                /*    */
                /*    */
            }
            /* 94 */
        });
        /* 95 */
        data = new StatusLineLayoutData();
        /* 96 */
        data.widthHint = 16;
        /* 97 */
        data.heightHint = 16;
        /* 98 */
        gif.setLayoutData(data);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\NotificationWarningContribution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */