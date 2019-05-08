package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class CustomerActionDialog
        extends JebDialog {
    private static final String surveySimpleURL = "pnfsoftware.com/survey";
    public static final String surveyURL = "https://www.pnfsoftware.com/survey";
    private Color cWhite = UIAssetManager.getInstance().getColor(255, 255, 255);

    public CustomerActionDialog(Shell parent) {
        super(parent, "Satisfaction Survey", true, true);
        this.scrolledContainer = true;
    }

    public Object open() {
        super.open();
        return null;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2, 10);
        Color white = parent.getDisplay().getSystemColor(1);
        parent.setBackground(white);
        Label l = new Label(parent, 0);
        l.setLayoutData(new GridData(16384, 128, false, false));
        l.setBackground(parent.getDisplay().getSystemColor(1));
        l.setImage(UIAssetManager.getInstance().getImage("jeb1/icon-jeb-48.png"));
        Composite c0 = new Composite(parent, 0);
        GridData data = new GridData();
        data.horizontalIndent = 15;
        c0.setLayoutData(data);
        c0.setLayout(new RowLayout(512));
        c0.setBackground(white);
        SelectionListener l_visitWebsite = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                BrowserUtil.openInBrowser("https://www.pnfsoftware.com/survey");
            }
        };
        String text = "Dear User,\n\nWe are conducting a satisfaction survey and would love to hear from you.\nThe survey contains 7 questions and will take just a minute of your time.\n\nThank you in advance from your participation! (If you decide to take this survey\nlater, the web link can also be found in the About box of the Help menu.)\n\n";
        Label t1 = new Label(c0, 0);
        t1.setBackground(white);
        t1.setText(text);
        Link t0 = new Link(c0, 0);
        t0.setBackground(white);
        t0.setText(String.format("<a href=\"%s\">Take the Survey</a> (Will navigate to %s)\n\n", new Object[]{"https://www.pnfsoftware.com/survey", "pnfsoftware.com/survey"}));
        t0.addSelectionListener(l_visitWebsite);
        Composite buttons = createOkayButton(parent);
        buttons.setBackground(this.cWhite);
    }
}


