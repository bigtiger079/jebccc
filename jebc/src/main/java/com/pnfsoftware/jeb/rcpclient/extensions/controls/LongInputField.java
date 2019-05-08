
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LongInputField
        extends Composite
        implements ITextControl {
    private Text textbox;

    public LongInputField(Composite parent, String name, String value) {
        super(parent, 0);
        setLayout(new GridLayout(1, false));
        this.textbox = create(this, name, value, true);
    }

    public static Text create(Composite parent, String name, String value, boolean fillLabel) {
        Label l = new Label(parent, 0);
        if (name != null) {
            l.setText(name);
        }
        l.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, fillLabel, false));
        Text textbox = new Text(parent, 2052);
        if (value != null) {
            textbox.setText(value);
        }
        textbox.setLayoutData(UIUtil.createGridDataFillHorizontally());
        return textbox;
    }

    public String getText() {
        return this.textbox.getText();
    }
}


