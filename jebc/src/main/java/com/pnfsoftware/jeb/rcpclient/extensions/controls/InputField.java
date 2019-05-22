package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class InputField extends Composite implements ITextControl {
    private Text textbox;

    public InputField(Composite parent, String name, String initialValue, int wantedLength) {
        super(parent, 0);
        setLayout(new GridLayout(2, false));
        this.textbox = new Text(this, 2052);
        if (initialValue != null) {
            this.textbox.setText(initialValue);
        }
        if (wantedLength >= 1) {
            GC gc = new GC(this.textbox);
            try {
                gc.setFont(this.textbox.getFont());
                FontMetrics fm = gc.getFontMetrics();
                GridData data = new GridData(wantedLength * fm.getAverageCharWidth(), fm.getHeight());
                this.textbox.setLayoutData(data);
            } finally {
                gc.dispose();
            }
        }
        Label l = new Label(this, 0);
        if (name != null) {
            l.setText(name);
        }
    }

    public String getText() {
        return this.textbox.getText();
    }
}


