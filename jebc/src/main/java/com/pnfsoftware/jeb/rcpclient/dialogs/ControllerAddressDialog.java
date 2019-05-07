package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.AbstractClientContext;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.net.NetProxyInfo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ControllerAddressDialog
        extends JebDialog {
    private AbstractClientContext context;
    private boolean success;
    private Text widgetHostname;
    private Text widgetPort;
    private NetProxyInfo proxyinfo;

    public ControllerAddressDialog(Shell parent, AbstractClientContext context) {
        super(parent, S.s(221), true, true);
        this.scrolledContainer = true;

        if (context == null) {
            throw new NullPointerException();
        }
        this.context = context;
    }

    public Boolean open() {
        super.open();
        return Boolean.valueOf(this.success);
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);

        Label label = new Label(parent, 64);
        label.setText(String.format("%s. %s.", new Object[]{S.s(223), S.s(225)}));
        label.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));

        Group g0 = UIUtil.createGroupGrid(parent, "Controller", 2, 2);

        Label l = new Label(g0, 64);
        l.setText("Both fields are mandatory.");
        l.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));


        new Label(g0, 0).setText(S.s(670));
        this.widgetHostname = new Text(g0, 2052);
        this.widgetHostname.setText(Strings.safe(this.context.getControllerInterface()));
        this.widgetHostname.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetHostname.selectAll();
        this.widgetHostname.setFocus();

        new Label(g0, 0).setText(S.s(671));
        this.widgetPort = new Text(g0, 2052);
        this.widgetPort.setText(Integer.toString(this.context.getControllerPort()));
        this.widgetPort.selectAll();
        this.widgetPort.setLayoutData(UIUtil.createGridDataFillHorizontally());

        Group g1 = UIUtil.createGroupGrid(parent, "Connection", 2, 2);

        UIUtil.createPushbox(g1, "Proxy settings...", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ProxyConfigDialog dlg = new ProxyConfigDialog(UIUtil.getParentShell(ControllerAddressDialog.this.shell), null, null);
                NetProxyInfo r = dlg.open();
                if (r != null) {
                    ControllerAddressDialog.this.proxyinfo = r;
                }

            }
        });
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        String hostname = this.widgetHostname.getText();
        if (Strings.isBlank(hostname)) {
            UI.error("Illegal hostname for controller");
            return;
        }

        int port = Conversion.stringToInt(this.widgetPort.getText());
        if ((port <= 0) || (port >= 65535)) {
            UI.error("Illegal port number for controller");
            return;
        }


        this.context.setControllerInterface(hostname);
        this.context.setControllerPort(port);
        if (this.proxyinfo != null) {
            this.context.setProxyString(this.proxyinfo.toString());
        }
        this.success = Boolean.TRUE.booleanValue();
        super.onConfirm();
    }
}


