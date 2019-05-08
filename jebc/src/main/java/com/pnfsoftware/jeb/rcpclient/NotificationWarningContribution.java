
package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.AnimatedGif;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileNotificationsHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class NotificationWarningContribution
        extends StatusLineContributionItem {
    private static final ILogger logger = GlobalLog.getLogger(NotificationWarningContribution.class);
    public static final String ID = "contribUnitNotificationWarning";
    private RcpClientContext context;

    public NotificationWarningContribution(RcpClientContext context) {
        super("contribUnitNotificationWarning", 5);
        this.context = context;
        setText(" ");
    }

    public void fill(final Composite statusLine) {
        byte[] gifbytes;
        try {
            gifbytes = Assets.readAsset("warning-animated.gif");
        } catch (IOException e1) {
            this.context.getErrorHandler().processThrowableSilent(e1);
            return;
        }
        Label sep = new Label(statusLine, 2);
        StatusLineLayoutData data = new StatusLineLayoutData();
        data.heightHint = 16;
        sep.setLayoutData(data);
        CLabel label = new CLabel(statusLine, 32);
        label.setText("");
        Point preferredSize = label.computeSize(-1, -1);
        int heightHint = preferredSize.y;
        data = new StatusLineLayoutData();
        data.heightHint = 16;
        label.setLayoutData(data);
        AnimatedGif gif = new AnimatedGif(statusLine, 0);
        gif.setSize(16, 16);
        gif.setLocation(0, (heightHint - 16) / 2);
        gif.load(new ByteArrayInputStream(gifbytes));
        gif.animate();
        gif.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent e) {
                NotificationWarningContribution.this.context.getStatusIndicator().removeContribution(NotificationWarningContribution.this);
                IRuntimeProject prj = NotificationWarningContribution.this.context.getOpenedProject();
                if (prj != null) {
                    FileNotificationsHandler.showUnitNotificationsDialog(statusLine.getShell(), NotificationWarningContribution.this.context, prj);
                }
            }
        });
        data = new StatusLineLayoutData();
        data.widthHint = 16;
        data.heightHint = 16;
        gif.setLayoutData(data);
    }
}


