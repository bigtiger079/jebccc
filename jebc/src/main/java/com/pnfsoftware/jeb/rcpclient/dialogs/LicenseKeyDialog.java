package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LicenseKeyDialog extends JebDialog {
    private String licdata;
    private String lickey;

    public LicenseKeyDialog(Shell parent, String licdata) {
        super(parent, "JEB", true, true);
        setVisualBounds(-1, 50, -1, -1);
        this.licdata = licdata;
    }

    public String open() {
        super.open();
        return this.lickey;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        String url = "https://www.pnfsoftware.com/genlk";
        String message = String.format(S.s(736), Licensing.user_name, "https://www.pnfsoftware.com/genlk");
        message = message.replace('\n', ' ').trim();
        String clickableUrl = String.format("<a href=\"%s\">%s</a>", "https://www.pnfsoftware.com/genlk", "https://www.pnfsoftware.com/genlk");
        message = message.replace("https://www.pnfsoftware.com/genlk", clickableUrl);
        Link t0 = new Link(parent, 64);
        t0.setText(message);
        t0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        ((GridData) t0.getLayoutData()).minimumWidth = 100;
        t0.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String url2 = String.format("%s?licdata=%s", "https://www.pnfsoftware.com/genlk", LicenseKeyDialog.this.licdata);
                BrowserUtil.openInBrowser(url2);
            }
        });
        new Label(parent, 0).setText("\n" + S.s(433) + ": ");
        Text text0 = new Text(parent, 2060);
        text0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        text0.setText(this.licdata);
        text0.selectAll();
        text0.setFocus();
        new Label(parent, 0).setText(S.s(436) + ": ");
        final Text text1 = new Text(parent, 2052);
        text1.setLayoutData(UIUtil.createGridDataFillHorizontally());
        Composite c4 = new Composite(parent, 0);
        c4.setLayout(new RowLayout(256));
        final Button btnOk = UIUtil.createPushbox(c4, S.s(605), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                LicenseKeyDialog.this.lickey = text1.getText();
                LicenseKeyDialog.this.shell.close();
            }
        });
        btnOk.setEnabled(false);
        this.shell.setDefaultButton(btnOk);
        text1.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String str = text1.getText().trim();
                btnOk.setEnabled(LicenseKeyAutoDialog.looksLikeLicenseKey(str));
            }
        });
    }
}


