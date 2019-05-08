package com.pnfsoftware.jeb.rcpclient.dialogs.jebio;

import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public class JebIoHelpDialog
        extends MessageDialog {
    public JebIoHelpDialog(Shell shell) {
        super(shell, "Help", (Image) null, "Participants in the JEB Malware Sharing Network receive some samples that other users have been sharing. The samples you receive are determined algorithmically based on the quantity and quality of your contributions.\n\nIn order to share a file currently opened in JEB, click the \"Share\" button in the toolbar, or use the \"File, Share\" menu entry.\n\nThis service is entirely optional. Your contributions are anonymous.", 2, new String[]{"OK", "More Help Online"}, 0);
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