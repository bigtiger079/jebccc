package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputDialog extends JebDialog {
    private Text text;
    private String message;
    private boolean confirmed;
    private String value;
    private boolean multiline;
    private int minColumnCount;
    private int minLineCount;

    public InputDialog(Shell parent, String caption, String initialValue) {
        this(parent, caption, initialValue, false, 0, 0);
    }

    public InputDialog(Shell parent, String caption, String initialValue, boolean multiline, int minColumnCount, int minLineCount) {
        super(parent, caption, true, true);
        this.scrolledContainer = true;
        this.value = Strings.safe(initialValue);
        this.multiline = multiline;
        this.minColumnCount = minColumnCount;
        this.minLineCount = minLineCount;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return this.value;
    }

    public String open() {
        super.open();
        return this.confirmed ? this.value : null;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        Label label = new Label(parent, 0);
        label.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        if (this.message != null) {
            label.setText(this.message);
        }
        if (this.minColumnCount <= 0) {
            if (this.value != null) {
                this.minColumnCount = this.value.length();
            } else {
                this.minColumnCount = 40;
            }
        }
        this.minColumnCount = Math.max(30, Math.min(this.minColumnCount, 60));
        this.minLineCount = Math.max(1, Math.min(this.minLineCount, 10));
        if ((this.multiline) && (this.minLineCount >= 1)) {
            this.text = UIUtil.createTextboxInGrid(parent, 2626, this.minColumnCount, this.minLineCount);
        } else {
            this.text = UIUtil.createTextboxInGrid(parent, 2048, this.minColumnCount, 1);
        }
        if (this.value != null) {
            this.text.setText(this.value);
            this.text.selectAll();
            this.value = null;
        }
        UIUtil.disableTabOutput(this.text);
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.confirmed = true;
        this.value = this.text.getText();
        super.onConfirm();
    }
}


