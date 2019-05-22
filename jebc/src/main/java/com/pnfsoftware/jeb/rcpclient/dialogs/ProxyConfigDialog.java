package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.IGraphicalTaskExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.net.Net;
import com.pnfsoftware.jeb.util.net.NetProxyInfo;

import java.io.IOException;
import java.net.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProxyConfigDialog extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(ProxyConfigDialog.class);
    private NetProxyInfo proxyinfo;
    private NetProxyInfo proxyinfo2;
    private IGraphicalTaskExecutor executor;
    private Combo widgetType;
    private Text widgetHostname;
    private Text widgetPort;
    private Text widgetUsername;
    private Text widgetPassword;

    public ProxyConfigDialog(Shell parent, NetProxyInfo proxyinfo, IGraphicalTaskExecutor executor) {
        super(parent, S.s(668), true, true);
        this.proxyinfo = proxyinfo;
        this.executor = executor;
    }

    public NetProxyInfo open() {
        super.open();
        return this.proxyinfo2;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        UIUtil.createLabel(parent, S.s(672));
        this.widgetType = new Combo(parent, 12);
        GridData layoutData = UIUtil.createGridDataFillHorizontally();
        layoutData.widthHint = UIUtil.determineTextWidth(this.widgetType, 40);
        this.widgetType.setLayoutData(layoutData);
        this.widgetType.add("Direct (no proxy)");
        for (String proxyType : NetProxyInfo.getProxyTypes()) {
            this.widgetType.add(proxyType);
        }
        this.widgetType.select(0);
        this.widgetType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ProxyConfigDialog.this.onProxyTypeChange();
            }
        });
        UIUtil.createLabel(parent, S.s(670));
        this.widgetHostname = new Text(parent, 2052);
        this.widgetHostname.setLayoutData(UIUtil.createGridDataFillHorizontally());
        UIUtil.createLabel(parent, S.s(671));
        this.widgetPort = new Text(parent, 2052);
        this.widgetPort.setLayoutData(UIUtil.createGridDataFillHorizontally());
        Group grpAuth = UIUtil.createGroupGrid(parent, "Authentication (optional)", 2, 2);
        UIUtil.createLabel(grpAuth, S.s(811));
        this.widgetUsername = new Text(grpAuth, 2052);
        this.widgetUsername.setLayoutData(UIUtil.createGridDataFillHorizontally());
        UIUtil.createLabel(grpAuth, S.s(631));
        this.widgetPassword = new Text(grpAuth, 2052);
        this.widgetPassword.setLayoutData(UIUtil.createGridDataFillHorizontally());
        if (this.proxyinfo != null) {
            this.widgetHostname.setText(Strings.safe(this.proxyinfo.getHostname()));
            this.widgetPort.setText(Integer.toString(this.proxyinfo.getPort()));
            int index = Arrays.asList(this.widgetType.getItems()).indexOf(this.proxyinfo.getType());
            if (index >= 0) {
                this.widgetType.select(index);
            }
            this.widgetUsername.setText(Strings.safe(this.proxyinfo.getUser()));
            this.widgetPassword.setText(Strings.safe(this.proxyinfo.getPassword()));
        }
        onProxyTypeChange();
        createOkayCancelButtons(parent);
    }

    private void onProxyTypeChange() {
        boolean enabled = this.widgetType.getSelectionIndex() > 0;
        this.widgetHostname.setEnabled(enabled);
        this.widgetPort.setEnabled(enabled);
        this.widgetUsername.setEnabled(enabled);
        this.widgetPassword.setEnabled(enabled);
    }

    protected void onConfirm() {
        this.proxyinfo2 = check();
        if (this.proxyinfo2 == null) {
            return;
        }
        if (!verify(this.proxyinfo2)) {
            return;
        }
        super.onConfirm();
    }

    private NetProxyInfo check() {
        int index = this.widgetType.getSelectionIndex();
        if (index == 0) {
            return new NetProxyInfo(Proxy.NO_PROXY);
        }
        index--;
        if ((index < 0) || (index >= NetProxyInfo.getProxyTypes().size())) {
            UI.error("Illegal proxy type");
            return null;
        }
        String proxyType = NetProxyInfo.getProxyTypes().get(index);
        String hostname = this.widgetHostname.getText();
        if (Strings.isBlank(hostname)) {
            UI.error("Illegal hostname");
            return null;
        }
        int port = Conversion.stringToInt(this.widgetPort.getText());
        if ((port <= 0) || (port >= 65535)) {
            UI.error("Illegal port number");
            return null;
        }
        String username = this.widgetUsername.getText();
        String password = this.widgetPassword.getText();
        return NetProxyInfo.build(proxyType, hostname, port, username, password);
    }

    private boolean verify(NetProxyInfo proxyinfo) {
        final AtomicBoolean r = new AtomicBoolean();
        NetProxyInfo previousProxyinfo = Net.getGlobalProxyInformation();
        Net.setGlobalProxyInformation(proxyinfo);
        final Net net = new Net();
        Runnable task = new Runnable() {
            public void run() {
                try {
                    net.queryBinary("https://www.pnfsoftware.com/ping");
                    r.set(true);
                } catch (IOException e1) {
                    ProxyConfigDialog.logger.catching(e1);
                    UI.error("The connectivity test failed:\n\n" + e1);
                }
            }
        };
        if (this.executor == null) {
            logger.info("Verifying connectivity, please wait...");
            BusyIndicator.showWhile(Display.getCurrent(), task);
        } else {
            this.executor.executeTaskWithPopupDelay(500, "Verifying connectivity...", false, task);
        }
        if (!r.get()) {
            Net.setGlobalProxyInformation(previousProxyinfo);
            return false;
        }
        return true;
    }
}


