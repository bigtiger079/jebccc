package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class OptionsSimpleViewCombo
        extends AbstractOptionsSimpleWidget {
    public OptionsSimpleViewCombo(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey) {
        super(changes, listener, propertyKey);
    }

    public Combo createComboBox(Composite parent, String label, String toolTip, final int tokenPosition, String[] options) {
        Combo combo = buildComboBox(parent, label, getToken(tokenPosition), toolTip, options);
        combo.setData("TOKENIZE_NUMBER", Integer.valueOf(tokenPosition));
        combo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Combo source = (Combo) e.getSource();
                int newIndex = source.getSelectionIndex();
                String newValue = source.getItem(newIndex);
                if (tokenPosition >= 0) {
                    String previousValue = Strings.safe(OptionsSimpleViewCombo.this.changes.getString(OptionsSimpleViewCombo.this.propertyKey));
                    newValue = OptionsSimpleViewCombo.this.getNewValue(newValue, previousValue, tokenPosition);
                }
                OptionsSimpleViewCombo.this.changes.addChange(OptionsSimpleViewCombo.this.propertyKey, newValue);
            }
        });
        addSimpleViewElements(combo);
        return combo;
    }

    private Combo buildComboBox(Composite parent, String label, String value, String toolTip, String[] items) {
        Label l = UIUtil.createLabel(parent, label);
        if (toolTip != null) {
            l.setToolTipText(toolTip);
        }
        Combo combo = new Combo(parent, 2056);
        combo.setItems(items);
        selectComboValue(combo, value);
        if (toolTip != null) {
            combo.setToolTipText(toolTip);
        }
        combo.setLayoutData(UIUtil.createGridDataFillHorizontally());
        return combo;
    }

    protected static int selectComboValue(Combo combo, String value) {
        String[] comboItems = combo.getItems();
        for (int i = 0; i < comboItems.length; i++) {
            if (comboItems[i].equalsIgnoreCase(value)) {
                combo.select(i);
                return i;
            }
        }
        combo.deselectAll();
        combo.setText("");
        return -1;
    }

    protected static void refresh(Combo t, Object[] data) {
        Object tokenPositionObj = t.getData("TOKENIZE_NUMBER");
        String newValue = data[1].toString();
        if (tokenPositionObj != null) {
            int tokenPosition = ((Integer) tokenPositionObj).intValue();
            newValue = getToken(newValue, tokenPosition);
        }
        selectComboValue(t, newValue);
    }
}


