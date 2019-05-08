package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class IntroDialog extends JebDialog {
    public IntroDialog(Shell parent) {
        super(parent, "An introduction to JEB", true, true);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.NONE;
    }

    protected void createContents(Composite parent) {
        parent.setLayout(new FillLayout());
        Browser browser = new Browser(parent, 0);
        browser.addTitleListener(new TitleListener() {
            public void changed(TitleEvent event) {
                IntroDialog.this.shell.setText(event.title);
            }
        });
        browser.setBounds(0, 0, 580, 340);
        browser.setUrl("https://www.pnfsoftware.com/jeb/intro.html");
        browser.setText("<a href=\"http://www.google.com\">test</a>");
        String s = browser.getText();
    }
}


