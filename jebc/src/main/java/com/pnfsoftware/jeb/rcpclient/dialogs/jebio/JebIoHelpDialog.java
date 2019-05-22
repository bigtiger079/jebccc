package com.pnfsoftware.jeb.rcpclient.dialogs.jebio;

import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class JebIoHelpDialog extends MessageDialog {
    public JebIoHelpDialog(Shell shell) {
        super(shell, "Help", null, JebIoMessages.msgHelp, 2, new String[]{"OK", "More Help Online"}, 0);
    }

    protected boolean isResizable() {
        return true;
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == 1) {
            BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/msninfo");
            return;
        }
        super.buttonPressed(buttonId);
    }
}