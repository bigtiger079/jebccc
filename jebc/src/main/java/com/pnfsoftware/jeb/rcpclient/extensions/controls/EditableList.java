
package com.pnfsoftware.jeb.rcpclient.extensions.controls;


import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class EditableList
        extends Composite {
    private Label l;
    private Table table;
    private Composite buttons;


    public EditableList(Composite parent, int style, String label, String[] values) {

        super(parent, 0);

        setLayout(new FillLayout());


        Composite ph = new Composite(this, 0);

        ph.setLayout(new GridLayout(2, false));


        this.l = UIUtil.createLabel(ph, label);

        this.l.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));

        this.table = new Table(ph, 0x800 | style);

        GridData d = new GridData(-1, 100);

        d.horizontalAlignment = 4;

        d.grabExcessHorizontalSpace = true;

        this.table.setLayoutData(d);

        resetItems(values);

        this.buttons = new Composite(ph, 0);


        this.buttons.setLayout(new GridLayout(1, true));

        this.table.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                boolean enabled = (EditableList.this.getSelectionIndices() != null) && (EditableList.this.getSelectionIndices().length != 0);

                for (Control c : EditableList.this.compositeEnabledOnSelection.keySet()) {

                    c.setEnabled(enabled);

                }

            }

        });

    }


    Map<Control, Boolean> compositeEnabledOnSelection = new WeakHashMap();


    public void addButton(String label, SelectionListener listener, boolean enableOnSelection) {

        Button b = UIUtil.createPushbox(this.buttons, label, listener);

        GridData data = new GridData();

        data.horizontalAlignment = 4;

        b.setLayoutData(data);

        if (enableOnSelection) {

            this.compositeEnabledOnSelection.put(b, Boolean.TRUE);

            b.setEnabled(false);

        }

    }


    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);

        this.l.setEnabled(enabled);

        this.table.setEnabled(enabled);

        for (Control button : this.buttons.getChildren()) {

            button.setEnabled(enabled);

        }

    }


    public Table getTable() {

        return this.table;

    }


    public int[] getSelectionIndices() {

        return this.table.getSelectionIndices();

    }


    public TableItem[] getSelection() {

        return this.table.getSelection();

    }


    public void resetItems(String[] values) {

        int selected = this.table.getSelectionIndex();

        this.table.removeAll();

        for (String e : values) {

            TableItem item = new TableItem(this.table, 0);

            item.setText(e);

        }

        this.table.setSelection(selected);

    }


    public void resetItems(List<ICheckable> values) {

        int selected = this.table.getSelectionIndex();

        this.table.removeAll();

        for (ICheckable e : values) {

            TableItem item = new TableItem(this.table, 0);

            item.setText(e.getText());

            item.setChecked(e.isChecked());

        }

        this.table.setSelection(selected);

    }


    public static class SimpleCheckable
            implements EditableList.ICheckable {
        private String text;

        private boolean checked;


        public SimpleCheckable(String text, boolean checked) {

            this.text = text;

            this.checked = checked;

        }


        public String getText() {

            return this.text;

        }


        public boolean isChecked() {

            return this.checked;

        }

    }


    public static abstract interface ICheckable {

        public abstract String getText();


        public abstract boolean isChecked();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\EditableList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */