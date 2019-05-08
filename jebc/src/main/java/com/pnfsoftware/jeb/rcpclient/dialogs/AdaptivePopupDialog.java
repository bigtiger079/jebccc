package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.IWidgetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AdaptivePopupDialog extends JebDialog {
    public static final int TYPE_INFORMATION = 1;
    public static final int TYPE_QUESTION = 2;
    private int type;
    private String message;
    private Button btnDoNotShow;
    private int retval;
    boolean doNotShow = false;

    public AdaptivePopupDialog(Shell parent, int type, String caption, String message, String widgetName) {
        super(parent, caption, true, true, widgetName);
        this.scrolledContainer = true;
        setMessage(message);
        setVisualBounds(30, -1, 15, -1);
        if ((type != 1) && (type != 2)) {
            throw new IllegalArgumentException("Invalid dialog type");
        }
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer open() {
        super.open();
        return Integer.valueOf(this.retval);
    }

    private void setPreferredShellIcon() {
        Image icon = null;
        if (this.type == 1) {
            icon = Display.getCurrent().getSystemImage(2);
        } else if (this.type == 2) {
            icon = Display.getCurrent().getSystemImage(4);
        }
        if (icon != null) {
            this.shell.setImage(icon);
        }
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        setPreferredShellIcon();
        Label label = new Label(parent, 64);
        label.setLayoutData(UIUtil.createGridDataFillHorizontally());
        label.setText(Strings.safe(this.message, "< " + S.s(287) + " >"));
        new Label(parent, 64).setText("");
        this.btnDoNotShow = UIUtil.createCheckbox(parent, S.s(281), null);
        if ((getWidgetManager() != null) && (getWidgetName() != null)) {
            this.btnDoNotShow.setSelection(!getWidgetManager().getShouldShowDialog(getWidgetName()));
        } else {
            this.btnDoNotShow.setEnabled(false);
        }
        if (this.type == 1) {
            createButtons(parent, 32, 32);
        } else if (this.type == 2) {
            createButtons(parent, 192, 64);
        }
    }

    protected void onConfirm() {
        record();
        super.onConfirm();
    }

    protected void onButtonNo() {
        record();
        super.onButtonNo();
    }

    protected void onButtonYes() {
        this.retval = 1;
        record();
        super.onButtonYes();
    }

    public boolean isDoNotShow() {
        return this.doNotShow;
    }

    private void record() {
        this.doNotShow = this.btnDoNotShow.getSelection();
        if ((getWidgetManager() != null) && (getWidgetName() != null)) {
            getWidgetManager().setShouldShowDialog(getWidgetName(), !this.doNotShow);
        }
    }
}


