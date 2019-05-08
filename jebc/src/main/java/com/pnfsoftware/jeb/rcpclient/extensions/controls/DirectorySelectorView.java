
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DirectorySelectorView
        extends Composite
        implements ITextControl {
    private Label l;
    private Text textbox;
    private Button browse;

    public DirectorySelectorView(Composite parent, String name, String value) {
        super(parent, 0);
        setLayout(new GridLayout(3, false));
        this.l = new Label(this, 0);
        if (name != null) {
            this.l.setText(name);
        }
        this.textbox = new Text(this, 2052);
        if (value != null) {
            this.textbox.setText(value);
        }
        this.textbox.setLayoutData(UIUtil.createGridDataForText(this.textbox, 50));
        this.browse = UIUtil.createPushbox(this, S.s(102), new DirectorySelectionListener(getShell(), this.textbox));
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.l != null) {
            this.l.setEnabled(enabled);
        }
        this.textbox.setEnabled(enabled);
        this.browse.setEnabled(enabled);
    }

    public Text getTextbox() {
        return this.textbox;
    }

    public String getText() {
        return this.textbox.getText();
    }
}


