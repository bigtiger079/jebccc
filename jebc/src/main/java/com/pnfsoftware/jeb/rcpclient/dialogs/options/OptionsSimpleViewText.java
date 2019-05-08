package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DirectorySelectorView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.LongInputField;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class OptionsSimpleViewText extends AbstractOptionsSimpleWidget {
    public OptionsSimpleViewText(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey) {
        super(changes, listener, propertyKey);
    }

    public Text create(Composite parent, String label, String toolTip, int tokenPosition) {
        Text text = buildText(parent, label, getToken(tokenPosition), toolTip);
        text.setData("TOKENIZE_NUMBER", Integer.valueOf(tokenPosition));
        syncText(text, tokenPosition);
        return text;
    }

    public Text create(Composite parent, String label, String toolTip) {
        Text text = buildText(parent, label, getValue(), toolTip);
        syncText(text);
        return text;
    }

    public DirectorySelectorView createDirectory(Composite parent, String label, String toolTip) {
        DirectorySelectorView dsv = new DirectorySelectorView(parent, label, getValue());
        dsv.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        Text text = dsv.getTextbox();
        if (toolTip != null) {
            text.setToolTipText(toolTip);
        }
        syncText(text);
        return dsv;
    }

    private Text buildText(Composite parent, String label, String value, String toolTip) {
        Text text = LongInputField.create(parent, label, value, false);
        if (toolTip != null) {
            text.setToolTipText(toolTip);
        }
        return text;
    }

    private void syncText(Text text) {
        syncText(text, -1);
    }

    private void syncText(final Text text, final int tokenPosition) {
        text.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String newValue = text.getText();
                if (tokenPosition >= 0) {
                    String previousValue = Strings.safe(OptionsSimpleViewText.this.getValue());
                    newValue = OptionsSimpleViewText.this.getNewValue(newValue, previousValue, tokenPosition);
                }
                OptionsSimpleViewText.this.changes.addChange(OptionsSimpleViewText.this.propertyKey, newValue);
            }
        });
        addSimpleViewElements(text);
    }

    protected static void refresh(Text t, Object[] data) {
        Object tokenPositionObj = t.getData("TOKENIZE_NUMBER");
        String newValue = data[1].toString();
        if (tokenPositionObj != null) {
            int tokenPosition = ((Integer) tokenPositionObj).intValue();
            newValue = getToken(newValue, tokenPosition);
        }
        t.setText(newValue);
    }
}


