
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ButtonGroup
        extends Composite {
    private Composite buttons;
    private List<Button> buttonList = new ArrayList();

    protected ButtonGroup(Composite parent, int style, GridData gridData) {
        super(parent, style);
        if (gridData != null) {
            setLayoutData(gridData);
            this.buttons = new Composite(this, 0);
            UIUtil.setStandardLayout(this, 1, 0);
        } else {
            this.buttons = this;
        }
        RowLayout rl = new RowLayout(256);
        rl.spacing = 6;
        this.buttons.setLayout(rl);
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        this.buttons.setBackground(color);
    }

    public Button add(String name, SelectionListener listener) {
        Button b = UIUtil.createPushbox(this.buttons, name, listener);
        this.buttonList.add(b);
        return b;
    }

    public List<Button> getButtonList() {
        return this.buttonList;
    }

    public static ButtonGroup buildBottomButtons(Composite parent, int style) {
        return buildBottomButtons(parent, style, 1);
    }

    public static ButtonGroup buildBottomButtons(Composite parent, int style, int horizontalSpan) {
        GridData gd = new GridData(4, 1024, true, true, horizontalSpan, 1);
        return new ButtonGroup(parent, style, gd);
    }

    public static ButtonGroup buildButtons(Composite parent, int style) {
        return new ButtonGroup(parent, style, null);
    }

    public static ButtonGroup buildButtons(Composite parent, int style, int horizontalSpan) {
        GridData gd = UIUtil.createGridDataSpanHorizontally(horizontalSpan);
        return new ButtonGroup(parent, style, gd);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\ButtonGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */