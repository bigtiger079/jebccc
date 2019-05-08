package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.base.RunnableWithProgressCallback;
import com.pnfsoftware.jeb.util.io.IO;

import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SoftwareUpdateDialog
        extends JebDialog {
    private RcpClientContext context;
    private boolean expiredLicense;

    public SoftwareUpdateDialog(Shell parent, RcpClientContext context, boolean expiredLicense) {
        super(parent, S.s(744), true, true);
        this.context = context;
        this.expiredLicense = expiredLicense;
        setVisualBounds(20, 60, 20, 80);
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        Group g0 = new Group(parent, 0);
        g0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        g0.setLayout(new GridLayout(1, false));
        g0.setText(S.s(395));
        Label t0 = new Label(g0, 64);
        t0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t0.setText("If your machine is connected to the Internet, press the button below to perform an update check.");
        Button btn = UIUtil.createPushbox(g0, S.s(194), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                SoftwareUpdateDialog.this.context.executeTask("Checking for update", new RunnableWithProgressCallback() {
                    public void run() {
                        SoftwareUpdateDialog.this.context.checkUpdate(true, SoftwareUpdateDialog.this.expiredLicense, this.callback);
                    }
                });
                SoftwareUpdateDialog.this.shell.close();
            }
        });
        btn.setEnabled(this.context != null);
        Group g1 = new Group(parent, 0);
        g1.setLayoutData(UIUtil.createGridDataFillHorizontally());
        g1.setLayout(new GridLayout(1, false));
        g1.setText("Manual update");
        Label t1 = new Label(g1, 64);
        t1.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t1.setText("Users in SCIF have the option to perform a manual update. Refer to your most recent update email from PNF Software for details.");
        UIUtil.createPushbox(g1, S.s(635) + "...", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ManualSoftwareUpdateDialog d = new ManualSoftwareUpdateDialog(SoftwareUpdateDialog.this.shell);
                String[] r = d.open();
                if (r == null) {
                    return;
                }
                try {
                    byte[] data = IO.readFile(r[0]);
                    SoftwareUpdateDialog.this.context.dumpUpdateToDisk(data, r[1]);
                } catch (IOException e) {
                    return;
                }
                SoftwareUpdateDialog.this.context.installUpdate(SoftwareUpdateDialog.this.shell, null);
                SoftwareUpdateDialog.this.shell.close();
            }
        });
        btn.setEnabled(this.context != null);
        Label t3 = new Label(parent, 64);
        t3.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t3.setText("If you cannot or do not have access to the registered email address, send us an email at support@pnfsoftware.com - Thank you.");
        Composite buttons = new Composite(parent, 0);
        buttons.setLayout(new GridLayout(2, false));
        buttons.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        createOkayButton(parent);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\SoftwareUpdateDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */