package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TriStateField extends Composite {
    private Combo wlist;

    public TriStateField(Composite parent, String name, Boolean initialState, String thirdStateLabel) {
        super(parent, 0);
        setLayout(new GridLayout(2, false));
        this.wlist = new Combo(this, 8);
        this.wlist.setItems(Strings.safe(thirdStateLabel, "Unknown"), "Yes", "No");
        this.wlist.setLayoutData(UIUtil.createGridDataFillHorizontally());
        setState(initialState);
        Label label = new Label(this, 0);
        label.setText(name);
    }

    public void setState(Boolean state) {
        int index = state ? 1 : state == null ? 0 : 2;
        this.wlist.select(index);
    }

    public Boolean getState() {
        int index = Math.max(0, this.wlist.getSelectionIndex());
        return index == 1 ? Boolean.TRUE : index == 0 ? null : Boolean.FALSE;
    }
}


