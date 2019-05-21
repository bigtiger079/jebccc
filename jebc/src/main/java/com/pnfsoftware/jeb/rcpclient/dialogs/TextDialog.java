package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextDialog extends JebDialog {
    private Text text;
    private String textLabel;
    private String input;
    private Font font;
    private int linecount;
    private int colcount;
    private boolean editable;
    private boolean selected;
    private String initialText;
    private Integer labelOk;
    private Integer labelCancel;
    protected Button btnOk;
    protected Button btnCancel;

    public TextDialog(Shell parent, String caption, String initialText, String widgetName) {
        super(parent, caption, true, true, widgetName);
        this.linecount = 1;
        this.colcount = 20;
        this.editable = true;
        this.selected = false;
        this.initialText = initialText;
        this.labelOk = 605;
        this.labelCancel = 105;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setInitialText(String text) {
        this.initialText = text;
    }

    public void setTextLabel(String label) {
        this.textLabel = label;
    }

    public void setLineCount(int linecount) {
        this.linecount = linecount;
    }

    public void setColumnCount(int colcount) {
        this.colcount = colcount;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setOkLabelId(Integer labelId) {
        this.labelOk = labelId;
    }

    public void setCancelLabelId(Integer labelId) {
        this.labelCancel = labelId;
    }

    public String open() {
        super.open();
        return this.input;
    }

    public String getInputText() {
        return this.input;
    }

    public final void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        createBeforeText(parent);
        createText(parent);
        createAfterText(parent);
        createButtons(parent);
    }

    protected void createBeforeText(Composite parent) {
    }

    protected void createAfterText(Composite parent) {
    }

    protected void createText(Composite parent) {
        if (this.textLabel != null) {
            new Label(parent, 0).setText(this.textLabel + ": ");
        }
        GridData data = new GridData();
        if (this.linecount >= 2) {
            this.text = new Text(parent, 2626);
        } else {
            this.text = new Text(parent, 2052);
        }
        this.text.setFont(this.font);
        GC gc = new GC(this.text);
        try {
            gc.setFont(this.text.getFont());
            FontMetrics fm = gc.getFontMetrics();
            data = new GridData(this.colcount * fm.getAverageCharWidth(), this.linecount * fm.getHeight());
        } finally {
            gc.dispose();
        }
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = 4;
        this.text.setLayoutData(data);
        this.text.setEditable(this.editable);
        if (this.initialText != null) {
            this.text.setText(this.initialText);
            if (this.selected) {
                this.text.selectAll();
            }
        }
        UIUtil.disableTabOutput(this.text);
    }

    protected void createButtons(Composite parent) {
        List<int[]> avails = new ArrayList<>();
        if (this.labelOk != null) {
            avails.add(new int[]{32, this.labelOk == -1 ? 'ɝ' : this.labelOk});
        }
        if (this.labelCancel != null) {
            avails.add(new int[]{256, this.labelCancel == -1 ? 105 : this.labelCancel});
        }
        createButtons(parent, 0, avails.toArray(new int[avails.size()][]), 32);
    }

    protected void onConfirm() {
        this.input = this.text.getText();
        super.onConfirm();
    }
}


