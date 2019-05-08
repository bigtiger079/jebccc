package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.AbstractClientContext;
import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ButtonGroup;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AboutDialog
        extends JebDialog {
    static final String[] app_thirdpartylist = {"Android Framework Resources (Apache License 2.0)", "ANTLR4 (The BSD License)", "Apache Commons (Apache License 2.0)", "APKTool (Apache License 2.0)", "AOSP 'dx' package (Apache License 2.0)", "Eclipse Platform (Eclipse Public License)", "Google Guava (Apache License 2.0)", "JSON-Simple (Apache License 2.0)", "Jsoup (MIT License)", "Jython (Python Software Foundation License 2.0)", "LZ4 for Java (Apache License 2.0)", "Objenesis (Apache License 2.0)", "Okhttp, Okio (Apache License 2.0)", "SnakeYAML (Apache License 2.0)", "SQLite (Apache License 2.0)"};
    private AbstractClientContext context;
    private Color cWhite = UIAssetManager.getInstance().getColor(255, 255, 255);

    public AboutDialog(Shell parent, AbstractClientContext context) {
        super(parent, String.format(S.s(2), new Object[]{"JEB"}), true, true);
        this.scrolledContainer = true;
        this.context = context;
    }

    public Object open() {
        if (Licensing.isDebugBuild()) {
            System.gc();
        }
        super.open();
        return null;
    }

    public void createContents(Composite ctl) {
        UIUtil.setStandardLayout(ctl, 2, 10);
        ctl.setBackground(this.cWhite);
        Label l = new Label(ctl, 0);
        l.setLayoutData(new GridData(16384, 128, false, false));
        l.setBackground(this.cWhite);
        l.setImage(UIAssetManager.getInstance().getImage("jeb1/icon-jeb-48.png"));
        Composite c0 = new Composite(ctl, 0);
        GridData data = new GridData();
        data.horizontalIndent = 15;
        data.horizontalAlignment = 4;
        data.grabExcessHorizontalSpace = true;
        c0.setLayoutData(data);
        c0.setLayout(new GridLayout(1, false));
        c0.setBackground(this.cWhite);
        SelectionListener l_visitLink = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                BrowserUtil.openInBrowser(e.text);
            }
        };
        Link t0 = new Link(c0, 64);
        t0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t0.setBackground(this.cWhite);
        t0.setText(String.format("<a href=\"%s\">%s</a>", new Object[]{"https://www.pnfsoftware.com", "PNF Software, Inc."}));
        t0.addSelectionListener(l_visitLink);
        if ((Licensing.isFloatingBuild()) && (this.context != null)) {
            Link t01 = new Link(c0, 64);
            t01.setBackground(this.cWhite);
            String url = String.format("http://%s:%d", new Object[]{this.context.getControllerInterface(), Integer.valueOf(this.context.getControllerPort())});
            t01.setText(String.format("<a href=\"%s\">%s</a>", new Object[]{url, "Visit your Floating Controller Web Portal"}));
            t01.addSelectionListener(l_visitLink);
        }
        Label t1 = new Label(c0, 64);
        t1.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t1.setBackground(this.cWhite);
        t1.setText(String.format("\n%s - %s\n%s © %s\n\n", new Object[]{"JEB", "Interactive Decompilation for Software Analysis", "PNF Software, Inc.", "2015-2018"}));
        final Text t2 = new Text(c0, 2114);
        t2.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append(AbstractClientContext.generateLicenseInformation());
        if (this.context != null) {
            String licenseKey = this.context.getPropertyManager().getString(".LicenseKey");
            if ((licenseKey != null) && (!licenseKey.isEmpty())) {
                sb.append(String.format("%s: %s", new Object[]{S.s(436), licenseKey}));
            }
        }
        final String text = sb.toString();
        t2.setText(text);
        t2.setFocus();
        t2.selectAll();
        UIUtil.createPushbox(c0, S.s(213), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String sel = t2.getSelectionText();
                if (sel.isEmpty()) {
                    sel = text;
                }
                UIUtil.copyTextToClipboard(sel);
                MessageDialog.openInformation(AboutDialog.this.shell, S.s(210), S.s(432));
            }
        });
        Label t3 = new Label(c0, 64);
        t3.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t3.setBackground(this.cWhite);
        t3.setText(String.format("\n%s.\n", new Object[]{S.s(775)}));
        StringBuilder tplText = new StringBuilder();
        for (String thirdparty : app_thirdpartylist) {
            tplText.append(String.format("- %s\n", new Object[]{thirdparty}));
        }
        Text t31 = UIUtil.createTextboxInGrid(c0, 2562, 0, 4);
        t31.setEditable(false);
        t31.setText(tplText.toString());
        Label t4 = new Label(c0, 64);
        t4.setLayoutData(UIUtil.createGridDataFillHorizontally());
        t4.setBackground(this.cWhite);
        t4.setText("\n" + AbstractClientContext.formatMemoryUsage());
        Link t41 = new Link(c0, 64);
        t41.setBackground(this.cWhite);
        t41.setText(String.format("<a href=\"%s\">%s</a>", new Object[]{"https://www.pnfsoftware.com/jeb/faqmem", S.s(382)}));
        t41.addSelectionListener(l_visitLink);
        Label t5 = new Label(c0, 64);
        t3.setBackground(this.cWhite);
        t5.setText("\n");
        ButtonGroup bg = ButtonGroup.buildButtons(ctl, 0, 2);
        bg.setBackground(this.cWhite);
        Button btn_ok = bg.add(S.s(605), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                AboutDialog.this.shell.close();
            }
        });
        bg.add("Satisfaction Survey", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                BrowserUtil.openInBrowser("https://www.pnfsoftware.com/survey");
            }
        });
        if (Licensing.isDemoBuild()) {
            bg.add("Purchase a License", new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/buy");
                }
            });
        }
        this.shell.setDefaultButton(btn_ok);
    }
}
