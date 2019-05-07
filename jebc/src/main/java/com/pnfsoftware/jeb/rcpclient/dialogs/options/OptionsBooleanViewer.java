package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class OptionsBooleanViewer
        extends AbstractOptionsSimpleWidget {
    public OptionsBooleanViewer(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey) {
        super(changes, listener, propertyKey);
    }

    public Button create(Composite parent, String label, String toolTip) {
        Button b = UIUtil.createCheckbox(parent, label, new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean newValue = ((Button) e.getSource()).getSelection();
                OptionsBooleanViewer.this.changes.addChange(OptionsBooleanViewer.this.propertyKey, Boolean.valueOf(newValue));
            }
        });
        b.setSelection(BooleanUtils.toBoolean(this.changes.getBoolean(this.propertyKey)));
        if (toolTip != null) {
            b.setToolTipText(toolTip);
        }
        b.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        addSimpleViewElements(this.listener, this.propertyKey, b);
        return b;
    }
}