package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.SystemInformation;
import com.pnfsoftware.jeb.rcpclient.IGraphicalTaskExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.net.Net;
import com.pnfsoftware.jeb.util.net.NetProxyInfo;

import java.io.IOException;
import java.nio.charset.Charset;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LicenseKeyAutoDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(LicenseKeyAutoDialog.class);
    private String licdata;
    private Net net;
    private boolean success;
    private String lickey;
    private IGraphicalTaskExecutor executor;
    private Text textKeyname;
    private Text textKey;
    private Button btnGen;
    private Button btnManualGen;

    public LicenseKeyAutoDialog(Shell parent, String licdata, Net net, IGraphicalTaskExecutor executor) {
        super(parent, "JEB", true, true);
        this.scrolledContainer = true;
        setVisualBounds(-1, 50, -1, -1);
        this.licdata = licdata;
        this.net = new Net(net);
        this.executor = executor;
    }

    public Net getNet() {
        return this.net;
    }

    public String open() {
        super.open();
        return this.lickey;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        parent.addListener(21, new Listener() {
            public void handleEvent(Event event) {
                if (!LicenseKeyAutoDialog.this.success) {
                    LicenseKeyAutoDialog.this.lickey = null;
                }
            }
        });
        String hello = String.format(S.s(364), new Object[]{Licensing.user_name});
        String message = String.format("%s. %s.\n\n", new Object[]{hello, S.s(636)});
        Label labelInfo = new Label(parent, 64);
        labelInfo.setText(message);
        labelInfo.setLayoutData(UIUtil.createGridDataFillHorizontally());
        new Label(parent, 0).setText(S.s(423) + ": ");
        this.textKeyname = new Text(parent, 2052);
        this.textKeyname.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.textKeyname.setText(String.format("%s on %s", new Object[]{SystemInformation.username, SystemInformation.compname}));
        this.textKeyname.selectAll();
        this.textKeyname.setFocus();
        new Label(parent, 0).setText(S.s(436) + ": ");
        this.textKey = new Text(parent, 2060);
        this.textKey.setLayoutData(UIUtil.createGridDataFillHorizontally());
        Composite c4 = new Composite(parent, 0);
        c4.setLayout(new RowLayout(256));
        this.btnGen = UIUtil.createPushbox(c4, S.s(362), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.widget;
                if (LicenseKeyAutoDialog.this.lickey != null) {
                    LicenseKeyAutoDialog.this.success = true;
                    LicenseKeyAutoDialog.this.shell.close();
                    return;
                }
                String keyname = LicenseKeyAutoDialog.this.textKeyname.getText();
                final String urlparams = String.format("licdata=%s&keyname=%s", new Object[]{Strings.urlencodeUTF8(LicenseKeyAutoDialog.this.licdata), Strings.urlencodeUTF8(keyname)});
                btn.setText(S.s(638) + "...");
                BusyIndicator.showWhile(event.display, new Runnable() {
                    public void run() {
                        String[] urlbases = {"https://www.pnfsoftware.com/jps/genkey", "https://lise.pnfsoftware.com/jps/genkey"};
                        for (String urlbase : urlbases) {
                            try {
                                String url = String.format("%s?%s", new Object[]{urlbase, urlparams});
                                byte[] data = LicenseKeyAutoDialog.this.net.queryBinary(url);
                                String str = new String(data, Charset.forName("US-ASCII")).trim();
                                if (LicenseKeyAutoDialog.looksLikeLicenseKey(str)) {
                                    LicenseKeyAutoDialog.this.lickey = str;
                                    return;
                                }
                            } catch (IOException e) {
                                LicenseKeyAutoDialog.logger.catchingSilent(e);
                            }
                        }
                        LicenseKeyAutoDialog.logger.error("Server did not respond with a license key", new Object[0]);
                    }
                });
                if (LicenseKeyAutoDialog.this.lickey != null) {
                    LicenseKeyAutoDialog.this.onLicenseKeyChange(false);
                    String msg = String.format("%s.\n\n%s.", new Object[]{S.s(438),
                            S.s(637)});
                    MessageDialog.openInformation(LicenseKeyAutoDialog.this.shell, "JEB", msg);
                } else {
                    btn.setText(S.s(362));
                    String msg = String.format("%s. Potential reasons for failure include:\n\n- This machine could be offline or blocking connections to pnfsoftware.com: Automatic Key Generation requires an active Internet connection. If that is the case, you may try Manual Key Generation.\n- You may have reached the maximum number of keys that can be generated for this license: If you need to generate new keys or deprecate old ones, email licensing@pnfsoftware.com.", new Object[]{
                            S.s(439)});
                    MessageDialog.openError(LicenseKeyAutoDialog.this.shell, "JEB", msg);
                }
            }
        });
        this.btnGen.setToolTipText("The automatic generation of a license key requires an active Internet connection.\nSpecify your proxy settings first if you are using one.");
        this.btnManualGen = UIUtil.createPushbox(c4, S.s(452), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                LicenseKeyDialog dlg2 = new LicenseKeyDialog(UIUtil.getParentShell(LicenseKeyAutoDialog.this.shell), LicenseKeyAutoDialog.this.licdata);
                LicenseKeyAutoDialog.this.lickey = dlg2.open();
                if (LicenseKeyAutoDialog.this.lickey != null) {
                    LicenseKeyAutoDialog.this.onLicenseKeyChange(true);
                    String msg = String.format("%s.\n\n%s.", new Object[]{S.s(770), S.s(637)});
                    MessageDialog.openInformation(LicenseKeyAutoDialog.this.shell, "JEB", msg);
                }
            }
        });
        UIUtil.createPushbox(c4, S.s(668), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                NetProxyInfo proxyinfo = new ProxyConfigDialog(UIUtil.getParentShell(LicenseKeyAutoDialog.this.shell), LicenseKeyAutoDialog.this.net.getProxyInformation(), LicenseKeyAutoDialog.this.executor).open();
                if (proxyinfo != null) {
                    Net.setGlobalProxyInformation(proxyinfo);
                }
            }
        });
        this.shell.setDefaultButton(this.btnGen);
    }

    void onLicenseKeyChange(boolean unknownKeyname) {
        if (this.lickey == null) {
            throw new IllegalStateException();
        }
        if (unknownKeyname) {
            this.textKeyname.setText("N/A");
        }
        this.textKeyname.setEditable(false);
        this.textKey.setText(this.lickey);
        this.btnGen.setText("Continue");
        this.btnManualGen.setEnabled(false);
    }

    static boolean looksLikeLicenseKey(String str) {
        if (str.length() < 10) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (((c < '0') || (c > '9')) && (c != 'Z')) {
                return false;
            }
        }
        return true;
    }
}


