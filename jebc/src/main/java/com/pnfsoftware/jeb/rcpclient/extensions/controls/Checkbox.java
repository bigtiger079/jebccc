
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class Checkbox
        extends Composite
        implements ITextControl {
    private Button btn;

    public Checkbox(Composite parent, String name, boolean selected) {
        super(parent, 0);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        setLayout(layout);
        this.btn = new Button(parent, 32);
        if (name != null) {
            this.btn.setText(name);
        }
        this.btn.setSelection(selected);
    }

    public String getText() {
        return Boolean.toString(this.btn.getSelection());
    }
}


